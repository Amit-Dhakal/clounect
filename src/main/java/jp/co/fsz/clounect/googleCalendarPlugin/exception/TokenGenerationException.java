package jp.co.fsz.clounect.googleCalendarPlugin.exception;

/**
 * <p>[概要] トークン生成に問題が発生した際にスローされる例外クラス。</p>
 * <p>[詳細] アクセストークン生成中に問題が発生したことを示す例外がスローされます。</p>
 * <p>[備考] この例外はランタイム例外であり、トークン生成の失敗に関する情報を保持します。</p>
 * <p>[環境] JDK17.0</p>
 * <p>著作権 FSZ 2024</p>
 *
 *  @author 著者FSZ
 * @since 1.0
 */
public class TokenGenerationException extends RuntimeException {

  private final String reason;

  /**
   * <p>[概要] トークン生成エラーが発生した際の例外クラスのコンストラクタ。</p>
   * <p>[詳細] アクセストークン生成中に問題が発生したことを示す例外を生成するコンストラクタ。</p>
   * <p>[備考] このコンストラクタはトークン生成に関するエラーの詳細情報を受け取り、例外を生成します。</p>
   *
   * @param reason トークン生成が失敗した理由を示す詳細情報
   */
  public TokenGenerationException(String reason) {
    super(reason);
    this.reason = reason;
  }

  /**
   * <p>[概要] トークン生成エラーが発生した際の例外クラスのコンストラクタ。</p>
   * <p>[詳細] アクセストークン生成中に問題が発生したことを示す例外を生成するコンストラクタ。</p>
   * <p>[備考] このコンストラクタはトークン生成に関するエラーの詳細情報と原因を受け取り、例外を生成します。</p>
   *
   * @param message トークン生成が失敗した理由を示す詳細情報
   * @param cause   トークン生成の失敗の原因
   * @since 1.0
   */
  public TokenGenerationException(String message, Throwable cause) {
    super(message, cause);
    this.reason = message;
  }
}
