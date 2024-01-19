package jp.co.fsz.clounect.core.service.impl;

import jp.co.fsz.clounect.core.dto.AppLogDto;
import jp.co.fsz.clounect.core.dto.AppUsagesLogDto;
import jp.co.fsz.clounect.core.exception.ResourceNotFoundException;
import jp.co.fsz.clounect.core.model.AppSiteInfo;
import jp.co.fsz.clounect.core.model.AppUsagesLog;
import jp.co.fsz.clounect.core.repository.AppUsagesLogRepo;
import jp.co.fsz.clounect.core.service.AppUsagesLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppUsagesLogServImpl implements AppUsagesLogService {

  final AppUsagesLogRepo appUsagesLogRepo;

  public AppUsagesLogServImpl(AppUsagesLogRepo appUsagesLogRepo) {
    this.appUsagesLogRepo = appUsagesLogRepo;
  }

  @Override
  public AppUsagesLogDto save(AppUsagesLogDto appUsagesLogDto) {
    AppUsagesLog appUsagesLogEntity = appUsagesLogDto.toEntity();
    appUsagesLogEntity.setCreatedAt(LocalDateTime.now());
    appUsagesLogRepo.save(appUsagesLogEntity);
    return AppUsagesLogDto.fromEntity(appUsagesLogEntity);
  }

  @Override
  public AppUsagesLogDto update(AppUsagesLogDto appUsagesLogDto) {
    AppUsagesLog appUsagesLogEntity = appUsagesLogDto.toEntity();
    appUsagesLogEntity.setLastUpdatedAt(LocalDateTime.now());
    appUsagesLogRepo.save(appUsagesLogEntity);
    return AppUsagesLogDto.fromEntity(appUsagesLogEntity);
  }

  @Override
  public boolean deleteAppUsagesLog(Long appUsagesLogId) {
    if (appUsagesLogRepo.existsById(appUsagesLogId)) {
      AppUsagesLog appUsagesLog = appUsagesLogRepo.findAppUsagesLogById(appUsagesLogId);
      appUsagesLog.setDeletedAt(LocalDateTime.now());
      appUsagesLog.setIsActive(Boolean.FALSE);

      appUsagesLogRepo.save(appUsagesLog);
      return true;
    } else {
      throw new ResourceNotFoundException("AppUsagesLog", "ID", appUsagesLogId);
    }
  }

  @Override
  public Page<AppUsagesLogDto> getAllAppUsages(Pageable pageable) {
    String sortParam = "createdAt";
    pageable.getSortOr(Sort.by(sortParam));
    return appUsagesLogRepo.findAll(pageable).map(AppUsagesLogDto::fromEntity);
  }

  @Override
  public List<AppUsagesLogDto> getAllActiveAppUsages() {
    return appUsagesLogRepo.findByIsActiveTrue().stream().map(AppUsagesLogDto::fromEntity)
        .toList();
  }

  @Override
  public AppUsagesLogDto getByAppUsagesLogId(Long id) {
    AppUsagesLog appUsagesLog = appUsagesLogRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("AppUsagesLog", "id", id));
    return AppUsagesLogDto.fromEntity(appUsagesLog);
  }

  @Override
  public Page<AppLogDto> getAllAppLog(Pageable pageable) {
    String sortParam = "createdAt";
    pageable.getSortOr(Sort.by(sortParam));
    return appUsagesLogRepo.findAll(pageable).map(AppLogDto::fromEntity);
  }
}