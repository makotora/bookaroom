package com.bookaroom.remote.services;

import com.bookaroom.models.ActionResponse;
import com.bookaroom.models.ConversationCreationResponse;
import com.bookaroom.models.ConversationMessageResponse;
import com.bookaroom.models.ConversationResponse;
import com.bookaroom.models.ListingReviewRequest;
import com.bookaroom.models.ListingReviewResponse;
import com.bookaroom.models.SendMessageRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MessagesService {
    String ENDPOINT_PATH = "/messages";

    @GET(ENDPOINT_PATH + "/getOrCreateConversation")
    Call<ConversationCreationResponse> getOrCreateConversation(
            @Query("userB") Long userB);

    @GET(ENDPOINT_PATH + "/getByUserAAndUserBRole")
    Call<List<ConversationResponse>> getByUserAAndUserBRole(
            @Query("userBRoleString") String userBRoleString);

    @GET(ENDPOINT_PATH + "/getConversationMessages")
    Call<List<ConversationMessageResponse>> getConversationMessages(
            @Query("conversationId") Long conversationId);

    @POST(ENDPOINT_PATH + "/sendMessage")
    Call<ActionResponse> sendMessage(
            @Body
                    SendMessageRequest sendMessageRequest);
}
