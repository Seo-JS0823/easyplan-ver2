package com.easyplan.auth.application.provided;

import java.time.Instant;

public record LoginResult(
		String publicId,
		String accessToken,
		Instant accessExpiresAt,
		String refreshToken,
		Instant refreshExpiresAt) {

}
