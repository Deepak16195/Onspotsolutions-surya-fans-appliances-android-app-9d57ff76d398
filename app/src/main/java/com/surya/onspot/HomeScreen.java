package com.surya.onspot;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.surya.onspot.model.RewardsDashboardModel;
import com.surya.onspot.qrscanFragments.ChooseModuleFragment;
import com.surya.onspot.qrscanFragments.HomeFragment;
import com.surya.onspot.tab.FragmentReceivedHistoryBase;
import com.surya.onspot.tab.ScanMainFragment;
import com.surya.onspot.utils.Constants;
import com.surya.onspot.utils.LocaleHelper;
import com.surya.onspot.utils.PreferenceKeys;
import com.surya.onspot.utils.Utils;

import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

// Home Screen Page For Onspot Written by Ajay Abrol
public class HomeScreen extends AppCompatActivity implements ListView.OnItemClickListener {

    boolean doubleBackToExitPressedOnce = false;
    LinearLayout frame;
    Context cntx;
    float mPreviousOffset = 0f;
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private LayoutManager mLayoutManager;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    /*private int[] navIcons = {
            R.drawable.ic_home_black, R.drawable.ic_about_us_black,
            R.drawable.ic_app_basics_black, R.drawable.ic_faq_black,
            R.drawable.ic_feedback_black, R.drawable.ic_rewards_black,
            R.drawable.ic_license_black, R.drawable.ic_scan_history_black, R.drawable.ic_my_profile_black
            , R.drawable.ic_terms_of_services_black};*/

    private String[] nav_menu_retailer_title = {
            "HOME",
            "ABOUT US",
            "APP BASICS",
            "FAQ",
            "LICENSES",
            "SEARCH HISTORY",
            "STOCK REPORT",
            "REPLACEMENT/WARRANTY",
            "TERMS OF SERVICE"};

    private String[] nav_menu_retailer_title_on_homepage = {
            "HOME",
            "ABOUT US",
            "APP BASICS",
            "FAQ",
            "LICENSES",
            "REPLACEMENT/WARRANTY",
            "TERMS OF SERVICE"};
    private String[] nav_menu_agancy_title = {
            "HOME",
            "ABOUT US",
            "APP BASICS",
            "FAQ",
            "SEARCH HISTORY",
            "STOCK REPORT",
            "LICENSES",
            "TERMS OF SERVICE"};
    private String[] nav_menu_agancy_title_hme = {
            "HOME",
            "ABOUT US",
            "APP BASICS",
            "FAQ",
            "LICENSES",
            "TERMS OF SERVICE"};
    private int[] navIcons = {
            R.drawable.ic_home_black_24dp,
            R.drawable.ic_group_black_24dp,
            R.drawable.ic_error_outline_black_24dp,
            R.drawable.ic_help_outline_black_24dp,
            R.drawable.ic_content_paste_black_24dp,
            R.drawable.ic_search,
            R.drawable.ic_stock_report,
            R.drawable.ic_warranty,
            R.drawable.ic_filter_none_black_24dp};
    private int[] nav_menu_icons_agancy = {
            R.drawable.ic_home_black_24dp,
            R.drawable.ic_group_black_24dp,
            R.drawable.ic_error_outline_black_24dp,
            R.drawable.ic_help_outline_black_24dp,
            R.drawable.ic_search,
            R.drawable.ic_stock_report,
            R.drawable.ic_content_paste_black_24dp,
            R.drawable.ic_filter_none_black_24dp};
    private float lastTranslate = 0.0f;
    private Menu menu;
    private Resources resources;
    private RewardsDashboardModel rewardDashboardModel = null;
    private Camera cam = null;
    private SnackbarRetryListener retryClickListener;
    private Snackbar mSnackbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cntx = getApplicationContext();
        Context context = LocaleHelper.setLocale(cntx, Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, ""));
        resources = context.getResources();
        initViews();
        changeStatusBarColor(getResources().getString(R.color.log_colorPrimaryDark));
//        Window window = getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.setContentViewetStatusBarColor(getResources().getColor(R.color.log_colorPrimaryDark));
        switchFragmentMain(new ChooseModuleFragment());
