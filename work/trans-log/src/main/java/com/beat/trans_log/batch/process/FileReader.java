package com.beat.trans_log.batch.process;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.springframework.stereotype.Component;

import com.beat.trans_log.db.constants.Status;
import com.beat.trans_log.dto.FileDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FileReader {
    
    public List<FileDto> readFilesFromDirectory(String path, String date) throws IOException {
    	
    	Path directoryPath = Paths.get( path );

        // Check validation directory 
        if (Files.notExists(directoryPath) || !Files.isDirectory(directoryPath)) {
            log.info("'{}' does not exist", directoryPath);
            return Collections.emptyList();
        }

        List<FileDto> fileDtoList = new ArrayList<>();
        
        // get current and sub directory
        Files.walkFileTree(directoryPath, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
    			String filename = file.toString();
    			if( filename.contains(date) ) {
	                try {
	                    FileDto fileDto = createFileDto(file);
	                    fileDtoList.add(fileDto);
	                } catch (IOException | NoSuchAlgorithmException e) {
	                    log.error("Error processing file: {}", file, e);
	                }
    			}
                return FileVisitResult.CONTINUE;
            }
        });

//        try (Stream<Path> paths = Files.walk(directoryPath.toAbsolutePath())) {
//            fileDtoList = paths
//                .filter(Files::isRegularFile)
//                .map(this::createFileDto)
//                .collect(Collectors.toList());
//        }

        return fileDtoList;
    }

    private FileDto createFileDto(Path path) throws IOException, NoSuchAlgorithmException {
        String filename = path.getFileName().toString();
        String hashContents = calculateFileHash(path);
        long filesize = Files.size(path);
        LocalDateTime now = LocalDateTime.now();
        
        return FileDto.builder()
            .filename(filename)
            .path(path.toString())
            .contents(hashContents)
            .filesize(filesize)
            .status(Status.FAILURE) // 초기 상태
            .date(now)
            .build();
    }
    
    // get file contents hash
    private String calculateFileHash(Path filePath) throws IOException, NoSuchAlgorithmException {
    	
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        try (InputStream stream = Files.newInputStream(filePath)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = stream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }

        byte[] hashBytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

}
