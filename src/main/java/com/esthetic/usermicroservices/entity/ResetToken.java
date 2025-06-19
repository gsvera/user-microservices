package com.esthetic.usermicroservices.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;

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
    public ResetToken(){} // default constructor
    public ResetToken(String token, String email) {
        this.status = 0;
        this.token = token;
        this.email = email;
        this.createDate = Timestamp.from(Instant.now());
    }
}
