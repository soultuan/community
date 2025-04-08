package com.tuanzisama.community;

import com.tuanzisama.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.thymeleaf.spring6.SpringTemplateEngine;

@SpringBootTest
class CommunityApplicationTests {

    @Autowired
    private MailClient mailClient;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	void testSendMail() {
		mailClient.sendMail("tuanzzz0219@gmail.com","test my mail","hello man!");
	}

	@Test
	void testStrings(){
		stringRedisTemplate.opsForValue().set("username","admin");
		stringRedisTemplate.opsForValue().set("password","123456");
	}

}
