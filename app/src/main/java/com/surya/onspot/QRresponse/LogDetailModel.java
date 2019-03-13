package com.surya.onspot.QRresponse;

import java.io.Serializable;

/**
 * Created by Prasanna on 08-Apr-18.
 */

public class LogDetailModel implements Serializable {

    private int LogID = 0;
    private int NumberOfCartons = 0;
    private String ScannedData = "";
    private String LogDate = "";
    private String LogTime = "";
    private int LogCompleted = 0;
    private int LogUploadToServerStatus = 0;

    public LogDetailModel(int logID, int numberOfCartons, String scannedData, String logDate, String logTime, int logCompleted, int logUploadToServerStatus) {
        LogID = logID;
        NumberOfCartons = numberOfCartons;
        ScannedData = scannedData;
        LogDate = logDate;
        LogTime = logTime;
        LogCompleted = logCompleted;
        LogUploadToServerStatus = logUploadToServerStatus;
    }

    public int getLogID() {
        return LogID;
    }

    public void setLogID(int logID) {
        LogID = logID;
    }

    public int getNumberOfCartons() {
        return NumberOfCartons;
    }

    public void setNumberOfCartons(int numberOfCartons) {
        NumberOfCartons = numberOfCartons;
    }

    public String getScannedData() {
        return ScannedData;
    }

    public void setScannedData(String scannedData) {
        ScannedData = scannedData;
    }

    public String getLogDate() {
        return LogDate;
    }

    public void setLogDate(String logDate) {
        LogDate = logDate;
    }

    public String getLogTime() {
        return LogTime;
    }

    public void setLogTime(String logTime) {
        LogTime = logTime;
    }

    public int getLogCompleted() {
        return LogCompleted;
    }

    public void setLogCompleted(int logCompleted) {
        LogCompleted = logCompleted;
    }

    public int getLogUploadToServerStatus() {
        return LogUploadToServerStatus;
    }

    public void setLogUploadToServerStatus(int logUploadToServerStatus) {
        LogUploadToServerStatus = logUploadToServerStatus;
    }
}
