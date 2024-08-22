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
@ConfigurationProperties( prefix = "app" )
public class AppProperties {
	private String defaultLogDirectory;
	private String hashAlgorithm;
}
