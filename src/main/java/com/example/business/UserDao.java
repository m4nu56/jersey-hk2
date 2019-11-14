package com.example.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDao {

	public UserDao() {
	}

	List<User> getList() {
		List<User> users = new ArrayList<>();
		users.add(new User("login1", "account"));
		users.add(new User("login2", "account"));
		return users;
	}
}
