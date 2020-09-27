package com.bookaroom.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bookaroom.R;
import com.bookaroom.adapters.MessagesAdapter;
import com.bookaroom.models.ActionResponse;
import com.bookaroom.models.ConversationMessageResponse;
import com.bookaroom.models.SendMessageRequest;
import com.bookaroom.remote.ApiUtils;
import com.bookaroom.remote.services.MessagesService;
import com.bookaroom.utils.ResponseUtils;
import com.bookaroom.utils.Utils;
import com.bookaroom.utils.navigation.NavigationUtils;
import com.bookaroom.utils.session.SessionManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagesActivity extends AppCompatActivity {

    public static final String INTENT_EXTRA_CONVERSATION_ID_NAME = "CONV_ID";
    public static final long DEFAULT_CONVERSATION_ID = -1;

    private Long conversationId;

    private MessagesService messagesService;

    private EditText edtSendMessage;
    private Button sendMessageButton;

    private RecyclerView messagesRecyclerView;
    private MessagesAdapter messagesAdapter;
    private List<ConversationMessageResponse> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        initializeIntentData();

        NavigationUtils.initializeBottomNavigationBar(this);

        messagesService = ApiUtils.getMessagesService(this);

        initializeSendMessageView();
        initializeMessagesView();
    }

    private void initializeIntentData() {
        Intent intent = getIntent();
        conversationId = intent.getLongExtra(INTENT_EXTRA_CONVERSATION_ID_NAME,
                                        DEFAULT_CONVERSATION_ID);
        if (conversationId == DEFAULT_CONVERSATION_ID) {
            Utils.makeInternalErrorToast(this);
            NavigationUtils.startHomeActivity(this);
        }
    }

    private void initializeSendMessageView() {
        edtSendMessage = findViewById(R.id.messages_edt_message);
        sendMessageButton = findViewById(R.id.message_send_message);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        final String message = Utils.getEditTextString(edtSendMessage);
        Call call = messagesService.sendMessage(new SendMessageRequest(conversationId,
                                                                       message));
        call.enqueue(new Callback<ActionResponse>() {
            @Override
            public void onResponse(
                    Call<ActionResponse> call,
                    Response<ActionResponse> response) {
                ResponseUtils.handleActionResponse(MessagesActivity.this, response,
                                                   (ar) -> handleMessageSent(message), (ar) -> {});
            }

            @Override
            public void onFailure(
                    Call call,
                    Throwable t) {

            }
        });
    }

    private void handleMessageSent(String message) {
        edtSendMessage.setText("");
        initializeMessagesList();
    }

    private void initializeMessagesView() {
        messages = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(this, R.layout.message,
                                              messages);

        messagesRecyclerView = findViewById(R.id.messages_list_layout);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                                                                      LinearLayoutManager.VERTICAL,
                                                                      false));
        messagesRecyclerView.setAdapter(messagesAdapter);

        initializeMessagesList();
    }

    private void initializeMessagesList() {
        Call call = messagesService.getConversationMessages(conversationId);
        call.enqueue(new Callback<List<ConversationMessageResponse>>() {
            @Override
            public void onResponse(
                    Call<List<ConversationMessageResponse>> call,
                    Response<List<ConversationMessageResponse>> response) {
                handleMessagesResponses(response.body());
            }

            @Override
            public void onFailure(
                    Call call,
                    Throwable t) {
                t.printStackTrace();
                Utils.makeInternalErrorToast(MessagesActivity.this);
            }
        });
    }

    private void handleMessagesResponses(List<ConversationMessageResponse> conversationResponses) {
        messagesAdapter.replaceAllWith(conversationResponses);
    }
}