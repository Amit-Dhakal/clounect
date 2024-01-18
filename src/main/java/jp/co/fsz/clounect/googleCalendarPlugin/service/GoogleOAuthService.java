package jp.co.fsz.clounect.googleCalendarPlugin.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import jp.co.fsz.clounect.googleCalendarPlugin.dto.OAuthToken;
import jp.co.fsz.clounect.googleCalendarPlugin.exception.TokenGenerationException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * <p>[概要] Google Calendar APIにアクセスするためのサービスクラス。</p>
 * <p>[詳細] Google Calendar APIとのやり取りに必要な各種メソッドを提供します。</p>
 * <p>[環境] JDK17.0</p>
 * <p>著作権 FSZ 2024</p>
 *
 * @author 著者FSZ
 * @since 1.0
 */
@Service
public class GoogleOAuthService {
  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  private static final String calendarEvent = "https://www.googleapis.com/auth/calendar.events";

  /**
   * <p>[概要] Google Calendar API からアクセストークンを取得するメソッド。</p>
   * <p>[詳細] 認証コードを使用して、Google Calendar アクセストークンを取得します。</p>
   *
   * @param clientId          クライアントID
   * @param clientSecret      クライアントシークレット
   * @param authorizationCode 認証コード
   * @param redirectUri       リダイレクトURI
   * @return 取得されたアクセストークン
   * @throws RuntimeException アクセストークンの取得に失敗した場合にスローされます。
   * @since 1.0
   */
  public OAuthToken getAccessToken(String clientId, String clientSecret,
      String authorizationCode, String redirectUri) {
    try {
      List<String> scopes = List.of(calendarEvent);
      GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
          GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientId,
          clientSecret, scopes).setAccessType("offline").setApprovalPrompt("force")
          .build();

      GoogleTokenResponse response = flow.newTokenRequest(authorizationCode)
          .setRedirectUri(redirectUri).execute();

      return new OAuthToken(response.getAccessToken(), response.getRefreshToken(),
          response.getTokenType(), Math.toIntExact(response.getExpiresInSeconds()));
    } catch (Exception e) {
      throw new TokenGenerationException("Failed to get access token", e);
    }
  }

  /**
   * <p>[概要] アクセストークンをリフレッシュするメソッド。</p>
   * <p>[詳細] リフレッシュトークンを使用して、Google Calendar アクセストークンを更新します。</p>
   *
   * @param clientId     クライアントID
   * @param clientSecret クライアントシークレット
   * @param refreshToken リフレッシュトークン
   * @return 更新されたアクセストークン
   * @throws RuntimeException アクセストークンの更新に失敗した場合にスローされます。
   * @since 1.0
   */
  public OAuthToken getRefreshAccessToken(String clientId, String clientSecret,
      String refreshToken) {
    try {
      GoogleRefreshTokenRequest tokenRequest = new GoogleRefreshTokenRequest(
          new NetHttpTransport(), new JacksonFactory(), refreshToken, clientId,
          clientSecret);

      tokenRequest.setGrantType("refresh_token");
      GoogleTokenResponse response = tokenRequest.execute();

      return new OAuthToken(response.getAccessToken(), response.getRefreshToken(),
          response.getTokenType(), Math.toIntExact(response.getExpiresInSeconds()));
    } catch (IOException e) {
      throw new TokenGenerationException("Failed to refresh access token", e);
    }
  }
}
