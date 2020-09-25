package com.bookaroom.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bookaroom.R;
import com.bookaroom.models.ConversationResponse;
import com.bookaroom.remote.PicassoTrustAll;
import com.bookaroom.utils.RequestUtils;
import com.bookaroom.utils.navigation.NavigationUtils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;

import java.util.List;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ConversationViewHolder> {
    private Activity context;
    private int itemLayoutResource;
    private List<ConversationResponse> conversationsMessages;

    public static class ConversationViewHolder extends RecyclerView.ViewHolder {
        private TextView userBName;
        private ImageView userBImage;
        private TextView lastMessageUserName;
        private TextView lastMessage;


        public ConversationViewHolder(View itemView) {
            super(itemView);
            userBName = itemView.findViewById(R.id.conversation_userBName);
            userBImage = itemView.findViewById(R.id.conversation_userBImage);
            lastMessageUserName = itemView.findViewById(R.id.conversation_lastMessageUserName);
            lastMessage = itemView.findViewById(R.id.conversation_lastMessage);
        }
    }

    public ConversationsAdapter(
            Activity context,
            int itemLayoutResource,
            List<ConversationResponse> listingShortViews) {
        this.context = context;
        this.itemLayoutResource = itemLayoutResource;
        this.conversationsMessages = listingShortViews;
    }

    @Override
    public ConversationViewHolder onCreateViewHolder(
            ViewGroup parent,
            int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(itemLayoutResource,
                                                                     parent,
                                                                     false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            ConversationViewHolder vh,
            int position) {
        showListingShortView((ConversationViewHolder) vh,
                             position);
    }

    private void showListingShortView(
            ConversationViewHolder vh,
            int position
    ) {
        ConversationResponse conversationResponse = conversationsMessages.get(position);

        PicassoTrustAll.getInstance(context).load(RequestUtils.getUrlForServerFilePath(conversationResponse.getUserBPicturePath()))
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE).into(vh.userBImage);

        vh.userBName.setText(conversationResponse.getUserBName());
        vh.lastMessageUserName.setText(conversationResponse.getLastMessageUserName());
        vh.lastMessage.setText(conversationResponse.getLastMessage());

        setOnViewClickListener(vh.itemView, conversationResponse);
    }

    @Override
    public int getItemCount() {
        return conversationsMessages == null ? 0 : conversationsMessages.size();
    }

    private void setOnViewClickListener(
            View parentView,
            ConversationResponse clickedConversationResponse) {

        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long clickedConversationId = clickedConversationResponse.getConversationId();

                NavigationUtils.startMessagesActivity(context, clickedConversationId);
            }
        });
    }

    public synchronized void addAll(List<ConversationResponse> conversationResponses) {
        conversationsMessages.addAll(conversationResponses);
        notifyDataSetChanged();
    }

    public synchronized void clearListingShortViews() {
        conversationsMessages.clear();
        notifyDataSetChanged();
    }

    public synchronized void replaceAllWith(List<ConversationResponse> conversationResponses) {
        clearListingShortViews();
        addAll(conversationResponses);
    }
}
