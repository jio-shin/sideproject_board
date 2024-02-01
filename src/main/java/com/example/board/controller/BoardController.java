package com.example.board.controller;

import com.example.board.dto.Board;
import com.example.board.dto.LoginInfo;
import com.example.board.service.BoardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

//http 요청을 받아서 응답해주는 컴포넌트 -> 컴포넌트는 스프링부트가 자동으로 bean 생성함
@Controller
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    //게시물 목록을 보여줌
    @GetMapping("/")
    public String list(@RequestParam(name="page",defaultValue = "1") int page, HttpSession session, Model model) {//model,session 스프링이 자동으로 넣어줌
        //게시물 목록읽어오기. 페이징 처리
        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
        model.addAttribute("loginInfo", loginInfo); //list에게 넘김

        int totalCount = boardService.getTotalCount();
        //페이지에 따른 게시글 뿌리기
        List<Board> list = boardService.getBoards(page); //1페이지면 10개 게시글,2페이지면 1개
        int pageCount= totalCount /10;
        if (totalCount%10>0){//나머지가 있으면 페이지 추가
            pageCount++;
        }
        int currentPage = page;
        model.addAttribute("list", list);
        model.addAttribute("pageCount", pageCount);
        model.addAttribute("currentPage", currentPage);

        return "list";
    }

    //board?id=1//id=1 : 파라미터
    @GetMapping("/board")
    public String board(@RequestParam("boardId") int boardId,Model model) {
        System.out.println("boardId = " + boardId);

        //id에 해당하는 게시물을 읽어옴
        //id에 해당하는 게시물의 조회수도 1증가
        Board board = boardService.getBoard(boardId);
        model.addAttribute("board", board);
        return "board";
    }

    @GetMapping("/writeform")
    public String writeForm(HttpSession session, Model model) {
        //로그인한 사용자만 글을 쓸수있다.
        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");

        //세션에서 로그인한 정보를 읽어들여서 로그인하지 않았다면 리스트보기로 자동이동
        if (loginInfo == null) {
            return "redirect:/loginform";
        }
        model.addAttribute("loginInfo", loginInfo);
        return "writeform";
    }

    @PostMapping("/write")
    public String write(@RequestParam("title") String title, @RequestParam("content") String content, HttpSession session) {
        //로그인한 사용자만 글을 쓸수있다.
        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");

        //세션에서 로그인한 정보를 읽어들여서 로그인하지 않았다면 리스트보기로 자동이동
        if (loginInfo == null) {
            return "redirect:/loginform";
        }
            System.out.println("title = " + title);
            System.out.println("content = " + content);

            // 로그인한 회원 정보 & 제목 내용을 저장
            boardService.addBoard(loginInfo.getUserId(), title, content);
            //글을 쓴 후에 리스트보기로 이동
            return "redirect:/";
        }
    //삭제한다. 관리자는 모든 글 삭제 가능
    @GetMapping("/delete")
    public String delete(@RequestParam("boardId")int boardId,HttpSession session){
        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginform";
        }
        //loginInfo.getUserId()에서 가져온 아이디가 쓴 글만 삭제하기
        List<String> roles = loginInfo.getRoles();
        if(roles.contains("ROLE_ADMIN")) {//관리자라면
            boardService.deleteBoard(boardId);
        }else {//관리자가 아니라면 글쓴 사람만 삭제가능
            boardService.deleteBoard(loginInfo.getUserId(), boardId);
        }
        return "redirect:/";

    }

    //수정한다.
    @GetMapping("/updateform")
    public String updateform(@RequestParam("boardId")int boardId,Model model,HttpSession session) {
        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginform";
        }

        //boardId에 해당하는 정보를 읽어와서 updateform.html에 전달
        Board board = boardService.getBoard(boardId, false);
        model.addAttribute("board",board);
        model.addAttribute("loginInfo", loginInfo);
        return "updateform";
    }
    @PostMapping("/update")
    //updateform에서 받은 hidden정보인 boardId에 해당하는 글의 제목과 내용을 수정.

    public String update(@RequestParam("boardId")int boardId,@RequestParam("title") String title,
                         @RequestParam("content") String content,HttpSession session){

        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginform";
        }
        //글쓴이만 수정 가능.
        Board board = boardService.getBoard(boardId, false);
        if (board.getUserId() != loginInfo.getUserId()) {
            return "redirect:/board?boardId="+boardId;  //글 보기로 이동
        }
        boardService.updateBoard(boardId, title, content);
        System.out.println("글수정완료");
        return"redirect:/board?boardId="+boardId;//수정된 글 보기로 리다이렉트
    }

}