package com.digitalhouse.instapartyapp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.digitalhouse.instapartyapp.R;
import com.digitalhouse.instapartyapp.model.Photos;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewPhotosAdapter extends RecyclerView.Adapter<RecyclerViewPhotosAdapter.ViewHolder> {

    private List<Photos> photosList;

    public RecyclerViewPhotosAdapter(List<Photos> photosList) {
        this.photosList = photosList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recycler_view_photos, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Photos img = photosList.get(i);
        viewHolder.bind(img);
    }

    @Override
    public int getItemCount() {
        return photosList.size();
    }

    public void update(List<Photos> imgs){
        this.photosList = imgs;
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView titulo;
        private ImageView photo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.txtTitulo);
            photo = itemView.findViewById(R.id.txtImagem);
        }

        public void bind(Photos img){
            Picasso.get().load(img.getUrlPhoto()).placeholder(R.drawable.gallery)
                    .resize(500,500).into(photo);
            titulo.setText(img.getTituloPhoto());
        }

    }
}
