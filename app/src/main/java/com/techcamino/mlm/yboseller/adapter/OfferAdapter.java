package com.techcamino.mlm.yboseller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.techcamino.mlm.yboseller.R;
import com.techcamino.mlm.yboseller.details.OfferDetails;

import java.util.List;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.MemberView> {

    private List<OfferDetails> offerDetailsList;
    private Context context;


    @NonNull
    @Override
    public MemberView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.offer_item_list, viewGroup, false);

        return new MemberView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberView unclearedView, final int i) {

        unclearedView.name.setText(offerDetailsList.get(i).getName());
        unclearedView.desc.setText(offerDetailsList.get(i).getDescription());
        unclearedView.offer.setText(offerDetailsList.get(i).getOfferValue());
        Glide.with(context).load(context.getResources().getDrawable(R.drawable.logo)).into(unclearedView.offerImage);

    }

    @Override
    public int getItemCount() {
        return offerDetailsList.size();
    }

    public class MemberView extends RecyclerView.ViewHolder {

        public TextView name,desc,offer;
        public ImageView offerImage;
        public RelativeLayout action;

        public MemberView(View view) {
            super(view);

            name = view.findViewById(R.id.name);
            desc = view.findViewById(R.id.desc);
            offer =  view.findViewById(R.id.offer);
            offerImage = view.findViewById(R.id.offer_image);

        }
    }

    public OfferAdapter(List<OfferDetails> offerDetailsList, Context context) {
        this.offerDetailsList = offerDetailsList;
        this.context = context;
    }


}
