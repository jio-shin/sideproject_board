package com.example.board.dao;

import com.example.board.dto.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.example.board.dto.Board;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository

public class BoardDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;//각종 sql 실행하기 위함
    private final SimpleJdbcInsert insertBoard;//insert하기 위함
    //public int getTotalCount;

    //Datasource의 구현체가 HikariCP -> HikariCP가 빈을 주입한다.
    public BoardDao(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        insertBoard = new SimpleJdbcInsert(dataSource)
                .withTableName("board")
                .usingGeneratedKeyColumns("board_id");//자동으로 증가되는 id 설정
    }
    @Transactional
    public void addBoard(int userId, String title, String content) {
        Board board = new Board();
        board.setUserId(userId);
        board.setTitle(title);
        board.setContent(content);
        board.setRegdate(LocalDateTime.now());
        SqlParameterSource params = new BeanPropertySqlParameterSource(board); //자동으로 칼럼에 맞춰서 변환시켜줌
        insertBoard.execute(params);
    }
    @Transactional(readOnly = true)//조회만 할경우 readOnly=true 성능향상
    public int getTotalCount() {
        String sql = "select count(*) as total_count from board"; // 무조건 1건의 데이터가 나온다.
        Integer totalCount = jdbcTemplate.queryForObject(sql, Map.of(), Integer.class);
        return totalCount.intValue(); //정수값 리턴
    }
    @Transactional(readOnly = true)
    public List<Board> getBoards(int page) {
        //start는 0,10,20,30 으로 표현됨
        int start = (page-1)*10;
        String sql = "select b.user_id,b.board_id,b.title,b.regdate,b.view_cnt,u.name from board b,user u where b.user_id=u.user_id order by board_id desc limit :start,10";

        RowMapper<Board> rowMapper = BeanPropertyRowMapper.newInstance(Board.class);
        List<Board> list = jdbcTemplate.query(sql, Map.of("start", start), rowMapper);

        return list;
    }
    @Transactional(readOnly = true)
    //id에 해당하는 게시물 읽어옴
    public Board getBoard(int boardId) {
        //1건 또는 0건 나오는 쿼리
        String sql ="select b.user_id,b.board_id,b.title,b.regdate,b.view_cnt,u.name,b.content from board b,user u where b.user_id=u.user_id and b.board_id= :boardId";
        RowMapper<Board> rowMapper = BeanPropertyRowMapper.newInstance(Board.class); //Board에 자동으로 담을 수 있는 매퍼를 만듦
        Board board = jdbcTemplate.queryForObject(sql, Map.of("boardId", boardId), rowMapper);
        return board;
    }
    //조회수 1 증가
    public void updateViewCnt(int boardId) {
        String sql ="update board set view_cnt=view_cnt+1 where board_id= :boardId";
        jdbcTemplate.update(sql,Map.of("boardId",boardId));
    }

    @Transactional
    public void deleteBoard(int boardId) {
        String sql = "delete from board where board_id = :boardId";
        jdbcTemplate.update(sql, Map.of("boardId", boardId));
    }

    @Transactional
    public void updateBoard(int boardId, String content, String title) {
        String sql = "update board set title = :title, content = :content where board_id = :boardId";

        // jdbcTemplate.update(sql, Map.of("boardId", boardId, "title", title, "content", content));

        Board board = new Board();
        board.setBoardId(boardId);
        board.setTitle(title);
        board.setContent(content);
        SqlParameterSource params = new BeanPropertySqlParameterSource(board);
        jdbcTemplate.update(sql,params);
    }
}
