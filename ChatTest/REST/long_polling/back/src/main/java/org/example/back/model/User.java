package org.example.back.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Data
public class User {
    private String userId;  // 유저 ID
    private String userName;  // 유저 이름
    private String email;  // 유저 이메일

}