//				startActivityForResult(new Intent(cntx, LoginDialog.class), REQUEST_CODE); 
        //		actionBar = getSupportActionBar();
        //		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        //		actionBar.setIcon(R.drawable.relianceknow_icon);
        //		actionBar.setTitle(getString(R.string.app_name_long));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Context context = LocaleHelper.setLocale(cntx, Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, ""));
        resources = context.getResources();
    }

    public void refreshDrawer() {
        Utils.out("IN DRAWER REFRESHED");
        mAdapter = null;
        if (Utils.getPreference(HomeScreen.this, PreferenceKeys.USER_TYPE, "").equalsIgnoreCase(Constants.USER_TYPE_RETAILER)) {
        if (Utils.getPreference(HomeScreen.this, PreferenceKeys.HOME_FRAG, "").equalsIgnoreCase("homeS")) {

            mAdapter = new MyAdapter(this, drawerLayout, navIcons, nav_menu_retailer_title_on_homepage);
        }else {
            mAdapter = new MyAdapter(this, drawerLayout, navIcons, nav_menu_retailer_title);
        }
        } else {

            if (Utils.getPreference(HomeScreen.this, PreferenceKeys.HOME_FRAG, "").equalsIgnoreCase("homeS")) {

                mAdapter = new MyAdapter(this, drawerLayout, nav_menu_icons_agancy, nav_menu_agancy_title_hme);
            }else {
                mAdapter = new MyAdapter(this, drawerLayout, nav_menu_icons_agancy, nav_menu_agancy_title);
            }

        }
        mRecyclerView = findViewById(R.id.RecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        drawerLayout.setStatusBarBackground(R.color.log_colorPrimaryDark);

        if (Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, "en").equals("en")) {
            ((ImageView) findViewById(R.id.imageView_bottom_jgj_logo)).setImageResource(R.drawable.onspot_drawer_icon);
        } else {
            ((ImageView) findViewById(R.id.imageView_bottom_jgj_logo)).setImageResource(R.drawable.onspot_drawer_icon);
        }
    }

    public void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        if (Utils.getPreference(HomeScreen.this, PreferenceKeys.USER_TYPE, "").equalsIgnoreCase(Constants.USER_TYPE_RETAILER)) {
            mAdapter = new MyAdapter(this, drawerLayout, navIcons, nav_menu_retailer_title);
        } else {
            mAdapter = new MyAdapter(this, drawerLayout, nav_menu_icons_agancy, nav_menu_agancy_title);
        }
        mRecyclerView = findViewById(R.id.RecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        drawerLayout.setStatusBarBackground(R.color.log_colorPrimaryDark);
        toolbar = findViewById(R.id.toolbar);
        frame = findViewById(R.id.content_frame);

        //Set the custom toolbar
        if (toolbar != null) {
            toolbar.setTitle(R.string.app_name);
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            setSupportActionBar(toolbar);
        }

        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
                syncState();
                changeStatusBarColor(getString(R.color.log_colorPrimaryDark));
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                syncState();
                changeStatusBarColor(getString(R.color.log_colorPrimaryDark));
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                float moveFactor = (mRecyclerView.getWidth() * slideOffset) / 3;

                changeStatusBarColor(getString(R.color.log_colorPrimaryDark));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    frame.setTranslationX(moveFactor);
                } else {
                    TranslateAnimation anim = new TranslateAnimation(lastTranslate, moveFactor, 0.0f, 0.0f);
                    anim.setDuration(0);
                    anim.setFillAfter(true);
                    frame.startAnimation(anim);
                    lastTranslate = moveFactor;
                }
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    private void changeStatusBarColor(String color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        // enable navigation bar tint
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setTintColor(Color.parseColor(color));
//        Window window = getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.setStatusBarColor(getResources().getColor(R.color.log_colorPrimaryDark));
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        updateMenuTitles(menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        updateMenuTitles(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    private void updateMenuTitles(Menu menu) {

        Resources res = cntx.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(new Locale(Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, "")));
        }
        res.updateConfiguration(conf, dm);
        /*Context context = LocaleHelper.setLocale(cntx, Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, ""));
        resources = context.getResources();*/
        MenuItem settings = menu.findItem(R.id.action_settings);
        settings.setTitle(res.getString(R.string.action_settings));
        MenuItem exit = menu.findItem(R.id.action_exit);
        exit.setTitle(res.getString(R.string.action_exit));
    }

    // This is the menu option selected items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                switchFragment(new FragmentReceivedHistoryBase());
                break;
            case R.id.action_exit:
                finish();
                break;
            case R.id.action_share:
                Intent in = new Intent(Intent.ACTION_SEND);
                in.setType("text/plain");
                in.putExtra(Intent.EXTRA_SUBJECT, "Link to download Onspot App");
                in.putExtra(Intent.EXTRA_TEXT, "http://www.onspotsolutions.com/");
                startActivity(Intent.createChooser(in, "Share using"));
                break;
            case R.id.action_scanhistory:
                switchFragmentMain(new ScanMainFragment());
                break;
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        //				Toast.makeText(getApplicationContext(), leftSliderData[position - 1], Toast.LENGTH_SHORT).show();
    }

    private FragmentManager.OnBackStackChangedListener getListener() {
        FragmentManager.OnBackStackChangedListener result = new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                FragmentManager manager = getSupportFragmentManager();
                if (manager != null) {
                    int backStackEntryCount = manager.getBackStackEntryCount();
                    if (backStackEntryCount == 0) {
                        finish();
                    }
                    if (backStackEntryCount > 1) {
                        try {
                            Fragment fragment = manager.getFragments()
                                    .get(backStackEntryCount - 1);
                            if (fragment != null) {
                                fragment.onResume();
                            }
                        } catch (IndexOutOfBoundsException ie) {
                            ie.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        return result;
    }

    private void switchFragmentMain(final Fragment mTarget) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_root, mTarget)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }
        }, Constants.DRAWER_TIME_DELAY);
    }

    private void switchFragment(final Fragment mTarget) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String backStateName = mTarget.getClass().getName();
                FragmentManager manager = getSupportFragmentManager();
                boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
                boolean samefrag = false;
                if (manager.findFragmentByTag(backStateName) != null) {
                    samefrag = manager.findFragmentByTag(backStateName).isAdded();
                }

                if (!fragmentPopped && !samefrag) {
                    FragmentTransaction ft = manager.beginTransaction();
                    ft.replace(R.id.fragment_root, mTarget, backStateName)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .addToBackStack(backStateName)
                            .commit();
                }
            }
        }, Constants.DRAWER_TIME_DELAY);
    }

    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            if (mSnackbar != null && mSnackbar.isShown()) {
                mSnackbar.dismiss();
            }
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
//        Toast.makeText(cntx, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                doubleBackToExitPressedOnce = false;
//            }
//        }, 2000);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        /*Locale newLocale;
        // .. create or get your new Locale object here.
        Context context = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            context = ContextWrapper.wrap(newBase, Locale.forLanguageTag(Utils.getPreference(newBase, PreferenceKeys.KEY_LANGUAGE, "")));
        }
        super.attachBaseContext(context);*/
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    public RewardsDashboardModel getRedeemData() {
        return rewardDashboardModel;
    }

    public void setRedeemData(RewardsDashboardModel model) {
        rewardDashboardModel = model;
    }

    public void flashLightOn() {
        try {
            if (getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_FLASH)) {
                cam = Camera.open();
                Camera.Parameters p = cam.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                cam.setParameters(p);
                cam.startPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void flashLightOff() {
        try {
            if (getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_FLASH)) {
                cam.stopPreview();
                cam.release();
                cam = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSnackbar(View v, String message, SnackbarRetryListener retryListener, final String tag) {
        this.retryClickListener = retryListener;
        mSnackbar = Snackbar.make(v, message, Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            retryClickListener.onRetryClickListener(tag);
                        } catch (NullPointerException ne) {
                            ne.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        mSnackbar.show();
    }

    public void dismissSnackbar() {
        if (mSnackbar != null && mSnackbar.isShown()) {
            mSnackbar.dismiss();
        }
    }

    public interface SnackbarRetryListener {
        void onRetryClickListener(String apiTag);
    }
}