package com.bookaroom.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bookaroom.R;
import com.bookaroom.models.ConversationMessageResponse;
import com.bookaroom.models.ListingReviewResponse;
import com.bookaroom.remote.PicassoTrustAll;
import com.bookaroom.utils.RequestUtils;
import com.bookaroom.utils.Utils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MessageViewHolder> {
    private Activity context;
    private int itemLayoutResource;
    private List<ListingReviewResponse> reviews;

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private RatingBar ratingBar;
        private ImageView image;
        private TextView name;
        private TextView comments;


        public MessageViewHolder(View itemView) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.review_rating_bar);
            image = itemView.findViewById(R.id.review_user_image);
            name = itemView.findViewById(R.id.review_user_name);
            comments = itemView.findViewById(R.id.review_comments);
        }
    }

    public ReviewsAdapter(
            Activity context,
            int itemLayoutResource,
            List<ListingReviewResponse> listingShortViews) {
        this.context = context;
        this.itemLayoutResource = itemLayoutResource;
        this.reviews = listingShortViews;
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
        ListingReviewResponse review = reviews.get(position);

        if (!Utils.isNullOrEmpty(review.getUserImagePath())) {
            PicassoTrustAll.getInstance(context).load(RequestUtils.getUrlForServerFilePath(review.getUserImagePath()))
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(vh.image);
        }

        vh.ratingBar.setRating(review.getRating());
        vh.ratingBar.setEnabled(false);
        vh.name.setText(review.getUserName());
        vh.comments.setText(review.getComments());

    }

    @Override
    public int getItemCount() {
        return reviews == null ? 0 : reviews.size();
    }

    public synchronized void add(ListingReviewResponse review) {
        reviews.add(review);
        notifyDataSetChanged();
    }

    public synchronized void addAll(List<ListingReviewResponse> reviews) {
        this.reviews.addAll(reviews);
        notifyDataSetChanged();
    }

    public synchronized void clearListingShortViews() {
        reviews.clear();
        notifyDataSetChanged();
    }

    public synchronized void replaceAllWith(List<ListingReviewResponse> reviews) {
        clearListingShortViews();
        addAll(reviews);
    }
}
