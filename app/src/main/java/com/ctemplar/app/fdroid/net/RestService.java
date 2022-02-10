package com.ctemplar.app.fdroid.net;

import com.ctemplar.app.fdroid.net.request.AddAppTokenRequest;
import com.ctemplar.app.fdroid.net.request.AntiPhishingPhraseRequest;
import com.ctemplar.app.fdroid.net.request.AutoReadEmailRequest;
import com.ctemplar.app.fdroid.net.request.AutoSaveContactEnabledRequest;
import com.ctemplar.app.fdroid.net.request.CaptchaVerifyRequest;
import com.ctemplar.app.fdroid.net.request.ChangePasswordRequest;
import com.ctemplar.app.fdroid.net.request.CheckUsernameRequest;
import com.ctemplar.app.fdroid.net.request.DarkModeRequest;
import com.ctemplar.app.fdroid.net.request.DisableLoadingImagesRequest;
import com.ctemplar.app.fdroid.net.request.IncludeOriginalMessageRequest;
import com.ctemplar.app.fdroid.net.request.NotificationEmailRequest;
import com.ctemplar.app.fdroid.net.request.PublicKeysRequest;
import com.ctemplar.app.fdroid.net.request.RecoverPasswordRequest;
import com.ctemplar.app.fdroid.net.request.RecoveryEmailRequest;
import com.ctemplar.app.fdroid.net.request.SettingsRequest;
import com.ctemplar.app.fdroid.net.request.SignInRequest;
import com.ctemplar.app.fdroid.net.request.SignUpRequest;
import com.ctemplar.app.fdroid.net.request.SignatureRequest;
import com.ctemplar.app.fdroid.net.request.SubjectEncryptedRequest;
import com.ctemplar.app.fdroid.net.request.TokenRefreshRequest;
import com.ctemplar.app.fdroid.net.request.UpdateReportBugsRequest;
import com.ctemplar.app.fdroid.net.request.WarnExternalLinkRequest;
import com.ctemplar.app.fdroid.net.request.contacts.ContactsEncryptionRequest;
import com.ctemplar.app.fdroid.net.request.domains.CreateDomainRequest;
import com.ctemplar.app.fdroid.net.request.domains.UpdateDomainRequest;
import com.ctemplar.app.fdroid.net.request.emails.UnsubscribeMailingRequest;
import com.ctemplar.app.fdroid.net.request.filters.EmailFilterOrderListRequest;
import com.ctemplar.app.fdroid.net.request.filters.EmailFilterRequest;
import com.ctemplar.app.fdroid.net.request.folders.AddFolderRequest;
import com.ctemplar.app.fdroid.net.request.folders.EditFolderRequest;
import com.ctemplar.app.fdroid.net.request.folders.EmptyFolderRequest;
import com.ctemplar.app.fdroid.net.request.folders.MoveToFolderRequest;
import com.ctemplar.app.fdroid.net.request.mailboxes.CreateMailboxKeyRequest;
import com.ctemplar.app.fdroid.net.request.mailboxes.CreateMailboxRequest;
import com.ctemplar.app.fdroid.net.request.mailboxes.DefaultMailboxRequest;
import com.ctemplar.app.fdroid.net.request.mailboxes.DeleteMailboxKeyRequest;
import com.ctemplar.app.fdroid.net.request.mailboxes.EnabledMailboxRequest;
import com.ctemplar.app.fdroid.net.request.mailboxes.UpdateMailboxPrimaryKeyRequest;
import com.ctemplar.app.fdroid.net.request.messages.MarkMessageAsReadRequest;
import com.ctemplar.app.fdroid.net.request.messages.MarkMessageIsStarredRequest;
import com.ctemplar.app.fdroid.net.request.messages.SendMessageRequest;
import com.ctemplar.app.fdroid.net.response.AddAppTokenResponse;
import com.ctemplar.app.fdroid.net.response.CaptchaResponse;
import com.ctemplar.app.fdroid.net.response.CaptchaVerifyResponse;
import com.ctemplar.app.fdroid.net.response.CheckUsernameResponse;
import com.ctemplar.app.fdroid.net.response.PagableResponse;
import com.ctemplar.app.fdroid.net.response.RecoverPasswordResponse;
import com.ctemplar.app.fdroid.net.response.SignInResponse;
import com.ctemplar.app.fdroid.net.response.SignUpResponse;
import com.ctemplar.app.fdroid.net.response.contacts.ContactData;
import com.ctemplar.app.fdroid.net.response.contacts.ContactsResponse;
import com.ctemplar.app.fdroid.net.response.domains.CustomDomainResponse;
import com.ctemplar.app.fdroid.net.response.domains.CustomDomainsResponse;
import com.ctemplar.app.fdroid.net.response.domains.DomainsResponse;
import com.ctemplar.app.fdroid.net.response.filters.EmailFilterOrderListResponse;
import com.ctemplar.app.fdroid.net.response.filters.EmailFilterResponse;
import com.ctemplar.app.fdroid.net.response.filters.EmailFilterResult;
import com.ctemplar.app.fdroid.net.response.folders.FoldersResponse;
import com.ctemplar.app.fdroid.net.response.folders.FoldersResult;
import com.ctemplar.app.fdroid.net.response.invites.InviteCodeResponse;
import com.ctemplar.app.fdroid.net.response.keys.KeysResponse;
import com.ctemplar.app.fdroid.net.response.mailboxes.MailboxKeyResponse;
import com.ctemplar.app.fdroid.net.response.mailboxes.MailboxKeysResponse;
import com.ctemplar.app.fdroid.net.response.mailboxes.MailboxResponse;
import com.ctemplar.app.fdroid.net.response.mailboxes.MailboxesResponse;
import com.ctemplar.app.fdroid.net.response.messages.EmptyFolderResponse;
import com.ctemplar.app.fdroid.net.response.messages.MessageAttachment;
import com.ctemplar.app.fdroid.net.response.messages.MessagesResponse;
import com.ctemplar.app.fdroid.net.response.messages.MessagesResult;
import com.ctemplar.app.fdroid.net.response.myself.BlackListContact;
import com.ctemplar.app.fdroid.net.response.myself.MyselfResponse;
import com.ctemplar.app.fdroid.net.response.myself.SettingsResponse;
import com.ctemplar.app.fdroid.net.response.myself.WhiteListContact;
import com.ctemplar.app.fdroid.net.response.whiteBlackList.BlackListResponse;
import com.ctemplar.app.fdroid.net.response.whiteBlackList.WhiteListResponse;

