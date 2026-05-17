/**
 * Closes a pull request immediately when it has no linked open issue, or when the
 * PR author is not assigned to any linked open issue.
 *
 * Uses GitHub's closingIssuesReferences (keywords such as "Fixes #123" in the PR body).
 */

const dryRun = (process.env.DRY_RUN || 'false').toString().toLowerCase() === 'true';
const requireAuthorAssigned =
  (process.env.REQUIRE_AUTHOR_ASSIGNED || 'true').toLowerCase() === 'true';

const COMMENT_MARKER = '<!-- linked-issue-enforcer -->';

const baseMessage = `Hi @{{author}}, I'm **LinkedIssueBot**.

This pull request has been automatically closed for the following reason:

`;

const messageSuffix = `

Before reopening, please link this PR to an **open** issue you are **assigned** to (for example: \`Fixes #123\` in the PR description).

Thank you,
Hiero Enterprise Java maintainers`;

const messages = {
  no_issue: `${baseMessage}- This pull request is not linked to any open issue.`,
  not_assigned: `${baseMessage}- You are not assigned to the linked issue.`,
};

const isBotAuthor = (pr) => pr.user?.type === 'Bot';

const isAuthorAssigned = (issue, login) => {
  if (!issue || issue.state?.toUpperCase() !== 'OPEN') return false;
  const assignees = issue.assignees?.nodes?.map((a) => a.login) || [];
  return assignees.includes(login);
};

async function getLinkedIssues(github, prNumber, owner, repo) {
  const query = `
    query($owner: String!, $repo: String!, $prNumber: Int!) {
      repository(owner: $owner, name: $repo) {
        pullRequest(number: $prNumber) {
          closingIssuesReferences(first: 100) {
            nodes {
              number
              state
              assignees(first: 100) {
                nodes { login }
              }
            }
          }
        }
      }
    }
  `;
  try {
    const result = await github.graphql(query, { owner, repo, prNumber });
    const allIssues =
      result.repository.pullRequest.closingIssuesReferences.nodes || [];
    return allIssues.filter((issue) => issue.state === 'OPEN');
  } catch (err) {
    console.error(`GraphQL query failed for PR #${prNumber}:`, err.message);
    return null;
  }
}

async function validatePR(github, pr, owner, repo) {
  const issues = await getLinkedIssues(github, pr.number, owner, repo);

  if (issues === null) {
    console.log(`Skipping PR #${pr.number} due to API error (fail-safe).`);
    return { valid: true };
  }

  if (issues.length === 0) return { valid: false, reason: 'no_issue' };

  if (requireAuthorAssigned) {
    const assigned = issues.some((issue) =>
      isAuthorAssigned(issue, pr.user.login),
    );
    if (!assigned) return { valid: false, reason: 'not_assigned' };
  }

  return { valid: true };
}

async function closePR(github, pr, owner, repo, reason) {
  const author = pr.user?.login || 'there';
  const body =
    COMMENT_MARKER +
    messages[reason].replace('{{author}}', author) +
    messageSuffix;

  if (dryRun) {
    console.log(`[DRY RUN] Would close PR #${pr.number} (${reason})`);
    return;
  }

  await github.rest.issues.createComment({
    owner,
    repo,
    issue_number: pr.number,
    body,
  });
  await github.rest.pulls.update({
    owner,
    repo,
    pull_number: pr.number,
    state: 'closed',
  });
  console.log(`Closed PR #${pr.number} (${reason})`);
}

module.exports = async ({ github, context, core }) => {
  const { owner, repo } = context.repo;

  let pr;
  if (context.eventName === 'workflow_dispatch') {
    const prNumber = parseInt(process.env.PR_NUMBER || '', 10);
    if (!prNumber) {
      core.setFailed('PR_NUMBER is required for workflow_dispatch');
      return;
    }
    const { data } = await github.rest.pulls.get({
      owner,
      repo,
      pull_number: prNumber,
    });
    pr = data;
  } else {
    pr = context.payload.pull_request;
  }

  if (!pr) {
    core.setFailed('No pull request in workflow context');
    return;
  }

  if (pr.state !== 'open') {
    console.log(`PR #${pr.number} is not open (${pr.state}). Skipping.`);
    return;
  }

  const authorLogin = pr.user?.login;
  if (!authorLogin) {
    console.warn(`PR #${pr.number} missing author login. Skipping for safety.`);
    return;
  }

  if (isBotAuthor(pr)) {
    console.log(`PR #${pr.number} authored by bot (${authorLogin}). Skipping.`);
    return;
  }

  console.log(`Evaluating PR #${pr.number} by @${authorLogin}`);

  const { valid, reason } = await validatePR(github, pr, owner, repo);
  if (valid) {
    console.log(`PR #${pr.number} is valid.`);
    return;
  }

  await closePR(github, pr, owner, repo, reason);
};
