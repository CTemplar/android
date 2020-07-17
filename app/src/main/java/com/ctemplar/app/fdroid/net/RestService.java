package com.ctemplar.app.fdroid.net;

import com.ctemplar.app.fdroid.net.request.AddAppTokenRequest;
import com.ctemplar.app.fdroid.net.request.AddFolderRequest;
import com.ctemplar.app.fdroid.net.request.AntiPhishingPhraseRequest;
import com.ctemplar.app.fdroid.net.request.AutoSaveContactEnabledRequest;
import com.ctemplar.app.fdroid.net.request.CaptchaVerifyRequest;
import com.ctemplar.app.fdroid.net.request.ChangePasswordRequest;
import com.ctemplar.app.fdroid.net.request.CheckUsernameRequest;
import com.ctemplar.app.fdroid.net.request.ContactsEncryptionRequest;
import com.ctemplar.app.fdroid.net.request.CreateMailboxRequest;
import com.ctemplar.app.fdroid.net.request.CustomFilterRequest;
import com.ctemplar.app.fdroid.net.request.DefaultMailboxRequest;
import com.ctemplar.app.fdroid.net.request.EditFolderRequest;
import com.ctemplar.app.fdroid.net.request.EmptyFolderRequest;
import com.ctemplar.app.fdroid.net.request.EnabledMailboxRequest;
import com.ctemplar.app.fdroid.net.request.MarkMessageAsReadRequest;
import com.ctemplar.app.fdroid.net.request.MarkMessageIsStarredRequest;
import com.ctemplar.app.fdroid.net.request.MoveToFolderRequest;
import com.ctemplar.app.fdroid.net.request.PublicKeysRequest;
import com.ctemplar.app.fdroid.net.request.RecoverPasswordRequest;
import com.ctemplar.app.fdroid.net.request.RecoveryEmailRequest;
import com.ctemplar.app.fdroid.net.request.SendMessageRequest;
import com.ctemplar.app.fdroid.net.request.SettingsRequest;
import com.ctemplar.app.fdroid.net.request.SignInRequest;
import com.ctemplar.app.fdroid.net.request.SignUpRequest;
import com.ctemplar.app.fdroid.net.request.SignatureRequest;
import com.ctemplar.app.fdroid.net.request.SubjectEncryptedRequest;
import com.ctemplar.app.fdroid.net.request.TokenRefreshRequest;
import com.ctemplar.app.fdroid.net.response.AddAppTokenResponse;
import com.ctemplar.app.fdroid.net.response.CaptchaResponse;
import com.ctemplar.app.fdroid.net.response.CaptchaVerifyResponse;
import com.ctemplar.app.fdroid.net.response.CheckUsernameResponse;
import com.ctemplar.app.fdroid.net.response.Contacts.ContactData;
import com.ctemplar.app.fdroid.net.response.Contacts.ContactsResponse;
import com.ctemplar.app.fdroid.net.response.Domains.DomainsResponse;
import com.ctemplar.app.fdroid.net.response.Filters.FilterResult;
import com.ctemplar.app.fdroid.net.response.Filters.FiltersResponse;
import com.ctemplar.app.fdroid.net.response.Folders.FoldersResponse;
import com.ctemplar.app.fdroid.net.response.Folders.FoldersResult;
import com.ctemplar.app.fdroid.net.response.KeyResponse;
import com.ctemplar.app.fdroid.net.response.Mailboxes.MailboxesResponse;
import com.ctemplar.app.fdroid.net.response.Mailboxes.MailboxesResult;
import com.ctemplar.app.fdroid.net.response.Messages.EmptyFolderResponse;
import com.ctemplar.app.fdroid.net.response.Messages.MessageAttachment;
import com.ctemplar.app.fdroid.net.response.Messages.MessagesResponse;
import com.ctemplar.app.fdroid.net.response.Messages.MessagesResult;
import com.ctemplar.app.fdroid.net.response.Myself.BlackListContact;
import com.ctemplar.app.fdroid.net.response.Myself.MyselfResponse;
import com.ctemplar.app.fdroid.net.response.Myself.SettingsEntity;
import com.ctemplar.app.fdroid.net.response.Myself.WhiteListContact;
import com.ctemplar.app.fdroid.net.response.RecoverPasswordResponse;
import com.ctemplar.app.fdroid.net.response.SignInResponse;
import com.ctemplar.app.fdroid.net.response.SignUpResponse;
import com.ctemplar.app.fdroid.net.response.WhiteBlackLists.BlackListResponse;
import com.ctemplar.app.fdroid.net.response.WhiteBlackLists.WhiteListResponse;

import javax.inject.Singleton;

import io.reactivex.Observable;
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
    Observable<MessageAttachment> updateAttachment(
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
    Observable<MessagesResult> updateMessage(@Path("id") long id, @Body SendMessageRequest request);

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
    Observable<SettingsEntity> updateRecoveryEmail(
            @Path("id") long settingId,
            @Body RecoveryEmailRequest body
    );

    @PATCH("users/settings/{id}/")
    Observable<SettingsEntity> updateSubjectEncrypted(
            @Path("id") long settingId,
            @Body SubjectEncryptedRequest body
    );

    @PATCH("users/settings/{id}/")
    Observable<SettingsEntity> updateContactsEncryption(
            @Path("id") long settingId,
            @Body ContactsEncryptionRequest body
    );

    @PATCH("users/settings/{id}/")
    Observable<SettingsEntity> updateAutoSaveEnabled(
            @Path("id") long settingId,
            @Body AutoSaveContactEnabledRequest request
    );

    @PATCH("users/settings/{id}/")
    Observable<SettingsEntity> updateAntiPhishingPhrase(
            @Path("id") long settingId,
            @Body AntiPhishingPhraseRequest request
    );

    @POST("users/app-token/")
    Observable<AddAppTokenResponse> addAppToken(@Body AddAppTokenRequest request);

    @DELETE("users/app-token/{token}/")
    Observable<Response<Void>> deleteAppToken(@Path("token") String token);
}
