package com.example.board.dao;

import com.example.board.dto.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class UserDao {
    //Spring JDBC를 이용한 코드 작성

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertUser;
    public UserDao(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        insertUser = new SimpleJdbcInsert(dataSource)
                .withTableName("user")
                .usingGeneratedKeyColumns("user_id");//자동으로 증가되는 id 설정
                //권한 부여를 위해서 key를 받은 것
    }

    @Transactional
    //UserService 에서 이미 트렌젝션이 시작됐기 때문에 그 트렌젝션에 포함된다.
    public User addUser(String email, String name, String password) {
        //insert into user(email,name,password,regdate)values(?,?,?,now());
        //select last_insert_id();
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRegdate(LocalDateTime.now());

        //dto(user)가 갖고있는 값을 파라미터로 바꿔서 넣어줌!
        SqlParameterSource params = new BeanPropertySqlParameterSource(user);

        //내부적으로 select last_insert_id()쿼리가 실행됨
        Number number = insertUser.executeAndReturnKey(params);
        int userId = number.intValue();
        user.setUserId(userId);
        return user;
    }
    @Transactional
    public void mappingUserRole(int userId){
        // Service에서 이미 트랜잭션이 시작했기 때문에, 그 트랜잭션에 포함된다.
        // insert into user_role( user_id, role_id ) values ( ?, 1);
        String sql = "insert into user_role( user_id, role_id ) values (:userId, 1)";
        SqlParameterSource params = new MapSqlParameterSource("userId", userId);
        jdbcTemplate.update(sql, params);
    }

@Transactional
    public User getUser(String email) {
    try {
        String sql = "select user_id,email,name,password,regdate from user where email = :email";

        //"email"부분에 :email값이 들어가고 email 부분에 String email이 들어감.
        SqlParameterSource params = new MapSqlParameterSource("email", email);

        //클래스 정보를 매핑해줌
        RowMapper<User> rowMapper = BeanPropertyRowMapper.newInstance(User.class);
        User user = jdbcTemplate.queryForObject(sql, params, rowMapper);
        return user;
    } catch (Exception e) {
        return null;
    }
}

@Transactional(readOnly = true)
    public List<String> getRoles(int userId) {
    String sql = "select r.name from user_role ur, role r where ur.role_id = r.role_id and ur.user_id = :userId";

    List<String> roles = jdbcTemplate.query(sql, Map.of("userId", userId), (rs, rowNum) -> {
        return rs.getString(1);
    });
    return roles;
}
}
