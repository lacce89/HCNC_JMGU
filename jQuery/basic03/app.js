$(document).ready(function () {
    const API_URL = "http://localhost:3000/tasks";

    // 서버에서 할 일 목록 불러오기
    function loadTasks() {
        $.ajax({
            url: API_URL,
            method: "GET",
            success: function (tasks) {
                $("#taskList").empty();          // 기존 목록 초기화
                $("#completedTaskList").empty(); // 완료된 목록 초기화

                tasks.forEach(function (task) {
                    let taskItem = $(`
                        <li class="list-group-item d-flex justify-content-between align-items-center" data-id="${task.id}">
                            <span class="task-text ${task.completed ? 'completed' : ''}">${task.text}</span>
                            <div>
                                <button class="btn btn-success btn-sm completeTask">${task.completed ? '취소' : '완료'}</button>
                                <button class="btn btn-danger btn-sm deleteTask">삭제</button>
                            </div>
                        </li>
                    `);

                    if (task.completed) {
                        $("#completedTaskList").append(taskItem); // 완료된 목록에 추가
                    } else {
                        $("#taskList").append(taskItem); // 일반 목록에 추가
                    }
                });
            }
        });
    }

    // 할 일 추가
    $("#addTaskBtn").click(function () {
        let taskText = $("#taskInput").val().trim();
        if (taskText === "") {
            alert("할 일을 입력하세요!");
            return;
        }

        $.ajax({
            url: API_URL,
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify({ text: taskText }),
            success: function () {
                $("#taskInput").val(""); // 입력창 초기화
                loadTasks(); // 목록 갱신
            }
        });
    });

    // 할 일 삭제
    $(document).on("click", ".deleteTask", function () {
        let taskId = $(this).closest("li").attr("data-id");

        $.ajax({
            url: `${API_URL}/${taskId}`,
            method: "DELETE",
            success: function () {
                loadTasks(); // 목록 갱신
            }
        });
    });

    // 할 일 완료 처리 (토글 기능 추가)
    $(document).on("click", ".completeTask", function () {
        let taskId = $(this).closest("li").attr("data-id");
        let taskItem = $(this).closest("li");
        let taskText = taskItem.find(".task-text");
        let completeButton = $(this);

        $.ajax({
            url: `${API_URL}/${taskId}/complete`,
            method: "PUT",
            success: function (response) {
                let updatedTask = response.updatedTasks.find(task => task.id == taskId);
                if (updatedTask) {
                    taskText.toggleClass("completed");
                    completeButton.text(updatedTask.completed ? "취소" : "완료");

                    // 완료된 항목 이동
                    if (updatedTask.completed) {
                        $("#completedTaskList").append(taskItem);
                    } else {
                        $("#taskList").append(taskItem);
                    }
                }
            }
        });
    });

    // "완료된 목록 보기" 버튼을 눌렀을 때 최신 상태 불러오기
    $("#completedTasksModal").on("show.bs.modal", function () {
        $.ajax({
            url: API_URL,
            method: "GET",
            success: function (tasks) {
                $("#completedTaskList").empty();
                tasks.forEach(function (task) {
                    if (task.completed) {
                        let taskItem = $(`
                            <li class="list-group-item d-flex justify-content-between align-items-center" data-id="${task.id}">
                                <span class="task-text completed">${task.text}</span>
                            </li>
                        `);
                        $("#completedTaskList").append(taskItem);
                    }
                });
            }
        });
    });

    // 페이지 로드 시 기존 할 일 불러오기
    loadTasks();
});
