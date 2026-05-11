package com.easyplan._shared.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CookieProp {
	ACCESS("AT", true),
	REFRESH("RT", true),
	REMEMBER("RMT", true),
	ZONE_ID("ZONE", true),
	
	;
	private final String name;
	
	private final boolean httpOnly;
}
