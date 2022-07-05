package com.eqmonterozo.capture;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.File;
import java.util.ArrayList;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    private ArrayList images, placeName, description, latitude, longitude, address, timestamp, addedBy;
    private Boolean isFromSearch;

    public PlaceAdapter(ArrayList images, ArrayList placeName, ArrayList description, ArrayList latitude, ArrayList longitude, ArrayList address, ArrayList timestamp, ArrayList addedBy, Boolean isFromSearch) {
        this.images = images;
        this.placeName = placeName;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.timestamp = timestamp;
        this.addedBy = addedBy;
        this.isFromSearch = isFromSearch;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.txtPlaceName.setText(placeName.get(position).toString());
        holder.txtDescription.setText(description.get(position).toString());

        JsonArray outputJsonArray = JsonParser.parseString(String.valueOf(images.get(position))).getAsJsonArray();

        if (isFromSearch) {
            String firstImage = outputJsonArray.get(0).toString();

            Glide.with(holder.itemView.getContext()).load(firstImage.substring(1, firstImage.length() - 1)).into(holder.imgPlace);

            holder.cardPlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(holder.itemView.getContext(), PlaceDetailsActivity.class);
                    intent.putExtra("images", String.valueOf(images.get(position)));
                    intent.putExtra("placeName", String.valueOf(placeName.get(position)));
                    intent.putExtra("description", String.valueOf(description.get(position)));
                    intent.putExtra("latitude", String.valueOf(latitude.get(position)));
                    intent.putExtra("longitude", String.valueOf(longitude.get(position)));
                    intent.putExtra("address", String.valueOf(address.get(position)));
                    intent.putExtra("timestamp", String.valueOf(timestamp.get(position)));
                    intent.putExtra("addedBy", String.valueOf(addedBy.get(position)));
                    holder.itemView.getContext().startActivity(intent);

                }
            });
        } else {
            System.out.println(outputJsonArray.get(0));
            File file = new File(outputJsonArray.get(0).toString().replace("\"", ""));
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                holder.imgPlace.setImageBitmap(bitmap);
            }
        }

    }

    @Override
    public int getItemCount() {
        return placeName.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtPlaceName, txtDescription;
        private ImageView imgPlace;
        private MaterialCardView cardPlace;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtPlaceName = itemView.findViewById(R.id.txtPlaceName);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            imgPlace = itemView.findViewById(R.id.imgPlace);
            cardPlace = itemView.findViewById(R.id.cardPlace);

        }
    }


}