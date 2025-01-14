package com.esthetic.usermicroservices.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserPlanDTO {
    private int id;
    private String idUser;
    private int idPlan;
    private Timestamp createdDate;
    private int duration;
}
