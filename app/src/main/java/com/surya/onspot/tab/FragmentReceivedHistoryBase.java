package com.surya.onspot.tab;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.surya.onspot.utils.PagerSlidingTabStrip;
import com.surya.onspot.HomeScreen;
import com.surya.onspot.R;
import com.surya.onspot.qrscanFragments.FragmentCompleted;
import com.surya.onspot.qrscanFragments.FragmentPending;
import com.surya.onspot.utils.LocaleHelper;
import com.surya.onspot.utils.PreferenceKeys;
import com.surya.onspot.utils.Utils;

import java.util.Locale;

public class FragmentReceivedHistoryBase extends Fragment {

    LayoutInflater mInflater;
    View view;
    Context ctx;
    ViewPager mViewPager;
    ActionBar actionbar;
    SectionsPagerAdapter adapter;
    PagerSlidingTabStrip tabs;
    private Resources resources;

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
        resources = context.getResources();
        ((HomeScreen) getActivity()).getSupportActionBar().setTitle(resources.getString(R.string.received_history_page_title));
        ((HomeScreen) getActivity()).refreshDrawer();
    }

    public void refreshTabs() {
        Utils.out("LANGUAGE TAB : " + Utils.getPreference(ctx, PreferenceKeys.KEY_LANGUAGE, "en"));
        Context context = LocaleHelper.setLocale(ctx, Utils.getPreference(ctx, PreferenceKeys.KEY_LANGUAGE, "en"));
        resources = context.getResources();
        ((HomeScreen) getActivity()).getSupportActionBar().setTitle(resources.getString(R.string.received_history_page_title));
        Locale l = Locale.getDefault();
        LinearLayout mTabsLinearLayout = ((LinearLayout) tabs.getChildAt(0));
        for (int index = 0; index < mTabsLinearLayout.getChildCount(); index++) {
            if (mTabsLinearLayout != null) {
                TextView tv = (TextView) mTabsLinearLayout.getChildAt(index);
                tv.setText(resources.getStringArray(R.array.received_tabs)[index].toUpperCase(l));
            }
        }
        Utils.out("TAB REFRESHED");
    }

    private void updateMenuTitles(Menu menu) {
        Context context = LocaleHelper.setLocale(ctx, Utils.getPreference(ctx, PreferenceKeys.KEY_LANGUAGE, ""));
        resources = context.getResources();
        MenuItem settings = menu.findItem(R.id.action_settings);
        settings.setTitle(resources.getString(R.string.action_settings));
        MenuItem exit = menu.findItem(R.id.action_exit);
        exit.setTitle(resources.getString(R.string.action_exit));
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        updateMenuTitles(menu);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pager_activity_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewPager = view.findViewById(R.id.pager);
        actionbar = getActivity().getActionBar();

        tabs = view.findViewById(R.id.tabs);
        tabs.setTextColorResource(R.color.white);
//		Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MyriadPro-Regular.otf");
        tabs.setTypeface(null, Typeface.BOLD);
        tabs.setShouldExpand(true);
        adapter = new SectionsPagerAdapter(getChildFragmentManager());
        mViewPager.setOffscreenPageLimit(0);
        mViewPager.setAdapter(adapter);
        tabs.setViewPager(mViewPager);

        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                Utils.out("PAGE SELECTED " + position);
                switch (position) {
                    case 0:
                        FragmentCompleted fragment = (FragmentCompleted) adapter.instantiateItem(mViewPager, 0);
                        if (fragment != null) {
                            fragment.fragmentBecameVisible();
                        }
                        break;
                    case 1:
                        FragmentPending fragmentEmail = (FragmentPending) adapter.instantiateItem(mViewPager, 1);
                        if (fragmentEmail != null) {
                            fragmentEmail.fragmentBecameVisible();
                        }
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void onBackStackChanged() {
        getFragmentManager().popBackStack("HomeActivity", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            FragmentManager manager = ((Fragment) object).getFragmentManager();
            FragmentTransaction trans = manager.beginTransaction();
            trans.remove((Fragment) object);
            trans.commit();
            super.destroyItem(container, position, object);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FragmentCompleted();
                case 1:
                    return new FragmentPending();
            }
            return null;
        }

        @Override
        public int getCount() {
            Context context = LocaleHelper.setLocale(ctx, Utils.getPreference(ctx, PreferenceKeys.KEY_LANGUAGE, ""));
            Resources resources = context.getResources();
            return resources.getStringArray(R.array.received_tabs).length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            Context context = LocaleHelper.setLocale(ctx, Utils.getPreference(ctx, PreferenceKeys.KEY_LANGUAGE, ""));
            Resources resources = context.getResources();
            return resources.getStringArray(R.array.received_tabs)[position].toUpperCase(l);
        }
    }
}
