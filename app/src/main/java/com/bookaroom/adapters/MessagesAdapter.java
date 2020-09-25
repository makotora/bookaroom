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
import com.bookaroom.models.ConversationMessageResponse;
import com.bookaroom.models.ConversationResponse;
import com.bookaroom.remote.PicassoTrustAll;
import com.bookaroom.utils.RequestUtils;
import com.bookaroom.utils.Utils;
import com.bookaroom.utils.navigation.NavigationUtils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {
    private Activity context;
    private int itemLayoutResource;
    private List<ConversationMessageResponse> conversationsMessages;

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView userName;
        private ImageView userImage;
        private TextView messsage;


        public MessageViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.message_user);
            userImage = itemView.findViewById(R.id.message_user_image);
            messsage = itemView.findViewById(R.id.message_message);
        }
    }

    public MessagesAdapter(
            Activity context,
            int itemLayoutResource,
            List<ConversationMessageResponse> listingShortViews) {
        this.context = context;
        this.itemLayoutResource = itemLayoutResource;
        this.conversationsMessages = listingShortViews;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(
            ViewGroup parent,
            int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(itemLayoutResource,
                                                                     parent,
                                                                     false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            MessageViewHolder vh,
            int position) {
        showListingShortView((MessageViewHolder) vh,
                             position);
    }

    private void showListingShortView(
            MessageViewHolder vh,
            int position
    ) {
        ConversationMessageResponse conversationMessageResponse = conversationsMessages.get(position);

        if (!Utils.isNullOrEmpty(conversationMessageResponse.getUserPicturePath())) {
            PicassoTrustAll.getInstance(context).load(RequestUtils.getUrlForServerFilePath(conversationMessageResponse.getUserPicturePath()))
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(vh.userImage);
        }

        vh.userName.setText(conversationMessageResponse.getUserName());
        vh.messsage.setText(conversationMessageResponse.getMessage());
    }

    @Override
    public int getItemCount() {
        return conversationsMessages == null ? 0 : conversationsMessages.size();
    }

    public synchronized void add(ConversationMessageResponse conversationMessage) {
        conversationsMessages.add(conversationMessage);
        notifyDataSetChanged();
    }

    public synchronized void addAll(List<ConversationMessageResponse> conversationMessages) {
        conversationsMessages.addAll(conversationMessages);
        notifyDataSetChanged();
    }

    public synchronized void clearListingShortViews() {
        conversationsMessages.clear();
        notifyDataSetChanged();
    }

    public synchronized void replaceAllWith(List<ConversationMessageResponse> conversationMessages) {
        clearListingShortViews();
        addAll(conversationMessages);
    }
}
