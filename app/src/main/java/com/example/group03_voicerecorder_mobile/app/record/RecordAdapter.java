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

public class RecordAdapter extends BaseAdapter {
    Context context;
    String nameList[];
    String primaryDateList[];
    LayoutInflater inflater;

    public RecordAdapter(Context ctx, String nameList[], String primaryDateList[]) {
        this.context = ctx;
        this.nameList = nameList;
        this.primaryDateList = primaryDateList;
        inflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount() {
        return nameList.length;
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
        recordTitle.setText(primaryDateList[position]);
        recordDate.setText(nameList[position]);

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