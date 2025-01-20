            package org.example.back.controller;

            import org.example.back.model.ChatMessage;
            import org.springframework.http.ResponseEntity;
            import org.springframework.web.bind.annotation.*;
            import org.springframework.web.context.request.async.DeferredResult;

            import java.util.*;
            import java.util.concurrent.ConcurrentHashMap;

            @RestController
            @RequestMapping("/chat")
            @CrossOrigin(origins = "http://localhost:3000")
            public class MessageController {

                // 메시지를 저장하는 Map (key: 수신자 ID, value: 메시지 리스트)
                private final Map<String, List<Map<String, String>>> chatMessages = new ConcurrentHashMap<>();

                // 대기 중인 클라이언트 요청 (key: 사용자 ID, value: 대기 중인 요청 리스트)
                private final Map<String, List<DeferredResult<ResponseEntity<Map<String, Object>>>>> waitingClients = new ConcurrentHashMap<>();

                // 메시지 전송
                @PostMapping("/send")
                public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody ChatMessage chatMessage) {
                    System.out.println(">>> Received POST request at /chat/send");
                    System.out.println(">>> Incoming request body: " + chatMessage);

                    String sendId = chatMessage.getSendId();
                    String receiveId = chatMessage.getReceiveId();
                    Map<String, String> messageData = new HashMap<>();
                    messageData.put("sendId", sendId);
                    messageData.put("message", chatMessage.getMessage());
                    messageData.put("receiveId", receiveId);

                    // 메시지 저장
                    chatMessages.putIfAbsent(receiveId, new ArrayList<>());
                    chatMessages.get(receiveId).add(messageData);

                    chatMessages.putIfAbsent(sendId, new ArrayList<>());
                    chatMessages.get(sendId).add(messageData);

                    System.out.println(">>> Stored message: " + messageData);

                    // 대기 중인 클라이언트 요청 처리
                    if (waitingClients.containsKey(receiveId)) {
                        List<DeferredResult<ResponseEntity<Map<String, Object>>>> clients = waitingClients.get(receiveId);
                        for (DeferredResult<ResponseEntity<Map<String, Object>>> client : clients) {
                            Map<String, Object> response = new HashMap<>();
                            response.put("status", "success");
                            response.put("messages", List.of(messageData));
                            client.setResult(ResponseEntity.ok(response));
                        }
                        waitingClients.remove(receiveId); // 처리된 클라이언트 요청 삭제
                    }

                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "success");
                    response.put("data", messageData);

                    return ResponseEntity.ok(response);
                }

                // Long polling을 사용한 메시지 기록 조회
                @GetMapping("/longpoll")
                public DeferredResult<ResponseEntity<Map<String, Object>>> longPollMessages(
                        @RequestParam String user1,
                        @RequestParam String user2) {
                    System.out.println(">>> Received GET request at /chat/longpoll with user1: " + user1 + " and user2: " + user2);

                    // DeferredResult 생성 (타임아웃: 10초)
                    DeferredResult<ResponseEntity<Map<String, Object>>> deferredResult = new DeferredResult<>(10000L);
                    waitingClients.putIfAbsent(user1, new ArrayList<>());
                    waitingClients.get(user1).add(deferredResult);

                    // 타임아웃 처리
                    deferredResult.onTimeout(() -> {
                        System.out.println(">>> Long polling timed out for user: " + user1);
                        Map<String, Object> timeoutResponse = new HashMap<>();
                        timeoutResponse.put("status", "timeout");
                        timeoutResponse.put("messages", Collections.emptyList());
                        deferredResult.setResult(ResponseEntity.ok(timeoutResponse));

                        // 대기 중인 클라이언트 요청 삭제
                        waitingClients.get(user1).remove(deferredResult);
                        if (waitingClients.get(user1).isEmpty()) {
                            waitingClients.remove(user1);
                        }
                    });

                    System.out.println(">>> Long polling setup complete for user: " + user1);
                    return deferredResult;
                }

                // 기존 메시지 기록 조회 (선택적으로 유지)
                @GetMapping("/history")
                public ResponseEntity<Map<String, Object>> getChatHistory(
                        @RequestParam String user1,
                        @RequestParam String user2) {
                    System.out.println(">>> Received GET request at /chat/history with user1: " + user1 + " and user2: " + user2);

                    List<Map<String, String>> messages = new ArrayList<>();
                    if (chatMessages.containsKey(user1)) {
                        for (Map<String, String> msg : chatMessages.get(user1)) {
                            if (msg.get("sendId").equals(user2) || msg.get("receiveId").equals(user2)) {
                                messages.add(msg);
                            }
                        }
                    }

                    System.out.println(">>> Messages between " + user1 + " and " + user2 + ": " + messages);

                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "success");
                    response.put("messages", messages);

                    System.out.println(">>> Sending response: " + response);

                    return ResponseEntity.ok(response);
                }
            }
