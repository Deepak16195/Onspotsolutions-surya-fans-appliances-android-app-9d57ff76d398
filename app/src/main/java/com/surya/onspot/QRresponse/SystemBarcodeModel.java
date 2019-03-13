package com.surya.onspot.QRresponse;

import org.json.JSONArray;

import java.io.Serializable;

/**
 * Created by Prasanna on 08-Apr-18.
 */

public class SystemBarcodeModel implements Serializable {

    private JSONArray jsonArray = new JSONArray();

    public SystemBarcodeModel(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }
}
