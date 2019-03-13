package com.surya.onspot.services;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.surya.onspot.intera.AsyncResponseActivity;
import com.surya.onspot.utils.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class ServiceCalls extends AsyncTask<Integer, Void, Bundle> implements OnDismissListener {

    ProgressDialog bar;
    Context context;
    // InterfaceServiceCall mListener;
    HttpClient client;
    HttpPost postRequest;
    HttpGet getRequest;
    InterfaceServiceCall mListener;
    int textView;
    HttpResponse response;
    String mstrResponse = "", msg = "", url = "";
    String json = "", BarMsg = "";
    int flag;
    int service;

    /**
     * @param ctx  = Screen context
     * @param URL  = API url
     * @param JSON = Input JSONObject with string format
     * @param Msg  = Message to show in dialog
     * @param Flag = If Dialog need to show while API call then pass 0 'zero' else pass any integer number except 10
     */
    public ServiceCalls(Context ctx, String URL, String JSON, String Msg,
                        int Flag, AsyncResponseActivity asyncResponse) {
        context = ctx;
        // mListener = listener;
        url = URL;
        json = JSON;
        BarMsg = Msg;
        flag = Flag;
    }

    public ServiceCalls(Context ctx, InterfaceServiceCall listener, String URL, String Msg, int Flag, int TextView, int src) {
        context = ctx;
        mListener = listener;
        url = URL;
        BarMsg = Msg;
        flag = Flag;
        textView = TextView;
        service = src;
    }

    public ServiceCalls(Context ctx, InterfaceServiceCall listener, String URL, String JSON, String Msg, int Flag) {
        context = ctx;
        mListener = listener;
        url = URL;
        json = JSON;
        BarMsg = Msg;
        flag = Flag;
    }


    public ServiceCalls(Context ctx, String URL, String JSON, ProgressDialog mbar, int Flag, AsyncResponseActivity asyncResponse) {
        context = ctx;
        // mListener = listener;
        url = URL;
        json = JSON;
        flag = Flag;
        bar = mbar;
    }

    public ServiceCalls(Context ctx, InterfaceServiceCall listener, String URL, String Msg, int Flag) {
        context = ctx;
        mListener = listener;
        url = URL;
        BarMsg = Msg;
        flag = Flag;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        bar = new ProgressDialog(context);
        bar.setMessage(BarMsg);
        bar.setCancelable(true);
        bar.setOnDismissListener(this);
        bar.setCanceledOnTouchOutside(false);
        if (flag == 0)
            bar.show();
        Utils.out("API URL : " + url);
    }


    @Override
    protected Bundle doInBackground(Integer... params) {
        try {
            client = new DefaultHttpClient();
            switch (params[0]) {
                case 1: // Post request
                    postRequest = new HttpPost(url.replace(" ", "%20"));
                    postRequest.setHeader("Accept", "text/html, application/xml, application/xhtml+xml, text/html; q=0.9, text/plain;q=0.8, image/png, */*; q=0.5");
                    postRequest.setHeader("Content-Type", "application/x-www-form-urlencoded");
                    HashMap<String, String> hashMap = new HashMap<>();
                    if (!json.isEmpty()) {
                        JSONObject objJSObject = new JSONObject(json);
                        Iterator<String> iter = objJSObject.keys();
                        while (iter.hasNext()) {
                            String key = iter.next();
                            try {
                                String value = objJSObject.getString(key);
                                hashMap.put(key, value);
                            } catch (JSONException e) {
                                // Something went wrong!
                            }
                        }
                    }
                    StringEntity entity = new StringEntity(getDataString(hashMap));
                    Utils.out("API INPUT : " + json);
                    postRequest.setEntity(entity);
                    response = client.execute(postRequest);
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK || response.getStatusLine().getStatusCode() == HttpStatus.SC_ACCEPTED || response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
                        mstrResponse = EntityUtils.toString(response.getEntity());
                        Log.d("Response", mstrResponse);
                        msg = "OK";
                    } else {
                        msg = "Oops, our server is unavailable";
                    }
                    break;
            }
        } catch (Exception e) {
            System.out.println("service exception :" + e);
            msg = "Oops, our server is unavailable";
        }
        Bundle result = new Bundle();
        result.putString("Message", msg);
        result.putString("Response", mstrResponse);
        return result;
    }

    @Override
    protected void onPostExecute(Bundle result) {
        super.onPostExecute(result);
        if (bar.isShowing())
            bar.dismiss();
        bar.setCanceledOnTouchOutside(false);
        Utils.out("API OUTPUT : " + result.getString("Response"));
        if (!isCancelled())
            mListener.onResponseReceived(result);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        this.cancel(true);
    }

    private String getDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }
}
