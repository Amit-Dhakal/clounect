package jp.co.fsz.clounect.core.service;

import jp.co.fsz.clounect.core.user.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>[概要] AWS Cognito インタラクション サービス クラス</p>
 * <p>[詳細] AWS Cognito と連携してユーザーの登録や管理を行うためのサービスクラス。 </p>
 * <p>[備 考] なし。</p>
 * <p>[環境] JDK17.0</p>
 * <p>著作権 FSZ 2024</p>
 *
 * @author 著者FSZ
 * @since 1.0
 */
@Service
@Slf4j
public class AwsCognitoService {

  @Value("${spring.security.oauth2.client.registration.cognito.clientId}")
  private String clientId;

  @Value("${spring.security.oauth2.client.clientId}")
  private String awsClientId;

  @Value("${spring.security.oauth2.client.clientSecret}")
  private String awsClientSecret;

  @Value("${spring.security.oauth2.client.registration.cognito.clientSecret}")
  private String clientSecret;

  @Value("spring.security.oauth2.client.userPoolRegion")
  private String userPoolRegion;

  @Value("${spring.security.oauth2.client.userPoolId}")
  private String userPoolId;

  /**
   * <p> [概 要] Gets AWS login credentials using the provided AWS client ID and client
   * secret.</p>
   * <p> [詳 細] Gets AWS login credentials using the provided AWS client ID and client
   * secret.</p>
   *
   * @return AWS login credentials.
   * @since 1.0
   */
  private AwsBasicCredentials getAWSLoginCredentials() {
    return AwsBasicCredentials.create(awsClientId, awsClientSecret);
  }

  /**
   * <p> [概 要] Creates and returns a Cognito Identity Provider </p>
   * <p> [詳 細] Creates and returns a Cognito Identity Provider client with the specified
   * AWS region * and AWS login credentials. </p>
   *
   * @return Cognito Identity Provider client.
   * @since 1.0
   */
  private CognitoIdentityProviderClient createCognitoIdentityProviderClient() {
    return CognitoIdentityProviderClient.builder()
        .credentialsProvider(StaticCredentialsProvider.create(getAWSLoginCredentials()))
        .build();
  }

  /**
   * <p> [概 要] Registers a user in the AWS Cognito user pool.</p>
   * <p> [詳 細] Registers a user in the AWS Cognito user pool.</p>
   *
   * @param user The user information to be registered.
   * @throws CognitoIdentityProviderException If an exception occurs during the user
   *                                          registration process.
   *                                          {@link CognitoIdentityProviderException}
   * @throws IllegalArgumentException         If the provided user information is invalid
   *                                          or missing required fields.
   * @throws AwsServiceException              If an AWS service-related exception occurs.
   *                                          {@link
   *                                          software.amazon.awssdk.awscore.exception.AwsServiceException}
   * @throws SdkClientException               If an error occurs in the AWS SDK for Java.
   *                                          {@link
   *                                          software.amazon.awssdk.core.exception.SdkClientException}
   * @since 1.0
   */
  public void registerUser(UserDto user) {
    log.info("Inside registerUser");
    validateUserRegistrationInput(user);

    try (CognitoIdentityProviderClient client = createCognitoIdentityProviderClient()) {
      AttributeType userAttrs = AttributeType.builder().name("name")
          .value(user.getShortName()).build();
      AttributeType userAttrsEmail = AttributeType.builder().name("email")
          .value(user.getEmail()).build();
      AttributeType userAttrsEmailVerified = AttributeType.builder()
          .name("email_verified").value("true").build();
      AttributeType phoneNumber = AttributeType.builder().name("phone_number")
          .value(String.valueOf(user.getPhone())).build();
      AttributeType address = AttributeType.builder().name("address")
          .value(String.valueOf(user.getCompanyName())).build();
      AttributeType customUserId = AttributeType.builder().name("custom:user_id")
          .value(String.valueOf(user.getId())).build();
      AttributeType companyName = AttributeType.builder().name("custom:company_name")
          .value(String.valueOf(user.getCompanyName())).build();

      List<AttributeType> userAttrsList = new ArrayList<>();
      userAttrsList.add(userAttrs);
      userAttrsList.add(userAttrsEmail);
      userAttrsList.add(userAttrsEmailVerified);
      userAttrsList.add(customUserId);
      userAttrsList.add(companyName);
      userAttrsList.add(phoneNumber);
      userAttrsList.add(address);

      AdminCreateUserRequest signUpRequest = AdminCreateUserRequest.builder()
          .userPoolId(userPoolId).desiredDeliveryMediums(DeliveryMediumType.EMAIL)
          .username(user.getEmail()).validationData(userAttrsEmail)
          .temporaryPassword(user.getPassword()).userAttributes(userAttrsList).build();

      AdminCreateUserResponse signUpResponse = client.adminCreateUser(signUpRequest);
      log.info("Response:: {}", signUpResponse.toString());

      log.info("{} has been signed up", user.getEmail());
      addUserToGroup(client, user);
    } catch (CognitoIdentityProviderException e) {
      log.error("An exception occurred: ", e);
      throw e;
    }
  }

