package com.surya.onspot.qrscanFragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.surya.onspot.HomeScreen;
import com.surya.onspot.QRscanapi.API_CONSTANTS;
import com.surya.onspot.R;
import com.surya.onspot.utils.LocaleHelper;
import com.surya.onspot.utils.PreferenceKeys;
import com.surya.onspot.utils.Utils;


public class AppBasicsFragment extends Fragment implements OnClickListener {

    Context cntx;

    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.page_home, container, false);
        cntx = getActivity();
        initViews();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Context context = LocaleHelper.setLocale(cntx, Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, ""));
        Resources resources = context.getResources();
        ((HomeScreen) getActivity()).getSupportActionBar().setTitle(resources.getString(R.string.app_basics_title));
        ((TextView) view.findViewById(R.id.textView_app_basics)).setText(resources.getString(R.string.app_basics));

        WebView objWebView = view.findViewById(R.id.webView_content);
        objWebView.getSettings().setJavaScriptEnabled(true);
        objWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        final AlertDialog alertDialog = new AlertDialog.Builder(cntx).create();

//        progressBar = ProgressDialog.show(cntx, "", "Loading...");

        objWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                /*try {
                    if (progressBar.isShowing()) {
                        progressBar.dismiss();
                    }
                } catch (NullPointerException ne) {
                    ne.printStackTrace();
                } catch (WindowManager.BadTokenException be) {
                    be.printStackTrace();
                } catch (IllegalStateException ie) {
                    ie.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(cntx, "Oh no! " + description, Toast.LENGTH_SHORT).show();
                alertDialog.setTitle("Error");
                alertDialog.setMessage(description);
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                });
                alertDialog.show();
            }
        });

        if (Utils.getPreference(cntx, PreferenceKeys.KEY_LANGUAGE, "en").equals("en")) {
            objWebView.loadUrl(API_CONSTANTS.URL_APP_BASICS_EN);
        } else {
            objWebView.loadUrl(API_CONSTANTS.URL_APP_BASICS_HI);
        }
    }

    private void initViews() {
//		mlayout = (LinearLayout)view.findViewById(R.id.);
//		mlayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switchFragment(new AppBasicsFragment());
    }

    private void switchFragment(Fragment mTarget) {
        ((FragmentActivity) cntx).getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_root, mTarget)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

}
