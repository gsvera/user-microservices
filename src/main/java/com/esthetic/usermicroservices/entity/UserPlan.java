package com.esthetic.usermicroservices.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "tbl_user_plan")
public class UserPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "id_user")
    private String idUser;
    @Column(name = "id_plan")
    private int idPlan;
    @Column(name = "created_date")
    private Timestamp createdDate;
    private int duration;

    public UserPlan(String idUser, int idPlan, int duration) {
        this.idUser = idUser;
        this.idPlan = idPlan;
        this.duration = duration;
    }
}
