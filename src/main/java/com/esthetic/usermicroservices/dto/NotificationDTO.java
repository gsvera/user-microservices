package com.esthetic.usermicroservices.dto;

public class NotificationDTO {
    public String typeEvent;
    public String title;
    public String message;
    public String dataId;
    public NotificationDTO(){} // Constructor aux
    public NotificationDTO(String typeEvent,String title, String message, String dataId){
        this.typeEvent = typeEvent;
        this.title = title;
        this.message = message;
        this.dataId = dataId;
    }
}
