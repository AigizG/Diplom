package com.example.activeleisure.equipment;

import com.example.activeleisure.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "equipment_categories")
public class EquipmentCategory extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String name;
    private String description;
}
