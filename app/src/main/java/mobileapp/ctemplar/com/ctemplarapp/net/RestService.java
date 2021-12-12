package mobileapp.ctemplar.com.ctemplarapp.net;

import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Single;
import mobileapp.ctemplar.com.ctemplarapp.net.request.AddFirebaseTokenRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.AutoReadEmailRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SubscriptionMobileUpgradeRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.WarnExternalLinkRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.folders.AddFolderRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.AntiPhishingPhraseRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.AutoSaveContactEnabledRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.CaptchaVerifyRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.ChangePasswordRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.CheckUsernameRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.contacts.ContactsEncryptionRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.mailboxes.CreateMailboxKeyRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.mailboxes.CreateMailboxRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.DarkModeRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.mailboxes.DefaultMailboxRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.DisableLoadingImagesRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.folders.EditFolderRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.folders.EmptyFolderRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.mailboxes.DeleteMailboxKeyRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.mailboxes.EnabledMailboxRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.mailboxes.UpdateMailboxPrimaryKeyRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.messages.MarkMessageAsReadRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.messages.MarkMessageIsStarredRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.folders.MoveToFolderRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.NotificationEmailRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.PublicKeysRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.RecoverPasswordRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.RecoveryEmailRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.messages.SendMessageRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SettingsRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SignInRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SignUpRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SignatureRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SubjectEncryptedRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.TokenRefreshRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.UpdateReportBugsRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.filters.EmailFilterOrderListRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.filters.EmailFilterRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.AddFirebaseTokenResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.CaptchaResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.CaptchaVerifyResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.CheckUsernameResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.SubscriptionMobileUpgradeResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.keys.KeysResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.RecoverPasswordResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.SignInResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.SignUpResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.contacts.ContactData;
import mobileapp.ctemplar.com.ctemplarapp.net.response.contacts.ContactsResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.domains.DomainsResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.filters.EmailFilterOrderListResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.filters.EmailFilterResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.filters.EmailFilterResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.folders.FoldersResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.folders.FoldersResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.mailboxes.MailboxKeyResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.mailboxes.MailboxKeysResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.mailboxes.MailboxResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.mailboxes.MailboxesResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.messages.EmptyFolderResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.messages.MessageAttachment;
import mobileapp.ctemplar.com.ctemplarapp.net.response.messages.MessagesResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.myself.BlackListContact;
import mobileapp.ctemplar.com.ctemplarapp.net.response.myself.MyselfResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.myself.SettingsResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.myself.WhiteListContact;
import mobileapp.ctemplar.com.ctemplarapp.net.response.whiteBlackList.BlackListResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.whiteBlackList.WhiteListResponse;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

@Singleton
public interface RestService {
    @POST("auth/sign-in/")
    Observable<SignInResponse> signIn(@Body SignInRequest request);

    @POST("auth/sign-up/")
    Single<SignUpResponse> signUp(@Body SignUpRequest request);

    @GET("auth/sign-out/")
    Observable<Response<Void>> signOut(
            @Query("platform") String platform,
            @Query("device_token") String deviceToken
    );

    @POST("auth/refresh/")
    Call<SignInResponse> refreshToken(@Body TokenRefreshRequest request);

    @POST("auth/refresh/")
    Single<SignInResponse> refreshTokenSingle(@Body TokenRefreshRequest request);

    @POST("auth/check-username/")
    Observable<CheckUsernameResponse> checkUsername(@Body CheckUsernameRequest request);

    @POST("auth/recover/")
    Observable<RecoverPasswordResponse> recoverPassword(@Body RecoverPasswordRequest request);

    @POST("auth/reset/")
    Single<RecoverPasswordResponse> resetPassword(@Body RecoverPasswordRequest request);

    @POST("auth/change-password/")
    Single<ResponseBody> changePassword(@Body ChangePasswordRequest request);

    @GET("auth/captcha/")
    Observable<CaptchaResponse> getCaptcha();

    @POST("auth/captcha-verify/")
    Observable<CaptchaVerifyResponse> captchaVerify(@Body CaptchaVerifyRequest request);

    @POST("auth/mobile-upgrade/")
    Single<SubscriptionMobileUpgradeResponse> subscriptionUpgrade(
            @Body SubscriptionMobileUpgradeRequest request);

    @Multipart
    @POST("emails/attachments/create/")
    Observable<MessageAttachment> uploadAttachment(
            @Part MultipartBody.Part document,
            @Part("message") long message,
            @Part("is_inline") boolean isInline,
            @Part("is_encrypted") boolean isEncrypted,
            @Part("file_type") String fileType,
            @Part("name") String name,
            @Part("actual_size") long actualSize
    );

