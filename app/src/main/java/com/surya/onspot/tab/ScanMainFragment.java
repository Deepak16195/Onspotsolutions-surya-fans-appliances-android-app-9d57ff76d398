package com.surya.onspot.tab;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.surya.onspot.utils.PagerSlidingTabStrip;
import com.surya.onspot.HomeScreen;
import com.surya.onspot.R;
import com.surya.onspot.utils.LocaleHelper;
import com.surya.onspot.utils.PreferenceKeys;
import com.surya.onspot.utils.Utils;

import java.util.Locale;


public class ScanMainFragment extends Fragment {

    LayoutInflater mInflater1;
    View view;
    Context ctx;
    ViewPager mViewPager1;
    ActionBar actionbar1;
    SectionsPagerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        ctx = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        Context context = LocaleHelper.setLocale(ctx, Utils.getPreference(ctx, PreferenceKeys.KEY_LANGUAGE, ""));
        Resources resources = context.getResources();
        ((HomeScreen) getActivity()).getSupportActionBar().setTitle(resources.getString(R.string.scan_history_title));
        Fragment childFrag = adapter.getRegisteredFragment(mViewPager1.getCurrentItem());
        if (childFrag != null) {
            childFrag.onResume();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.scan_activity_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mViewPager1 = view.findViewById(R.id.pager);
        actionbar1 = getActivity().getActionBar();

        PagerSlidingTabStrip tabs1 = view.findViewById(R.id.tabs);
        tabs1.setTextColorResource(R.color.white);
//		Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MyriadPro-Regular.otf");
        tabs1.setTypeface(null, Typeface.BOLD);
        adapter = new SectionsPagerAdapter(getChildFragmentManager());
        mViewPager1.setAdapter(adapter);
        tabs1.setShouldExpand(true);
        tabs1.setViewPager(mViewPager1);

        Context context = LocaleHelper.setLocale(ctx, Utils.getPreference(ctx, PreferenceKeys.KEY_LANGUAGE, ""));
        Resources resources = context.getResources();
        ((HomeScreen) getActivity()).getSupportActionBar().setTitle(resources.getString(R.string.scan_history_title));

    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ValidQrCodes();
                case 1:
                    return new InValidQrCodes();
            }
            return null;
        }

        @Override
        public int getCount() {
            Context context = LocaleHelper.setLocale(ctx, Utils.getPreference(ctx, PreferenceKeys.KEY_LANGUAGE, ""));
            Resources resources = context.getResources();
            return resources.getStringArray(R.array.settings).length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            Context context = LocaleHelper.setLocale(ctx, Utils.getPreference(ctx, PreferenceKeys.KEY_LANGUAGE, ""));
            Resources resources = context.getResources();
            return resources.getStringArray(R.array.settings)[position].toUpperCase(l);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }

}
