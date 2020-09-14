package com.bookaroom.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.bookaroom.R;
import com.bookaroom.utils.dto.SelectedImageInfo;

import java.util.List;

public class SelectedImagesAdapter extends RecyclerView.Adapter<SelectedImagesAdapter.SelectedImageViewHolder> {
    private int resource;
    private List<SelectedImageInfo> selectedImages;

    public static class SelectedImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private Button removeButton;

        public SelectedImageViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.host_listing_picture);
            removeButton = (Button) itemView.findViewById(R.id.host_btn_remove_listing_picture);
        }
    }

    public SelectedImagesAdapter(int resource, List<SelectedImageInfo> selectedImages) {
        this.resource = resource;
        this.selectedImages = selectedImages;
    }

    @Override
    public SelectedImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);

        return new SelectedImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SelectedImageViewHolder vh, int position) {
        SelectedImageInfo selectedImage = selectedImages.get(position);
        vh.imageView.setImageBitmap(selectedImage.getBitmap());

        setupSelfRemoveButton(vh.removeButton, position);
    }

    @Override
    public int getItemCount() {
        return selectedImages.size();
    }

    private void setupSelfRemoveButton(Button removeSelfButton, int position) {
        removeSelfButton.setTag(position);

        removeSelfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                selectedImages.remove(pos);
                notifyDataSetChanged();
            }
        });
    }
}
