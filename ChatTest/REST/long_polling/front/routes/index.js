const express = require("express"); // Express 모듈 가져오기
const router = express.Router(); // 라우터 생성

// GET 요청: "/" 경로로 들어왔을 때 처리
router.get("/", (req, res) => {
  // index.pug 파일을 렌더링하며 title 값을 전달
  res.render("index", { title: "Chat Application" });
});

module.exports = router; // 라우터를 외부에서 사용할 수 있도록 모듈로 내보냄
