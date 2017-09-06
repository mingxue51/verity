package com.protovate.verity.service;

import com.protovate.verity.data.responses.Audios;
import com.protovate.verity.data.responses.Count;
import com.protovate.verity.data.responses.Coupon;
import com.protovate.verity.data.responses.Credits;
import com.protovate.verity.data.responses.Info;
import com.protovate.verity.data.responses.Invitations;
import com.protovate.verity.data.responses.Jobs;
import com.protovate.verity.data.responses.LockResponse;
import com.protovate.verity.data.responses.Note;
import com.protovate.verity.data.responses.Notes;
import com.protovate.verity.data.responses.Photos;
import com.protovate.verity.data.responses.StepZero;
import com.protovate.verity.data.responses.Success;
import com.protovate.verity.data.responses.User;
import com.protovate.verity.data.responses.Videos;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
import rx.Observable;

/**
 * Created by Yan on 5/20/15.
 */
public interface ApiClient {
    @FormUrlEncoded
    @POST("/account/check") void checkUserInfo(
            @Field("field") String info,
            @Field("value") String email,
            Callback<Info> callback
    );

    @FormUrlEncoded
    @POST("/account/forgot") void forgotPassword(
            @Field("email") String email,
            Callback<Info> callback
    );

    @FormUrlEncoded
    @POST("/account/login") void login(
            @Field("email") String email,
            @Field("password") String password,
            @Field("push_token") String pushToken,
            @Field("device_type") String deviceType,
            @Field("device_id") String deviceId,
            @Field("model") String deviceModel,
            @Field("version") String deviceVersion,
            Callback<User> callback
    );

    @GET("/account/logout") void logout(
            @Query("access_token") String accessToken,
            Callback<Info> callback
    );

    @FormUrlEncoded
    @PUT("/account/password") void changePassword(
            @Field("old_password") String oldPassword,
            @Field("new_password") String newPassword,
            @Query("access_token") String accessToken,
            Callback<Success> callback
    );

    @Multipart
    @POST("/account/signup") void signup(
            @Part("first_name") String firstName,
            @Part("last_name") String lastName,
            @Part("password") String password,
            @Part("email") String email,
            @Part("photo") TypedFile photo,
            @Part("record_1") TypedFile audio1,
            @Part("record_2") TypedFile audio2,
            @Part("record_3") TypedFile audio3,
            @Part("record_4") TypedFile audio4,
            @Part("record_5") TypedFile audio5,
            @Part("record_6") TypedFile audio6,
            @Part("record_7") TypedFile audio7,
            @Part("record_8") TypedFile audio8,
            @Part("record_9") TypedFile audio9,
            @Part("record_10") TypedFile audio10,
            @Part("push_token") String pushToken,
            @Part("device_type") String deviceType,
            @Part("device_id") String deviceId,
            @Part("model") String deviceModel,
            @Part("version") String deviceVersion,
            Callback<User> callback
    );

    @FormUrlEncoded
    @POST("/account/update") void update(
            @Field("email") String email,
            @Query("access_token") String accessToken,
            Callback<User> callback
    );

    @GET("/me") Observable<User> me(
            @Query("access_token") String accessToken,
            @Query("expand") String expand
    );

    @GET("/credits") void credits(
            @Query("access_token") String accessToken,
            Callback<Credits> callback
    );

    // lock part
    @Multipart
    @POST("/lock") void lock(
            @Part("file_voice") TypedFile voice,
            @Part("file_picture") TypedFile picture,
            @Part("voice_answer") String voiceAnswer,
            @Part("address_line_1") String addressLine1,
            @Part("address_line_2") String addressLine2,
            @Part("city") String city,
            @Part("state") String state,
            @Part("country") String country,
            @Part("zip") String zip,
            @Part("lat") String lat,
            @Part("lng") String lng,
            @Part("notes") String notes,
            @Query("access_token") String accessToken,
            Callback<LockResponse> callback
    );

    @GET("/lock/{id}") Observable<LockResponse> getLock(
            @Path("id") String id,
            @Query("access_token") String accessToken,
            @Query("expand") String expand
    );

    @GET("/lock/{id}/assets") void getVlockNotes(
            @Path("id") String id,
            @Query("type") String type,
            @Query("access_token") String accessToken,
            Callback<Notes> callback
    );

    @GET("/lock/{id}/assets") void getVlockPhotos(
            @Path("id") String id,
            @Query("type") String type,
            @Query("access_token") String accessToken,
            Callback<Photos> callback
    );

