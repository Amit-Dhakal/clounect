package jp.co.fsz.clounect.core.service;

import jp.co.fsz.clounect.core.model.AppSiteInfo;

public interface AppSiteInfoService {

  AppSiteInfo getAppSiteInfoById(Long id);
  AppSiteInfo save(AppSiteInfo appSiteInfo);
}
