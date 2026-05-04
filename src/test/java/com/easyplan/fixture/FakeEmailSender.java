package com.easyplan.fixture;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.easyplan.member.application.required.EmailSender;

public class FakeEmailSender implements EmailSender {

    private final Map<String, String> cache = new ConcurrentHashMap<>();

    private String lastToken;
    private String lastLink;
    private String lastToEmail;

    @Override
    public void sendJoinVerification(String toEmail) {
        String emailToken = UUID.randomUUID().toString();

        cache.put("verify:" + emailToken, toEmail);

        this.lastToken = emailToken;
        this.lastLink = "/api/members/verify?token=" + emailToken;
        this.lastToEmail = toEmail;
    }

    @Override
    public String verifyToken(String token) {
        String email = cache.get("verify:" + token);

        if (email == null) {
            throw new IllegalArgumentException("만료되었거나 유효하지 않은 인증 토큰입니다.");
        }

        cache.remove("verify:" + token);
        return email;
    }

    public String getLastToken() {
        return lastToken;
    }

    public String getLastLink() {
        return lastLink;
    }

    public String getLastToEmail() {
        return lastToEmail;
    }
}