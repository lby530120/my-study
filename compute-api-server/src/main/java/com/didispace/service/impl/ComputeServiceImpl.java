package com.didispace.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.didispace.bean.User;
import com.didispace.dao.UserMapper;
import com.didispace.service.ComputeService;

public class ComputeServiceImpl implements ComputeService {
	
	@Autowired
	private UserMapper userMapper;
	
    @Override
    public Integer add(int a, int b) {
        return a + b;
    }

	@Override
	public User getUser(User user) {
		return userMapper.findByUserId(user.getUserId());
	}

}
