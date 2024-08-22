package com.beat.trans_log.runner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.beat.trans_log.batch.process.FileReader;
import com.beat.trans_log.batch.process.FileTransferService;
import com.beat.trans_log.config.AppProperties;
import com.beat.trans_log.config.SftpAuthenticationProperties;
import com.beat.trans_log.dto.FileDto;
import com.beat.trans_log.util.DateFormater;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TranLogAppRunner implements ApplicationRunner {

	@Autowired
	private AppProperties appProperties;

	@Autowired
	private SftpAuthenticationProperties authenticationProperties;

	@Autowired
	private FileTransferService fileTransferService;
	
	@Autowired
	private FileReader fileReader;

	@Override
	public void run(ApplicationArguments args) throws Exception {
//		log.info( "Argument list : {}",  args.getOptionNames() );
		log.info("non-option Argument : {}", args.getNonOptionArgs());

		String localDir = appProperties.getDefaultLogDirectory();
		String remoteDir = authenticationProperties.getRemoteDir();
		String user = authenticationProperties.getUser();
		String host = authenticationProperties.getHost();
		String pwd = authenticationProperties.getPassword();
		String port = authenticationProperties.getPort();

		String dateFormat;
		String sysCode = "";

		Map<String, String> params = new HashMap<>();

		if (args.containsOption("directory")) {
			localDir = args.getOptionValues("directory").get(0);
			log.info("Argument directory : {}", localDir);
		}

		if (args.containsOption("date")) {
			dateFormat = args.getOptionValues("date").get(0);
			log.info("Argument date : {}", dateFormat);
		} else {
			dateFormat = DateFormater.getYesterday();
		}

		if (args.containsOption("sysCode")) {
			sysCode = args.getOptionValues("sysCode").get(0);
			log.info("Argument sysCode : {}", sysCode);
		}

//		List<Path> fileList = DirectoryListing.listFilesInDirectory(directoryName);
//		
//		for( Path filePath : fileList ) {
//			String filename = filePath.toString();
//			if( filename.contains(dateFormat) ) {
//				// sftp 전송단 연결
//				log.info( "file path : {}", filePath.toString() );				
//				log.info( "file name : {}", filePath.getFileName() );				
//				log.info( "file name : {}", filePath.getParent().toString() );
//				
//				String hashContents = DirectoryListing.calculateFileHash( filePath );
//				
//				LocalDateTime now = LocalDateTime.now();
//				
//				FileDto fileDto =
//						FileDto.builder().systemCode( sysCode )
//								 .filename( filePath.getFileName().toString() )
//								 .path( filePath.getParent().toString() )
//								 .contents( hashContents )
//								 .filesize( Files.size( filePath ) )
//								 .status( Status.SUCCESS )
//								 .date( now )
//								 .build();
//								
//				
//				uploadComponent.doScpIdPassword(filePath.toString(), filePath.getFileName().toString());
//				
//			}
//		}

		params.put("remoteDir", remoteDir);
		params.put("sysCode", sysCode);
		params.put("user", user);
		params.put("host", host);
		params.put("pwd", pwd);
		params.put("port", port);

		List<FileDto> fileDtoList = fileReader.readFilesFromDirectory(localDir, dateFormat);

		fileTransferService.transferFiles(fileDtoList, params);

	}

}
