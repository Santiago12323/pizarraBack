package com.escuelaing.arsw.pizarraBack;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

@SpringBootApplication
public class PizarraBackApplication {


	@Autowired
	private StringRedisTemplate redisTemplate;  // Inyecta redisTemplate

	public static void main(String[] args) {
		SpringApplication.run(PizarraBackApplication.class, args);
	}

	@PostConstruct
	public void example() {
		// Guarda un valor en Redis
		redisTemplate.opsForValue().set("testkey", "testvalue", Duration.ofMinutes(1));
		// Lee el valor desde Redis
		String value = redisTemplate.opsForValue().get("testkey");
		System.out.println("Redis test value: " + value);
	}

}
