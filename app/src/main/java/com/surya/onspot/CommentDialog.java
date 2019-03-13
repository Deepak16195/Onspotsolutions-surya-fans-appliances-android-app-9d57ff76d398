package com.surya.onspot;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.surya.onspot.utils.Utils;

public class CommentDialog extends AlertDialog {

    public OnCommentSubmitClickListener onCommentSubmitClickListener;
    Activity context;
    private EditText editTextComment;
    private TextView btnSubmit, btnTitle, btnUploadImage;
    private Button btnCancelDialog;
    private int MAX_CHARACTER_SIZE = 250;


    protected CommentDialog(@NonNull Activity context) {
        super(context);
        this.context = context;
        initView();
    }

    public void setOnCommentSubmitClickListener(OnCommentSubmitClickListener objListener) {
        this.onCommentSubmitClickListener = objListener;
    }

    private void initView() {

        LayoutInflater objLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = objLayoutInflater.inflate(R.layout.comment_layout, null, false);
//        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        editTextComment = view.findViewById(R.id.editText_comment);
        btnTitle = view.findViewById(R.id.button_title_comment);
        btnSubmit = view.findViewById(R.id.button_submit_comment);
        btnUploadImage = (Button) view.findViewById(R.id.button_image_upload);
        btnCancelDialog = view.findViewById(R.id.button_cancel_comment);

        btnCancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCommentSubmitClickListener.onUploadImageButtonClickListener();
                dismiss();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextComment.getText().toString().trim().length() > 0) {
                    Utils.hideSoftKeyboard(context);
                    onCommentSubmitClickListener.onCommentSubmitClickListener(editTextComment.getText().toString());
                    dismiss();
                } else {
                    Utils.showToast(context, "Please enter comment.");
                }
            }
        });

        editTextComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    btnTitle.setText("Enter Summary (" + (MAX_CHARACTER_SIZE - charSequence.length()) + ")");
                } else {
                    btnTitle.setText("Enter Summary (" + MAX_CHARACTER_SIZE + ")");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        this.setView(view);
    }

    public interface OnCommentSubmitClickListener {
        void onCommentSubmitClickListener(String comment);

        void onUploadImageButtonClickListener();
    }
}