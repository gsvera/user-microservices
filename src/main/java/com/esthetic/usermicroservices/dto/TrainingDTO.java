package com.esthetic.usermicroservices.dto;

import com.esthetic.usermicroservices.entity.Training;

public class TrainingDTO {
    public Long id;
    public Integer orderShow;
    public String nameVideo;
    public String description;
    public String linkVideo;
    public TrainingDTO(){} // constructo default
    public TrainingDTO(Training training) {
        this.id = training.getId();
        this.orderShow = training.getOrderShow();
        this.nameVideo = training.getNameVideo();
        this.description = training.getDescription();
        this.linkVideo = training.getLinkVideo();
    }
}
