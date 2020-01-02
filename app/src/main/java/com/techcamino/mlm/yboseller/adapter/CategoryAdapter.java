package com.techcamino.mlm.yboseller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.techcamino.mlm.yboseller.R;
import com.techcamino.mlm.yboseller.details.CategoryDetails;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MemberView> {

    private List<CategoryDetails> categoryDetailsList;
    private ArrayList<CategoryDetails> selecttedList = new ArrayList<>();
    private Context context;
    private boolean check = true;


    @NonNull
    @Override
    public MemberView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_item_list, viewGroup, false);

        return new MemberView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MemberView unclearedView, final int i) {

        unclearedView.name.setText(categoryDetailsList.get(i).getCategoryName());
        unclearedView.checkBox.setOnCheckedChangeListener(null);
        unclearedView.checkBox.setChecked(categoryDetailsList.get(i).isChecked());
        unclearedView.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(unclearedView.checkBox.isChecked()){
                    selecttedList.add(categoryDetailsList.get(i));
                    categoryDetailsList.get(i).setChecked(true);
                }else {
                    selecttedList.remove(categoryDetailsList.get(i));
                    categoryDetailsList.get(i).setChecked(false);
                }
            }
        });

        unclearedView.categoryList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = (unclearedView.checkBox.isChecked()) ? false : true ;
                unclearedView.checkBox.setChecked(check);
            }
        });


    }

    public ArrayList<CategoryDetails> getCategorySelected(){
        return selecttedList;
    }

    @Override
    public int getItemCount() {
        return categoryDetailsList.size();
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

    public CategoryAdapter(List<CategoryDetails> categoryDetailsList, Context context) {
        this.categoryDetailsList = categoryDetailsList;
        this.context = context;
    }


}
