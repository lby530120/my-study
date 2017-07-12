package com.didispace.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.didispace.bean.User;
import com.didispace.service.ComputeService;

@RestController
@RequestMapping(value="/user") 
public class HelloController {
	
	@Autowired
	ComputeService computeService;

	@RequestMapping(value="/{id}", method= RequestMethod.GET)
	public User getUser(@PathVariable Long id) { 
        // 处理"/users/{id}"的GET请求，用来获取url中id值的User信息 
        // url中的id可通过@PathVariable绑定到函数的参数中 
        return computeService.getUser(new User(id)); 
    } 
}
