package com.tinu.maintanceappliccation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tinu.maintanceappliccation.ApplicationClass;
import com.tinu.maintanceappliccation.R;
import com.tinu.maintanceappliccation.models.HistoryModel;
import com.tinu.maintanceappliccation.models.TaskListModel;
import com.tinu.maintanceappliccation.utility.CommonMethod;
import com.tinu.maintanceappliccation.utility.SharedPreferenceUtility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import androidx.recyclerview.widget.RecyclerView;

public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.ListViewHolder> {

    private Context context;
    List<HistoryModel.Data> historyModelList;
    private CustomOnClickListener customOnClickListener;
    private LayoutInflater inflater = null;
    String lockOrUnlock;
    SimpleDateFormat sdf = null;
    SimpleDateFormat day = null;
    SimpleDateFormat month = null;
    SimpleDateFormat year = null;
    SimpleDateFormat amOrpm = null;
    SimpleDateFormat hh = null;
    SimpleDateFormat mm = null;

    /////


    public class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tv_maintenance_person, tv_baltery_power, tv_task_details, tv_workDetails, tv_date;

        LinearLayout ll_outer;

        public ListViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);


            tv_maintenance_person = (TextView) itemView.findViewById(R.id.tv_maintenance_person);
            // tv_status = (TextView)itemView.findViewById(R.id.tv_status);
            tv_baltery_power = (TextView) itemView.findViewById(R.id.tv_baltery_power);
            tv_task_details = (TextView) itemView.findViewById(R.id.tv_task_details);
            tv_workDetails = (TextView) itemView.findViewById(R.id.tv_workDetails);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            try {
                sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                day = new SimpleDateFormat("d");
                month = new SimpleDateFormat("M");
                year = new SimpleDateFormat("y");
                amOrpm = new SimpleDateFormat("a");
                hh = new SimpleDateFormat("hh");
                mm = new SimpleDateFormat("mm");
            } catch (Exception e) {
                e.printStackTrace();
                sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                day = new SimpleDateFormat("d");
                month = new SimpleDateFormat("M");
                year = new SimpleDateFormat("y");
                amOrpm = new SimpleDateFormat("a");
                hh = new SimpleDateFormat("hh");
                mm = new SimpleDateFormat("mm");
            }

        }

        @Override
        public void onClick(View v) {

        }

    }

    public HistoryRecyclerViewAdapter(Context context, List<HistoryModel.Data> historyModelList, CustomOnClickListener customOnClickListener) {

        this.context = context;
        this.historyModelList = historyModelList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.customOnClickListener = customOnClickListener;


    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        ListViewHolder lrcv = new ListViewHolder(layoutView);
        return lrcv;
    }

    @Override
    public void onBindViewHolder(final ListViewHolder holder, final int position) {
        if (historyModelList.get(position).getMaintenance_person_name() == null) {
            holder.tv_maintenance_person.setVisibility(View.GONE);
        } else {
            if (historyModelList.get(position).getMaintenance_person_name() != null && !historyModelList.get(position).getMaintenance_person_name().isEmpty()) {
                holder.tv_maintenance_person.setVisibility(View.VISIBLE);

            }
        }
        if (historyModelList.get(position).getWork_details() == null) {
            holder.tv_workDetails.setVisibility(View.GONE);
        } else {
            if (historyModelList.get(position).getWork_details() != null && !historyModelList.get(position).getWork_details().isEmpty()) {
                holder.tv_workDetails.setVisibility(View.VISIBLE);

            }
        }
        if (historyModelList.get(position).getTask_details() == null) {
            holder.tv_task_details.setVisibility(View.GONE);
        } else {
            if (historyModelList.get(position).getTask_details() != null && !historyModelList.get(position).getTask_details().isEmpty()) {
                holder.tv_task_details.setVisibility(View.VISIBLE);

            }
        }

        holder.tv_maintenance_person.setText(historyModelList.get(position).getMaintenance_person_name());
        //holder.tv_status.setText(historyModelList.get(position).getDevice_status());
        holder.tv_baltery_power.setText(historyModelList.get(position).getBattery_level());
        holder.tv_task_details.setText(ApplicationClass.getCurrentContext().getResources().getString(R.string.typeOfMain) + ":" + "   " + historyModelList.get(position).getTask_details());
        holder.tv_workDetails.setText(ApplicationClass.getCurrentContext().getResources().getString(R.string.enter_work) + ":" + "   " + historyModelList.get(position).getWork_details());

        try {
            Date date_start = null;
            if (historyModelList.get(position).getUpdated_at()!=null)
            date_start = sdf.parse(historyModelList.get(position).getUpdated_at());
            //SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MMM-d,hh:mm a", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat(ApplicationClass.getCurrentContext().getResources().getString(R.string.paymentDateFormat), Locale.ENGLISH);
            // assuming a timezone in India




            //outputFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
            outputFormat.setTimeZone(TimeZone.getDefault());

            if (date_start!=null)
            {
                if (!SharedPreferenceUtility.getIsEnglishLag())
                {

                    String  dateToStr= CommonMethod.dateFormatInJapanease(month.format(date_start),day.format(date_start),amOrpm.format(date_start),
                            year.format(date_start),hh.format(date_start),mm.format(date_start));
                    holder.tv_date.setText(dateToStr);



                }
                else
                {
                    holder.tv_date.setText(""+outputFormat.format(date_start));

                }

            }

            /////////////

            ////////////
            //System.out.println(outputFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }



    }


    @Override
    public int getItemCount() {
        if (historyModelList != null) {
            if (historyModelList.size() > 0) {
                return historyModelList.size();
            } else {
                return 0;
            }
        }
        return 0;
    }


    public interface CustomOnClickListener {

        void OrderDetails(TaskListModel.Data taskListModel);
    }


}
