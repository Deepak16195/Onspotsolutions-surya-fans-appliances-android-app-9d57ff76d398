package com.surya.onspot.qrscanFragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.surya.onspot.R;
import com.surya.onspot.utils.Utils;

import static com.surya.onspot.R.id.linearLayout_otp_next;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentRewardsOtp1 extends Fragment {

    private View view = null;
    private Activity activity = null;
    private LinearLayout linearLayoutNext = null;
    private Button buttonNext = null;
    private EditText editTextOtpMobileNo = null;

    public FragmentRewardsOtp1(Bundle bundle) {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_rewards_otp1, container, false);
        activity = getActivity();
        initializeView();
        /*linearLayoutNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextOtpMobileNo.getText().toString().trim().length() == 10) {
                    Bundle objBundle = new Bundle();
                    objBundle.putSerializable("OBJECT", "");
                    FragmentRewardsOtp2 fragment = new FragmentRewardsOtp2(objBundle);
                    switchFragment(fragment);
                } else {
                    Utils.showToast(activity, "Please enter valid mobile number");
                }
            }
        });*/

        editTextOtpMobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 10) {
                    view.findViewById(R.id.linearLayout_otp_next).setVisibility(LinearLayout.VISIBLE);
                } else {
                    view.findViewById(R.id.linearLayout_otp_next).setVisibility(LinearLayout.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextOtpMobileNo.getText().toString().trim().length() == 10) {
                    Bundle objBundle = new Bundle();
                    objBundle.putSerializable("OBJECT", "");
                    FragmentRewardsOtp2 fragment = new FragmentRewardsOtp2(objBundle);
                    switchFragment(fragment);
                } else {
                    Utils.showToast(activity, "Please enter valid mobile number");
                }
            }
        });

        return view;
    }

    private void initializeView() {
        linearLayoutNext = view.findViewById(linearLayout_otp_next);
        buttonNext = view.findViewById(R.id.button_otp_next);
        editTextOtpMobileNo = view.findViewById(R.id.editText_otp_mobile_no);
    }

    private void switchFragment(Fragment mTarget) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_root, mTarget)
                .setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
                .addToBackStack(null)
                .commit();
    }
}
