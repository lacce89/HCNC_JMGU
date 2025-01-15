const express = require("express"); // Express 모듈 가져오기
const path = require("path"); // 경로 모듈
const cookieParser = require("cookie-parser"); // 쿠키 파서
const logger = require("morgan"); // 로그 모듈

// 라우터 불러오기
const indexRouter = require("./routes/index");
const usersRouter = require("./routes/users");
const chatRouter = require("./routes/chat");

const app = express(); // Express 애플리케이션 생성

// 뷰 엔진 설정
app.set("views", path.join(__dirname, "views")); // 뷰 파일 경로 설정
app.set("view engine", "pug"); // Pug를 뷰 엔진으로 설정

// 미들웨어 설정
app.use(logger("dev")); // 로그 출력
app.use(express.json()); // JSON 요청 파싱
app.use(express.urlencoded({ extended: false })); // URL 인코딩된 데이터 파싱
app.use(cookieParser()); // 쿠키 파싱
app.use(express.static(path.join(__dirname, "public"))); // 정적 파일 경로 설정

// 라우터 사용
app.use("/", indexRouter); // 기본 라우터
app.use("/users", usersRouter); // 사용자 라우터
app.use("/chat", chatRouter); // 채팅 라우터

// 오류 처리
app.use((req, res, next) => {
  const err = new Error("Not Found");
  err.status = 404;
  next(err);
});

// 개발 및 프로덕션 환경별 에러 처리
app.use((err, req, res, next) => {
  res.locals.message = err.message;
  res.locals.error = req.app.get("env") === "development" ? err : {};

  res.status(err.status || 500);
  res.render("error");
});

module.exports = app; // 앱 모듈 내보내기
