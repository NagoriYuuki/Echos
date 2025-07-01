package com.yuuuki.echos.dao;

import com.yuuuki.echos.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * MessageDao 负责所有与 messages 表相关的数据库操作。
 * @Repository 注解告诉 Spring Boot，这是一个数据访问层的组件，请将它纳入管理。
 */
@Repository
public class MessageDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询所有的留言，并按时间倒序排列。
     * @return 包含所有留言对象的列表
     */
    public List<Message> findAll() {
        String sql = "SELECT * FROM messages ORDER BY create_time DESC";
        return jdbcTemplate.query(sql, new MessageRowMapper());
    }

    /**
     * 将一条新的留言保存到数据库中。
     * @param message 包含要保存内容的 Message 对象
     */
    public void save(Message message) {
        String sql = "INSERT INTO messages (content) VALUES (?)";
        jdbcTemplate.update(sql, message.getContent());
    }

    // --- 新增的方法 ---
    /**
     * 为指定ID的留言增加一次点赞。
     * @param id 要点赞的留言的ID
     */
    public void increaseLikes(int id) {
        // 定义 UPDATE 语句，将 likes 字段的值加 1
        String sql = "UPDATE messages SET likes = likes + 1 WHERE id = ?";
        // 执行更新操作，并传入要更新的留言ID
        jdbcTemplate.update(sql, id);
    }


    /**
     * 这是一个静态内部类，实现了 RowMapper 接口。
     * 它的唯一作用就是将数据库查询结果的一行 (ResultSet) 映射成一个 Message 对象。
     */
    private static class MessageRowMapper implements RowMapper<Message> {
        @Override
        public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
            Message message = new Message();
            message.setId(rs.getInt("id"));
            message.setContent(rs.getString("content"));
            message.setLikes(rs.getInt("likes"));
            message.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
            return message;
        }
    }
}
