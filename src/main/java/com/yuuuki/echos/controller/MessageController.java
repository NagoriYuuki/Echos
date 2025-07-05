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
    private MessageDao msd;

    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    @GetMapping("/api/messages")
    @ResponseBody
    public List<Message> getMessages() {
        log.info("check1 : GET /api/messages");
        return msd.findAll();
    }

    @PostMapping("/api/messages")
    public String postMessage(@RequestParam("content") String content) {
        log.info("API: '{}'", content);
        if (content == null || content.trim().isEmpty()) {
            log.warn("no message");
            return "redirect:/";
        }
        try {
            Message newMessage = new Message();
            newMessage.setContent(content);
            log.info("Attempting to save the new message to the database...");
            msd.save(newMessage);
            log.info("Message saved successfully!");
        } catch (Exception e) {
            log.error("RE:", e);
        }
        return "redirect:/";
    }

    @PostMapping("/api/messages/{id}/like")
    public ResponseEntity<Void> likeMessage(@PathVariable("id") int id) {
        log.info("API request received: POST /api/messages/{}/like", id);
        try {
            msd.increaseLikes(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("RE {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
