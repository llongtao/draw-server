package com.llt.im.dao;

import com.llt.im.model.entity.ImageData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ImageDataDao extends JpaRepository<ImageData, Long> {
    @Query(value = "SELECT * FROM image_data  ORDER BY  RAND() LIMIT 1",nativeQuery = true)
    ImageData queryRandom();
}
