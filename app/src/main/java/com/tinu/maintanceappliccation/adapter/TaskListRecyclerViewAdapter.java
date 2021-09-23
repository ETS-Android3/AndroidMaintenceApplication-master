package com.tinu.maintanceappliccation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.tinu.maintanceappliccation.ApplicationClass;
import com.tinu.maintanceappliccation.R;
import com.tinu.maintanceappliccation.models.TaskListModel;
import com.tinu.maintanceappliccation.utility.CommonMethod;
import com.tinu.maintanceappliccation.utility.SharedPreferenceUtility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import androidx.recyclerview.widget.RecyclerView;

public class TaskListRecyclerViewAdapter extends RecyclerView.Adapter<TaskListRecyclerViewAdapter.ListViewHolder> {

    private Context context;
    List<TaskListModel.Data> taskModelList;
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

    public class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tv_date,tv_device_name,tv_Location,tv_message,tv_landmark,tv_type;

        LinearLayout ll_outer;

        public ListViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            tv_date =(TextView) itemView.findViewById(R.id.tv_date);
            tv_device_name = (TextView) itemView.findViewById(R.id.tv_device_name);
            tv_Location =(TextView)itemView.findViewById(R.id.tv_Location);
            tv_message =(TextView)itemView.findViewById(R.id.tv_message);
            ll_outer = (LinearLayout)itemView.findViewById(R.id.ll_outer);
            tv_landmark = (TextView)itemView.findViewById(R.id.tv_landmark);
            tv_type = (TextView) itemView.findViewById(R.id.tv_type);


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

    public TaskListRecyclerViewAdapter(Context context, List<TaskListModel.Data> taskModelList, CustomOnClickListener customOnClickListener) {

        this.context = context;
        this.taskModelList = taskModelList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.customOnClickListener=customOnClickListener;


    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_error_notification, parent, false);
        ListViewHolder lrcv = new ListViewHolder(layoutView);
        return lrcv;
    }

    @Override
    public void onBindViewHolder(final ListViewHolder holder, final int position) {

       // holder.tv_baltery_power.setText(taskModelList.get(position).getBaltteryPower());
        holder.tv_device_name.setText(taskModelList.get(position).getDevice_name());
        holder.tv_message.setText(taskModelList.get(position).getTask_details());
        holder.tv_landmark.setText(taskModelList.get(position).getLandmark());
        holder.tv_Location.setText(taskModelList.get(position).getLocation_address());
        if (!SharedPreferenceUtility.getIsEnglishLag())
        {
            if (taskModelList.get(position).getType().equalsIgnoreCase("inspection work"))
            {
                holder.tv_type.setText(ApplicationClass.getCurrentContext().getResources().getString(R.string.repair));
            }
            else if (taskModelList.get(position).getType().equalsIgnoreCase("install")){
                holder.tv_type.setText("導入");

            }
            else if (taskModelList.get(position).getType().equalsIgnoreCase("battery replacement")){
                holder.tv_type.setText(" バッテリー交換");

            }
            else if (taskModelList.get(position).getType().equalsIgnoreCase("device replacement")){
                holder.tv_type.setText(context.getResources().getString(R.string.device_replacment));

            }
            else if (taskModelList.get(position).getType().equalsIgnoreCase("periodic inspection")){
                holder.tv_type.setText(context.getResources().getString(R.string.periodic_inspection));

            }
        }
        else {
           // holder.tv_type.setText(taskModelList.get(position).getType());
            if (taskModelList.get(position).getType().equalsIgnoreCase("battery replacement")){
                holder.tv_type.setText("Battery Replacement");

            }
            if (taskModelList.get(position).getType().equalsIgnoreCase("device replacement")){
                holder.tv_type.setText("Device Replacement");

            }
            if (taskModelList.get(position).getType().equalsIgnoreCase("install")){
                holder.tv_type.setText("Install");

            }
            if (taskModelList.get(position).getType().equalsIgnoreCase("periodic inspection")){
                holder.tv_type.setText("Periodic Inspection");

            }
            if (taskModelList.get(position).getType().equalsIgnoreCase("inspection work")){
                holder.tv_type.setText("Inspection Work");

            }
        }


        try {
            Date date_start;
            date_start = sdf.parse(taskModelList.get(position).getCreated_at());
            //SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MMM-d,hh:mm a", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat(ApplicationClass.getCurrentContext().getResources().getString(R.string.paymentDateFormat), Locale.ENGLISH);
            // assuming a timezone in India




            //outputFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
            outputFormat.setTimeZone(TimeZone.getDefault());

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

            /////////////

            ////////////
            //System.out.println(outputFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }




        holder.ll_outer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                customOnClickListener .OrderDetails(taskModelList.get(position));

            }
        });


    }


    @Override
    public int getItemCount() {
        if (taskModelList !=null)
        {
            if (taskModelList.size()>0)
            {
                return taskModelList.size();
            }
            else
            {
                return 0;
            }
        }
        return 0;
    }



    public interface CustomOnClickListener {

        void OrderDetails(TaskListModel.Data taskListModel);
    }


}
