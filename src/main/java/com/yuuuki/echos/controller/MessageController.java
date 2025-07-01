package com.yuuuki.echos.controller;

import com.yuuuki.echos.dao.MessageDao;
import com.yuuuki.echos.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MessageController {

    private static final Logger log = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageDao messageDao;

    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    @GetMapping("/api/messages")
    @ResponseBody
    public List<Message> getMessages() {
        log.info("API request received: GET /api/messages");
        return messageDao.findAll();
    }

    @PostMapping("/api/messages")
    public String postMessage(@RequestParam("content") String content) {
        log.info("API request received: POST /api/messages with content: '{}'", content);
        if (content == null || content.trim().isEmpty()) {
            log.warn("Attempted to post an empty message. Redirecting without saving.");
            return "redirect:/";
        }
        try {
            Message newMessage = new Message();
            newMessage.setContent(content);
            log.info("Attempting to save the new message to the database...");
            messageDao.save(newMessage);
            log.info("Message saved successfully!");
        } catch (Exception e) {
            log.error("!!! An error occurred while saving the message !!!", e);
        }
        return "redirect:/";
    }

    // --- 新增的API接口 ---
    /**
     * API 接口：为指定留言点赞。
     * @PathVariable("id") 会从 URL 路径中提取 id 值，例如 /api/messages/123/like -> id = 123
     * @return 返回一个空的成功响应，表示操作完成。
     */
    @PostMapping("/api/messages/{id}/like")
    public ResponseEntity<Void> likeMessage(@PathVariable("id") int id) {
        log.info("API request received: POST /api/messages/{}/like", id);
        try {
            messageDao.increaseLikes(id);
            // 返回一个 HTTP 200 OK 状态，但没有响应体。
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("!!! An error occurred while liking message {} !!!", id, e);
            // 如果发生错误，返回一个服务器内部错误状态。
            return ResponseEntity.internalServerError().build();
        }
    }
}
