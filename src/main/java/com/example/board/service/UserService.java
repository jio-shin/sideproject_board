package com.example.board.service;

import com.example.board.dao.UserDao;
import com.example.board.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor    //final필드 초기화하는 생성자를 자동 생성
public class UserService {
    private final UserDao userDao;

    //spring이 UserService를 빈으로 생성할 때 생성자를 이용해서 생성하는데
    //이때 UserDao의 빈이 등록되어있는지 보고 그 빈을 주입한다.(생성자 주입)
  /*  public UserService(UserDao userDao) {
        this.userDao = userDao;
    }*/

    //보통 서비스에서는 하나의 트렌젝션으로 처리
    @Transactional
    public User addUser(String name, String email, String password) {
        User user1 = userDao.getUser(email);
        if (user1 != null) {
            throw new RuntimeException("이미 가입된 이메일입니다.");
        }
        User user = userDao.addUser(email, name, password);
        userDao.mappingUserRole(user.getUserId());//권한부여
        return user;
        //트렌젝션 종료
    }

    @Transactional
    public User getUser(String email) {
        return userDao.getUser(email);
    }

    @Transactional(readOnly = true)
    public List<String> getRoles(int userId) {
        return userDao.getRoles(userId);
    }
}
