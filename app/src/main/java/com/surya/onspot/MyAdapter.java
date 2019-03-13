package com.surya.onspot;


import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.surya.onspot.qrscanFragments.AboutUsFragment;
import com.surya.onspot.qrscanFragments.AppBasicsFragment;
import com.surya.onspot.qrscanFragments.ChooseModuleFragment;
import com.surya.onspot.qrscanFragments.FaqFragment;
import com.surya.onspot.qrscanFragments.FeedbackFragment;
import com.surya.onspot.qrscanFragments.FragmentSearchHistory;
import com.surya.onspot.qrscanFragments.FragmentStockReport;
import com.surya.onspot.qrscanFragments.HomeFragment;
import com.surya.onspot.qrscanFragments.LicenceFragment;
import com.surya.onspot.qrscanFragments.TermservicesFragment;
import com.surya.onspot.utils.Constants;
import com.surya.onspot.utils.LocaleHelper;
import com.surya.onspot.utils.PreferenceKeys;
import com.surya.onspot.utils.Utils;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    private static final int TYPE_ITEM = 1;    // IF the view under inflation and population is header or Item
    Context cntx;
    private FragmentActivity mfrag;
    private DrawerLayout mdrawer;
    private String mNavTitles[]; // String Array to store the passed titles Value from HomeScreen.java
    private int mIcons[];       // Int Array to store the passed icons resource value from HomeScreen.java
    private TextView textViewHeader, textViewHeaderGov;
    // Creating a ViewHolder which extends the RecyclerView View Holder
    // ViewHolder are used to to store the inflated views in order to recycle them

    public MyAdapter(Context cntx) {
        this.cntx = cntx;
    }

    public MyAdapter(FragmentActivity frag, DrawerLayout drawer, int Icons[], String Titles[]) {
        mfrag = frag;
        mdrawer = drawer;
        mIcons = Icons;
        mNavTitles = Titles;
    }

    private void switchFragment(final Fragment mTarget) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                String backStateName = mTarget.getClass().getName();
                FragmentManager manager = mfrag.getSupportFragmentManager();
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false); //Inflating the layout
            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view
            return vhItem; // Returning the created object
            //inflate your layout and pass it to view holder

        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawerlist_header, parent, false); //Inflating the layout
            Context context = LocaleHelper.setLocale(mfrag.getApplicationContext(), Utils.getPreference(mfrag.getApplicationContext(), PreferenceKeys.KEY_LANGUAGE, ""));
            Resources resources = context.getResources();
            /*if (Utils.getPreference(mfrag.getApplicationContext(), PreferenceKeys.KEY_LANGUAGE, "").equals("hi")) {
                ((ImageView) v.findViewById(R.id.imageView_drawer_header)).setImageResource(R.drawable.hi_jago_grahak_jago);
            } else {
                ((ImageView) v.findViewById(R.id.imageView_drawer_header)).setImageResource(R.drawable.en_jago_grahak_jago);
            }*/
            textViewHeader = v.findViewById(R.id.textView_drawer_header);
            textViewHeaderGov = v.findViewById(R.id.textView_drawer_header_government);
            textViewHeader.setText(resources.getString(R.string.drawer_header_text));
            textViewHeaderGov.setText(resources.getString(R.string.drawer_header_text_gov));
            ViewHolder vhHeader = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view
            return vhHeader; //returning the object created
        }
        return null;
    }

    //Below first we ovverride the method onCreateViewHolder which is called when the ViewHolder is
    //Created, In this method we inflate the item_row.xml layout if the viewType is Type_ITEM or else we inflate header.xml
    // if the viewType is TYPE_HEADER
    // and pass it to the view holder

    public void refreshHeaderText() {
        textViewHeader.setText(mfrag.getApplicationContext().getResources().getString(R.string.drawer_header_text));
    }

    //Next we override a method which is called when the item in a row is needed to be displayed, here the int position
    // Tells us item at which position is being constructed to be displayed and the holder id of the holder object tell us
    // which view type is being created 1 for item row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder.Holderid == 1) {                              // as the list view is going to be called after the header view so we decrement the
            // position by 1 and pass it to the holder while setting the text and image
            Context context = LocaleHelper.setLocale(mfrag.getApplicationContext(), Utils.getPreference(mfrag.getApplicationContext(), PreferenceKeys.KEY_LANGUAGE, ""));
            Resources resources = context.getResources();
            // Setting the Text with the array of our Titles

            if (mNavTitles[position - 1].equals("HOME")) {
                holder.textView.setText(resources.getString(R.string.drawer_home));
            } else if (mNavTitles[position - 1].equals("ABOUT US")) {
                holder.textView.setText(resources.getString(R.string.drawer_about_us));
            } else if (mNavTitles[position - 1].equals("APP BASICS")) {
                holder.textView.setText(resources.getString(R.string.drawer_app_basics));
            } else if (mNavTitles[position - 1].equals("FAQ")) {
                holder.textView.setText(resources.getString(R.string.drawer_faq));
            } else if (mNavTitles[position - 1].equals("SEARCH HISTORY")) {
                holder.textView.setText(resources.getString(R.string.drawer_search_history));
            } else if (mNavTitles[position - 1].equals("FEEDBACK")) {
                holder.textView.setText(resources.getString(R.string.drawer_feedback));
            } else if (mNavTitles[position - 1].equals("REWARDS")) {
                holder.textView.setText(resources.getString(R.string.drawer_rewards));
            } else if (mNavTitles[position - 1].equals("LICENSES")) {
                holder.textView.setText(resources.getString(R.string.drawer_license));
            } else if (mNavTitles[position - 1].equals("SCAN HISTORY")) {
                holder.textView.setText(resources.getString(R.string.drawer_scan_history));
            } else if (mNavTitles[position - 1].equals("STOCK REPORT")) {
                holder.textView.setText(resources.getString(R.string.drawer_stock_report));
            } else if (mNavTitles[position - 1].equals("MY PROFILE")) {
                holder.textView.setText(resources.getString(R.string.drawer_my_profile));
            } else if (mNavTitles[position - 1].equals("TERMS OF SERVICE")) {
                holder.textView.setText(resources.getString(R.string.drawer_terms_of_services));
            } else if (mNavTitles[position - 1].equals("REPLACEMENT/WARRANTY")) {
                holder.textView.setText(resources.getString(R.string.drawer_replace_warranty));
            }
            holder.imageView.setImageResource(mIcons[position - 1]);// Setting the image with array of our icons
        }
    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        return mNavTitles.length + 1; // the number of items in the list will be +1 the titles including the header view.
    }

    // Witht the following method we check what type of view is being passed
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;


        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        int Holderid;

        TextView textView;
        ImageView imageView;

        public ViewHolder(View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);

            // Here we set the appropriate view in accordance with the the view type as passed when the holder object is created

            if (ViewType == TYPE_ITEM) {

                textView = itemView.findViewById(R.id.rowText);

                // Creating TextView object with the id of textView from item_row.xml
                imageView = itemView.findViewById(R.id.rowIcon);// Creating ImageView object with the id of ImageView from item_row.xml
                Holderid = 1;

                // setting holder id as 1 as the object being populated are of type item row
            } else {
                Holderid = 0;                                                // Setting holder id = 0 as the object being populated are of type header view
            }


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (getPosition() == 0) {
                        mdrawer.closeDrawers();
                        switchFragment(new ChooseModuleFragment());
                    } else if (mNavTitles[getPosition() - 1].equals("HOME")) {
                        mdrawer.closeDrawers();
                        Utils.addPreference(mfrag.getBaseContext(), PreferenceKeys.MODULE_TYPE, "fans");
                        switchFragment(new HomeFragment());
//                        switchFragment(new ChooseModuleFragment());
                    } else if (mNavTitles[getPosition() - 1].equals("ABOUT US")) {
                        mdrawer.closeDrawers();
                        switchFragment(new AboutUsFragment());
                    } else if (mNavTitles[getPosition() - 1].equals("APP BASICS")) {
                        mdrawer.closeDrawers();
                        switchFragment(new AppBasicsFragment());
                    } else if (mNavTitles[getPosition() - 1].equals("FAQ")) {
                        mdrawer.closeDrawers();
                        switchFragment(new FaqFragment());
                    } else if (mNavTitles[getPosition() - 1].equals("FEEDBACK")) {
                        mdrawer.closeDrawers();
                        switchFragment(new FeedbackFragment());
                    } else if (mNavTitles[getPosition() - 1].equals("REWARDS")) {
//                        holder.textView.setText(resources.getString(R.string.drawer_rewards));
                    } else if (mNavTitles[getPosition() - 1].equals("LICENSES")) {
                        mdrawer.closeDrawers();
                        switchFragment(new LicenceFragment());
                    } else if (mNavTitles[getPosition() - 1].equals("SCAN HISTORY")) {
//                        holder.textView.setText(resources.getString(R.string.drawer_scan_history));
                    } else if (mNavTitles[getPosition() - 1].equals("MY PROFILE")) {
//                        holder.textView.setText(resources.getString(R.string.drawer_my_profile));
                    } else if (mNavTitles[getPosition() - 1].equals("REPLACEMENT/WARRANTY")) {
                        mdrawer.closeDrawers();
                        switchFragment(new FragmentRetailerReplacement());
                    } else if (mNavTitles[getPosition() - 1].equals("TERMS OF SERVICE")) {
                        mdrawer.closeDrawers();
                        switchFragment(new TermservicesFragment());
                    } else if (mNavTitles[getPosition() - 1].equals("SEARCH HISTORY")) {
                        mdrawer.closeDrawers();
                        switchFragment(new FragmentSearchHistory());
                    } else if (mNavTitles[getPosition() - 1].equals("STOCK REPORT")) {
                        mdrawer.closeDrawers();
                        switchFragment(new FragmentStockReport());
                    }

                   /* switch (getPosition()) {
                        case 1:
                            mdrawer.closeDrawers();
                            switchFragment(new HomeFragment());
                            break;
                        case 2:
                            mdrawer.closeDrawers();
                            switchFragment(new AboutUsFragment());
                            break;
                        case 3:
                            mdrawer.closeDrawers();
                            switchFragment(new AppBasicsFragment());
                            break;
                        case 4:
                            mdrawer.closeDrawers();
                            switchFragment(new FaqFragment());
                            break;
                        case 5:
                            mdrawer.closeDrawers();
                            switchFragment(new FeedbackFragment());
                            break;
                        case 6:
                            mdrawer.closeDrawers();
                            switchFragment(new LicenceFragment());
                            break;
                        case 7:
                            mdrawer.closeDrawers();
                            switchFragment(new FragmentRetailerReplacement());
                            break;
                        case 8:
                            mdrawer.closeDrawers();
                            switchFragment(new TermservicesFragment());
                            break;
                        default:
                            break;

                    }*/
                }
            });
        }
    }

}