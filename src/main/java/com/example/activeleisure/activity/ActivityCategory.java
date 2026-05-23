package com.example.activeleisure.activity;

import com.example.activeleisure.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "activity_categories")
public class ActivityCategory extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String name;
    private String description;
}
