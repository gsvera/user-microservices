package com.esthetic.usermicroservices.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class CatalogPlanDTO {
    private int id;
    private String name;
    private String descriptionEs;
    private String descriptionEn;
    private double price;
    private int duration;
    private int active;
}
