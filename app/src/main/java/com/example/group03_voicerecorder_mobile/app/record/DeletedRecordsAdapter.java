package com.example.group03_voicerecorder_mobile.app.record;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.data.database.DatabaseHelper;

import org.w3c.dom.Text;

import java.util.List;

public class DeletedRecordsAdapter extends BaseAdapter {
    Context context;
    List<Record> deletedRecords;
    LayoutInflater inflater;

    public DeletedRecordsAdapter(Context ctx, List<Record> records) {
        this.context = ctx;
        this.deletedRecords = records;
        inflater = LayoutInflater.from(ctx);
    }
    @Override
    public int getCount() {
        return deletedRecords.size();
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
        convertView = inflater.inflate(R.layout.list_deleted_item, null);
        TextView title = convertView.findViewById(R.id.recordName);
        TextView duration = convertView.findViewById(R.id.recordDuration);
        TextView date = convertView.findViewById(R.id.recordDate);
        AppCompatButton recover = convertView.findViewById(R.id.recover_btn);
        AppCompatButton delete = convertView.findViewById(R.id.delete_btn);

        String fileNameNoExt = deletedRecords.get(position).getFilename().substring(0, deletedRecords.get(position).getFilename().lastIndexOf("."));
        title.setText(fileNameNoExt);
        date.setText(deletedRecords.get(position).getTimestampString());
        duration.setText(deletedRecords.get(position).getDurationString());

        recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoverRecord(position);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePermanently(position);
            }
        });

        return convertView;
    }

    public void recoverRecord(int position) {
        int recordId = deletedRecords.get(position).getId();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        databaseHelper.updateDeletedState(recordId, 0);
        deletedRecords.remove(position);
        notifyDataSetChanged();
    }

    public void deletePermanently(int position) {
        int recordId = deletedRecords.get(position).getId();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        databaseHelper.deleteRecording(recordId);
        deletedRecords.remove(position);
        notifyDataSetChanged();
    }
}
