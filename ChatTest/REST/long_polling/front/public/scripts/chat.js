document.addEventListener("DOMContentLoaded", () => {
    const sendButton = document.getElementById("sendButton"); // 전송 버튼
    const messageInput = document.getElementById("messageInput"); // 입력창
    const messagesContainer = document.getElementById("messages"); // 메시지 출력 영역
  
    const urlParams = new URLSearchParams(window.location.search);
    const sender = urlParams.get("sender"); // 송신자
    const receiver = urlParams.get("receiver"); // 수신자
  
    console.log("Sender:", sender);
    console.log("Receiver:", receiver);
  
    // 채팅 기록 가져오기 함수
    async function fetchHistory() {
      try {
        console.log("Fetching chat history...");
        const response = await fetch(`http://localhost:8080/chat/history?user1=${sender}&user2=${receiver}`);
        const data = await response.json();
  
        console.log("Fetched history response:", data);
  
        if (data.status === "success") {
          messagesContainer.innerHTML = ""; // 기존 메시지 삭제
          data.messages.forEach((msg) => {
            const li = document.createElement("li");
            li.textContent = `${msg.sendId}: ${msg.message}`;
            messagesContainer.appendChild(li);
          });
        }
      } catch (error) {
        console.error("Error fetching chat history:", error);
      }
    }
  
    // 메시지 전송 함수
    sendButton.addEventListener("click", async () => {
      const message = messageInput.value;
      if (!message) return;
  
      console.log("Sending message:", {
        sendId: sender,
        receiveId: receiver,
        message: message,
      });
  
      try {
        const response = await fetch("http://localhost:8080/chat/send", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            sendId: sender,
            receiveId: receiver,
            message: message,
          }),
        });
  
        const data = await response.json();
        console.log("Message send response:", data);
  
        if (data.status === "success") {
          const li = document.createElement("li");
          li.textContent = `${sender}: ${message}`;
          messagesContainer.appendChild(li);
          messageInput.value = ""; // 입력창 초기화
        }
      } catch (error) {
        console.error("Error sending message:", error);
      }
    });
  
    // 주기적으로 채팅 기록 가져오기
    setInterval(fetchHistory, 3000); // 3초마다 fetchHistory 호출
  
    // 페이지 로드 시 첫 채팅 기록 가져오기
    fetchHistory();
  });
  