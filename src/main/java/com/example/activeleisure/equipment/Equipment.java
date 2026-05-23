package com.example.activeleisure.equipment;

import com.example.activeleisure.common.BaseEntity;
import com.example.activeleisure.common.Enums.EquipmentConditionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "equipment")
public class Equipment extends BaseEntity {
    @Column(nullable = false)
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private EquipmentCategory category;
    @Column(nullable = false)
    private Integer quantityTotal;
    @Column(nullable = false)
    private Integer quantityAvailable;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentConditionStatus conditionStatus = EquipmentConditionStatus.AVAILABLE;
    private String description;
}
