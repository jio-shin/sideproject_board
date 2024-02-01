package com.example.board.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
@Getter
@Setter
@ToString
public class Board {
    private int boardId;
    private String title;
    private String content;
    private int userId;
    private LocalDateTime regdate;
    private int viewCnt;

    private String name;
    //u.name은 조인을 해서 추가해줘야함.
    //b.user_id,b.board_id,b.title,b.regdate,b.view_cnt,u.name
}
