package com.bookaroom.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.bookaroom.R;
import com.bookaroom.adapters.ConversationsAdapter;
import com.bookaroom.enums.UserRole;
import com.bookaroom.enums.ViewMode;
import com.bookaroom.models.ConversationResponse;
import com.bookaroom.remote.ApiUtils;
import com.bookaroom.remote.services.MessagesService;
import com.bookaroom.utils.Utils;
import com.bookaroom.utils.navigation.NavigationUtils;
import com.bookaroom.utils.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationsActivity extends AppCompatActivity {

    private MessagesService messagesService;
    
    private RecyclerView conversationsRecyclerView;
    private ConversationsAdapter conversationsAdapter;
    private List<ConversationResponse> conversations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        NavigationUtils.initializeBottomNavigationBar(this);
        
        messagesService = ApiUtils.getMessagesService(this);
        
        initializeConversationsView();
    }

    private void initializeConversationsView() {
        conversations = new ArrayList<>();
        conversationsAdapter = new ConversationsAdapter(this, R.layout.conversation, conversations);

        conversationsRecyclerView = findViewById(R.id.conversation_list_layout);
        conversationsRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                                                                           LinearLayoutManager.VERTICAL,
                                                                           false));
        conversationsRecyclerView.setAdapter(conversationsAdapter);

        initializeConversationsList();
    }

    private void initializeConversationsList() {
        ViewMode viewMode = SessionManager.getViewMode(this);

        UserRole bRole;
        if (viewMode == ViewMode.Guest) {
            bRole = UserRole.Host;
        }
        else {
            bRole = UserRole.Guest;;
        }

        Call call = messagesService.getByUserAAndUserBRole(bRole.name());
        call.enqueue(new Callback<List<ConversationResponse>>() {
            @Override
            public void onResponse(
                    Call<List<ConversationResponse>> call,
                    Response<List<ConversationResponse>> response) {
                handleConversationsResponses(response.body());
            }

            @Override
            public void onFailure(
                    Call call,
                    Throwable t) {
                Utils.makeInternalErrorToast(ConversationsActivity.this);
            }
        });
    }

    private void handleConversationsResponses(List<ConversationResponse> conversationResponses) {
        conversationsAdapter.addAll(conversationResponses);
    }
}