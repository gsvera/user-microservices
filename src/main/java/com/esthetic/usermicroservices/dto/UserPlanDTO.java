package com.esthetic.usermicroservices.dto;

import com.esthetic.usermicroservices.entity.CatalogPlan;
import com.esthetic.usermicroservices.entity.UserPlan;

import java.time.Instant;

public class UserPlanDTO {
    public Long id;
    public String idUser;
    public CatalogPlanDTO catalogPlanDTO;
    public Instant createdDate;
    public int duration;
    public Instant startDate;
    public Instant endDate;
    public Boolean isActive;
    public UserPlanDTO(UserPlan userPlan, CatalogPlan catalogPlan) {
        this.id = userPlan.getId();
        this.idUser = userPlan.getIdUser();
        this.createdDate = userPlan.getCreatedDate();
        this.duration = userPlan.getDuration();
        this.startDate = userPlan.getStartDate();
        this.endDate = userPlan.getEndDate();
        this.isActive = userPlan.getIsActive();
        this.catalogPlanDTO = new CatalogPlanDTO(catalogPlan);
    }
}
