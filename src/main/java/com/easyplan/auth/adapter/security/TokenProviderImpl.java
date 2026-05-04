package com.easyplan.auth.adapter.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.easyplan._shared.domain.PublicId;
import com.easyplan._shared.time.UTC;
import com.easyplan.auth.application.required.TokenProvider;
import com.easyplan.auth.domain.RefreshTokenHash;
import com.easyplan.auth.domain.TokenClaims;
import com.easyplan.auth.domain.TokenPair;
import com.easyplan.member.domain.MemberRole;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class TokenProviderImpl implements TokenProvider {
	
	private static final String REFRESH_TOKEN_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	
	private static final int REFRESH_TOKEN_LENGTH = 64;
	
	private final SecretKey key;
	
	private final SecureRandom secureRandom = new SecureRandom();
	
	public TokenProviderImpl(@Value("${jwt.secret}") String secretKey) {
		this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
	}
	
	@Override
	public String createRefreshToken() {
		return createSecureRandomToken();
	}

	private String createSecureRandomToken() {
		StringBuilder token = new StringBuilder(REFRESH_TOKEN_LENGTH);
		for(int i = 0; i < REFRESH_TOKEN_LENGTH; i++) {
			int index = secureRandom.nextInt(REFRESH_TOKEN_ALPHABET.length());
			token.append(REFRESH_TOKEN_ALPHABET.charAt(index));
		}
		return token.toString();
	}

	@Override
	public String createAccessToken(PublicId publicId, MemberRole role, long accessTokenTime) {
		Instant now = UTC.nowSecond();
		Date iss = Date.from(now);
		Date exp = new Date(iss.getTime() + accessTokenTime);
		
		Claims claims = Jwts.claims()
				.subject(publicId.publicId())
				.add("role", role.name())
				.issuedAt(iss)
				.expiration(exp)
				.build();
		
		String accessToken = Jwts.builder()
				.claims(claims)
				.signWith(key)
				.compact();
		
		return accessToken;
	}

	@Override
	public RefreshTokenHash hashToken(String refreshToken) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(refreshToken.getBytes(StandardCharsets.UTF_8));
			String hash = Base64.getEncoder().encodeToString(hashBytes);
			return new RefreshTokenHash(hash);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(getClass().getSimpleName() + ": NoSuchAlgorithm");
		}
	}

	@Override
	public TokenPair createTokenPair(PublicId publicId, MemberRole role, long accessTokenTime) {
		String access = createAccessToken(publicId, role, accessTokenTime);
		String refresh = createRefreshToken();
		return new TokenPair(access, refresh);
	}

	@Override
	public TokenClaims extractTokenClaims(String accessToken) {
		Claims claims = extractClaims(accessToken);
		
		PublicId publicId = new PublicId(claims.getSubject());
		MemberRole role = MemberRole.valueOf(claims.get("role", String.class));
		Instant expiresAt = claims.getExpiration().toInstant();
		
		return new TokenClaims(publicId, role, expiresAt);
	}
	
	private Claims extractClaims(String accessToken) {
		try {
			Claims claims = Jwts.parser()
					.verifyWith(key)
					.build()
					.parseSignedClaims(accessToken)
					.getPayload();
			
			return claims;
		} catch (ExpiredJwtException e) {
			return null;
		} catch (SignatureException | UnsupportedJwtException e) {
			return null;
		} catch (MalformedJwtException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public boolean validateRefreshToken(String refreshToken, RefreshTokenHash tokenHash) {
		RefreshTokenHash rawTokenHash = hashToken(refreshToken);
		return constantTimeEquals(rawTokenHash.tokenHash(), tokenHash.tokenHash());
	}
	
	private boolean constantTimeEquals(String a, String b) {
		if(a.length() != b.length()) {
			return false;
		}
		
		int result = 0;
		for(int i = 0; i < a.length(); i++) {
			result |= a.charAt(i) ^ b.charAt(i);
		}
		return result == 0;
	}

}
