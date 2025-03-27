package com.tuanzisama.community;

import com.tuanzisama.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CommunityApplicationTests {

    @Autowired
    private MailClient mailClient;

	@Test
	void contextLoads() {
	}

	@Test
	void testSendMail() {
		mailClient.sendMail("tuanzzz0219@gmail.com","test my mail","hello man!");
	}

}
