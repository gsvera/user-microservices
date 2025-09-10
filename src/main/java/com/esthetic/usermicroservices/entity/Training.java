package com.esthetic.usermicroservices.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "tbl_training")
public class Training {
    @Id
    private Long id;
    @Column(name = "order_show")
    private Integer orderShow;
    @Column(name = "name_video")
    private String nameVideo;
    private String description;
    @Column(name = "link_video")
    private String linkVideo;
}
