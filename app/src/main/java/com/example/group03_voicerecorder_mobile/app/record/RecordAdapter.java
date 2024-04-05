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
    String dateList[];
    String primaryDateList[];
    LayoutInflater inflater;
    int selectedItem = -1; // Variable to track selected item position

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

    // Method to set the selected item position
    public void setSelectedItem(int position) {
        selectedItem = position;
        notifyDataSetChanged(); // Refresh the list view
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        setSelectedItem(position);
        convertView = inflater.inflate(R.layout.list_record_item, null);
        TextView recordTitle = convertView.findViewById(R.id.recordTitle);
        TextView recordDate = convertView.findViewById(R.id.recordDate);
        recordTitle.setText(primaryDateList[position]);
        recordDate.setText(dateList[position]);

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

        // Handle menu item clicks (if needed)
        popupMenu.show();
    }
}