    @GET("/lock/{id}/assets") void getVlockVideos(
            @Path("id") String id,
            @Query("type") String type,
            @Query("access_token") String accessToken,
            Callback<Videos> callback
    );

    @GET("/lock/{id}/assets") void getVlockAudios(
            @Path("id") String id,
            @Query("type") String type,
            @Query("access_token") String accessToken,
            Callback<Audios> callback
    );

    @Multipart
    @POST("/lock/{id}/audio") void createVlockAudio(
            @Path("id") String id,
            @Part("audio") TypedFile audio,
            @Query("access_token") String accessToken,
            Callback<User> callback
    );

    @FormUrlEncoded
    @PUT("/lock/{id}/location") void createVlockLocation(
            @Path("id") String id,
            @Field("address_line_1") String addressLine1,
            @Field("address_line_2") String addressLine2,
            @Field("city") String city,
            @Field("state") String state,
            @Field("country") String country,
            @Field("zip") String zip,
            @Field("lat") String lat,
            @Field("lng") String lng,
            @Query("access_token") String accessToken,
            Callback<User> callback
    );

    @FormUrlEncoded
    @POST("/lock/{id}/notes") void createVlockNote(
            @Path("id") String id,
            @Field("notes") String note,
            @Query("access_token") String accessToken,
            Callback<Note> callback
    );

    @Multipart
    @POST("/lock/step1") void step1(
            @Part("file_picture") TypedFile picture,
            @Query("access_token") String accessToken,
            Callback<Info> callback
    );

    @Multipart
    @POST("/account/step2") void step2(
            @Part("photo") TypedFile photo,
            Callback<Info> callback
    );

    @FormUrlEncoded
    @PUT("/lock/{id}/unlock") void unloackVlock(
            @Path("id") String id,
            @Field("address_line_1") String addressLine1,
            @Field("address_line_2") String addressLine2,
            @Field("city") String city,
            @Field("state") String state,
            @Field("country") String country,
            @Field("zip") String zip,
            @Field("lat") String lat,
            @Field("lng") String lng,
            @Query("access_token") String accessToken,
            Callback<User> callback
    );

    @Multipart
    @POST("/lock/{id}/video") void createVlockVideo(
            @Path("id") String id,
            @Part("video") TypedFile video,
            @Part("thumbnail") TypedFile thumbnail,
            @Query("access_token") String accessToken,
            Callback<Success> callback
    );

    @Multipart
    @POST("/lock/{id}/photo") void createVlockPhoto(
            @Path("id") String id,
            @Part("photo") TypedFile photo,
            @Query("access_token") String accessToken,
            Callback<Success> callback
    );

    @GET("/lock/completed") void getAllCompletedLocks(
            @Query("access_token") String accessToken,
            Callback<Count> callback
    );
//
//    @GET("/lock/previous") void getPreviousLocks24Hrs(
//            @Query("expand") String expand,
//            @Query("access_token") String accessToken,
//            Callback<Jobs> callback
//    );

    @GET("/lock/previous") Observable<Jobs> getPreviousLocks24Hrs(
            @Query("expand") String expand,
            @Query("access_token") String accessToken
    );

    @FormUrlEncoded
    @POST("/purchase/coupon") Observable<Coupon> purchaseCreditsWithCoupon(
            @Field("code") String code,
            @Query("access_token") String accessToken
    );

    @GET("/invitation/pending") Observable<Invitations> getPendingInvitations(
            @Query("access_token") String accessToken
    );

    @PUT("/invitation/{id}/accept") Observable<Invitations> acceptInvitation(
            @Path("id") String id,
            @Query("access_token") String accessToken
    );

    @PUT("/invitation/{id}/reject") Observable<Invitations> declineInvitation(
            @Path("id") String id,
            @Query("access_token") String accessToken
    );

    @GET("/invitation/history") Observable<Invitations> getHistoryInvitations(
            @Query("access_token") String accessToken
    );

    @FormUrlEncoded
    @POST("/purchase/cc") void purchaseCreditsWithCard(
            @Field("plan_id") String planId,
            @Field("name") String name,
            @Field("credit_card_number") String creditCardNumber,
            @Field("expiry_month") String expiryMonth,
            @Field("expiry_year") String expiryYear,
            @Field("cvv") String cvv,
            @Query("access_token") String accessToken,
            Callback<Success> callback
    );

    @POST("/lock/step0") Observable<StepZero> step0(
            @Query("access_token") String accessToken
    );

    @FormUrlEncoded
    @POST("/lock/{id}/asset") void delete(
            @Path("id") String id,
            @Field("ids") String ids,
            @Query("access_token") String accessToken,
            Callback<Success> callback
    );

}
