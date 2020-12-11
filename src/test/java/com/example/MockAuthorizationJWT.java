package com.example;

import com.example.utils.JWTUtils;
import com.example.utils.PlatformConstants;

import java.util.LinkedHashMap;

import static com.example.utils.JWTUtils.AUTHENTICATION_SCHEME;

public class MockAuthorizationJWT {

	public static String mockAuthorizationJWT() {
		return mockAuthorizationJWT("myLogin", "myAccount", 1000L * 1000);
	}

	public static String mockAuthorizationJWT(String login, String compte) {
		return mockAuthorizationJWT(login, compte, 1000000000L * 1000000000);
	}

	private static String mockAuthorizationJWT(String login, String compte, long expirationMillis) {

		if (PlatformConstants.INSTANCE.getJwtConfig() == null) {
			throw new RuntimeException("Il faut initialiser les PlatformConstants avec les app.properties");
		}

		LinkedHashMap<String, String> mapClaims = new LinkedHashMap<>();
		mapClaims.put("login", login);
		mapClaims.put("compte", compte);

		return AUTHENTICATION_SCHEME + " " + new JWTUtils(PlatformConstants.INSTANCE.getJwtConfig())
			.createJWT(expirationMillis, PlatformConstants.ISSUER, PlatformConstants.AUDIENCE, mapClaims);

	}
}