  /**
   * <p> [概 要] Updates user attributes</p>
   * <p> [詳 細] User a user details.</p>
   *
   * @param user The user information to be updated.
   * @throws CognitoIdentityProviderException If an exception occurs during the user
   *                                          registration process.
   *                                          {@link CognitoIdentityProviderException}
   * @throws IllegalArgumentException         If the provided user information is invalid
   *                                          or missing required fields.
   * @throws AwsServiceException              If an AWS service-related exception occurs.
   *                                          {@link
   *                                          software.amazon.awssdk.awscore.exception.AwsServiceException}
   * @throws SdkClientException               If an error occurs in the AWS SDK for Java.
   *                                          {@link
   *                                          software.amazon.awssdk.core.exception.SdkClientException}
   * @since 1.0
   */
  public void updateUser(UserDto user) {
    log.info("Inside updateUser");
    validateUserRegistrationInput(user);

    try (CognitoIdentityProviderClient client = createCognitoIdentityProviderClient()) {
      AttributeType userAttrs = AttributeType.builder().name("name")
          .value(user.getShortName()).build();
      AttributeType companyName = AttributeType.builder().name("custom:company_name")
          .value(String.valueOf(user.getCompanyName())).build();
      AttributeType phoneNumber = AttributeType.builder().name("phone_number")
          .value(String.valueOf(user.getPhone())).build();
      AttributeType customUserId = AttributeType.builder().name("custom:user_id")
          .value(String.valueOf(user.getId())).build();
      AttributeType address = AttributeType.builder().name("address")
          .value(String.valueOf(user.getCompanyName())).build();

      List<AttributeType> userAttrsList = new ArrayList<>();
      userAttrsList.add(userAttrs);
      userAttrsList.add(companyName);
      userAttrsList.add(phoneNumber);
      userAttrsList.add(customUserId);
      userAttrsList.add(address);

      AdminUpdateUserAttributesRequest adminUpdateUserAttributesRequest = AdminUpdateUserAttributesRequest.builder()
          .userPoolId(userPoolId).userAttributes(userAttrsList).build();

      AdminUpdateUserAttributesResponse updateResponse = client.adminUpdateUserAttributes(
          adminUpdateUserAttributesRequest);
      log.info("Response:: {}", updateResponse.toString());

      log.info("Details for {} has been update", user.getEmail());
    } catch (CognitoIdentityProviderException e) {
      log.error("An exception occurred: ", e);
      throw e;
    }
  }

