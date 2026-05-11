package com.easyplan._global.webapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easyplan._shared.response.GlobalResponse;

@RestController
public class CsrfController {
	@GetMapping("/api/csrf")
	public GlobalResponse<Void> csrf() {
		return GlobalResponse.ok("");
	}
}
