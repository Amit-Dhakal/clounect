package jp.co.fsz.clounect.core.service.impl;

import jp.co.fsz.clounect.core.dto.DashboardDto;
import jp.co.fsz.clounect.core.service.AppMasterService;
import jp.co.fsz.clounect.core.service.DashboardService;
import jp.co.fsz.clounect.core.user.role.Role;
import jp.co.fsz.clounect.core.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {

  private UserService userService;

  private AppMasterService appMasterService;

  @Autowired
  public DashboardServiceImpl(UserService userService, AppMasterService appMasterService) {
    this.userService = userService;
    this.appMasterService = appMasterService;
  }

  @Override
  public DashboardDto prepareDashboard() {
    DefaultOidcUser defaultOidcUser = (DefaultOidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String name = defaultOidcUser.getName();
    String address = String.valueOf(defaultOidcUser.getAddress());
    String companyName = String.valueOf(defaultOidcUser.getAttributes().get("custom:company_name"));

    DashboardDto dashboardDto = DashboardDto.builder().name(name).companyName(companyName)
        .address(address).isAdmin(false).build();

    if(defaultOidcUser.getAuthorities().contains("ROLE_ADMIN")) {
      dashboardDto.setAdmin(true);
    }

    getAppCount(dashboardDto);
    getUserCount(dashboardDto);

    return dashboardDto;
  }

  private void getUserCount(DashboardDto dashboardDto) {
    HashMap<String, Long> userCount = userService.getUserCount();
    dashboardDto.setTotalUsers(userCount.get("totalUsers"));
    dashboardDto.setTotalActiveUsers(userCount.get("inactiveUsers"));
    dashboardDto.setTotalInactiveUsers(userCount.get("activeUsers"));
  }

  private void getAppCount(DashboardDto dashboardDto) {
      HashMap<String, Long> appCounts = appMasterService.getAppCount();
    dashboardDto.setTotalApps(appCounts.get("totalApps"));
    dashboardDto.setTotalActiveApps(appCounts.get("activeApps"));
    dashboardDto.setTotalInactiveApps(appCounts.get("inactiveApps"));
  }
}
