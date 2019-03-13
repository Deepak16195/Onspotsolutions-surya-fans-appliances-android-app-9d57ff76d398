package com.surya.onspot.QRscanapi;

import android.app.Application;

import com.surya.onspot.utils.PreferenceKeys;
import com.surya.onspot.utils.Utils;

public class API_CONSTANTS extends Application {

    public static String HOST_URL = "https://surya.onspotsolutions.com/";

//    public static String API_BASE = Utils.getPreference(context, PreferenceKeys.MODULE_TYPE, "fans").equalsIgnoreCase("fans") ? API_BASE : API_BASE_APPLIANCE1;
//    public static String API_BASE1 = Utils.getPreference(context, PreferenceKeys.MODULE_TYPE, "fans").equalsIgnoreCase("fans") ? API_BASE1 : API_BASE_APPLIANCE;

    //    (Utils.getPreference(cntx, PreferenceKeys.MODULE_TYPE, "fans").equalsIgnoreCase("fans") ? API_CONSTANTS.API_BASE : API_CONSTANTS.API_BASE_APPLIANCE1)
//(Utils.getPreference(getContext(), PreferenceKeys.MODULE_TYPE, "fans").equalsIgnoreCase("fans") ? API_CONSTANTS.API_BASE1 : API_CONSTANTS.API_BASE_APPLIANCE)
    public static String API_BASE = HOST_URL + "api/app_v1/";
    public static String API_BASE1 = HOST_URL + "api/v1/";
    public static String API_BASE_APPLIANCE = HOST_URL + "appliance/api/v1/";
    public static String API_BASE_APPLIANCE1 = HOST_URL + "appliance/api/app_v1/";

    public static String NO_INTERNET_MSG = "No internet connection.";
    public static String NO_INTERNET_MSG1 = "Location Settings not ON.";
    public static String REQ_TIMEOUT_MSG = "We could not connect to the server.";
    public static String SUCCESS = "success";
    public static String RESPONSE_MESSAGE = "responseMessage";
    public static int SOCKET_TIMEOUT_EXCEPTION_CODE = 600;
    public static String API_SCAN_QR = "scan_qr";
    //    public static String HOST1 = API_BASE + "validateQrCode.json?";
    public static String QR_CODE_KEY = "qr_code";
    // --- To Fetch Response
    public static String MOBILE_NUMBER_KEY = "mobile_number";
    public static String EMAIL_KEY = "email";
    public static String NAME_KEY = "name";
    public static String EMAIL_RESULT_KEY = "email_result";
    public static String STATUS_KEY = "status";
    public static String LATITUDE_KEY = "latitude";
    public static String LONGITUDE_KEY = "longitude";
    public static String ACCURACY_KEY = "accuracy";
    public static String ADDRESS = "address";
    public static String Flag = "flag";
    public static String REPORT_REG_EMAIL_KEY = "reg_email";
    public static String REPORT_REG_MOB_NO_KEY = "reg_mobile_num";
    public static String REPORT_REG_NAME_KEY = "reg_name";
    public static String REPORT_INVALID_QR_CODE_KEY = "code";
    public static String REPORT_RESPONSE_TYPE_KEY = "response_type";
    public static String REPORT_USER_COMMENT_KEY = "user_comment";
    public static String REPORT_REG_EMAIL_VALUE = "sachins@onspotsolutions.com";
    public static String REPORT_REG_MOB_NO_VALUE = "98920416174";
    public static String REPORT_REG_NAME_VALUE = "Sachin";
    public static String API_REPORT_INVALID_BARCODE_PRODUCT = API_BASE + "invalid_report";

    // --- To Send Report
    public static String REPORT_RESPONSE_TYPE_VALUE = "Invalid";
    public static String API_SIGN_UP = API_BASE + "sign_up";
    public static String USER_Id_KEY = "app_user_id";
    public static String USER_EMAIL_KEY = "email";
    public static String USER_MOB_KEY = "mobile_number";
    public static String USER_NAME_KEY = "name";
    public static String USER_STATUS_KEY = "clicked_on_existing";
    public static String API_SIGN_IN = API_BASE + "sign_in";
    public static String API_SIGN_IN_MOB_NUM = "mobile_number";
    public static String API_VERIFY_OTP_WHILE_SIGN_IN_UP = API_BASE + "verify_otp";
    public static String OTP_VER_KEY = "otp_code";
    public static String OTP_MOB_KEY = "mobile_number";
    public static String OTP_USER_ID = "user_id";

    // --- To Send User Value
    public static String OTP_USER_EXIST_KEY = "clicked_on_existing";
    // --- To get list of barcode available in system. This is to verify whether scanned barcode is exists in our system or not.
    public static String API_GET_SYSTEM_BARCODE_LIST = "get_list_recive_shipment";
    // --- UPLOAD DATA TO SERVER
    public static String API_UPLOAD_DATA_TO_SERVER = "recived_shipments";
    // --- To get list of completed received shipment history
    public static String API_RECEIVE_HISTORY = "recived_history";
    public static String API_SHIPMENT_HISTORY = "shipment_history";
    public static String API_REPLACE_WARRANTY = "warranty_replacement";
    public static String API_START_WARRANTY = "start_warranty";
    public static String API_SEARCH_HISTORY = "history_with_date";
    //    public static String CHANGE_NO_HOST = API_BASE + "changeMobileNumber.json?";
    public static String CHANGE_NO_OLD_NO_KEY = "old_mobile_num";
    public static String CHANGE_NO_NEW_NO_KEY = "new_mobile_num";
    public static String CHANGE_NO_EMAIL_KEY = "reg_email";
    //    public static String CHANGE_NO_VER_HOST = API_BASE + "changeMobNumKeyVal.json?";
    public static String CHANGE_NO_VER_KEY = "verify_key";
    public static String CHANGE_NO_VER_OLD_NO_KEY = "old_mobile_num";
    public static String CHANGE_NO_VER_NEW_NO_KEY = "new_mobile_num";

