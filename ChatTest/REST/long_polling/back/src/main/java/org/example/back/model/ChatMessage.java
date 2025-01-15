package org.example.back.model;

public class ChatMessage {
    private String sendId;    // 송신자 ID
    private String receiveId; // 수신자 ID
    private String message;   // 메시지 내용

    // 기본 생성자
    public ChatMessage() {}

    // 모든 필드를 초기화하는 생성자
    public ChatMessage(String sendId, String receiveId, String message) {
        this.sendId = sendId;
        this.receiveId = receiveId;
        this.message = message;
    }

    // Getter와 Setter (필드 접근 및 설정)
    public String getSendId() { return sendId; }
    public void setSendId(String sendId) { this.sendId = sendId; }
    public String getReceiveId() { return receiveId; }
    public void setReceiveId(String receiveId) { this.receiveId = receiveId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "sendId='" + sendId + '\'' +
                ", receiveId='" + receiveId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
