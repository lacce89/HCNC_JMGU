document.addEventListener("DOMContentLoaded", () => {
  const sendButton = document.getElementById("sendButton");
  const messageInput = document.getElementById("messageInput");
  const messagesContainer = document.getElementById("messages");

  const urlParams = new URLSearchParams(window.location.search);
  const sender = urlParams.get("sender");
  const receiver = urlParams.get("receiver");

  console.log("Sender:", sender);
  console.log("Receiver:", receiver);

  // 메시지 출력 함수
  function appendMessage(msg) {
      const li = document.createElement("li");
      li.textContent = `${msg.sendId}: ${msg.message}`;
      messagesContainer.appendChild(li);
  }

  // Long polling으로 채팅 기록 가져오기
  async function longPolling() {
      try {
          console.log("Starting long polling...");
          const response = await fetch(`http://localhost:8080/chat/longpoll?user1=${sender}&user2=${receiver}`);
          const data = await response.json();

          console.log("Received data from long polling:", data);

          if (data.status === "success") {
              // 새로운 메시지 출력
              data.messages.forEach((msg) => appendMessage(msg));
          }

          // 새로운 요청으로 Long Polling 지속
          longPolling();
      } catch (error) {
          console.error("Error in long polling:", error);

          // 에러 발생 시 일정 시간 후 재시도
          setTimeout(longPolling, 3000);
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
              appendMessage({ sendId: sender, message });
              messageInput.value = "";
          }
      } catch (error) {
          console.error("Error sending message:", error);
      }
  });

  // Long polling 시작
  longPolling();
});
