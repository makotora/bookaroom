package com.bookaroom.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bookaroom.R;
import com.bookaroom.models.ListingShortViewResponse;
import com.bookaroom.remote.PicassoTrustAll;
import com.bookaroom.utils.RequestUtils;
import com.bookaroom.utils.Utils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;

import java.util.List;

public class ListingShortViewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private int itemLayoutResource;
    private int loadingLayoutResource;
    private List<ListingShortViewResponse> listingShortViews;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public static class ListingShortViewViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView costView;
        private TextView addressView;
        private TextView averageRatingView;


        public ListingShortViewViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.listing_sv_image);
            costView = itemView.findViewById(R.id.listing_sv_cost);
            addressView = itemView.findViewById(R.id.listing_sv_address);
            averageRatingView = itemView.findViewById(R.id.listing_sv_avg_rating);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    public ListingShortViewsAdapter(
            Context context,
            int itemLayoutResource,
            int loadingLayoutResource,
            List<ListingShortViewResponse> listingShortViews) {
        this.context = context;
        this.itemLayoutResource = itemLayoutResource;
        this.loadingLayoutResource = loadingLayoutResource;
        this.listingShortViews = listingShortViews;
    }

    @Override
    public int getItemViewType(int position) {
        return listingShortViews.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            ViewGroup parent,
            int viewType) {
        View view;

        if (viewType == VIEW_TYPE_LOADING) {
            view = LayoutInflater.from(parent.getContext()).inflate(loadingLayoutResource,
                                                                    parent,
                                                                    false);
            return new LoadingViewHolder(view);
        }

        view = LayoutInflater.from(parent.getContext()).inflate(itemLayoutResource,
                                                                parent,
                                                                false);
        return new ListingShortViewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            RecyclerView.ViewHolder vh,
            int position) {
        if (vh instanceof ListingShortViewViewHolder) {

            showListingShortView((ListingShortViewViewHolder) vh, position);
        } else if (vh instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) vh, position);
        }
    }

    private void showListingShortView(
            ListingShortViewViewHolder vh,
            int position
    ) {
        ListingShortViewResponse listingShortView = listingShortViews.get(position);

        PicassoTrustAll.getInstance(context).load(RequestUtils.getUrlForServerFilePath(listingShortView.getMainImagePath()))
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE).into(vh.imageView);
        vh.addressView.setText(listingShortView.getAddress());
        vh.costView.setText(Utils.getDoubleStringOrDefault(listingShortView.getCost(),
                                                           "N/A") +
                                    " " + listingShortView.getCurrency());
        vh.averageRatingView.setText(Utils.getDoubleStringOrDefault(listingShortView.getAverageRating(),
                                                                    "N/A"));

        setOnViewClickListener(vh.itemView,
                               listingShortView);
    }

    @Override
    public int getItemCount() {
        return listingShortViews == null ? 0 : listingShortViews.size();
    }

    private void setOnViewClickListener(
            View parentView,
            ListingShortViewResponse clickedListingShortViewResponse) {

        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long clickedListingId = clickedListingShortViewResponse.getId();
                System.out.println("Listing " + clickedListingId + " was clicked!!");
            }
        });
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        // Just render the loading resource
    }

    public synchronized void addAllListingShortViews(List<ListingShortViewResponse> newListingShortViews) {
        listingShortViews.addAll(newListingShortViews);
        notifyDataSetChanged();
    }

    public synchronized void clearListingShortViews() {
        listingShortViews.clear();
        notifyDataSetChanged();
    }

    public synchronized void replaceListingShortViewsWith(List<ListingShortViewResponse> listingShortViews) {
        clearListingShortViews();
        addAllListingShortViews(listingShortViews);
    }

    public ListingShortViewResponse getLoadingItem() {
        return null;
    }
    public synchronized void addItem(ListingShortViewResponse element) {
        listingShortViews.add(element);
        notifyItemChanged(listingShortViews.size() - 1);
    }

    public synchronized void removeLastItem() {
        listingShortViews.remove(getItemCount() - 1);
        notifyDataSetChanged();
    }
}
