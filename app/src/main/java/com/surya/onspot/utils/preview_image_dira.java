package com.surya.onspot.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.surya.onspot.R;


/**
 * Created by student on 08/06/17.
 */

class ScannedHistoryDialog extends AlertDialog {

    private Activity context;
    //       Main Components.
    private View view;

    private String title = "Title";
    private String number = "#123456789";
    private String tag = "inputTag";

    //    Callback listener.
    private DialogActionClickListener dialogActionClickListener;

    public ScannedHistoryDialog(@NonNull Context context, String tag) {
        super(context);
        this.context = (Activity) context;
        this.tag = tag;
        initView();
    }

    public ScannedHistoryDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.context = (Activity) context;
        initView();

    }


    public ScannedHistoryDialog(@NonNull Context context, @StyleRes int themeResId, String tag) {
        super(context, themeResId);
        this.context = (Activity) context;
        this.tag = tag;
        initView();

    }

    public ScannedHistoryDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = (Activity) context;
    }

    private void initView() {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.layout_scanned_dialog, null, false);
        setListener();
        this.setView(view);
    }

    private void setListener() {

    }

    @Override
    public void show() {
        super.show();
//        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public DialogActionClickListener getDialogActionClickListener() {
        return dialogActionClickListener;
    }

    public void setDialogActionClickListener(DialogActionClickListener dialogActionClickListener) {
        this.dialogActionClickListener = dialogActionClickListener;
    }

    public interface DialogActionClickListener {
        void onActionPerformed(Bundle bundle, String tag);
    }

    public class UrlNotFoundException extends Exception {
        String s1;

        UrlNotFoundException(String s2) {
            s1 = s2;
        }

        @Override
        public String toString() {
            return ("Error : " + s1);
        }

        @Override
        public void printStackTrace() {
            super.printStackTrace();
        }
    }
}