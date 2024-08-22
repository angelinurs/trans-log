package com.beat.trans_log.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beat.trans_log.dto.FileDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DirectoryListing {

	private DirectoryListing() {
	    throw new IllegalStateException("Utility class");
	}
	
    public static List<Path> listFilesInDirectory(String path) throws IOException {
    	
    	Path directoryPath = Paths.get( path );

        // Check validation directory 
        if (Files.notExists(directoryPath) || !Files.isDirectory(directoryPath)) {
            log.info("'{}' does not exist", directoryPath);
            return Collections.emptyList();
        }

        // Get file list using directory stream
//        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath)) {
//            for (Path entry : stream) {
//                log.info("1. {}", entry.getFileName());
//            }
//        }
        
        List<Path> fileList = new ArrayList<>();
        
        // get current and sub directory
        Files.walkFileTree(directoryPath, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                log.info("file list : {}", file);
                fileList.add(file);
                return FileVisitResult.CONTINUE;
            }
        });
        
        return fileList;
    }
    
    public static boolean deleteFile( Path file ) {
        if (Files.notExists(file)) {
            log.warn("File '{}' does not exist", file);
            return false;
        }

        try {
            Files.delete(file);
            log.info("File '{}' deleted successfully", file);
            return true;
        } catch (NoSuchFileException e) {
            log.error("No such file: '{}'", file);
            return false;
        } catch (IOException e) {
            log.error("Error deleting file '{}': {}", file, e.getMessage());
            return false;
        }
    }
    
    public static String calculateFileHash(Path filePath) throws IOException, NoSuchAlgorithmException {
    	
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
