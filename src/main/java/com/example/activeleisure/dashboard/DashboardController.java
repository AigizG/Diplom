package com.example.activeleisure.dashboard;

import com.example.activeleisure.dto.ApiDtos.AdminDashboardResponse;
import com.example.activeleisure.dto.ApiDtos.ManagerDashboardResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Dashboards")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/manager")
    public ManagerDashboardResponse manager() {
        return dashboardService.manager();
    }

    @GetMapping("/admin")
    public AdminDashboardResponse admin() {
        return dashboardService.admin();
    }
}
