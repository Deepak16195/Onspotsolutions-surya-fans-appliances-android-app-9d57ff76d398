package com.surya.onspot.utils;

public class Listview_Item {
    private int icon;
    private String title;
    private String name;
    private String scanQrdata;
    private String So_Number_Date;
    private String responseMessage;
    private String Cpo_No;

    private String Cpo_Date;
    private String procurement_agency;
    private String Manufacturing_Location;
    private String Product_Refrence_Number;
    private String Dispatch_Date;
    private String Destination_Agency;
    private String Destination_State;
    private String Destination_Point;
    private String MRP;
    private String Serial_No;


    public Listview_Item() {
        super();
    }


    public Listview_Item(String scanQrdata, String So_Number_Date, String responseMessage, String Cpo_No, String Cpo_Date, String procurement_agency, String Manufacturing_Location, String Product_Refrence_Number, String Dispatch_Date, String Destination_Agency, String Destination_State, String Destination_Point, String MRP, String Serial_No) {
        super();

        this.scanQrdata = scanQrdata;
        this.So_Number_Date = So_Number_Date;
        this.responseMessage = responseMessage;
        this.Cpo_No = Cpo_No;
        this.Cpo_Date = Cpo_Date;
        this.procurement_agency = procurement_agency;
        this.Manufacturing_Location = Manufacturing_Location;
        this.Product_Refrence_Number = Product_Refrence_Number;
        this.Dispatch_Date = Dispatch_Date;
        this.Destination_Agency = Destination_Agency;
        this.Destination_State = Destination_State;
        this.Destination_Point = Destination_Point;
        this.MRP = MRP;
        this.Serial_No = Serial_No;
    }

    public String getscanQrdata() {
        return scanQrdata;
    }

    public String getSo_Number_Date() {
        return So_Number_Date;
    }

    public void setSo_Number_Date(String So_Number_Date) {
        this.So_Number_Date = So_Number_Date;
    }

    public String getresponseMessage() {
        return responseMessage;
    }

    public void setresponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getCpo_No() {
        return Cpo_No;
    }

    public void setCpo_No(String Cpo_No) {
        this.Cpo_No = Cpo_No;
    }

    public String getCpo_Date() {
        return Cpo_Date;
    }

    public void setCpo_Date(String Cpo_Date) {
        this.Cpo_Date = Cpo_Date;
    }

    public String getprocurement_agency() {
        return procurement_agency;
    }

    public void setprocurement_agency(String procurement_agency) {
        this.procurement_agency = procurement_agency;
    }

    public String getDispatch_Date() {
        return Dispatch_Date;
    }

    public void setDispatch_Date(String Dispatch_Date) {
        this.Dispatch_Date = Dispatch_Date;
    }

    public String getProduct_Refrence_Number() {
        return Product_Refrence_Number;
    }

    public void setProduct_Refrence_Number(String Product_Refrence_Number) {
        this.Product_Refrence_Number = Product_Refrence_Number;
    }

    public String getManufacturing_Location() {
        return Manufacturing_Location;
    }

    public void setManufacturing_Location(String Manufacturing_Location) {
        this.Manufacturing_Location = Manufacturing_Location;
    }

    public String getDestination_Agency() {
        return Destination_Agency;
    }

    public void setDestination_Agency(String Destination_Agency) {
        this.Destination_Agency = Destination_Agency;
    }

    public String getDestination_State() {
        return Destination_State;
    }

    public void setDestination_State(String Destination_State) {
        this.Destination_State = Destination_State;
    }

    public String getDestination_Point() {
        return Destination_Point;
    }

    public void setDestination_Point(String Destination_Point) {
        this.Destination_Point = Destination_Point;
    }

    public String getMRP() {
        return MRP;
    }

    public void setmRP(String MRP) {
        this.MRP = MRP;
    }

    public String getSerial_No() {
        return Serial_No;
    }

    public void setSerial_No(String Serial_No) {
        this.Serial_No = Serial_No;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}