  /**
   * <p> [概 要] Validates input parameters for the registerUser method.</p>
   * <p> [詳 細] Validates input parameters for the registerUser method.</p>
   *
   * @param user The user information to be registered.
   * @throws IllegalArgumentException If the provided user information is invalid or
   *                                  missing required fields.
   * @since 1.0
   */
  private void validateUserRegistrationInput(UserDto user) {
    if (user == null || user.getShortName() == null || user.getEmail() == null
        || user.getPassword() == null) {
      throw new IllegalArgumentException(
          "User information must not be null, and must contain name, email, and password.");
    }
  }

  /**
   * <p> [概 要] Adds a user to a specified group in the AWS Cognito user pool.</p>
   * <p> [詳 細] Adds a user to a specified group in the AWS Cognito user pool.</p>
   *
   * @param client The AWS Cognito Identity Provider client.
   * @param user   The user to be added to the group.
   * @throws CognitoIdentityProviderException If an exception occurs during the user
   *                                          addition to the group process.
   *                                          {@link CognitoIdentityProviderException}
   * @throws IllegalArgumentException         If the provided AWS Cognito Identity
   *                                          Provider client or user is null.
   * @throws AwsServiceException              If an AWS service-related exception occurs.
   *                                          {@link AwsServiceException}
   * @throws SdkClientException               If an error occurs in the AWS SDK for Java.
   *                                          {@link SdkClientException}
   * @since 1.0
   */
  public void addUserToGroup(CognitoIdentityProviderClient client, UserDto user) {
    log.info("Inside addUserToGroup");

    // Validate input parameters
    validateAddUserToGroupInput(client, user);

    AdminAddUserToGroupRequest addUserToGroupRequest = AdminAddUserToGroupRequest.builder()
        .userPoolId(userPoolId).groupName(user.getRole().name()).username(user.getEmail())
        .build();

    // Add user to group
    AdminAddUserToGroupResponse adminAddUserToGroupResponse = client.adminAddUserToGroup(
        addUserToGroupRequest);

    log.info("Response:: {}", adminAddUserToGroupResponse.toString());
    log.info("{} has been added to {}", user.getEmail(), user.getRole().name());
  }

  /**
   * <p> [概 要] Validates input parameters for the addUserToGroup method.</p>
   * <p> [詳 細] Validates input parameters for the addUserToGroup method.</p>
   *
   * @param client The AWS Cognito Identity Provider client.
   * @param user   The user to be added to the group.
   * @throws IllegalArgumentException If the provided AWS Cognito Identity Provider client
   *                                  or user is null.
   * @since 1.0
   */
  private void validateAddUserToGroupInput(CognitoIdentityProviderClient client,
      UserDto user) {
    if (client == null || user == null) {
      throw new IllegalArgumentException(
          "AWS Cognito Identity Provider client and user cannot be null");
    }
  }

  /**
   * <p> [概 要] Deletes a user from the AWS Cognito user pool.</p>
   * <p> [詳 細] Deletes a user from the AWS Cognito user pool.</p>
   *
   * @param username The username of the user to be deleted.
   * @throws CognitoIdentityProviderException If an exception occurs during the user
   *                                          deletion process.
   *                                          {@link CognitoIdentityProviderException}
   * @throws IllegalArgumentException         If the provided username is null or empty.
   * @throws AwsServiceException              If an AWS service-related exception occurs.
   *                                          {@link
   *                                          software.amazon.awssdk.awscore.exception.AwsServiceException}
   * @throws SdkClientException               If an error occurs in the AWS SDK for Java.
   *                                          {@link
   *                                          software.amazon.awssdk.core.exception.SdkClientException}
   * @since 1.0
   */
  public void deleteUser(String username) {
    log.info("Inside deleteUser");
    validateUserInput(username);

    try (CognitoIdentityProviderClient client = createCognitoIdentityProviderClient()) {
      AdminDeleteUserRequest adminDeleteUserRequest = AdminDeleteUserRequest.builder()
          .userPoolId(userPoolId).username(username).build();

      AdminDeleteUserResponse deleteUserResponse = client.adminDeleteUser(
          adminDeleteUserRequest);

      log.info("Response:: {} has been deleted", deleteUserResponse.toString());
      log.info("{} has been deleted", username);
    } catch (CognitoIdentityProviderException e) {
      log.error("An exception occurred: ", e);
      throw e;
    }
  }

