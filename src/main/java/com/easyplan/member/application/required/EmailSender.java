package com.easyplan.member.application.required;

public interface EmailSender {
	void sendJoinVerification(String toEmail);
	
	String verifyToken(String token);
}
