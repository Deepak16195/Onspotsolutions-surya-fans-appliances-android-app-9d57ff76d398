package com.surya.onspot.qrscanFragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.surya.onspot.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentRewardsOtp2 extends Fragment {

    View view = null;
    private Activity activity = null;

    private EditText editTextOtpOne, editTextOtpTwo, editTextOtpThree, editTextOtpFour;


    public FragmentRewardsOtp2(Bundle bundle) {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_rewards_otp2, container, false);
        initializeView();
        return view;
    }

    private void initializeView() {
        editTextOtpOne = view.findViewById(R.id.editText_otp_one);
        editTextOtpTwo = view.findViewById(R.id.editText_otp_two);
        editTextOtpThree = view.findViewById(R.id.editText_otp_three);
        editTextOtpFour = view.findViewById(R.id.editText_otp_four);

        editTextOtpOne.requestFocus();

        editTextOtpOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    editTextOtpTwo.requestFocus();
                } else {
                    view.findViewById(R.id.linearLayout_otp_verify).setVisibility(LinearLayout.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextOtpTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    editTextOtpThree.requestFocus();
                } else {
                    view.findViewById(R.id.linearLayout_otp_verify).setVisibility(LinearLayout.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextOtpThree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    editTextOtpFour.requestFocus();
                } else {
                    view.findViewById(R.id.linearLayout_otp_verify).setVisibility(LinearLayout.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextOtpFour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editTextOtpOne.getText().toString().length() > 0 &&
                        editTextOtpTwo.getText().toString().length() > 0 &&
                        editTextOtpThree.getText().toString().length() > 0 &&
                        s.toString().trim().length() > 0) {
                    view.findViewById(R.id.linearLayout_otp_verify).setVisibility(LinearLayout.VISIBLE);
                } else {
                    view.findViewById(R.id.linearLayout_otp_verify).setVisibility(LinearLayout.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

}
