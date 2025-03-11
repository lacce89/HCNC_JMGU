const express = require("express");
const app = express();
const port = 3000;

const cors = require("cors");
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

let tasks = [];

// 📌 할 일 목록 불러오기
app.get("/tasks", (req, res) => {
    console.log("[GET] 할 일 목록 요청됨");
    res.json(tasks);
});

// 📌 할 일 추가
app.post("/tasks", (req, res) => {
    const newTask = { id: Date.now(), text: req.body.text, completed: false };
    tasks.push(newTask);
    console.log("[POST] 새로운 할 일 추가됨:", newTask);
    res.json(newTask);
});

// 📌 할 일 삭제
app.delete("/tasks/:id", (req, res) => {
    const taskId = parseInt(req.params.id);
    tasks = tasks.filter(task => task.id !== taskId);
    console.log(`[DELETE] 할 일 삭제됨: ID ${taskId}`);
    res.json({ message: "할 일이 삭제되었습니다!" });
});

// 📌 할 일 완료 처리
app.put("/tasks/:id/complete", (req, res) => {
    const taskId = parseInt(req.params.id);
    tasks = tasks.map(task => {
        if (task.id === taskId) {
            task.completed = !task.completed;
        }
        return task;
    });

    console.log(`[PUT] 할 일 완료 상태 변경: ID ${taskId}`);
    res.json({ message: "완료 상태가 변경되었습니다!", updatedTasks: tasks });
});

app.listen(port, () => {
    console.log(`서버가 ${port}번 포트에서 실행 중입니다.`);
});
