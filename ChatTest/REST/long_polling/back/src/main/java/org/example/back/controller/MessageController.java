package org.example.back.controller;

import org.example.back.model.ChatMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController // RESTful 컨트롤러를 나타냄
@RequestMapping("/chat") // /chat 경로로 들어오는 요청을 처리
@CrossOrigin(origins = "http://localhost:3000") // CORS 설정: 프론트엔드(3000번 포트)에서 요청 허용
public class MessageController {

    // 메시지를 저장하는 Map (key: 수신자 ID, value: 메시지 리스트)
    private final Map<String, List<Map<String, String>>> chatMessages = new HashMap<>();

    // 메시지 전송
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody ChatMessage chatMessage) {
        // 요청 데이터 출력
        System.out.println(">>> Received POST request at /chat/send");
        System.out.println(">>> Incoming request body: " + chatMessage);

        // 메시지 저장
        String sendId = chatMessage.getSendId();
        String receiveId = chatMessage.getReceiveId();
        Map<String, String> messageData = new HashMap<>();
        messageData.put("sendId", sendId);
        messageData.put("message", chatMessage.getMessage());
        messageData.put("receiveId", receiveId);

        // 수신자의 메시지 리스트에 추가
        chatMessages.putIfAbsent(receiveId, new ArrayList<>());
        chatMessages.get(receiveId).add(messageData);

        // 송신자의 메시지 리스트에도 추가
        chatMessages.putIfAbsent(sendId, new ArrayList<>());
        chatMessages.get(sendId).add(messageData);

        System.out.println(">>> Stored message for receiver " + receiveId + ": " + messageData);
        System.out.println(">>> Stored message for sender " + sendId + ": " + messageData);

        // JSON 응답 생성
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", messageData);

        // 응답 데이터 출력
        System.out.println(">>> Sending response: " + response);

        return ResponseEntity.ok(response);
    }


    // 메시지 기록 조회
    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getChatHistory(
            @RequestParam String user1,
            @RequestParam String user2) {
        // 요청 데이터 출력
        System.out.println(">>> Received GET request at /chat/history with user1: " + user1 + " and user2: " + user2);

        // 두 사용자 간의 메시지 필터링
        List<Map<String, String>> messages = new ArrayList<>();
        if (chatMessages.containsKey(user1)) {
            for (Map<String, String> msg : chatMessages.get(user1)) {
                if (msg.get("sendId").equals(user2) || msg.get("receiveId").equals(user2)) {
                    messages.add(msg);
                }
            }
        }

        System.out.println(">>> Messages between " + user1 + " and " + user2 + ": " + messages);

        // JSON 응답 생성
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("messages", messages);

        // 응답 데이터 출력
        System.out.println(">>> Sending response: " + response);

        return ResponseEntity.ok(response);
    }
}
