package com.didispace;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.didispace.bean.User;
import com.didispace.service.ComputeService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ApplicationTests {

	@Autowired
	ComputeService computeService;

	@Test
	public void testAdd() throws Exception {
		Assert.assertEquals("compute-service:add", new Integer(3), computeService.add(1, 2));
	}

	@Test
	public void testGetUser() throws Exception {
		User user = computeService.getUser(new User(2l));
		Assert.assertEquals("compute-service:getUser", "0201",	user.getUserKind());
	}

}
