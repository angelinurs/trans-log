package com.beat.trans_log.db.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.beat.trans_log.db.entities.FileEntity;
import com.beat.trans_log.db.repository.FileEntityRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FileEntityService {

    private final FileEntityRepository fileEntityRepository;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public FileEntity saveFileEntity( FileEntity fileEntity) {
    	
        if (fileEntity == null) {
        	log.warn("FileEntity is null");
            throw new IllegalArgumentException("FileEntity must not be null");
        }

        try {
            log.info("Saving FileEntity: {}", fileEntity);
            FileEntity savedEntity = fileEntityRepository.save(fileEntity);
            log.info("Saved FileEntity: {}", savedEntity);
            return savedEntity;
        } catch (Exception e) {
            log.error("Failed to save FileEntity: {}", fileEntity, e);
            throw e;
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<FileEntity> saveFileEntities( List<FileEntity> fileEntities) {
    	
    	if (fileEntities == null || fileEntities.isEmpty() ) {
    		log.warn("FileEntity is null");
    	}
    	
    	try {
    		log.info("Saving FileEntity: {}", fileEntities);
    		List<FileEntity> savedEntitis = fileEntityRepository.saveAll(fileEntities);
    		log.info("Saved FileEntity: {}", savedEntitis.toString());
    		return savedEntitis;
    	} catch (Exception e) {
    		log.error("Failed to save FileEntity: {}", fileEntities, e);
    		throw e;
    	}
    }

}
