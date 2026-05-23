package com.example.activeleisure.activity;

import com.example.activeleisure.common.BaseEntity;
import com.example.activeleisure.common.Enums.ActivityStatus;
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

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "activities")
public class Activity extends BaseEntity {
    @Column(nullable = false)
    private String title;
    @Column(nullable = false, length = 4000)
    private String description;
    @Column(length = 1000)
    private String shortDescription;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ActivityCategory category;
    private String difficultyLevel;
    private Integer durationHours;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
    private String location;
    private Integer minAge;
    @Column(length = 2000)
    private String healthRestrictions;
    private Integer minParticipants;
    private Integer maxParticipants;
    @Column(length = 2000)
    private String requiredEquipmentDescription;
    private String imageUrl;
    @Column(length = 4000)
    private String galleryImages;
    @Column(length = 4000)
    private String includedServices;
    @Column(length = 4000)
    private String notIncludedServices;
    @Column(length = 4000)
    private String routeDescription;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityStatus status = ActivityStatus.ACTIVE;
}
