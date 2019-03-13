package com.surya.onspot.qrscanFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.surya.onspot.HomeScreen;
import com.surya.onspot.R;
import com.surya.onspot.model.RewardsDashboardModel;

public class FragmentRewardList1 extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    RewardsDashboardModel object = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view = null;
    private Context cntx = null;

    public FragmentRewardList1() {

    }

    // TODO: Rename and change types and number of parameters
    public static FragmentRewardList1 newInstance(String param1, String param2) {
        FragmentRewardList1 fragment = new FragmentRewardList1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_reward_list1, container, false);

        cntx = getActivity();

        return view;
    }

    private void pushToNextScreen(int optionNumber) {
        switch (optionNumber) {
            case 1:
                switchFragment(new FragmentRedeemDetailScreen());
                break;
            case 2:
                switchFragment(new FragmentEarnedDetailScreen());
                break;
            case 3:
                switchFragment(new FragmentRedemptionDetailScreen());
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        object = ((HomeScreen) getActivity()).getRedeemData();

        if (object != null) {
            if (object.getImageUrl().length() > 0) {
                Picasso.with(getActivity()).load(object.getImageUrl()).into(((ImageView) view.findViewById(R.id.imageView_rewards_dashboard_image)));
            }
            ((TextView) view.findViewById(R.id.textView_rewards_dashboard_title)).setText(object.getTitle());
            ((TextView) view.findViewById(R.id.textView_rewards_dashboard_count)).setText(object.getTitleCount());

            view.findViewById(R.id.linearLayout_redeem).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pushToNextScreen(1);
                }
            });

            view.findViewById(R.id.linearLayout_earned_history).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pushToNextScreen(2);
                }
            });

            view.findViewById(R.id.linearLayout_redemption_history).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pushToNextScreen(3);
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void switchFragment(Fragment mTarget) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_root, mTarget)
                .setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
                .addToBackStack(null)
                .commit();
    }

}
