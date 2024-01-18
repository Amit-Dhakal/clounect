package jp.co.fsz.clounect.core.service;

import jp.co.fsz.clounect.core.model.AppSiteInfo;
import org.springframework.stereotype.Service;

public interface AppSiteInfoService {

  AppSiteInfo getAppSiteInfoById(Long id);
}
