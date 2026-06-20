package org.hiero.spring.sample.web;

import org.hiero.base.AccountClient;
import org.hiero.base.mirrornode.AccountRepository;
import org.hiero.base.data.AccountInfo;
import com.hedera.hashgraph.sdk.Hbar;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountClient accountClient;
    private final AccountRepository accountRepository;

    public AccountController(AccountClient accountClient, AccountRepository accountRepository) {
        this.accountClient = accountClient;
        this.accountRepository = accountRepository;
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<?> getInfo(@PathVariable("accountId") String accountId) {
        try {
            Optional<AccountInfo> info = accountRepository.findById(accountId);
            return info.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<?> getBalance(@PathVariable("accountId") String accountId) {
        try {
            Hbar balance = accountClient.getAccountBalance(accountId);
            return ResponseEntity.ok(Collections.singletonMap("balance", balance.toString()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
