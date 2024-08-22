package com.beat.trans_log.db.entities;

import java.time.LocalDateTime;

import com.beat.trans_log.db.constants.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@Table(name = "LOG_FILE_STATUS")
@NoArgsConstructor
public class FileEntity {
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;
	
	@Column(name = "SYSTEMCODE", nullable = false)
	private String systemCode;
	
	@Column(name = "NAME", nullable = false)
	private String filename;
	
	@Column(name = "PATH", nullable = false)
	private String path;
	
	@Column(name = "CONTENTS")
	private String contens;
	
	@Column(name = "SIZE")
	private Long filesize;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "Status")
	private Status status;
	
	@Column(name = "DATE", nullable = false)
	private LocalDateTime date;

	@Builder
	public FileEntity(Long id, String systemCode, String filename, String path, String contens, Long filesize,
			Status status, LocalDateTime date) {
		this.id = id;
		this.systemCode = systemCode;
		this.filename = filename;
		this.path = path;
		this.contens = contens;
		this.filesize = filesize;
		this.status = status;
		this.date = date;
	}
	
	
}
