package com.techcamino.mlm.yboseller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.techcamino.mlm.yboseller.R;
import com.techcamino.mlm.yboseller.details.ShopDetails;

import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.MemberView> {

    private List<ShopDetails> shopDetailsList;
    private Context context;
    private boolean check = true;


    @NonNull
    @Override
    public MemberView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shop_item_list, viewGroup, false);

        return new MemberView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MemberView unclearedView, final int i) {

        unclearedView.name.setText(shopDetailsList.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return shopDetailsList.size();
    }

    public class MemberView extends RecyclerView.ViewHolder {

        public TextView name;
        public CheckBox checkBox;
        public LinearLayout categoryList;

        public MemberView(View view) {
            super(view);

            name = view.findViewById(R.id.name);
            checkBox = view.findViewById(R.id.category_check);
            categoryList = view.findViewById(R.id.category_layout);

        }
    }

    public ShopAdapter(List<ShopDetails> shopDetailsList, Context context) {
        this.shopDetailsList = shopDetailsList;
        this.context = context;
    }


}
