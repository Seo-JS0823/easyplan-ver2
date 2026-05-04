package com.easyplan.auth.domain.policy;

import java.time.Instant;

public interface AuthSessionPolicy {
	Instant refreshExpiresAt();
	Instant accessExpiresAt();
	long getAccessTokenTime();
	long getRefreshTokenTime();
}
