package com.bookaroom.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class ListingShortViewsAdapter extends RecyclerView.Adapter<ListingShortViewsAdapter.ListingShortViewViewHolder> {
    private Context context;
    private int resource;
    private List<ListingShortViewResponse> listingShortViews;

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

    public ListingShortViewsAdapter(
            Context context, int resource,
            List<ListingShortViewResponse> listingShortViews) {
        this.context = context;
        this.resource = resource;
        this.listingShortViews = listingShortViews;
    }

    @Override
    public ListingShortViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);

        return new ListingShortViewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListingShortViewViewHolder vh, int position) {
        ListingShortViewResponse listingShortView = listingShortViews.get(position);

        PicassoTrustAll.getInstance(context).load(RequestUtils.getUrlForServerFilePath(listingShortView.getMainImagePath()))
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE).into(vh.imageView);
        vh.addressView.setText(listingShortView.getAddress());
        vh.costView.setText(Utils.getDoubleStringOrDefault(listingShortView.getCost(), "N/A") +
                                    " " + listingShortView.getCurrency());
        vh.averageRatingView.setText(Utils.getDoubleStringOrDefault(listingShortView.getAverageRating(), "N/A"));

        setOnViewClickListener(vh.itemView, listingShortView);
    }

    @Override
    public int getItemCount() {
        return listingShortViews.size();
    }

    private void setOnViewClickListener(View parentView,
                                        ListingShortViewResponse clickedListingShortViewResponse) {

        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long clickedListingId = clickedListingShortViewResponse.getId();
                System.out.println("Listing " + clickedListingId + " was clicked!!");
            }
        });
    }

    public synchronized void addListingShortView(ListingShortViewResponse listingShortView) {
        listingShortViews.add(listingShortView);
        notifyDataSetChanged();
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
}
