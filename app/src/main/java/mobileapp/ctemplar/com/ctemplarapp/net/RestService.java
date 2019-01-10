package mobileapp.ctemplar.com.ctemplarapp.net;

import javax.inject.Singleton;

import io.reactivex.Observable;
import mobileapp.ctemplar.com.ctemplarapp.net.request.ChangePasswordRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.CheckUsernameRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.MarkMessageAsReadRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.MarkMessageIsStarredRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.MoveToFolderRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.PublicKeysRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.RecoverPasswordRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SendMessageRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SignInRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SignUpRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.CheckUsernameResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactData;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactsResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Folders.FoldersResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.KeyResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Mailboxes.MailboxesResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.RecoverPasswordResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.SignInResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.SignUpResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.myself.MyselfResponse;
import mobileapp.ctemplar.com.ctemplarapp.settings.ChangePasswordActivity;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
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

    @GET("/emails/messages/")
    Observable<MessagesResponse> getMessages(@Query("limit") int limit, @Query("offset") int offset, @Query("folder") String folder);

    @GET("/emails/messages/")
    Observable<MessagesResponse> getStarredMessages(@Query("limit") int limit, @Query("offset") int offset, @Query("starred") int starred);

    @GET("/emails/messages/")
    Observable<MessagesResponse> getMessage(@Query("id") long id);

    @DELETE("/emails/messages/{id}/")
    Observable<ResponseBody> deleteMessage(@Path("id") long id);

    @PATCH("/emails/messages/")
    Observable<ResponseBody> toFolder(@Query("id__in") long id, @Body MoveToFolderRequest request);

    @GET("/emails/messages/")
    Observable<MessagesResponse> getChainMessages(@Query("id__in") long id);

    @PATCH("/emails/messages/{id}/")
    Observable<MessagesResult> markMessageAsRead(@Path("id") long id, @Body MarkMessageAsReadRequest request);

    @PATCH("/emails/messages/{id}/")
    Observable<MessagesResult> markMessageIsStarred(@Path("id") long id, @Body MarkMessageIsStarredRequest request);

    @GET("/emails/custom-folder/")
    Observable<FoldersResponse> getFolders(@Query("limit") int limit, @Query("offset") int offset);

    @GET("/users/myself/")
    Observable<MyselfResponse> getMyself();

    @GET("/emails/mailboxes/")
    Observable<MailboxesResponse> getMailboxes(@Query("limit") int limit, @Query("offset") int offset);

    @POST("/emails/keys/")
    Observable<KeyResponse> getKeys(@Body PublicKeysRequest request);

    @POST("/emails/messages/")
    Observable<MessagesResult> sendMessage(@Body SendMessageRequest request);

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

}
