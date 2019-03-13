package com.surya.onspot.QRresponse;

import java.io.Serializable;

/**
 * Created by Prasanna on 08-Apr-18.
 */

public class NewShipmentDetailModel implements Serializable {

    private String NewShipmentID = "";
    private String PackedRatio = "";
    private String ShipmentCode = "";
    private String ProductName = "";
    private String UploadToServer = "";

    public NewShipmentDetailModel(String newShipmentID, String packedRatio, String shipmentCode, String productName, String uploadToServer) {
        NewShipmentID = newShipmentID;
        PackedRatio = packedRatio;
        ShipmentCode = shipmentCode;
        ProductName = productName;
        UploadToServer = uploadToServer;
    }

    public String getNewShipmentID() {
        return NewShipmentID;
    }

    public void setNewShipmentID(String newShipmentID) {
        NewShipmentID = newShipmentID;
    }

    public String getPackedRatio() {
        return PackedRatio;
    }

    public void setPackedRatio(String packedRatio) {
        PackedRatio = packedRatio;
    }

    public String getShipmentCode() {
        return ShipmentCode;
    }

    public void setShipmentCode(String shipmentCode) {
        ShipmentCode = shipmentCode;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getUploadToServer() {
        return UploadToServer;
    }

    public void setUploadToServer(String uploadToServer) {
        UploadToServer = uploadToServer;
    }
}
