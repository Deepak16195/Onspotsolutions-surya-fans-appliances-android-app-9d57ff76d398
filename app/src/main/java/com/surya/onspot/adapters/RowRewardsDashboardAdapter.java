package com.surya.onspot.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.surya.onspot.R;
import com.surya.onspot.model.RewardsDashboardModel;
import com.surya.onspot.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class RowRewardsDashboardAdapter extends BaseAdapter {

    private List<RewardsDashboardModel> objects = new ArrayList<RewardsDashboardModel>();

    private Context context;
    private LayoutInflater layoutInflater;

    public RowRewardsDashboardAdapter(Context context, List<RewardsDashboardModel> objects) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.objects = objects;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public RewardsDashboardModel getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.row_rewards_dashboard, null);
            convertView.setTag(new ViewHolder(convertView));
        }
        initializeViews(getItem(position), (ViewHolder) convertView.getTag());
        return convertView;
    }

    private void initializeViews(RewardsDashboardModel object, ViewHolder holder) {
        //TODO implement
        Utils.out("IMAGE URL : " + object.getImageUrl());
        if (object.getImageUrl().trim().length() > 0) {
            Picasso.with(context).load(object.getImageUrl()).into(holder.imageViewRewardsDashboardImage);
        }
        holder.textViewRewardsDashboardCount.setText(object.getTitleCount());
        holder.textViewRewardsDashboardTitle.setText(object.getTitle());
    }

    protected class ViewHolder {
        private ImageView imageViewRewardsDashboardImage;
        private TextView textViewRewardsDashboardCount;
        private TextView textViewRewardsDashboardTitle;

        public ViewHolder(View view) {
            imageViewRewardsDashboardImage = view.findViewById(R.id.imageView_rewards_dashboard_image);
            textViewRewardsDashboardCount = view.findViewById(R.id.textView_rewards_dashboard_count);
            textViewRewardsDashboardTitle = view.findViewById(R.id.textView_rewards_dashboard_title);
        }
    }
}
