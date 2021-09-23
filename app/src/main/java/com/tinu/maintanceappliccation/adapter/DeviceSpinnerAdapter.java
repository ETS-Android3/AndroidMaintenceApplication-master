package com.tinu.maintanceappliccation.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.tinu.maintanceappliccation.R;

import java.util.List;

/**
 * Created by gizmeon on 28/6/17.
 */

public class DeviceSpinnerAdapter extends BaseAdapter {
    private Context context;
    //private List<CashAgent.CashAgentList> cashAgentLists;
    private List<String> cashAgentLists;
    LayoutInflater inflter;
    public DeviceSpinnerAdapter(Context context, List<String> idTypeModelList) {


        this.context = context;
        this.cashAgentLists = idTypeModelList;
        inflter = (LayoutInflater.from(context));
    }
    @Override
    public int getCount() {
        if (cashAgentLists!=null)
            return cashAgentLists.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = inflter.inflate(R.layout.spinner_item, null);
        //ImageView icon = (ImageView) view.findViewById(R.id.imageView);
        TextView names = (TextView) convertView.findViewById(R.id.tv_id_type);

        names.setText(cashAgentLists.get(position));
        return convertView;



    }

}
