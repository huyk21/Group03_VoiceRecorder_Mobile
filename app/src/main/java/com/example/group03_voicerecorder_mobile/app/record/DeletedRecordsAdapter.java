package com.example.group03_voicerecorder_mobile.app.record;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.data.database.DatabaseHelper;
import com.example.group03_voicerecorder_mobile.utils.Utilities;

import org.w3c.dom.Text;

import java.io.File;
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
    public Object getItem(int position) {
        return deletedRecords.get(position);
    }

    @Override
    public long getItemId(int position) {
        return deletedRecords.get(position).getId();
    }
    @Override
    public int getCount() {
        return deletedRecords.size();
    }

    public void clearRecords() {
        deletedRecords.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.list_deleted_item, null);
        TextView title = convertView.findViewById(R.id.recordName);
        TextView duration = convertView.findViewById(R.id.recordDuration);
        TextView date = convertView.findViewById(R.id.recordDate);
        AppCompatButton recover = convertView.findViewById(R.id.recover_btn);
        AppCompatButton delete = convertView.findViewById(R.id.delete_btn);

        String fileNameNoExt = deletedRecords.get(position).getFilename();
        title.setText(fileNameNoExt);
        date.setText(deletedRecords.get(position).getTimestampString());
        duration.setText(deletedRecords.get(position).getDurationString());

        recover.setOnClickListener(v -> {
            int status = recoverRecord(position);
            if (status != 0) {
                Toast.makeText(context, "The record is successfully restored", Toast.LENGTH_SHORT).show();
            }
        });

        delete.setOnClickListener(v -> {
            showConfirmDeleteDialog(position);
        });

        return convertView;
    }

    public int recoverRecord(int position) {
        int recordId = deletedRecords.get(position).getId();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        int rows = databaseHelper.updateDeletedState(recordId, 0);
        deletedRecords.remove(position);
        notifyDataSetChanged();
        return rows;
    }

    public void deletePermanently(int position) {
        int recordId = deletedRecords.get(position).getId();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        databaseHelper.deleteRecording(recordId);
        deletedRecords.remove(position);
        // Generate the filename based on record ID
        String fileName = "amplitudes_" + recordId + ".json";

        // Determine where to save the file
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);

        // Check if the file exists before attempting to delete
        if(file.exists()) {
            boolean isDeleted = file.delete();
            if(isDeleted) {
                Log.d("Delete File", "Amplitude file deleted successfully.");
            } else {
                Log.e("Delete File", "Failed to delete amplitude file.");
            }
        }
        notifyDataSetChanged();
    }

    private void showConfirmDeleteDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_delete, null);
        builder.setView(dialogView);

        String filePath = deletedRecords.get(position).getFilePath();
        System.out.println(filePath);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utilities.deleteFile(filePath, context);
                deletePermanently(position);
                notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
