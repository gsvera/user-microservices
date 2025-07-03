package com.esthetic.usermicroservices.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

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
    private Instant createdDate;
    private int duration;
    @Column(name = "start_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Instant startDate;
    @Column(name = "end_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Instant endDate;
    @Column(name = "is_active")
    private Boolean isActive;
    @Column(name = "is_used")
    private Boolean isUsed;
    public UserPlan(){} // Jpa requiere el constructor
    public UserPlan(String idUser, CatalogPlan idPlan, int duration, Instant createdDate, Instant startDate, Instant endDate, Boolean isActive) {
        this.idUser = idUser;
        this.catalogPlan = idPlan;
        this.duration = duration;
        this.createdDate = createdDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
    }
}
