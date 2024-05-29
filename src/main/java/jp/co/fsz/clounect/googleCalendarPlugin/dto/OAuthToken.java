package jp.co.fsz.clounect.googleCalendarPlugin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * <p>[概要] OAuth トークン情報を保持するクラス。</p>
 * <p>[詳細] アクセストークン、リフレッシュトークン、トークンタイプ、および有効期限を保持します。</p>
 * <p>[備考] Getter, Setter, 引数つきコンストラクタ、引数なしコンストラクタが提供されています。</p>
 * <p>[環境] JDK17.0</p>
 * <p>著作権 FSZ 2024</p>
 *
 *  @author 著者FSZ
 * @since 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OAuthToken {
  private String accessToken;
  private String refreshToken;
  private String tokenType;
  private int expiration;
}