    @Multipart
    @PATCH("emails/attachments/update/{id}/")
    Single<MessageAttachment> updateAttachment(
            @Path("id") long id,
            @Part MultipartBody.Part document,
            @Part("message") long message,
            @Part("is_inline") boolean isInline,
            @Part("is_encrypted") boolean isEncrypted,
            @Part("file_type") String fileType,
            @Part("name") String name,
            @Part("actual_size") long actualSize
    );

    @DELETE("emails/attachments/{id}/")
    Observable<Response<Void>> deleteAttachment(@Path("id") long id);

    @GET("emails/messages/")
    Observable<MessagesResponse> getMessages(
            @Query("limit") int limit,
            @Query("offset") int offset,
            @Query("folder") String folder
    );

    @GET("emails/messages/")
    Observable<MessagesResponse> getStarredMessages(
            @Query("limit") int limit,
            @Query("offset") int offset,
            @Query("starred") boolean starred
    );

    @GET("emails/messages/")
    Observable<MessagesResponse> getMessage(@Query("id") long id);

    @DELETE("emails/messages/")
    Observable<Response<Void>> deleteMessages(@Query("id__in") String messageIds);

    @POST("emails/empty-folder/")
    Observable<EmptyFolderResponse> emptyFolder(@Body EmptyFolderRequest request);

    @PATCH("emails/messages/")
    Observable<Response<Void>> toFolder(
            @Query("id__in") String messageIds,
            @Body MoveToFolderRequest request
    );

    @GET("emails/messages/")
    Observable<MessagesResponse> getChainMessages(@Query("id__in") long id);

    @PATCH("emails/messages/")
    Observable<Response<Void>> markMessageAsRead(
            @Query("id__in") String messageIds,
            @Body MarkMessageAsReadRequest request
    );

    @PATCH("emails/messages/")
    Observable<Response<Void>> markMessageIsStarred(
            @Query("id__in") long id,
            @Body MarkMessageIsStarredRequest request
    );

