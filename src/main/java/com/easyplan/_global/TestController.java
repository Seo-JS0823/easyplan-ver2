package com.easyplan._global;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {
	@GetMapping("/hello")
	public String hello() {
		System.out.println("Easyplan Frontend 에서 접속하였습니다.");
		return "Easyplan 서버와 연결되었습니다!";
	}
}
