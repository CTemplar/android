package mobileapp.ctemplar.com.ctemplarapp.net;

import javax.inject.Singleton;

import io.reactivex.Observable;
import mobileapp.ctemplar.com.ctemplarapp.net.request.CheckUsernameRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.RecoverPasswordRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SendMessageRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SignInRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SignUpRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.CheckUsernameResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Mailboxes.MailboxesResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.RecoverPasswordResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.SignInResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.SignUpResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.myself.MyselfResponse;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
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

    @GET("/emails/messages/")
    Observable<MessagesResponse> getMessages(@Query("limit") int limit, @Query("offset") int offset, @Query("folder") String folder);

    @GET("/emails/messages/")
    Observable<MessagesResponse> getMessage(@Query("id") long id);

    @GET("/users/myself/")
    Observable<MyselfResponse> getMyself();

    @GET("/emails/mailboxes/")
    Observable<MailboxesResponse> getMailboxes(@Query("limit") int limit, @Query("offset") int offset);

    @POST("/emails/messages/")
    Observable<MessagesResult> sendMessage(@Body SendMessageRequest request);

}
