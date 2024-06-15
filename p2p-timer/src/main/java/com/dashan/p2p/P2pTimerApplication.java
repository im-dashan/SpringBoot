package com.dashan.p2p;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDubboConfiguration
@EnableScheduling  // Spring Task定时任务
public class P2pTimerApplication {

	public static void main(String[] args) {
		SpringApplication.run(P2pTimerApplication.class, args);
	}

}
