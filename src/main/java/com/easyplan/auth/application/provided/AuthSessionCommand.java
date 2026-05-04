package com.easyplan.auth.application.provided;

import com.easyplan.auth.domain.request.LoginRequest;

public interface AuthSessionCommand {
	LoginResult login(LoginRequest login);
	
	ReissueResult reissue(String refreshToken);
	
	void logout(String refreshToken);
}
