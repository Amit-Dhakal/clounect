package jp.co.fsz.clounect.core.service.impl;

import jp.co.fsz.clounect.core.dto.AppDataDto;
import jp.co.fsz.clounect.core.exception.ResourceNotFoundException;
import jp.co.fsz.clounect.core.model.AppData;
import jp.co.fsz.clounect.core.repository.AppDataRepository;
import jp.co.fsz.clounect.core.service.AppDataService;
import jp.co.fsz.clounect.core.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AppDataServiceImpl implements AppDataService {

  private final AppDataRepository appDataRepository;

  @Autowired
  public AppDataServiceImpl(AppDataRepository appDataRepository) {
    this.appDataRepository = appDataRepository;
  }

  @Override
  public Page<AppDataDto> getAllAppDatas(Pageable pageable) {
    log.info("Inside getAllAppDatas");
    String sortParam = "createdAt";
    pageable.getSortOr(Sort.by(sortParam));
    return appDataRepository.findAll(pageable).map(AppDataDto::fromEntity);
  }

  @Override
  public Page<AppDataDto> searchAppDataByDetail(String query, Pageable pageable) {
    log.info("inside search app data by detail");
    String sortParam = "createdAt";
    pageable.getSortOr(Sort.by(sortParam));

    return appDataRepository.getAppDataByDetail(query, pageable).map(AppDataDto::fromEntity);
  }

  @Override
  public List<AppDataDto> getAllActiveAppDatas() {
    log.info("Inside getAllActiveAppDatas");
    return appDataRepository.findByIsActiveTrue().stream().map(AppDataDto::fromEntity).toList();
  }

  @Override
  public Page<AppDataDto> getAllActiveAppDatas(Pageable pageable) {
    log.info("Inside getAllActiveAppDatas");
    String sortParam = "createdAt";
    pageable.getSortOr(Sort.by(sortParam));
    return appDataRepository.findByIsActiveTrue(pageable).map(AppDataDto::fromEntity);
  }

  @Override
  public AppDataDto getAppDataById(Long id) throws ResourceNotFoundException {
    log.info("Inside getAppDataById");
    AppData appData = appDataRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("AppData", "id", id));
    return AppDataDto.fromEntity(appData);
  }

  @Override
  public AppDataDto getAppDataByUuid(String uuid) throws ResourceNotFoundException {
    log.info("Inside getAppDataByUuid");
    AppData appData = appDataRepository.findByUuid(UUID.fromString(uuid))
        .orElseThrow(() -> new ResourceNotFoundException("AppData", "uuid", uuid));
    return AppDataDto.fromEntity(appData);
  }

  @Override
  public AppDataDto saveAppData(AppDataDto appDataDto) {
    log.info("Inside saveAppData");
    AppData appData = appDataDto.toEntity();
    appDataRepository.save(appData);
    appDataDto.setId(appData.getId());
    return appDataDto;
  }

  @Override
  public AppDataDto updateAppData(String uuid, AppDataDto appDataDto) throws ResourceNotFoundException {
    log.info("Inside updateAppData");
    AppData appData = appDataDto.toEntity();
    if (appDataRepository.existsByUuid(UUID.fromString(uuid))) {
      appData.setId(appDataDto.getId());
      appDataRepository.save(appData);
    } else {
      throw new ResourceNotFoundException("AppData", "uuid", uuid);
    }
    return appDataDto;
  }

  @Override
  public boolean deleteAppData(String uuid) throws ResourceNotFoundException {
    log.info("Inside deleteAppData");
    if (appDataRepository.existsByUuid(UUID.fromString(uuid))) {
      Optional<AppData> optionalAppData = appDataRepository.findByUuid(UUID.fromString(uuid));
      AppData appData = optionalAppData.get();
      appData.setDeletedAt(LocalDateTime.now());
      appData.setIsActive(Boolean.FALSE);

      appDataRepository.save(appData);
      return true;
    } else {
      throw new ResourceNotFoundException("AppData", "uuid", uuid);
    }
  }

  @Override
  public boolean enableDisableAppData(String uuid, boolean value)
      throws ResourceNotFoundException {
    log.info("Inside enableDisableAppData");
    if (appDataRepository.existsByUuid(UUID.fromString(uuid))) {
      Optional<AppData> optionalAppData = appDataRepository.findByUuid(UUID.fromString(uuid));
      AppData appData = optionalAppData.get();
      LocalDateTime deletedAt = null;
      User deletedBy = null;

      //If value = false (Disable operation), so deletedBy and deletedAt fields, else enable operation
      if (!value) {
        deletedBy = getAuthenticatedUser();
        deletedAt = LocalDateTime.now();
      }
      appData.setDeletedAt(deletedAt);
      appData.setIsActive(value);

      appDataRepository.save(appData);
      return true;
    } else {
      throw new ResourceNotFoundException("AppData", "uuid", uuid);
    }
  }

  @Override
  public Optional<AppDataDto> getByRecIdAndAppSiteId(Integer recId, Long appSiteId) {
    Optional<AppData> obj = appDataRepository.getAppData(String.valueOf(recId), appSiteId);
    log.info("obj:: {}", obj);
    log.info("obj isPresent:: {}", obj.isPresent());

    return obj.map(AppDataDto::fromEntity);
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
}
