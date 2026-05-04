package com.easyplan.member.domain.request;

public class MemberAccountRequest {
	public record MemberDeactivate(String currentPassword) {}
	
	public record MemberRecover(String currentPassword) {}
}
