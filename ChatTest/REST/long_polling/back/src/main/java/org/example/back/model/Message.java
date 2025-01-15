package org.example.back.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Data
public class Message {

    //sendId recieverId message
    private String sendId;
    private String recieverId;
    private String message;



}
