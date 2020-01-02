package com.techcamino.mlm.yboseller.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.techcamino.mlm.yboseller.R;
import com.techcamino.mlm.yboseller.details.FilterDetails;

import java.util.ArrayList;
import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.MemberView> {

    private List<FilterDetails> filterDetailsList;
    private Context context;
    private LayoutInflater inflter;
    private CustomDialogListener mViewClickListener;
    private int cardP;
    int index = -1;
    private boolean clicked = false;

    public FilterAdapter(Context applicationContext, ArrayList<FilterDetails> filterDetailsList) {
        this.context = applicationContext;
        this.filterDetailsList = filterDetailsList;
        inflter = (LayoutInflater.from(applicationContext));
        this.mViewClickListener = (CustomDialogListener) context;
    }

    public interface CustomDialogListener {
        void onItemClicked(FilterDetails filterDetails);
    }

    @NonNull
    @Override
    public MemberView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_items, viewGroup, false);
        Log.d("In view holder"," Inini");
        return new MemberView(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final MemberView unclearedView, final int i) {


        unclearedView.name.setText(filterDetailsList.get(i).getGridName());

        unclearedView.mainCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = i;
                notifyDataSetChanged();
                mViewClickListener.onItemClicked(filterDetailsList.get(i));
            }
        });

        if(index==i){
            unclearedView.mainCard.setCardBackgroundColor(context.getResources().getColor(R.color.red));
            unclearedView.name.setTextColor(context.getResources().getColor(R.color.white));
        }else{
            unclearedView.mainCard.setCardBackgroundColor(context.getResources().getColor(R.color.white));
            unclearedView.name.setTextColor(context.getResources().getColor(R.color.black));
        }
    }


    @Override
    public int getItemCount() {
        Log.d("In item count"," Inini");
        return filterDetailsList.size();
    }



    public class MemberView extends RecyclerView.ViewHolder {

        public TextView name;
        public CardView mainCard;

        public MemberView(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.grid_name);
            mainCard = (CardView) view.findViewById(R.id.cardview);
        }

        /*public void bind(final DashboardGridDetails item, final CustomDialogListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClicked(item);
                }
            });
        }*/
    }


}
