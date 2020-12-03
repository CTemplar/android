package mobileapp.ctemplar.com.ctemplarapp.net;

import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Single;
import mobileapp.ctemplar.com.ctemplarapp.net.request.AddFirebaseTokenRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.AddFolderRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.AntiPhishingPhraseRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.AutoSaveContactEnabledRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.CaptchaVerifyRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.ChangePasswordRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.CheckUsernameRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.ContactsEncryptionRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.CreateMailboxRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.CustomFilterRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.DarkModeRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.DefaultMailboxRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.DisableLoadingImagesRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.EditFolderRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.EmptyFolderRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.EnabledMailboxRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.MarkMessageAsReadRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.MarkMessageIsStarredRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.MoveToFolderRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.NotificationEmailRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.PublicKeysRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.RecoverPasswordRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.RecoveryEmailRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SendMessageRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SettingsRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SignInRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SignUpRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SignatureRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SubjectEncryptedRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.TokenRefreshRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.UpdateReportBugsRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.AddFirebaseTokenResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.CaptchaResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.CaptchaVerifyResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.CheckUsernameResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactData;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactsResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Domains.DomainsResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Filters.FilterResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Filters.FiltersResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Folders.FoldersResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Folders.FoldersResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.KeyResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Mailboxes.MailboxesResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Mailboxes.MailboxesResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.EmptyFolderResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessageAttachment;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.BlackListContact;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.MyselfResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.SettingsResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.WhiteListContact;
import mobileapp.ctemplar.com.ctemplarapp.net.response.RecoverPasswordResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.SignInResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.SignUpResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.WhiteBlackLists.BlackListResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.WhiteBlackLists.WhiteListResponse;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
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
    Observable<SignUpResponse> signUp(@Body SignUpRequest request);

    @GET("auth/sign-out/")
    Observable<Response<Void>> signOut(
            @Query("platform") String platform,
            @Query("device_token") String deviceToken
    );

    @POST("auth/refresh/")
    Call<SignInResponse> refreshToken(@Body TokenRefreshRequest request);

    @POST("auth/check-username/")
    Observable<CheckUsernameResponse> checkUsername(@Body CheckUsernameRequest request);

    @POST("auth/recover/")
    Observable<RecoverPasswordResponse> recoverPassword(@Body RecoverPasswordRequest request);

    @POST("auth/reset/")
    Observable<RecoverPasswordResponse> resetPassword(@Body RecoverPasswordRequest request);

    @POST("auth/change-password/")
    Observable<ResponseBody> changePassword(@Body ChangePasswordRequest request);

    @GET("auth/captcha/")
    Observable<CaptchaResponse> getCaptcha();

    @POST("auth/captcha-verify/")
    Observable<CaptchaVerifyResponse> captchaVerify(@Body CaptchaVerifyRequest request);

    @Multipart
    @POST("emails/attachments/create/")
    Observable<MessageAttachment> uploadAttachment(
            @Part MultipartBody.Part document,
            @Part("message") long message,
            @Part("is_encrypted") boolean isEncrypted
    );

    @Multipart
    @PATCH("emails/attachments/update/{id}/")
    Single<MessageAttachment> updateAttachment(
            @Path("id") long id,
            @Part MultipartBody.Part document,
            @Part("message") long message,
            @Part("is_encrypted") boolean isEncrypted
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

    @GET("search/messages/")
    Observable<MessagesResponse> searchMessages(
            @Query("q") String query,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @GET("emails/messages/")
    Observable<MessagesResponse> getMessage(@Query("id") long id);

    @DELETE("emails/messages/")
    Observable<Response<Void>> deleteMessages(@Query("id__in") String messageIds);

    @POST("emails/empty-folder/")
    Observable<EmptyFolderResponse> emptyFolder(@Body EmptyFolderRequest request);

    @PATCH("emails/messages/")
    Observable<Response<Void>> toFolder(
            @Query("id__in") long id,
            @Body MoveToFolderRequest request
    );

    @GET("emails/messages/")
    Observable<MessagesResponse> getChainMessages(@Query("id__in") long id);

    @PATCH("emails/messages/")
    Observable<Response<Void>> markMessageAsRead(
            @Query("id__in") long id,
            @Body MarkMessageAsReadRequest request
    );

    @PATCH("emails/messages/")
    Observable<Response<Void>> markMessageIsStarred(
            @Query("id__in") long id,
            @Body MarkMessageIsStarredRequest request
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
    Observable<Response<MailboxesResult>> createMailbox(@Body CreateMailboxRequest request);

    @POST("emails/keys/")
    Observable<KeyResponse> getKeys(@Body PublicKeysRequest request);

    @POST("emails/messages/")
    Observable<MessagesResult> sendMessage(@Body SendMessageRequest request);

    @PATCH("emails/messages/{id}/")
    Single<MessagesResult> updateMessage(@Path("id") long id, @Body SendMessageRequest request);

    @PATCH("emails/mailboxes/{id}/")
    Observable<MailboxesResult> updateDefaultMailbox(
            @Path("id") long mailboxId,
            @Body DefaultMailboxRequest body
    );

    @PATCH("emails/mailboxes/{id}/")
    Observable<MailboxesResult> updateEnabledMailbox(
            @Path("id") long mailboxId,
            @Body EnabledMailboxRequest body
    );

    @PATCH("emails/mailboxes/{id}/")
    Observable<MailboxesResult> updateSignature(
            @Path("id") long mailboxId,
            @Body SignatureRequest body
    );

    @GET("emails/domains/")
    Observable<DomainsResponse> getDomains();

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
    Observable<FiltersResponse> getFilterList();

    @POST("users/filters/")
    Observable<FilterResult> createFilter(@Body CustomFilterRequest customFilterRequest);

    @PATCH("users/filters/{id}/")
    Observable<FilterResult> updateFilter(
            @Path("id") long id,
            @Body CustomFilterRequest customFilterRequest
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
    Observable<SettingsResponse> updateReportBugs(
            @Path("id") long settingId,
            @Body UpdateReportBugsRequest request
    );

    @POST("users/app-token/")
    Observable<AddFirebaseTokenResponse> addFirebaseToken(@Body AddFirebaseTokenRequest request);

    @DELETE("users/app-token/{token}/")
    Observable<Response<Void>> deleteFirebaseToken(@Path("token") String token);
}
