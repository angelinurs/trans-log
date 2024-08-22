package com.beat.trans_log.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beat.trans_log.db.entities.FileEntity;

@Repository
public interface FileEntityRepository extends JpaRepository<FileEntity, Long> {

}
