package jp.co.fsz.clounect.googleCalendarPlugin.exception;

import lombok.Getter;

/**
 * <p>[概要] 必要な資格情報が不足している際に発生する例外クラス。</p>
 * <p>[詳細] アプリケーションで必要な資格情報が不足している場合に、この例外が発生します。</p>
 * <p>[備考] これはランタイム例外であり、不足している資格情報に関する情報を保持しています。</p>
 * <p>[環境] JDK17.0</p>
 * <p>著作権 FSZ 2024</p>
 *
 *  @author 著者FSZ
 * @since 1.0
 */
@Getter
public class NotFoundException extends RuntimeException {
  private final String reason;

  /**
   * <p>[概要] NotFoundExceptionクラスのコンストラクタ。特定の情報が見つからない場合の例外を表します。</p>
   * <p>[詳細] 特定の情報が見つからないことを示す例外を生成するためのコンストラクタで、理由をパラメータとして受け取ります。</p>
   * <p>[備考] このコンストラクタは見つからない状況の理由に関する情報を受け取り、例外を生成します。</p>
   *
   * @param reason 見つからない状況の理由を示す説明
   * @since 1.0
   */
  public NotFoundException(String reason) {
    super(reason);
    this.reason = reason;
  }
}

