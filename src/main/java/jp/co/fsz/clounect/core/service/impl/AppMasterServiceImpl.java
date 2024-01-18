package jp.co.fsz.clounect.core.service.impl;

import jp.co.fsz.clounect.core.dto.AppMasterDto;
import jp.co.fsz.clounect.core.exception.ResourceNotFoundException;
import jp.co.fsz.clounect.core.model.AppMaster;
import jp.co.fsz.clounect.core.repository.AppMasterRepository;
import jp.co.fsz.clounect.core.repository.projections.AppStat;
import jp.co.fsz.clounect.core.service.AppMasterService;
import jp.co.fsz.clounect.core.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AppMasterServiceImpl implements AppMasterService {

  private final AppMasterRepository appMasterRepository;

  @Autowired
  public AppMasterServiceImpl(AppMasterRepository appMasterRepository) {
    this.appMasterRepository = appMasterRepository;
  }

  @Override
  public Page<AppMasterDto> getAllAppMasters(Pageable pageable) {
    log.info("Inside getAllAppMasters");
    String sortParam = "createdAt";
    pageable.getSortOr(Sort.by(sortParam));
    return appMasterRepository.findAll(pageable).map(AppMasterDto::fromEntity);
  }

  @Override
  public List<AppMasterDto> getAllActiveAppMasters() {
    log.info("Inside getAllActiveAppMasters");
    return appMasterRepository.findByIsActiveTrue().stream().map(AppMasterDto::fromEntity).toList();
  }

  @Override
  public Page<AppMasterDto> getAllActiveAppMasters(Pageable pageable) {
    log.info("Inside getAllActiveAppMasters");
    String sortParam = "createdAt";
    pageable.getSortOr(Sort.by(sortParam));
    return appMasterRepository.findByIsActiveTrue(pageable).map(AppMasterDto::fromEntity);
  }

  @Override
  public AppMasterDto getAppMasterById(Long id) throws ResourceNotFoundException {
    log.info("Inside getAppMasterById");
    AppMaster appMaster = appMasterRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("AppMaster", "id", id));
    return AppMasterDto.fromEntity(appMaster);
  }

  @Override
  public AppMasterDto getAppMasterByUuid(String uuid) throws ResourceNotFoundException {
    log.info("Inside getAppMasterByUuid");
    AppMaster appMaster = appMasterRepository.findByUuid(UUID.fromString(uuid))
        .orElseThrow(() -> new ResourceNotFoundException("AppMaster", "uuid", uuid));
    return AppMasterDto.fromEntity(appMaster);
  }

  @Override
  public AppMasterDto saveAppMaster(AppMasterDto appMasterDto) {
    log.info("Inside saveAppMaster");
    AppMaster appMaster = appMasterDto.toEntity();
    appMaster.setCreatedAt(LocalDateTime.now());
    appMasterRepository.save(appMaster);
    appMasterDto.setId(appMaster.getId());
    return appMasterDto;
  }

  @Override
  public AppMasterDto updateAppMaster(String uuid, AppMasterDto appMasterDto) throws ResourceNotFoundException {
    log.info("Inside updateAppMaster");
    AppMaster appMaster = appMasterDto.toEntity();
    if (appMasterRepository.existsByUuid(UUID.fromString(uuid))) {
      appMaster.setId(appMasterDto.getId());
      appMasterRepository.save(appMaster);
    } else {
      throw new ResourceNotFoundException("AppMaster", "uuid", uuid);
    }
    return appMasterDto;
  }

  @Override
  public boolean deleteAppMaster(String uuid) throws ResourceNotFoundException {
    log.info("Inside deleteAppMaster");
    if (appMasterRepository.existsByUuid(UUID.fromString(uuid))) {
      Optional<AppMaster> optionalAppMaster = appMasterRepository.findByUuid(UUID.fromString(uuid));
      AppMaster appMaster = optionalAppMaster.get();
      appMaster.setDeletedAt(LocalDateTime.now());
      appMaster.setIsActive(Boolean.FALSE);
      appMaster.setDeletedBy(getAuthenticatedUser());

      appMasterRepository.save(appMaster);
      return true;
    } else {
      throw new ResourceNotFoundException("AppMaster", "uuid", uuid);
    }
  }

  @Override
  public boolean enableDisableApp(String uuid, boolean value)
      throws ResourceNotFoundException {
    log.info("Inside enableDisableApp");
    if (appMasterRepository.existsByUuid(UUID.fromString(uuid))) {
      Optional<AppMaster> optionalAppMaster = appMasterRepository.findByUuid(UUID.fromString(uuid));
      AppMaster appMaster = optionalAppMaster.get();
      LocalDateTime deletedAt = null;
      User deletedBy = null;

      //If value = false (Disable operation), so deletedBy and deletedAt fields, else enable operation
      if (!value) {
        deletedBy = getAuthenticatedUser();
        deletedAt = LocalDateTime.now();
      }
      appMaster.setDeletedBy(deletedBy);
      appMaster.setDeletedAt(deletedAt);
      appMaster.setIsActive(value);

      appMasterRepository.save(appMaster);
      return true;
    } else {
      throw new ResourceNotFoundException("AppMaster", "uuid", uuid);
    }
  }

  private User getAuthenticatedUser() {
    log.info("Inside getAuthenticatedUser");
    DefaultOidcUser defaultOidcUser = (DefaultOidcUser) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    Long userId = Long.parseLong(
        (String) defaultOidcUser.getAttributes().get("custom:user_id"));
    User user = new User();
    user.setId(userId);
    return user;
  }

  @Override
  public HashMap<String, Long> getAppCount() {
    HashMap<String, Long> appCountMap = new HashMap<>();

    AppStat appCount = appMasterRepository.getAppCounts();
    Long totalApp = appCount.getTotal();
    Long activeApps = appCount.getActive();
    Long inactiveApps = appCount.getInactive();

    // Assign values to map
    appCountMap.put("totalApps", totalApp != null ? totalApp : 0);
    appCountMap.put("inactiveApps", inactiveApps != null ? inactiveApps : 0);
    appCountMap.put("activeApps", activeApps != null ? activeApps : 0);

    return appCountMap;
  }
}
