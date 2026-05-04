package com.easyplan.auth.application.required;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.auth.domain.RefreshTokenHash;
import com.easyplan.auth.domain.TokenClaims;
import com.easyplan.auth.domain.TokenPair;
import com.easyplan.member.domain.MemberRole;

public interface TokenProvider {
	String createAccessToken(PublicId publicId, MemberRole role, long accessTokenTime);
	String createRefreshToken();
	RefreshTokenHash hashToken(String refreshToken);
	TokenPair createTokenPair(PublicId publicId, MemberRole role, long accessTokenTime);
	TokenClaims extractTokenClaims(String accessToken);
	boolean validateRefreshToken(String refreshToken, RefreshTokenHash tokenHash);
}
