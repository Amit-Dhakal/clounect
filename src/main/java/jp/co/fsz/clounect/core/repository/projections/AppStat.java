package jp.co.fsz.clounect.core.repository.projections;

import org.springframework.beans.factory.annotation.Value;

public interface AppStat {
  @Value("#{target.total}")
  Long getTotal();

  @Value("#{target.inactive}")
  Long getInactive();
  Long getActive();
}