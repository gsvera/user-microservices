package com.esthetic.usermicroservices.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
@Table(name = "tbl_reset_token")
public class ResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int status;
    private String token;
    private String email;
    @Column(name = "create_date")
    private Timestamp createDate;
}
