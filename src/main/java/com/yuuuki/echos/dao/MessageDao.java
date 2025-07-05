package com.yuuuki.echos.dao;

import com.yuuuki.echos.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@Repository
public class MessageDao {

    @Autowired
    private JdbcTemplate temp;

    public List<Message> findAll() {
        String sql = "SELECT * FROM messages ORDER BY create_time DESC";
//        System.out.println("query");
        return temp.query(sql, new MessageRowMapper());
    }

    public void save(Message message) {
        String sql = "INSERT INTO messages (content) VALUES (?)";
        temp.update(sql, message.getContent());
    }

    public void increaseLikes(int id) {
        String sql = "UPDATE messages SET likes = likes + 1 WHERE id = ?";
        temp.update(sql, id);
    }


    private static class MessageRowMapper implements RowMapper<Message> {
        @Override
        public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
            Message ms = new Message();
            ms.setId(rs.getInt("id"));
            ms.setContent(rs.getString("content"));
            ms.setLikes(rs.getInt("likes"));
            ms.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
            return ms;
        }
    }
}
