package com.example.board.controller;


import com.example.board.dto.LoginInfo;
import com.example.board.dto.User;
import com.example.board.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor //final 변수 초기화
public class UserController {
    private final UserService userService;
    @GetMapping("/userRegForm")
    public String userRegForm() {
        return "userRegForm";
    }

    //userRegForm 에서 입력한 이름,이메일,암호가 저장되는것
    @PostMapping("/userReg")
    public String userReg(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password
    ) {
        System.out.println("name = " + name);
        System.out.println("email = " + email);
        System.out.println("password = " + password);

        //회원정보 저장
        userService.addUser(name, email, password);

        //redirect 는 브라우저에 자동으로 /welcome 으로 이동시키는 것
        return "redirect:/welcome";
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "/welcome";
    }

    @GetMapping("/loginform")
    public String loginform() {
        return "loginform";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam("email")String email,
            @RequestParam("password")String password,
            HttpSession httpSession //스프링이 자동으로 session을 처리하는 객체를 넣어줌
    ) {
        //email에 해당하는 회원 정보를 읽어온 후
        //아이디 암호가 맞다면 세션에 회원 정보를 저장
        System.out.println("email = " + email);
        System.out.println("password = " + password);

        try {
            User user = userService.getUser(email);
            //System.out.println(user);
            if (user.getPassword().equals(password)) {
                System.out.println("암호가 같습니다.");
                //세션에 회원 정보를 저장.
                // 각각의 브라우저마다 다른 세션이 부여되는 것
                LoginInfo loginInfo = new LoginInfo(user.getUserId(), user.getEmail(), user.getName());

               //권한정보를 읽어와서 loginInfo에 추가
               List<String> roles = userService.getRoles(user.getUserId());
               loginInfo.setRoles(roles);

                httpSession.setAttribute("loginInfo",loginInfo);
                //(key, value)
                System.out.println("세션에 로그인정보가 저장됨");
            }else {
                throw new RuntimeException("암호가 일치하지 않습니다.");
            }
        } catch (Exception ex) {
            //로그인 실패시 로그인화면으로
            System.out.println("로그인실패");
            return "redirect:/loginform?error=true";
        }

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        //세션에서 회원정보삭제
        session.removeAttribute("loginInfo");
        return "redirect:/";
    }
}
