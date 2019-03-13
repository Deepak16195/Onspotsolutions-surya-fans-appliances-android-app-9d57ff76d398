package com.surya.onspot.QRresponse;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.AsyncTask;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.surya.onspot.intera.AsyncResponseActivity;
import com.surya.onspot.services.InterfaceServiceCall;
import com.surya.onspot.utils.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


public class ServiceCalls extends AsyncTask<Integer, Void, String> implements
        OnDismissListener {

    ProgressDialog bar;
    Context context;
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
    private AsyncResponseActivity asyncResponse;

    /**
     * @param ctx  = Screen context
     * @param URL  = API url
     * @param JSON = Input JSONObject with string format
     * @param Msg  = Message to show in dialog
     * @param Flag = If Dialog need to show while API call then pass 0 'zero' else pass any integer number except 10
     */

    public ServiceCalls(Context ctx, String URL, String JSON, String Msg, int Flag, AsyncResponseActivity asyncResponse) {
        context = ctx;
        // mListener = listener;
        url = URL;
        /*try {
            json = new JSONObject(JSON).put("language", Utils.getPreference(ctx, PreferenceKeys.KEY_LANGUAGE, "en")).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        json = JSON;
        BarMsg = Msg;
        flag = Flag;
        this.asyncResponse = asyncResponse;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (flag != 10) {
            bar = new ProgressDialog(context);
            bar.setMessage(BarMsg);
            bar.setCancelable(false);
            bar.setOnDismissListener(this);
            if (flag == 0)
                bar.show();
        }
        Utils.out("API URL : " + url);
    }

    @Override
    protected String doInBackground(Integer... params) {
//        try {
//            // client = new MyHttpsClient(context);
//            HttpParams httpParams = new BasicHttpParams();
//            int timeoutConnection = 10000;
//            int socketTimeoutConnection = 12000;
//
//            HashMap<String, String> hashMap = new HashMap<>();
//            if (!json.isEmpty()) {
//                JSONObject objJSObject = new JSONObject(json);
//                Iterator<String> iter = objJSObject.keys();
//                while (iter.hasNext()) {
//                    String key = iter.next();
//                    try {
//                        String value = objJSObject.getString(key);
//                        hashMap.put(key, value);
//                    } catch (JSONException e) {
//                        // Something went wrong!
//                    }
//                }
//            }
//
//            HttpConnectionParams.setConnectionTimeout(httpParams,
//                    timeoutConnection);
//            HttpConnectionParams.setSoTimeout(httpParams,
//                    socketTimeoutConnection);
//            client = new DefaultHttpClient(httpParams);
//            switch (params[0]) {
//                case 1: // Post request
//                    postRequest = new HttpPost(url.replace(" ", "%20"));
//                    postRequest.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeoutConnection);
//                    postRequest.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, socketTimeoutConnection);
//                    postRequest.getParams().setLongParameter(ConnManagerPNames.TIMEOUT, timeoutConnection);
////                    postRequest.setHeader("Accept", "text/html, application/xml, application/xhtml+xml, text/html; q=0.9, text/plain;q=0.8, image/png, */*; q=0.5");
//                    postRequest.setHeader("Accept", "application/json");
//                    postRequest.setHeader("Content-Type", "application/x-www-form-urlencoded");
//
//                    StringEntity entity = new StringEntity(getDataString(hashMap));
//                    Utils.out("API INPUT : " + json);
//
//                    postRequest.setEntity(entity);
//                    response = client.execute(postRequest);
//                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK
//                            || response.getStatusLine().getStatusCode() == HttpStatus.SC_ACCEPTED
//                            || response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
//                        mstrResponse = EntityUtils.toString(response.getEntity());
//                        Log.d("Response", mstrResponse);
//                        msg = "OK";
//                    } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_REQUEST_TIMEOUT) {
//                        mstrResponse = "{\"success\":false,\"result_code\":" + HttpStatus.SC_REQUEST_TIMEOUT + ",\"responseMessage\":\"Oops! Server not responding. Please try again.\"}";
//                        return mstrResponse;
//                    } else {
//                        msg = "Oops, our server is unavailable";
//                    }
//                    break;
//            }
//        } catch (SocketTimeoutException se) {
//            Utils.out("SOCKET TIME OUT EXCEPTION");
//            mstrResponse = "{\"success\":false,\"result_code\":" + HttpStatus.SC_REQUEST_TIMEOUT + ",\"responseMessage\":\"Oops! Server not responding. Please try again.\"}";
//            return mstrResponse;
//        } catch (Exception e) {
//           /* System.out.println("service exception :" + e);
//            msg = "Oops, our server is unavailable";*/
//            Utils.out("SOCKET TIME OUT EXCEPTION");
//            mstrResponse = "{\"success\":false,\"result_code\":" + HttpStatus.SC_REQUEST_TIMEOUT + ",\"responseMessage\":\"Oops! Server not responding. Please try again.\"}";
//            return mstrResponse;
//        }

        try {
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(12000, TimeUnit.SECONDS)
                    .readTimeout(12000, TimeUnit.SECONDS)
                    .writeTimeout(12000, TimeUnit.SECONDS)
                    .build();

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

            AndroidNetworking.post(url.replace(" ", "%20"))
                    .setOkHttpClient(okHttpClient)
                    .addHeaders("Accept", "application/json")
                    .addHeaders("Content-Type", "application/x-www-form-urlencoded")
                    .addBodyParameter(hashMap)
                    .setTag("OnSpotSuryaApiCall")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (bar.isShowing()) {
                                cancel(true);
                                bar.dismiss();
                            }
                            mstrResponse = response.toString();
                            asyncResponse.myAsyncResponse(mstrResponse);
                        }

                        @Override
                        public void onError(ANError error) {
                            // handle error
                            if (bar.isShowing()) {
                                cancel(true);
                                bar.dismiss();
                            } mstrResponse = error.getErrorDetail();
                            asyncResponse.myAsyncResponse(mstrResponse);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mstrResponse;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Utils.out("API OUTPUT : " + mstrResponse);
//        asyncResponse.myAsyncResponse(mstrResponse);
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

    interface CustomCallback {
        void onResponse(String parameter);
    }

}
