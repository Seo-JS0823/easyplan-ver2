package com.easyplan.member.application.provided;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.member.domain.MemberRole;
import com.easyplan.member.domain.MemberStatus;
import com.easyplan.member.domain.exception.MemberException;
import com.easyplan.member.domain.exception.MemberExceptionCode;

public interface MemberSummary {
	Long getId();
	PublicId getPublicId();
	MemberStatus getStatus();
	MemberRole getRole();
	
	default void canUseService() {
		MemberStatus status = this.getStatus();
		MemberRole role = this.getRole();
		
		if(status != MemberStatus.ACTIVE) {
			throw new MemberException(MemberExceptionCode.MEMBER_CANNOT_USE_SERVICE);
		}
		
		if(role == MemberRole.PENDING) {
			throw new MemberException(MemberExceptionCode.MEMBER_CANNOT_USE_SERVICE_VERIFY_EMAIL);
		}
	}
}
