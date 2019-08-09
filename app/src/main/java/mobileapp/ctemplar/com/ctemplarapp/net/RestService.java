package mobileapp.ctemplar.com.ctemplarapp.net;

import javax.inject.Singleton;

import io.reactivex.Observable;
import mobileapp.ctemplar.com.ctemplarapp.net.request.AddFolderRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.AutoSaveContactEnabledRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.CaptchaVerifyRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.ChangePasswordRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.CheckUsernameRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.CreateMailboxRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.CustomFilterRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.DefaultMailboxRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.EditFolderRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.EnabledMailboxRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.MarkMessageAsReadRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.MarkMessageIsStarredRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.MoveToFolderRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.PublicKeysRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.RecoverPasswordRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.RecoveryEmailRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SendMessageRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SettingsRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SignInRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SignUpRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SubjectEncryptedRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.CaptchaResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.CaptchaVerifyResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.CheckUsernameResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactData;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactsResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.DeleteAttachmentResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Domains.DomainsResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Filters.FilterResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Filters.FiltersResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Folders.FoldersResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Folders.FoldersResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.KeyResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Mailboxes.MailboxesResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Mailboxes.MailboxesResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessageAttachment;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.UnreadFoldersListResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.BlackListContact;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.MyselfResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.SettingsEntity;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.WhiteListContact;
import mobileapp.ctemplar.com.ctemplarapp.net.response.RecoverPasswordResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.SignInResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.SignUpResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.WhiteBlackLists.BlackListResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.WhiteBlackLists.WhiteListResponse;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
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

    @POST("/auth/sign-in/")
    Observable<SignInResponse> signIn(@Body SignInRequest request);

    @POST("/auth/sign-up/")
    Observable<SignUpResponse> signUp(@Body SignUpRequest request);

    @POST("/auth/check-username/")
    Observable<CheckUsernameResponse> checkUsername(@Body CheckUsernameRequest request);

    @POST("/auth/recover/")
    Observable<RecoverPasswordResponse> recoverPassword(@Body RecoverPasswordRequest request);

    @POST("/auth/reset/")
    Observable<RecoverPasswordResponse> resetPassword(@Body RecoverPasswordRequest request);

    @POST("/auth/change-password/")
    Observable<ResponseBody> changePassword(@Body ChangePasswordRequest request);

    @Multipart
    @POST("/emails/attachments/create/")
    Observable<MessageAttachment> uploadAttachment(@Part MultipartBody.Part document, @Part("message") long message);

    @DELETE("/emails/attachments/{id}/")
    Observable<DeleteAttachmentResponse> deleteAttachment(@Path("id") long id);

    @GET("/emails/messages/")
    Observable<MessagesResponse> getMessages(@Query("limit") int limit, @Query("offset") int offset, @Query("folder") String folder);

    @GET("/emails/messages/")
    Observable<MessagesResponse> getStarredMessages(@Query("limit") int limit, @Query("offset") int offset, @Query("starred") int starred);

    @GET("/emails/messages/")
    Observable<MessagesResponse> getMessage(@Query("id") long id);

    @DELETE("/emails/messages/{id}/")
    Observable<ResponseBody> deleteMessage(@Path("id") long id);

    @DELETE("/emails/messages/")
    Observable<Response<Void>> deleteSeveralMessages(@Query("id__in") String messagesId);

    @PATCH("/emails/messages/")
    Observable<ResponseBody> toFolder(@Query("id__in") long id, @Body MoveToFolderRequest request);

    @GET("/emails/messages/")
    Observable<MessagesResponse> getChainMessages(@Query("id__in") long id);

    @PATCH("/emails/messages/")
    Observable<Response<Void>> markMessageAsRead(@Query("id__in") long id, @Body MarkMessageAsReadRequest request);

    @PATCH("/emails/messages/")
    Observable<Response<Void>> markMessageIsStarred(@Query("id__in") long id, @Body MarkMessageIsStarredRequest request);

    @GET("/emails/custom-folder/")
    Observable<FoldersResponse> getFolders(@Query("limit") int limit, @Query("offset") int offset);

    @GET("/emails/unread/")
    Observable<UnreadFoldersListResponse> getUnreadFolders();

    @POST("/emails/custom-folder/")
    Observable<ResponseBody> addFolder(@Body AddFolderRequest request);

    @DELETE("/emails/custom-folder/{id}/")
    Observable<Response<Void>> deleteFolder(@Path("id") long id);

    @PATCH("/emails/custom-folder/{id}/")
    Observable<FoldersResult> editFolder(@Path("id") long id, @Body EditFolderRequest request);

    @GET("/users/myself/")
    Observable<MyselfResponse> getMyself();

    @GET("/emails/mailboxes/")
    Observable<MailboxesResponse> getMailboxes(@Query("limit") int limit, @Query("offset") int offset);

    @POST("/emails/mailboxes/")
    Observable<Response<MailboxesResult>> createMailbox(@Body CreateMailboxRequest request);

    @POST("/emails/keys/")
    Observable<KeyResponse> getKeys(@Body PublicKeysRequest request);

    @POST("/emails/messages/")
    Observable<MessagesResult> sendMessage(@Body SendMessageRequest request);

    @PATCH("/emails/messages/{id}/")
    Observable<MessagesResult> updateMessage(@Path("id") long id, @Body SendMessageRequest request);

    @PATCH("/users/settings/{id}/")
    Observable<ResponseBody> updateSettings(@Path("id") long id, @Body SettingsRequest request);

    @GET("/users/contacts/")
    Observable<ContactsResponse> getContacts(@Query("limit") int limit, @Query("offset") int offset, @Query("id__in") String id__in);

    @GET("/users/contacts/")
    Observable<ContactsResponse> getContacts(@Query("limit") int limit, @Query("offset") int offset);

    @GET("/users/contacts/")
    Observable<ContactsResponse> getContact(@Query("id") long id);

    @POST("/users/contacts/")
    Observable<ContactData> createContact(@Body ContactData contactData);

    @PATCH("/users/contacts/{id}/")
    Observable<ContactData> updateContact(@Path("id") long id, @Body ContactData contactData);

    // Good if response code is 204 (no content)
    @DELETE("/users/contacts/{id}/")
    Observable<ResponseBody> deleteContact(@Path("id") long id);

    @GET("/users/filters/")
    Observable<FiltersResponse> getFilterList();

    @POST("/users/filters/")
    Observable<FilterResult> createFilter(@Body CustomFilterRequest customFilterRequest);

    @PATCH("/users/filters/{id}/")
    Observable<FilterResult> updateFilter(@Path("id") long id, @Body CustomFilterRequest customFilterRequest);

    @DELETE("/users/filters/{id}/")
    Observable<Response<Void>> deleteFilter(@Path("id") long id);

    @DELETE("/users/blacklist/{id}/")
    Observable<ResponseBody> deleteBlacklistContact(@Path("id") long id);

    @DELETE("/users/whitelist/{id}/")
    Observable<ResponseBody> deleteWhitelistContact(@Path("id") long id);

    @GET("/users/blacklist/")
    Observable<BlackListResponse> getBlackListContacts();

    @POST("/users/blacklist/")
    Observable<BlackListContact> addBlacklistContact(@Body BlackListContact contact);

    @GET("/users/whitelist/")
    Observable<WhiteListResponse> getWhiteListContacts();

    @POST("/users/whitelist/")
    Observable<WhiteListContact> addWhitelistContact(@Body WhiteListContact contact);

    @PATCH("/emails/mailboxes/{id}/")
    Observable<MailboxesResult> updateDefaultMailbox(@Path("id") long mailboxId, @Body DefaultMailboxRequest body);

    @PATCH("/emails/mailboxes/{id}/")
    Observable<MailboxesResult> updateEnabledMailbox(@Path("id") long mailboxId, @Body EnabledMailboxRequest body);

    @GET("/emails/domains/")
    Observable<DomainsResponse> getDomains();

    @PATCH("/users/settings/{id}/")
    Observable<SettingsEntity> updateRecoveryEmail(@Path("id") long settingId, @Body RecoveryEmailRequest body);

    @PATCH("/users/settings/{id}/")
    Observable<SettingsEntity> updateSubjectEncrypted(@Path("id") long settingId, @Body SubjectEncryptedRequest body);

    @PATCH("/users/settings/{id}/")
    Observable<SettingsEntity> updateAutoSaveEnabled(@Path("id") long settingId, @Body AutoSaveContactEnabledRequest request);

    @GET("/auth/captcha/")
    Observable<CaptchaResponse> getCaptcha();

    @POST("/auth/captcha-verify/")
    Observable<CaptchaVerifyResponse> captchaVerify(@Body CaptchaVerifyRequest request);
}
