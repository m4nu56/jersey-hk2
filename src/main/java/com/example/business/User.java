package com.example.business;

import java.security.Principal;

public class User implements Principal {
	private String login;
	private String account;

	public User() {
	}

	public User(String login, String account) {
		this.login = login;
		this.account = account;
	}

	public String getName() {
		return login;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}
}
