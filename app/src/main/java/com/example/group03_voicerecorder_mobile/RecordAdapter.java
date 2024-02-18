package com.example.group03_voicerecorder_mobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RecordAdapter extends BaseAdapter {
    Context context;
    String dateList[];
    String primaryDateList[];
    LayoutInflater inflater;

    public RecordAdapter(Context ctx, String dateList[], String primaryDateList[]) {
        this.context = ctx;
        this.dateList = dateList;
        this.primaryDateList = primaryDateList;
        inflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount() {
        return dateList.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.record_item, null);
        TextView textPrimary = (TextView) convertView.findViewById(R.id.textPrimary);
        TextView textSecondary = (TextView) convertView.findViewById(R.id.textSecondary);
        textPrimary.setText(primaryDateList[position]);
        textSecondary.setText(dateList[position]);
        return convertView;
    }
}
