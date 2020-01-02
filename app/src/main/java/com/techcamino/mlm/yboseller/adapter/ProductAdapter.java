package com.techcamino.mlm.yboseller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.techcamino.mlm.yboseller.R;
import com.techcamino.mlm.yboseller.details.ProductDetails;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MemberView> {

    private List<ProductDetails> productDetailsList;
    private Context context;
    private boolean check = true;
    private CustomDialogListener mViewClickListener;

    public interface CustomDialogListener {
        void onItemClicked(ProductDetails productDetails);
        void onRemoveClicked(ProductDetails productDetails);
    }

    @NonNull
    @Override
    public MemberView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_item_list, viewGroup, false);

        return new MemberView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MemberView unclearedView, final int i) {

        unclearedView.name.setText(productDetailsList.get(i).getProductName());

        unclearedView.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewClickListener.onRemoveClicked(productDetailsList.get(i));
            }
        });

        unclearedView.cardMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewClickListener.onItemClicked(productDetailsList.get(i));
            }
        });

    }

    @Override
    public int getItemCount() {
        return productDetailsList.size();
    }

    public class MemberView extends RecyclerView.ViewHolder {

        public TextView name;
        public CheckBox checkBox;
        public LinearLayout categoryList;
        public CardView cardMain;
        public ImageView delete;

        public MemberView(View view) {
            super(view);

            name = view.findViewById(R.id.name);
            checkBox = view.findViewById(R.id.category_check);
            categoryList = view.findViewById(R.id.category_layout);
            delete = view.findViewById(R.id.remove);
            cardMain = view.findViewById(R.id.cardview);

        }
    }

    public ProductAdapter(List<ProductDetails> productDetails, Context context) {
        this.productDetailsList = productDetails;
        this.context = context;
        this.mViewClickListener = (CustomDialogListener) context;
    }


}
