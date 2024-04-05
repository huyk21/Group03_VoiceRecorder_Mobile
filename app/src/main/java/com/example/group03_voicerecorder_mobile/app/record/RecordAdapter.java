package com.example.group03_voicerecorder_mobile.app.record;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.group03_voicerecorder_mobile.R;

import java.util.List;

public class RecordAdapter extends BaseAdapter {
    Context context;
    String nameList[];
    String primaryDateList[];
    List<Record> records;
    LayoutInflater inflater;

    public RecordAdapter(Context ctx, List<Record> records) {
        this.context = ctx;
        this.records = records;
        inflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount() {
        return records.size();
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
        convertView = inflater.inflate(R.layout.list_record_item, null);
        TextView recordTitle = (TextView) convertView.findViewById(R.id.recordTitle);
        TextView recordDate = (TextView) convertView.findViewById(R.id.recordDate);
        TextView recordDuration = (TextView) convertView.findViewById(R.id.recordDuration);
        recordTitle.setText(records.get(position).getFilename());
        recordDate.setText(records.get(position).getTimestampString());
        recordDuration.setText(records.get(position).getDurationString());

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Show the popup menu
                showPopupMenu(v, position);
                return true;
            }
        });
        return convertView;
    }
    @SuppressLint("ResourceType")
    private void showPopupMenu(View anchorView, int position) {
        PopupMenu popupMenu = new PopupMenu(context, anchorView);
        popupMenu.getMenuInflater().inflate(R.layout.record_item_popup, popupMenu.getMenu());
        popupMenu.show();
    }
}