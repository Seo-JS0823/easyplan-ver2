package com.easyplan.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {
	PENDING(0),
	MEMBER(2),
	BRONZE(3),
	SILVER(4),
	GOLD(7),
	ADMIN(10)
	;
	
	private final int maxLimit;
}
