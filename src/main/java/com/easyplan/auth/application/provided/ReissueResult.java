package com.easyplan.auth.application.provided;

import java.time.Instant;

public record ReissueResult(
		String accessToken,
		Instant accessExpiresAt,
		String refreshToken,
		Instant refreshExpiresAt) {

}
