package com.beat.trans_log.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Component
@ConfigurationProperties( prefix = "sftp" )
public class SftpAuthenticationProperties {
	private String host;
    private String port;
    private String user;
    private String password;
    private String privateKey;
    private String privateKeyPassPhrase;
    private String remoteDir;
}
