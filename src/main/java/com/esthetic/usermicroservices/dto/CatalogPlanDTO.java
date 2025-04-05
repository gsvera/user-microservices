package com.esthetic.usermicroservices.dto;

import com.esthetic.usermicroservices.entity.CatalogPlan;
import jakarta.persistence.Column;
import lombok.Data;

@Data
public class CatalogPlanDTO {
    public int id;
    public String name;
    public String descriptionEs;
    public String descriptionEn;
    public double price;
    public int duration;
    public Boolean active;
    public CatalogPlanDTO(CatalogPlan catalogPlan) {
        this.id = catalogPlan.getId();
        this.name = catalogPlan.getName();
        this.descriptionEn = catalogPlan.getDescriptionEn();
        this.descriptionEs = catalogPlan.getDescriptionEs();
        this.price = catalogPlan.getPrice();
        this.duration = catalogPlan.getDuration();
        this.active = catalogPlan.getActive();
    }
}
