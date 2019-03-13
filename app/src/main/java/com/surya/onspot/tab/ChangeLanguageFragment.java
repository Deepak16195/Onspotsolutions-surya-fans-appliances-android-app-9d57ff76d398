package com.surya.onspot.tab;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.surya.onspot.HomeScreen;
import com.surya.onspot.R;
import com.surya.onspot.intera.ConnectionDetectorActivity;
import com.surya.onspot.utils.LocaleHelper;
import com.surya.onspot.utils.PreferenceKeys;
import com.surya.onspot.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link ChangeLanguageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangeLanguageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Context cntx = null;
    View rootView = null;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ConnectionDetectorActivity cd = null;
    private RadioButton radioButtonEngLang = null;
    private RadioButton radioButtonHinLang = null;

    public ChangeLanguageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChangeLanguageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChangeLanguageFragment newInstance(String param1, String param2) {
        ChangeLanguageFragment fragment = new ChangeLanguageFragment();
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
        rootView = inflater.inflate(R.layout.fragment_change_language, container, false);
        cntx = getActivity();
        initViews();
        cd = new ConnectionDetectorActivity(getActivity());
        return rootView;
    }

    private void initViews() {
        radioButtonEngLang = rootView.findViewById(R.id.radioButton_english);
        radioButtonHinLang = rootView.findViewById(R.id.radioButton_hindi);

        if ("en".equals(Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, ""))) {
            radioButtonEngLang.setChecked(true);
        } else {
            radioButtonHinLang.setChecked(true);
        }

        radioButtonEngLang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    LocaleHelper.setLocale(cntx, "en");
                    Utils.addPreference(cntx, PreferenceKeys.KEY_LANGUAGE, "en");
                    ((HomeScreen) getActivity()).refreshDrawer();
                    FragmentReceivedHistoryBase parent = (FragmentReceivedHistoryBase) ChangeLanguageFragment.this.getParentFragment();
                    parent.refreshTabs();
                }
            }
        });

        radioButtonHinLang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    LocaleHelper.setLocale(cntx, "hi");
                    Utils.addPreference(cntx, PreferenceKeys.KEY_LANGUAGE, "hi");
                    ((HomeScreen) getActivity()).refreshDrawer();
                    FragmentReceivedHistoryBase parent = (FragmentReceivedHistoryBase) ChangeLanguageFragment.this.getParentFragment();
                    parent.refreshTabs();
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void fragmentBecameVisible() {

    }
}
