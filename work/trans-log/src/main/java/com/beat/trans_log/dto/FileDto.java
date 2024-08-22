package com.beat.trans_log.dto;

import java.time.LocalDateTime;

import com.beat.trans_log.db.constants.Status;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class FileDto {
	
	private String systemCode;
	
	private String filename;
	
	private String path;
	
	private String contents;
	
	private Long filesize;
	
	private Status status;
	
	private LocalDateTime date;

	@Builder
	public FileDto(
			String systemCode, String filename, String path, String contents, Long filesize, 
			Status status, LocalDateTime date) {
		
		this.systemCode = systemCode;
		this.filename = filename;
		this.path = path;
		this.contents = contents;
		this.filesize = filesize;
		this.status = status;
		this.date = date;
	}
}
