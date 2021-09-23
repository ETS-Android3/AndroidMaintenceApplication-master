package com.tinu.maintanceappliccation.utility;
/*
 * Created by Ȿ₳Ɲ @ GIZMEON ©  on 07-03-2017.
 */

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.tinu.maintanceappliccation.ApplicationClass;
import com.tinu.maintanceappliccation.R;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;


public class DialogManager {

    public static void showUniActionDialog(Activity parentActivity, String alertMessage,
                                           String positiveText, final IUniActionDialogOnClickListener iUniActionDialogOnClickListener) {

        View dialogView = (CardView) parentActivity.getLayoutInflater().inflate(R.layout.custom_alert_dialog, null, false);


        TextView tv_alert_message = (TextView) dialogView.findViewById(R.id.tv_alert_message);
        Button ok = (Button) dialogView.findViewById(R.id.bt_ok);


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


    public static void showConfirmDialogue(Activity parentActivity, String alertMessage,
                                           final IMultiActionDialogOnClickListener iMultiActionDialogOnClickListener) {

        View dialogView = (CardView) parentActivity.getLayoutInflater().inflate(R.layout.custom_confir_dialomg, null, false);


        TextView tv_alert_message = (TextView) dialogView.findViewById(R.id.tv_alert_message);
        Button ok = (Button) dialogView.findViewById(R.id.bt_ok);
        Button cancel = (Button) dialogView.findViewById(R.id.btn_cancel);


        tv_alert_message.setText(alertMessage);


        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity).setView(dialogView).setCancelable(false);
        final AlertDialog dialog = builder.create();
        if (!((Activity) ApplicationClass.getCurrentActivity()).isFinishing()) {
            dialog.show();
        }


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                iMultiActionDialogOnClickListener.onPositiveClick();


            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                iMultiActionDialogOnClickListener.onNegativeClick();
            }
        });
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(parentActivity.getResources().getColor(R.color.colorAccent));

    }

    public static void showStatusDialogue(Activity parentActivity, String alertMessage,
                                          String taskDetails, String battery, String userEmail, String status, String paymentStatus, String initialCharges, final IUniActionDialogOnClickListener iUniActionDialogOnClickListener) {

        View dialogView = (LinearLayout) parentActivity.getLayoutInflater().inflate(R.layout.custom_alert_status, null, false);


        Button ok = (Button) dialogView.findViewById(R.id.bt_ok);
        TextView tv_taskDetails = (TextView) dialogView.findViewById(R.id.tv_taskDetails);
        TextView tv_baltterypower = (TextView) dialogView.findViewById(R.id.tv_baltterypower);
        TextView tv_userEmail = (TextView) dialogView.findViewById(R.id.tv_userEmail);
        TextView tv_status = (TextView) dialogView.findViewById(R.id.tv_status);
        TextView tv_payment = (TextView) dialogView.findViewById(R.id.tv_payment);
        TextView tv_deviceName = (TextView) dialogView.findViewById(R.id.tv_deviceName);
        ///REMOVE AT PROD

        if (taskDetails.equals("null")) {
            taskDetails = "";
        }
        if (battery.equals("null")) {
            battery = "";
            tv_baltterypower.setVisibility(View.GONE);

        } else {


            Double val = Double.valueOf(battery);
            DecimalFormat twoDForm = new DecimalFormat("#.#");
            Double value = Double.valueOf(twoDForm.format(val));
            tv_baltterypower.setText(ApplicationClass.getCurrentContext().getResources().getString(R.string.battery_power) + ":   " + value);

            if (value == 0.0) {

                tv_baltterypower.setVisibility(View.GONE);

            } else {

                tv_baltterypower.setVisibility(View.VISIBLE);

            }

        }
        if (userEmail.equals("null")) {
            userEmail = "";
        }
        if (status.equals("null")) {
            status = "";
        }


        if (!SharedPreferenceUtility.getIsEnglishLag()) {

            if (taskDetails.equals("battery replacement")) {
                taskDetails = " バッテリー交換";

            }

            if (taskDetails.equals("repair")) {
                taskDetails = "修理";
            }
            if (taskDetails.equals("install")) {
                taskDetails = "導入";

            }
            if (paymentStatus.equals("paid")) {
                paymentStatus = "精算済";
            }
            if (paymentStatus.equals("not paid")) {
                paymentStatus = "未精算";
            }

            if (status.equals("locked")) {
                status = "施錠中";
            }
            if (status.equals("unlocked")) {
                status = "解錠";
            }
        } else {
            if (taskDetails.equals("battery replacement")) {
                taskDetails = "Battery Replacement";
            }

            if (taskDetails.equals("repair")) {
                taskDetails = "Repair";
            }
            if (taskDetails.equals("install")) {
                taskDetails = "Install";

            }
            if (paymentStatus.equals("not paid")) {
                paymentStatus = "Not paid";
            }
            if (status.equals("locked")) {
                status = "Locked";
            }
            if (status.equals("unlocked")) {
                status = "Unlocked";
            }

            if (paymentStatus.equals("paid")) {
                paymentStatus = "Paid";
            }
            if (paymentStatus.equals("not paid")) {
                paymentStatus = "Not Paid";
            }
        }


        if (initialCharges.equalsIgnoreCase("null")) {

            tv_payment.setVisibility(View.GONE);

        } else {

            tv_payment.setVisibility(View.VISIBLE);
        }

        tv_taskDetails.setText(ApplicationClass.getCurrentContext().getResources().getString(R.string.typeOfMain) + ":    " + taskDetails);
        tv_userEmail.setText(ApplicationClass.getCurrentContext().getResources().getString(R.string.userEmail) + ":   " + userEmail);
        tv_status.setText(ApplicationClass.getCurrentContext().getResources().getString(R.string.status) + ":   " + status);
        tv_payment.setText(ApplicationClass.getCurrentContext().getResources().getString(R.string.paymentStatus) + ":   " + paymentStatus);
        tv_deviceName.setText(ApplicationClass.getDeviceName());


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

    public static void showAlertSingleActionDialog(String alertMessage,
                                                   final IUniActionDialogOnClickListener iUniActionDialogOnClickListener) {

        View dialogView = (CardView) ApplicationClass.getCurrentActivity().getLayoutInflater().inflate(R.layout.custom_alert_single_dialog, null, false);


        TextView tv_alert_message = (TextView) dialogView.findViewById(R.id.tv_alert_message);
        Button ok = (Button) dialogView.findViewById(R.id.bt_ok);


        tv_alert_message.setText(alertMessage);


        AlertDialog.Builder builder = new AlertDialog.Builder(ApplicationClass.getCurrentActivity()).setView(dialogView).setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                iUniActionDialogOnClickListener.onPositiveClick();
                dialog.dismiss();


            }
        });
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ApplicationClass.getCurrentActivity().getResources().getColor(R.color.colorAccent));

    }

    public static void showLogoutDialogue(final IMultiActionDialogOnClickListener iMultiActionDialogOnClickListener) {

        View dialogView = (CardView) ApplicationClass.getCurrentActivity().getLayoutInflater().inflate(R.layout.custom_logout_dialogue, null, false);


        TextView tv_alert_message = (TextView) dialogView.findViewById(R.id.tv_alert_message);
        Button ok = (Button) dialogView.findViewById(R.id.bt_ok);
        Button cancel = (Button) dialogView.findViewById(R.id.btn_cancel);


        AlertDialog.Builder builder = new AlertDialog.Builder(ApplicationClass.getCurrentActivity()).setView(dialogView).setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                iMultiActionDialogOnClickListener.onPositiveClick();


            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                iMultiActionDialogOnClickListener.onNegativeClick();
            }
        });
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ApplicationClass.getCurrentActivity().getResources().getColor(R.color.colorAccent));

    }


    public interface IMultiActionDialogOnClickListener {

        void onPositiveClick();

        void onNegativeClick();

    }

}