    // --- To Verify OTP
    public static String CHANGE_NO_VER_EMAIL_KEY = "reg_email";
    //    public static String SHARE_THIS_APP_HOST = API_BASE + "tellAppToFriend.json?";
    public static String API_SUBMIT_FEEDBACK = API_BASE + "UserFeedBack";
    /**
     * old_name
     * new_name
     * auth_token
     */
    public static String API_UPDATE_NAME = API_BASE + "changeRegName";
    /**
     * old_email
     * new_name
     * auth_token
     */
    public static String API_VERIFY_EMAIL = API_BASE + "changeRegEmail";
    /**
     * new_email
     * verify_key
     * auth_token
     */
    public static String API_UPDATE_EMAIL = API_BASE + "changeRegEmailKeyVal";
    /**
     * old_mobile_num
     * new_mobile_num
     * auth_token
     */
    public static String API_VERIFY_MOBILE_NUMBER = API_BASE + "changeMobileNumber";
    /**
     * new_mobile_num
     * auth_token
     * verify_key
     */
    public static String API_UPDATE_MOBILE_NUMBER = API_BASE + "changeMobNumKeyVal";

//*****************************************************************************************************

    /* New Api From D*/
    public static String API_DIRECT_SHIPMENT = "direct_shipment";
    public static String API_SHIPMENT_DONE = "agency_dispatch_mapping";
    public static String CURRENT_STOCK = "current_stock";
    public static String API_AGENT_LIST = "agencies_list";
    public static String API_PECKED_CARTOON = "packed_carton";

//*****************************************************************************************************

    public static String API_REWARDS_POINTS_LIST = API_BASE + "points_list";
    public static String API_REWARDS_OFFER_LIST = API_BASE + "offer_list";
    public static String API_REWARDS_EARNED_LIST = API_BASE + "earn_history";
    public static String API_REWARDS_REDEMPTION_LIST = API_BASE + "redeem_history";
    public static String API_REWARDS_COMPLETE_REDEEM = API_BASE + "complete_redeem";
    public static String DYNAMIC_URL_BASE = HOST_URL + "pages/";
    public static String URL_ABOUT_US_EN = DYNAMIC_URL_BASE + "au_en";


    // --- To Change Number
    public static String URL_ABOUT_US_HI = DYNAMIC_URL_BASE + "au_hi";
    public static String URL_APP_BASICS_EN = DYNAMIC_URL_BASE + "ab_en";
    public static String URL_APP_BASICS_HI = DYNAMIC_URL_BASE + "ab_hi";
    public static String URL_FAQ_EN = DYNAMIC_URL_BASE + "faq_en";
    public static String URL_FAQ_HI = DYNAMIC_URL_BASE + "faq_hi";
    public static String URL_LICENSE_EN = DYNAMIC_URL_BASE + "l_en";
    public static String URL_LICENSE_HI = DYNAMIC_URL_BASE + "l_hi";

    // --- To Change Number Verify
    public static String URL_TERMS_OF_SERVICES_EN = DYNAMIC_URL_BASE + "tnc_en";
    public static String URL_TERMS_OF_SERVICES_HI = DYNAMIC_URL_BASE + "tnc_hi";
    public static String responseData;
    public static String QR_CODE = "U1K24U9A";
    public static String MOBILE_NUMBER = "919987724124";
    public static String EMAIL = "pravinh@onspotsolutions.com";
    public static String EMAIL_RESULT = "yes";
    public static String NAME = "Sachin";
    public static String STATUS = "true";
    public static String LATITUDE = "19.058143";

    // --- To Share App
    public static String LONGITUDE = "72.897034";
    public static String ACCURACY = "12.2";
    public static String REPORT_INVALID_QR_CODE_VALUE = "n/a";
    public static String REPORT_USER_COMMENT_VALUE = "n/a";
    public static String USER_COUNTRY_KEY = "country";
    public static String USER_COUNTRY_CODE = "isoCode";
    public static String app_user_id = "n/a";
    public static String USER_EMAIL_VALUE = "n/a";
    public static String USER_MOB_VALUE = "n/a";
    public static String USER_NAME_VALUE = "n/a";
    public static String USER_COUNTRY_VALUE = "n/a";
    public static String USER_STATUS_VALUE = "n/a";
    public static String OTP_VER_VALUE = "verify_key";
    public static String OTP_MOB_VALUE = "mobile_number";
    public static String OTP_USER_EXIST_VALUE = "clicked_on_existing";
    public static String CHANGE_NO_OLD_NO_VALUE = "n/a";
    public static String CHANGE_NO_NEW_NO_VALUE = "n/a";
    public static String CHANGE_NO_EMAIL_VALUE = "n/a";
    public static String CHANGE_NO_VER_VALUE = "n/a";
    public static String CHANGE_NO_VER_OLD_NO_VALUE = "n/a";
    public static String CHANGE_NO_VER_NEW_NO_VALUE = "n/a";
    public static String CHANGE_NO_VER_EMAIL_VALUE = "n/a";
    public static String CrashError = "Oops! Something went wrong on server.";
}
