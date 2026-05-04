package com.easyplan.member.application;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.member.application.provided.MemberSummary;
import com.easyplan.member.domain.Member;
import com.easyplan.member.domain.MemberRole;
import com.easyplan.member.domain.MemberStatus;

import lombok.Getter;

@Getter
public class MemberSummaryImpl implements MemberSummary {

	private final Long id;
	
	private final PublicId publicId;
	
	private final MemberStatus status;
	
	private final MemberRole role;
	
	public MemberSummaryImpl(Member member) {
		this.id = member.getId();
		this.publicId = member.getMemberPublicId();
		this.status = member.getStatus();
		this.role = member.getRole();
	}
	
}
