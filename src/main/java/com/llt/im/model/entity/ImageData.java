package com.llt.im.model.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author llt11
 */
@Entity(name = "image_data")
@Data
public class ImageData {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name="img", columnDefinition="BLOB")
    private String img;

    @Column(name = "keyword", length = 10)
    private String keyword;

    @Column(name = "keyword_desc", length = 32)
    private String keywordDesc;

    @Column(name = "user_id", length = 32)
    private String userId;

}
