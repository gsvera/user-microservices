package com.esthetic.usermicroservices.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tbl_user_plan")
public class UserPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "id_user")
    private String idUser;
    @OneToOne
    @JoinColumn(name = "id_plan", insertable = true, updatable = true, nullable = false)
    private CatalogPlan catalogPlan;
    @Column(name = "created_date")
    private Timestamp createdDate;
    private int duration;
    @Column(name = "start_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
    @Column(name = "end_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
    @Column(name = "is_active")
    private Boolean isActive;
    public UserPlan(){} // Jpa requiere el constructor
    public UserPlan(String idUser, CatalogPlan idPlan, int duration, Timestamp createdDate, LocalDateTime startDate, LocalDateTime endDate, Boolean isActive) {
        this.idUser = idUser;
        this.catalogPlan = idPlan;
        this.duration = duration;
        this.createdDate = createdDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
    }
}
