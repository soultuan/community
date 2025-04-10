package com.tuanzisama.community;

import com.tuanzisama.community.pojo.User;
import com.tuanzisama.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.devtools.classpath.ClassPathFileSystemWatcher;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

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

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Test
	void testRabbitTemplate(){
		String exchangeName = "test.topic";
//		String message = "hello motherfucker";
		User user = new User();
		user.setUsername("tuanzisama");
		user.setPassword("123456");
		rabbitTemplate.convertAndSend(exchangeName,"test",user);
	}

	@Test
	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(name = "test.queue"),
			exchange = @Exchange(name = "test.topic",type = ExchangeTypes.TOPIC),
			key = {"test"}
	))
	void testRabbitTemplateReceive(User message) {
		System.out.println(message);
	}

//	@Test
//	@RabbitListener(bindings = @QueueBinding(
//			value = @Queue(name = "test.queue2"),
//			exchange = @Exchange(name = "test.topic",type = ExchangeTypes.TOPIC),
//			key = {"test"}
//	))
//	void testRabbitTemplateReceive2(User message) {
//		System.out.println(message);
//	}
}
