package com.eqmonterozo.capture;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private JsonArray images;

    public ImageAdapter(JsonArray images) {
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String image = images.get(position).toString();
        Glide.with(holder.itemView.getContext()).load(image.substring(1, image.length() - 1).replace(" ","")).into(holder.imgPlaceItem);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgPlaceItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPlaceItem = itemView.findViewById(R.id.imgPlaceItem);

        }
    }


}