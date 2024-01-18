package jp.co.fsz.clounect.core.repository.projections;

public interface UserStat {
  Long getTotal();
  Long getInactive();
  Long getActive();
}
