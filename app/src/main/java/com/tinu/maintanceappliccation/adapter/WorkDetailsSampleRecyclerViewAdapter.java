package com.tinu.maintanceappliccation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.tinu.maintanceappliccation.R;
import com.tinu.maintanceappliccation.models.WorkDetailsModel;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class WorkDetailsSampleRecyclerViewAdapter extends RecyclerView.Adapter<WorkDetailsSampleRecyclerViewAdapter.ListViewHolder> {

    private Context context;
    private CustomOnClickListener customOnClickListener;
    private LayoutInflater inflater = null;
    List<WorkDetailsModel> workDetailsModelList;

    public class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        CheckBox checkbox;
        TextView tvContent;

        public ListViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);

        }

        @Override
        public void onClick(View v) {

        }

    }

    public WorkDetailsSampleRecyclerViewAdapter(Context context, List<WorkDetailsModel> workDetailsModelList, WorkDetailsSampleRecyclerViewAdapter.CustomOnClickListener customOnClickListener) {

        this.context = context;
        this.workDetailsModelList = workDetailsModelList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.customOnClickListener = customOnClickListener;


    }

    @Override
    public WorkDetailsSampleRecyclerViewAdapter.ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_workdetails, parent, false);
        WorkDetailsSampleRecyclerViewAdapter.ListViewHolder lrcv = new WorkDetailsSampleRecyclerViewAdapter.ListViewHolder(layoutView);
        return lrcv;
    }

    @Override
    public void onBindViewHolder(final WorkDetailsSampleRecyclerViewAdapter.ListViewHolder holder, final int position) {

        holder.tvContent.setText(workDetailsModelList.get(position).getName());

        if(workDetailsModelList.get(position).getId()!=null){

            if (workDetailsModelList.get(position).getId().equals("1")) {
                holder.checkbox.setChecked(true);

            } else {
                holder.checkbox.setChecked(false);
            }
        }

        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                       @Override
                                                       public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                           if (isChecked) {
                                                               workDetailsModelList.get(position).setId("1");
                                                               customOnClickListener.updateClick(isChecked,workDetailsModelList.get(position));
                                                           }
                                                           else {
                                                               workDetailsModelList.get(position).setId("0");
                                                               customOnClickListener.updateClick(isChecked,workDetailsModelList.get(position));

                                                           }

                                                       }
                                                   }
        );


    }


    @Override
    public int getItemCount() {
        if (workDetailsModelList != null) {
            if (workDetailsModelList.size() > 0) {
                return workDetailsModelList.size();
            } else {
                return 0;
            }
        }
        return 0;
    }


    public interface CustomOnClickListener {

        void updateClick(boolean isChecked, WorkDetailsModel workDetailsModel);


    }

}

