package com.edutech.logisticsmanagementandtrackingsystem.Controller;

import com.edutech.logisticsmanagementandtrackingsystem.dto.ChatRequest;
import com.edutech.logisticsmanagementandtrackingsystem.dto.ChatResponse;
import com.edutech.logisticsmanagementandtrackingsystem.service.RuleBasedChatService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private RuleBasedChatService chatService;

    @PostMapping("/message")
    public ResponseEntity<ChatResponse> message(@RequestBody ChatRequest request) {
        if (request == null || request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ChatResponse("Message cannot be empty.", "EMPTY"));
        }

        ChatResponse response = chatService.reply(request.getMessage(), request.getContext());
        return ResponseEntity.ok(response);
    }
}