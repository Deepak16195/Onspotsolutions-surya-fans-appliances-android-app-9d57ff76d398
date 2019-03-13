package com.surya.onspot.model;

import java.io.Serializable;

/**
 * Created by Prasanna on 2/10/2018.
 */

public class RewardsDashboardModel implements Serializable {

    private String ImageUrl = "";
    private String TitleCount = "";
    private String Title = "";
    private int BrandId = 0;


    public RewardsDashboardModel(String imageUrl, String titleCount, String title, int brandId) {
        ImageUrl = imageUrl;
        TitleCount = titleCount;
        Title = title;
        BrandId = brandId;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getTitleCount() {
        return TitleCount;
    }

    public void setTitleCount(String titleCount) {
        TitleCount = titleCount;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public int getBrandId() {
        return BrandId;
    }

    public void setBrandId(int brandId) {
        BrandId = brandId;
    }
}
