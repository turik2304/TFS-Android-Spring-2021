package com.turik2304.coursework.network

import com.turik2304.coursework.network.calls.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

interface ZulipAPI {

    @GET("users")
    fun getAllUsers(): Single<GetAllUsersResponse>

    @GET("users/{user_id}")
    fun getUser(@Path("user_id") userId: Int): Single<GetUserResponse>

    @GET("users/me")
    fun getOwnProfile(): Single<GetOwnProfileResponse>

    @GET("users/me/subscriptions")
    fun getSubscribedStreams(): Single<GetSubscribedResponse>

    @GET("streams")
    fun getAllStreams(): Single<GetAllStreamsResponse>

    @GET("users/me/{stream_id}/topics")
    fun getTopics(@Path("stream_id") streamId: Int): Single<GetTopicsResponse>

    @GET("messages")
    fun getMessages(
        @Query("anchor") anchor: String,
        @Query("num_before") numBefore: Int,
        @Query("num_after") numAfter: Int,
        @Query("narrow") narrow: String,
        @Query("apply_markdown") applyMarkdown: Boolean = false,
    ): Single<GetMessagesResponse>

    @POST("messages")
    fun sendMessage(
        @Query("type") type: String = "stream",
        @Query("to") nameOfStream: String,
        @Query("topic") nameOfTopic: String,
        @Query("content") message: String
    ): Completable

    @POST("messages/{message_id}/reactions")
    fun sendReaction(
        @Path("message_id") messageId: Int,
        @Query("emoji_name") emojiName: String,
        @Query("emoji_code") emojiCode: String,
    ): Completable

    @DELETE("messages/{message_id}/reactions")
    fun removeReaction(
        @Path("message_id") messageId: Int,
        @Query("emoji_name") emojiName: String,
        @Query("emoji_code") emojiCode: String,
    ): Completable
}