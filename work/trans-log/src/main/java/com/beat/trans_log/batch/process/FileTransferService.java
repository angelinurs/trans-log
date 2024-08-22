package com.beat.trans_log.batch.process;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.beat.trans_log.db.constants.Status;
import com.beat.trans_log.db.entities.FileEntity;
import com.beat.trans_log.db.service.FileEntityService;
import com.beat.trans_log.dto.FileDto;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Logger;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FileTransferService {
	
	private final FileEntityService entityService;

    @Autowired
    public FileTransferService(FileEntityService entityService) {
        this.entityService = entityService;
    }

    private final ExecutorService executorService = Executors.newFixedThreadPool(5); // 5개씩 처리

    public void transferFiles(List<FileDto> fileDtoList, Map<String, String> jobParameters ) {
		
        for (int i = 0; i < fileDtoList.size(); i += 5) {
            List<FileDto> batch = fileDtoList.subList(i, Math.min(i + 5, fileDtoList.size()));
            executorService.submit(() -> processBatch(batch, jobParameters));
        }

        // ExecutorService 종료 대기
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void processBatch(List<FileDto> batch, Map<String, String> jobParameters ) {

        String remoteDir = jobParameters.get("remoteDir");
        String sysCode = jobParameters.get("sysCode");
        String user = jobParameters.get("user");
        String host = jobParameters.get("host");
        String pwd = jobParameters.get("pwd");
        String port = jobParameters.get("port");
        
        int portNo = Integer.parseInt( port );
		Path remoteDirPath = Paths.get( remoteDir );		
		
		Session session = null;
        
        try {
            JSch jsch = new JSch();
            
         // 임시 로그 상세
    		JSch.setLogger(new Logger() {
    		    @Override
    		    public boolean isEnabled(int level) {
    		        return true;
    		    }
    		    @Override
    		    public void log(int level, String message) {
    		        log.debug("JSch: {}", message);
    		    }
    		});
    		
            session = jsch.getSession( user, host, portNo );
            session.setPassword(pwd);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            for (FileDto fileDto : batch) {
    			// file upload
                Path remoteFileFullPath = remoteDirPath.resolve( fileDto.getFilename() );
                fileDto.setSystemCode( sysCode );
    			boolean success = 
    					uploadFile( session, 
    							fileDto.getPath(), 
    							remoteFileFullPath.toString().replace("\\", "/")
    							);
    			
    			if( success ) {
    				fileDto.setStatus(Status.SUCCESS); 
    			} else {
    				fileDto.setStatus(Status.FAILURE);
    			}
            }

            // DB 저장 로직 추가
            saveFileDtoList(batch);
            
        } catch (Exception e) {
        	log.error("{}", e.getLocalizedMessage() );
        } finally {
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }
	
	/**
	 * @author gipark
	 * @implNote Upload File
	 * @param session
	 * @param localFilePath
	 * @param remoteFilePath
	 * @throws JSchException
	 * @throws IOException
	 * @throws SftpException
	 */
	private boolean uploadFile( Session session, String localFilePath, String remoteFilePath ) {
		
		boolean success = true;
		
		Channel channel = null;
        ChannelSftp sftpChannel = null;
        
		try {
			channel = session.openChannel( "sftp" );
			channel.connect();
			
			sftpChannel = (ChannelSftp) channel;
			sftpChannel.put( localFilePath, remoteFilePath );
			
	    } catch (JSchException | SftpException e) {
			success = false;
	    	
		} finally {
	        if (sftpChannel != null && sftpChannel.isConnected()) {
	        	sftpChannel.disconnect();
	        }
	        if (channel != null && channel.isConnected()) {
	        	channel.disconnect();
	        }
	    }
		
		return success;
	}

    private void saveFileDtoList(List<FileDto> fileDtoList) {
    	
    	List<FileEntity> fileEntities = new ArrayList<>();
    	for( FileDto dto : fileDtoList ) {
    		fileEntities.add(convertToEntity(dto));
    	}
    	
    	entityService.saveFileEntities(fileEntities);
    }
    
    private FileEntity convertToEntity(FileDto fileDto) {
    	return FileEntity.builder()
    			          .systemCode( fileDto.getSystemCode() )
    			          .filename( fileDto.getFilename() )
    			          .path( fileDto.getPath() )
    			          .contens( fileDto.getContents() )
    			          .filesize( fileDto.getFilesize() )
    			          .status( fileDto.getStatus() )
    			          .date( fileDto.getDate() )
    			          .build();
    }

}
