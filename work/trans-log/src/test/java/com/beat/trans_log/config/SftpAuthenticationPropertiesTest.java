package com.beat.trans_log.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class SftpAuthenticationPropertiesTest {

	@Autowired
	SftpAuthenticationProperties sftpAuthenticationProperties;
	
	@Test
	final void testSftpAuthenticationProperties() {
//		fail("Not yet implemented"); // TODO
		log.info("sftpAuthenticationProperties.getHoot() : {}", sftpAuthenticationProperties.getHost());
		log.info("sftpAuthenticationProperties.getPort() : {}", sftpAuthenticationProperties.getPort());
		log.info("sftpAuthenticationProperties.getPrivateKey() : {}", sftpAuthenticationProperties.getPrivateKey());
		log.info("sftpAuthenticationProperties.getPrivateKeyPassPhrase() : {}", sftpAuthenticationProperties.getPrivateKeyPassPhrase());
		log.info("sftpAuthenticationProperties.getUser() : {}", sftpAuthenticationProperties.getUser());
		
	}

}
