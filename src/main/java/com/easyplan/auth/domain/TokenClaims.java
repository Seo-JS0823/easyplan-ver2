package com.easyplan.auth.domain;

import java.time.Instant;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.member.domain.MemberRole;

public record TokenClaims(PublicId publicId, MemberRole role, Instant expiresAt) {

}
