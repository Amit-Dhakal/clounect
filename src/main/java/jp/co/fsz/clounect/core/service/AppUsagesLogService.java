package jp.co.fsz.clounect.core.service;

import jp.co.fsz.clounect.core.dto.AppLogDto;
import jp.co.fsz.clounect.core.dto.AppUsagesLogDto;
import jp.co.fsz.clounect.core.model.AppSiteInfo;
import jp.co.fsz.clounect.core.model.AppUsagesLog;
import jp.co.fsz.clounect.core.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public  interface AppUsagesLogService {
  AppUsagesLogDto save(AppUsagesLogDto appUsagesLogDto);

  AppUsagesLogDto update(AppUsagesLogDto appUsagesLogDto);

  boolean deleteAppUsagesLog(Long  appUsagesLogId);

  Page<AppUsagesLogDto> getAllAppUsages(Pageable pageable);

  List<AppUsagesLogDto> getAllActiveAppUsages();

  AppUsagesLogDto getByAppUsagesLogId(Long Id);

  Page<AppLogDto> getAllAppLog(Pageable pageable);
}