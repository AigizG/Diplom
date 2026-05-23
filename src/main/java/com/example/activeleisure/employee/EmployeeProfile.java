package com.example.activeleisure.employee;

import com.example.activeleisure.common.BaseEntity;
import com.example.activeleisure.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "employee_profiles")
public class EmployeeProfile extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    private String specialization;
    private Integer experienceYears;
    private String bio;
    private boolean active = true;
}