import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Single;
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

    @GET("emails/domains/")
    Single<CustomDomainsResponse> getCustomDomains();

    @POST("emails/domains/")
    Single<CustomDomainResponse> createCustomDomain(@Body CreateDomainRequest request);

    @GET("domains/verify/{id}")
    Single<CustomDomainResponse> verifyCustomDomain(@Path("id") int id);

    @GET("emails/domains/{id}/")
    Single<CustomDomainResponse> getCustomDomain(@Path("id") int id);

    @PATCH("emails/domains/{id}/")
    Single<CustomDomainResponse> updateCustomDomain(@Path("id") int id, @Body UpdateDomainRequest request);

    @DELETE("emails/domains/{id}/")
    Single<Response<Void>> deleteCustomDomain(@Path("id") int id);

    @POST("/emails/list-unsubscribe/")
    Single<Response<Void>> unsubscribeMailing(@Body UnsubscribeMailingRequest request);

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
    Single<SettingsResponse> updateIncludeOriginalMessage(
            @Path("id") long settingId,
            @Body IncludeOriginalMessageRequest request
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
    Observable<AddAppTokenResponse> addAppToken(@Body AddAppTokenRequest request);

    @DELETE("users/app-token/{token}/")
    Observable<Response<Void>> deleteAppToken(@Path("token") String token);

    @POST("users/invites/")
    Single<InviteCodeResponse> generateInviteCode();

    @GET("users/invites/")
    Single<PagableResponse<InviteCodeResponse>> getInviteCodes(
            @Query("limit") int limit,
            @Query("offset") int offset
    );
}
