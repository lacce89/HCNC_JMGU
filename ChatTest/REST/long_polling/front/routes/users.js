const express = require("express"); // Express 모듈 가져오기
const router = express.Router(); // 라우터 생성

// GET 요청: "/users" 경로로 들어왔을 때 처리
router.get("/", (req, res, next) => {
  res.send("respond with a resource"); // 텍스트 응답을 반환
});

module.exports = router; // 라우터를 외부에서 사용할 수 있도록 모듈로 내보냄
