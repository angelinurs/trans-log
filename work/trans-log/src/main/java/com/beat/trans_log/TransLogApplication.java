package com.beat.trans_log;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TransLogApplication {

	/**
	 * @implNote {@link TranLogAppRunner}
	 * @param args
	 */
	public static void main(String[] args) {
//		SpringApplication.run(TransLogApplication.class, args);
		
		SpringApplication app = new SpringApplication(TransLogApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
	}
	
//	public static void main(String[] args) {
//		// deprecate
//		new SpringApplicationBuilder(TransLogApplication.class)
//		  .web(WebApplicationType.NONE)
//		  .run(args);
//		
//	}
	
}