  /**
   * <p> [概 要] AWS Cognito からユーザーを無効にします。</p>
   * <p> [詳 細] ユーザーを非アクティブ化し、そのユーザーのすべてのアクセス トークンを取り消します。</p>
   *
   * @param username 無効にするユーザーのユーザー名。
   * @throws CognitoIdentityProviderException ユーザー実行中に例外が発生した場合 ※削除処理です。
   *                                          {@link CognitoIdentityProviderException}
   * @throws IllegalArgumentException         指定されたユーザー名が null または空の場合。
   * @throws AwsServiceException              サービス関連の例外が発生した場合。
   *                                          {@link
   *                                          software.amazon.awssdk.awscore.exception.AwsServiceException}
   * @throws SdkClientException               でエラーが発生した場合。
   *                                          {@link
   *                                          software.amazon.awssdk.core.exception.SdkClientException}
   * @since 1.0
   */
  public void disableUser(String username) {
    log.info("Inside disableUser");
    validateUserInput(username);

    try (CognitoIdentityProviderClient client = createCognitoIdentityProviderClient()) {
      AdminDisableUserRequest adminDisableUserRequest = AdminDisableUserRequest.builder()
          .userPoolId(userPoolId).username(username).build();

      AdminDisableUserResponse adminDisableUserResponse = client.adminDisableUser(
          adminDisableUserRequest);

      log.info("Response:: {}", adminDisableUserResponse.toString());
      log.info("{} has been disabled", username);
    } catch (CognitoIdentityProviderException e) {
      log.error("An exception occurred: ", e);
      throw e;
    }
  }

  /**
   * <p> [概 要] AWS Cognito からユーザーを有効にします。</p>
   * <p> [詳 細] 指定したユーザーを管理者として有効にします。 どのユーザーでも機能します</p>
   *
   * @param username 有効にするユーザーのユーザー名。
   * @throws CognitoIdentityProviderException ユーザー実行中に例外が発生した場合 ※削除処理です。
   *                                          {@link CognitoIdentityProviderException}
   * @throws IllegalArgumentException         指定されたユーザー名が null または空の場合。
   * @throws AwsServiceException              サービス関連の例外が発生した場合。
   *                                          {@link
   *                                          software.amazon.awssdk.awscore.exception.AwsServiceException}
   * @throws SdkClientException               でエラーが発生した場合。
   *                                          {@link
   *                                          software.amazon.awssdk.core.exception.SdkClientException}
   * @since 1.0
   */
  public void enableUser(String username) {
    log.info("Inside enableUser");
    validateUserInput(username);

    try (CognitoIdentityProviderClient client = createCognitoIdentityProviderClient()) {
      AdminEnableUserRequest adminEnableUserRequest = AdminEnableUserRequest.builder()
          .userPoolId(userPoolId).username(username).build();

      AdminEnableUserResponse adminEnableUserResponse = client.adminEnableUser(
          adminEnableUserRequest);

      log.info("Response:: {}", adminEnableUserResponse.toString());
      log.info("{} has been enabled", username);
    } catch (CognitoIdentityProviderException e) {
      log.error("An exception occurred: ", e);
      throw e;
    }
  }

  /**
   * Validates input parameters for the deleteUser method.
   *
   * @param username The username of the user to be deleted.
   * @throws IllegalArgumentException If the provided username is null or empty.
   */
  private void validateUserInput(String username) {
    if (username == null || username.isEmpty()) {
      throw new IllegalArgumentException("Username cannot be null or empty");
    }
  }

}
