const express = require("express"); // Express 모듈 가져오기
const router = express.Router(); // 라우터 생성

// GET 요청: "/chat" 경로로 들어왔을 때 처리
router.get("/", (req, res) => {
  const { sender, receiver } = req.query; // 쿼리 파라미터에서 sender와 receiver 값을 추출

  // sender 또는 receiver 값이 없으면 홈으로 리다이렉트
  if (!sender || !receiver) {
    return res.redirect("/");
  }

  // chat.pug 파일을 렌더링하며 title, sender, receiver 값을 전달
  res.render("chat", {
    title: "Chat Room",
    sender: sender,
    receiver: receiver,
  });
});

module.exports = router; // 라우터를 외부에서 사용할 수 있도록 모듈로 내보냄