    @GET("search/messages/")
    Observable<MessagesResponse> searchMessages(
            @Query("q") String query,
            @Query("exact") boolean exact,
            @Query("folder") String folder,
            @Query("sender") String sender,
            @Query("receiver") String receiver,
            @Query("have_attachment") boolean haveAttachment,
            @Query("start_date") String startDate, // YYYY-MM-DD
            @Query("end_date") String endDate,
            @Query("size") long size,
            @Query("size_operator") String sizeOperator,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @GET("emails/custom-folder/")
    Observable<FoldersResponse> getFolders(@Query("limit") int limit, @Query("offset") int offset);

    @GET("emails/unread/")
    Observable<ResponseBody> getUnreadFolders();

    @POST("emails/custom-folder/")
    Observable<ResponseBody> addFolder(@Body AddFolderRequest request);

    @DELETE("emails/custom-folder/{id}/")
    Observable<Response<Void>> deleteFolder(@Path("id") long id);

    @PATCH("emails/custom-folder/{id}/")
    Observable<FoldersResult> editFolder(@Path("id") long id, @Body EditFolderRequest request);

    @GET("emails/mailboxes/")
    Observable<MailboxesResponse> getMailboxes(
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @POST("emails/mailboxes/")
    Single<Response<MailboxResponse>> createMailbox(@Body CreateMailboxRequest request);

    @POST("emails/mailboxes-change-primary/")
    Single<Response<Void>> updateMailboxPrimaryKey(@Body UpdateMailboxPrimaryKeyRequest request);

    @GET("emails/mailbox-keys/")
    Single<MailboxKeysResponse> getMailboxKeys(
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @POST("emails/mailbox-keys/")
    Single<Response<MailboxKeyResponse>> createMailboxKey(@Body CreateMailboxKeyRequest request);

    @HTTP(method = "DELETE", path = "emails/mailbox-keys/{id}/", hasBody = true)
    Single<Response<Void>> deleteMailboxKey(@Path("id") long id, @Body DeleteMailboxKeyRequest request);

    @POST("emails/keys/")
    Observable<KeysResponse> getKeys(@Body PublicKeysRequest request);

    @POST("emails/messages/")
    Observable<MessagesResult> sendMessage(@Body SendMessageRequest request);

    @PATCH("emails/messages/{id}/")
    Single<MessagesResult> updateMessage(@Path("id") long id, @Body SendMessageRequest request);

    @PATCH("emails/mailboxes/{id}/")
    Observable<MailboxResponse> updateDefaultMailbox(
            @Path("id") long mailboxId,
            @Body DefaultMailboxRequest body
    );

    @PATCH("emails/mailboxes/{id}/")
    Observable<MailboxResponse> updateEnabledMailbox(
            @Path("id") long mailboxId,
            @Body EnabledMailboxRequest body
    );

    @PATCH("emails/mailboxes/{id}/")
    Observable<MailboxResponse> updateSignature(
            @Path("id") long mailboxId,
            @Body SignatureRequest body
    );

    @GET("emails/domains/")
    Observable<DomainsResponse> getDomains();

    @POST("emails/filter-order/")
    Observable<EmailFilterOrderListResponse> updateEmailFiltersOrder(@Body EmailFilterOrderListRequest request);

    @GET("users/myself/")
    Observable<MyselfResponse> getMyself();

    @PATCH("users/settings/{id}/")
    Observable<ResponseBody> updateSettings(@Path("id") long id, @Body SettingsRequest request);

    @GET("users/contacts/")
    Observable<ContactsResponse> getContacts(
            @Query("limit") int limit,
            @Query("offset") int offset,
            @Query("id__in") String id__in
    );

    @GET("users/contacts/")
    Observable<ContactsResponse> getContacts(
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @GET("users/contacts/")
    Observable<ContactsResponse> getContact(@Query("id") long id);

    @POST("users/contacts/")
    Observable<ContactData> createContact(@Body ContactData contactData);

    @PATCH("users/contacts/{id}/")
    Observable<ContactData> updateContact(@Path("id") long id, @Body ContactData contactData);

    @DELETE("users/contacts/{id}/")
    Observable<ResponseBody> deleteContact(@Path("id") long id);

    @GET("users/filters/")
    Observable<EmailFilterResponse> getFilterList();

    @POST("users/filters/")
    Observable<EmailFilterResult> createFilter(@Body EmailFilterRequest emailFilterRequest);

    @PATCH("users/filters/{id}/")
    Observable<EmailFilterResult> updateFilter(
            @Path("id") long id,
            @Body EmailFilterRequest emailFilterRequest
    );

    @DELETE("users/filters/{id}/")
    Observable<Response<Void>> deleteFilter(@Path("id") long id);

    @DELETE("users/blacklist/{id}/")
    Observable<ResponseBody> deleteBlacklistContact(@Path("id") long id);

    @DELETE("users/whitelist/{id}/")
    Observable<ResponseBody> deleteWhitelistContact(@Path("id") long id);

    @GET("users/blacklist/")
    Observable<BlackListResponse> getBlackListContacts();

    @POST("users/blacklist/")
    Observable<BlackListContact> addBlacklistContact(@Body BlackListContact contact);

    @GET("users/whitelist/")
    Observable<WhiteListResponse> getWhiteListContacts();

    @POST("users/whitelist/")
    Observable<WhiteListContact> addWhitelistContact(@Body WhiteListContact contact);

    @PATCH("users/settings/{id}/")
    Observable<SettingsResponse> updateRecoveryEmail(
            @Path("id") long settingId,
            @Body RecoveryEmailRequest body
    );

    @PATCH("users/settings/{id}/")
    Observable<SettingsResponse> updateNotificationEmail(
            @Path("id") long settingId,
            @Body NotificationEmailRequest body
    );

    @PATCH("users/settings/{id}/")
    Observable<SettingsResponse> updateSubjectEncrypted(
            @Path("id") long settingId,
            @Body SubjectEncryptedRequest body
    );

    @PATCH("users/settings/{id}/")
    Observable<SettingsResponse> updateContactsEncryption(
            @Path("id") long settingId,
            @Body ContactsEncryptionRequest body
    );

    @PATCH("users/settings/{id}/")
    Observable<SettingsResponse> updateAutoSaveEnabled(
            @Path("id") long settingId,
            @Body AutoSaveContactEnabledRequest request
    );

    @PATCH("users/settings/{id}/")
    Observable<SettingsResponse> updateAutoReadEmail(
            @Path("id") long settingId,
            @Body AutoReadEmailRequest request
    );

    @PATCH("users/settings/{id}/")
    Observable<SettingsResponse> updateAntiPhishingPhrase(
            @Path("id") long settingId,
            @Body AntiPhishingPhraseRequest request
    );

    @PATCH("users/settings/{id}/")
    Observable<SettingsResponse> updateDarkMode(
            @Path("id") long settingId,
            @Body DarkModeRequest request
    );

    @PATCH("users/settings/{id}/")
    Observable<SettingsResponse> updateDisableLoadingImages(
            @Path("id") long settingId,
            @Body DisableLoadingImagesRequest request
    );

    @PATCH("users/settings/{id}/")
    Observable<SettingsResponse> updateWarnExternalLink(
            @Path("id") long settingId,
            @Body WarnExternalLinkRequest request
    );

    @PATCH("users/settings/{id}/")
    Observable<SettingsResponse> updateReportBugs(
            @Path("id") long settingId,
            @Body UpdateReportBugsRequest request
    );

    @POST("users/app-token/")
    Observable<AddFirebaseTokenResponse> addFirebaseToken(@Body AddFirebaseTokenRequest request);

    @DELETE("users/app-token/{token}/")
    Observable<Response<Void>> deleteFirebaseToken(@Path("token") String token);
}
