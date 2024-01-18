package jp.co.fsz.clounect.core.dto;

import java.time.LocalDateTime;

/**
 * <p>[概 要] ErrorDetails 変更通知 REST API 処理クラス。</p>
 * <p>[詳細] エラーを時間、メッセージ、詳細とともに表示するクラス。</p>
 * <p>[備 考] なし。</p>
 * <p>[環 境] JDK17.0</p>
 * <p>Copyright フィーラーシステムZ, 2024</p>
 *
 * @author フィーラーシステムZ
 * @since 1.0
 */
public record ErrorDetails(LocalDateTime timestamp, String message, String details) {

}
