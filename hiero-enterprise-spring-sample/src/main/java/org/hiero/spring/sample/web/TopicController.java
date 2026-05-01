package org.hiero.spring.sample.web;

import org.hiero.base.TopicClient;
import com.hedera.hashgraph.sdk.TopicId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/topics")
public class TopicController {

    private final TopicClient topicClient;

    public TopicController(TopicClient topicClient) {
        this.topicClient = topicClient;
    }

    @PostMapping
    public ResponseEntity<?> createTopic(@RequestBody Map<String, String> body) {
        try {
            String memo = body.getOrDefault("memo", "");
            TopicId topicId = topicClient.createTopic(memo);
            return ResponseEntity.ok(Collections.singletonMap("topicId", topicId.toString()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/{topicId}/messages")
    public ResponseEntity<?> sendMessage(@PathVariable("topicId") String topicId, @RequestBody Map<String, String> body) {
        try {
            String message = body.get("message");
            topicClient.submitMessage(topicId, message);
            return ResponseEntity.ok(Collections.singletonMap("status", "SUCCESS"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
