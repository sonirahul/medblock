package com.medblock.repository;

import com.medblock.domain.FileAssets;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileAssetsRepository extends MongoRepository<FileAssets, String> {
}
