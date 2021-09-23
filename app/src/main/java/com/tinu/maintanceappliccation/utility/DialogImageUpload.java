package com.tinu.maintanceappliccation.utility;

/*
 * Created by Ȿ₳Ɲ @ GIZMEON ©  on 07-03-2017.
 */

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.tinu.maintanceappliccation.R;

import androidx.appcompat.app.AlertDialog;


public class DialogImageUpload {

    public static void showUniActionDialog(Activity parentActivity, String alertMessage,
                                           String positiveText, final IUniActionDialogOnClickListener iUniActionDialogOnClickListener) {

   View dialogView = (LinearLayout) parentActivity.getLayoutInflater().inflate(R.layout.custom_alert_image_uploading, null, false);



    TextView tv_alert_message = (TextView) dialogView.findViewById(R.id.tv_alert_message);
    Button ok=(Button)dialogView.findViewById(R.id.bt_ok);



    tv_alert_message.setText(alertMessage);




    AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity).setView(dialogView).setCancelable(false);
    final AlertDialog dialog = builder.create();
    dialog.show();


    ok.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {



                iUniActionDialogOnClickListener.onPositiveClick();
                dialog.dismiss();




        }
    });
    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(parentActivity.getResources().getColor(R.color.colorAccent));

}




public interface IUniActionDialogOnClickListener {

    void onPositiveClick();

}

public interface IMultiActionDialogOnClickListener {

    void onPositiveClick();

    void onNegativeClick();

}

}
