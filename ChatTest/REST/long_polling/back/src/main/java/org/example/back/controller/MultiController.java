package org.example.back.controller;

import org.example.back.model.Message;
import org.example.back.model.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/multi")  // "/multi" 경로로 들어오는 요청을 처리하는 컨트롤러
@CrossOrigin(origins = "http://localhost:3000")  // CORS 설정: 로컬 서버에서 오는 요청을 허용
public class MultiController {

    // 유저 목록을 저장할 리스트 변수 (유저 ID를 저장)
    private List<String> userList = new ArrayList<>();

    // RestTemplate 객체를 재사용하기 위해 클래스 레벨에서 선언
    private RestTemplate restTemplate = new RestTemplate();

    // HTTP 요청 헤더를 재사용하기 위해 클래스 레벨에서 선언
    private HttpHeaders headers = new HttpHeaders();

    // 생성자에서 기본 헤더 설정
    public MultiController() {
        headers.set("Content-Type", "application/json");  // JSON 형식으로 요청을 보낼 때 필요한 헤더 설정
    }

    // 유저를 추가하는 API
    @PostMapping("/addUser")
    public String addUser(@RequestBody User user) {  // User 객체로 받기
        System.out.println("유저 ID: " + user.getUserId());
        System.out.println("유저 이름: " + user.getUserName());
        System.out.println("유저 이메일: " + user.getEmail());
        if (!userList.contains(user.getUserId())) {  // 만약 유저 목록에 해당 유저가 없다면
            userList.add(user.getUserId());  // 유저 추가
            return "신규유저";  // 신규 유저라는 메시지 반환
        } else {
            return "기존 유저";  // 이미 유저가 목록에 존재하면 "기존 유저" 반환
        }
    }

    // 메시지를 보내는 API
    @PostMapping("/sendMessage")
    public String sendMessage(@RequestBody Message message) {  // 클라이언트가 보낸 메시지를 받음
        // 메시지와 관련된 정보를 콘솔에 출력 (디버깅용)
        System.out.println("보내는 사람 : " + message.getSendId());
        System.out.println("메세지 내용 : " + message.getMessage());
        System.out.println("받는사람 : 아마 없어도 될듯  " + message.getRecieverId());

        int chk = 0;  // 성공적으로 메시지가 전달된 유저의 수를 세는 변수

        // 유저 목록을 순회하며 각 유저에게 메시지를 전송
        for (String user : userList) {
            // 각 유저의 엔드포인트 URL 생성
            String url = "http://localhost:3000/multi/" + user;

            // 메시지 객체를 생성 (보내는 사람, 받는 사람, 메시지 내용)
            Message messageToSend = new Message(message.getSendId(), message.getRecieverId(), message.getMessage());

            // 메시지 객체와 헤더를 HttpEntity로 묶음
            HttpEntity<Message> entity = new HttpEntity<>(messageToSend, headers);

            try {
                // REST 요청을 보내고 응답 받기
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
                System.out.println("익스프레스에 보내고 다시 받은 값: " + response.getBody());

                // 응답이 null이 아니면 전송 성공으로 간주하고 chk 변수 증가
                if (response.getBody() != null) chk++;
            } catch (Exception e) {
                // 요청 실패 시 에러 메시지를 출력
                System.out.println("에러 발생: " + e.getMessage());
            }
        }

        // 메시지가 모든 유저에게 성공적으로 전달되었을 때
        if (chk == userList.size()) {
            System.out.println("전체 전송완료");
            return "전체 전송완료";  // 전송 완료 메시지 반환
        } else if (chk == 0) {
            return "전송실패";  // 전송 실패 메시지 반환
        } else {
            // 일부 유저에게만 메시지가 전송된 경우
            int result = userList.size() - chk;  // 실패한 유저의 수 계산
            return result + "명 실패";  // 실패한 유저 수를 반환
        }
    }
}
