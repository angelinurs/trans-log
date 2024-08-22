package com.beat.trans_log.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateFormater {

	  private DateFormater() {
	    throw new IllegalStateException("Utility class");
	  }

	
	public static String getYesterday() {
		
        LocalDate yesterday = LocalDate.now().minusDays(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = yesterday.format(formatter);

        log.info("Yesterday's date in YYYYMMDD format: {}", formattedDate); 
		
		return formattedDate;
	}
}
