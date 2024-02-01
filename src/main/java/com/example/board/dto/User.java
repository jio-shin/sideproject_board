package com.example.board.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor //기본생성자 자동 생성
@ToString //자동으로 문자열로 변환
public class User {
    private int userId;
    private String email;
    private String name;
    private String password;
    private LocalDateTime regdate;
}
