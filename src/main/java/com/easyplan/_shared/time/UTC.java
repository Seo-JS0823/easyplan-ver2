package com.easyplan._shared.time;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class UTC {
	public static Instant now() {
		return Instant.now();
	}
	
	public static Instant nowSecond() {
		return Instant.now().truncatedTo(ChronoUnit.SECONDS);
	}
}
