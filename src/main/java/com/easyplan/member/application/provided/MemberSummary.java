package com.easyplan.member.application.provided;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.member.domain.MemberRole;
import com.easyplan.member.domain.MemberStatus;

public interface MemberSummary {
	Long getId();
	PublicId getPublicId();
	MemberStatus getStatus();
	MemberRole getRole();
}
