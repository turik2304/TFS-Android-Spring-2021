package com.turik2304.coursework.data.network

import com.turik2304.coursework.data.network.models.response.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import org.json.JSONArray
import retrofit2.http.*

interface ZulipAPI {

    @GET("users")
    fun getAllUsers(): Observable<GetAllUsersResponse>

    @GET("users/{user_id}")
    fun getUser(@Path("user_id") userId: Int): Single<GetUserResponse>

    @GET("users/{user_email_or_id}/presence")
    fun getUserPresence(@Path("user_email_or_id") emailOrId: String): Observable<GetUserPresenceResponse>

    @GET("users/me")
    fun getOwnProfile(): Observable<GetOwnProfileResponse>

    @GET("users/me/subscriptions")
    fun getSubscribedStreams(): Observable<GetSubscribedStreamsResponse>

    @GET("streams")
    fun getAllStreams(): Observable<GetAllStreamsResponse>

    @GET("users/me/{stream_id}/topics")
    fun getTopics(@Path("stream_id") streamId: Int): Observable<GetTopicsResponse>

    @GET("messages")
    fun getMessages(
        @Query("anchor") anchor: String,
        @Query("num_before") numBefore: Int,
        @Query("num_after") numAfter: Int,
        @Query("narrow") narrow: JSONArray,
        @Query("apply_markdown") applyMarkdown: Boolean = false,
    ): Observable<GetMessagesResponse>

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

    @POST("register")
    fun registerMessageEvents(
        @Query("narrow") narrow: JSONArray,
        @Query("event_types") eventTypes: JSONArray = JSONArray().put("message")
    ): Observable<RegisterEventsResponse>

    @POST("register")
    fun registerReactionEvents(
        @Query("narrow") narrow: JSONArray,
        @Query("event_types") eventTypes: JSONArray = JSONArray().put("reaction")
    ): Observable<RegisterEventsResponse>

    @DELETE("events")
    fun unregisterEvents(
        @Query("queue_id") queueId: String
    ): Completable

    @GET("events")
    fun getMessageEvents(
        @Query("queue_id") queueId: String,
        @Query("last_event_id") lastEventId: String
    ): Observable<GetMessageEventResponse>

    @GET("events")
    fun getReactionEvents(
        @Query("queue_id") queueId: String,
        @Query("last_event_id") lastEventId: String
    ): Observable<GetReactionEventResponse>
}



