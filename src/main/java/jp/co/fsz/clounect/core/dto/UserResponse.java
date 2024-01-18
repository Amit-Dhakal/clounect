package jp.co.fsz.clounect.core.dto;

import jp.co.fsz.clounect.core.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>[概 要] UserResponse 変更通知 REST API 処理クラス。</p>
 * <p>[詳細] REST API の処理メソッドをユーザーに表示する方法を記述したクラスです。</p>
 * <p>[備 考] なし。</p>
 * <p>[環 境] JDK17.0</p>
 * <p>Copyright フィーラーシステムZ, 2024</p>
 *
 * @author フィーラーシステムZ
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
  private List<UserDto> content;
  private int pageNo;
  private int pageSize;
  private long totalElements;
  private int totalPages;
  private boolean last;
}
