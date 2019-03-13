package com.surya.onspot.qrscanFragments;

import java.io.Serializable;

/**
 * Created by Prasanna on 2/13/2018.
 */

public class ListModel implements Serializable {
    String data = "";
    private boolean isCheckBoxChecked = false;

    public ListModel(String data) {
        this.data = data;
    }

    public boolean isCheckBoxChecked() {
        return isCheckBoxChecked;
    }

    public void setCheckBoxChecked(boolean checkBoxChecked) {
        isCheckBoxChecked = checkBoxChecked;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
