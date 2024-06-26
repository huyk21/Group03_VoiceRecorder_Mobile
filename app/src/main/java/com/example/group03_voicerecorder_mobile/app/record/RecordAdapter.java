package com.example.group03_voicerecorder_mobile.app.record;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.app.GlobalConstants;
import com.example.group03_voicerecorder_mobile.app.audio_player.PlayBackActivity;
import com.example.group03_voicerecorder_mobile.app.details.DetailsActivities;
import com.example.group03_voicerecorder_mobile.app.search_audio.SearchAudioActivity;
import com.example.group03_voicerecorder_mobile.app.settings.UploadActivity;
import com.example.group03_voicerecorder_mobile.audio.TrimAudioActivity;
import com.example.group03_voicerecorder_mobile.data.database.DatabaseHelper;
import com.example.group03_voicerecorder_mobile.utils.AudioAPI;
import com.example.group03_voicerecorder_mobile.utils.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecordAdapter extends BaseAdapter {
    Context context;
    List<Record> records;
    LayoutInflater inflater;

    public RecordAdapter(Context ctx, List<Record> records) {
        this.context = ctx;
        this.records = records;
        inflater = LayoutInflater.from(ctx);
    }

    public void setRecordList(List<Record> updatedRecords) {
        this.records = updatedRecords;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return records.size();
    }

    @Override
    public Object getItem(int position) {
        Record record = records.get(position);
        return record;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.list_record_item, null);
        TextView recordTitle = convertView.findViewById(R.id.recordTitle);
        TextView recordDate = convertView.findViewById(R.id.recordDate);
        TextView recordDuration = convertView.findViewById(R.id.recordDuration);
        ImageButton bookmarkedBtn = convertView.findViewById(R.id.bookmarkButton);
        ImageButton playBtn = convertView.findViewById(R.id.playBtn);

        recordTitle.setText(records.get(position).getFilename());
        recordDate.setText(records.get(position).getTimestampString());
        recordDuration.setText(records.get(position).getDurationString());
        if (records.get(position).getBookmarked() == 1)
            bookmarkedBtn.setImageResource(R.drawable.baseline_bookmark_48);
        else
            bookmarkedBtn.setImageResource(R.drawable.bookmark_border_24);

        convertView.setOnLongClickListener(v -> {
            // Show the popup menu
            showPopupMenu(v, position);
            return true;
        });

        Record record = records.get(position);
        convertView.setBackgroundColor(record.isSelected() ? Color.LTGRAY : Color.TRANSPARENT);

        // Set initial bookmark state based on the record's bookmarked field
        int isBookmarked = records.get(position).getBookmarked();
        setBookmarkIcon(bookmarkedBtn, isBookmarked);

        bookmarkedBtn.setOnClickListener(v -> {
            // Update the UI with the new bookmark state
            records.get(position).setBookmarked(records.get(position).getBookmarked() == 1 ? 0 : 1);

            setBookmarkIcon(bookmarkedBtn, records.get(position).getBookmarked());

            // Update the database with the new bookmark state
            updateBookmarkState(records.get(position).getId(), records.get(position).getBookmarked());

        });
        playBtn.setOnClickListener(v -> {
            toPlaybackActivity(position);
        });
        return convertView;
    }

    public boolean containsNull() {
        for (Record record : records) {
            if (record == null) {
                return true;  // Returns true if any record is null
            }
        }
        return false;  // Returns false if no null records are found
    }

    public void selectAllRecords() {
        for (Record record : records) {
            record.setSelected(true);
        }

    }

    public List<Record> getSelectedRecord() {
        List<Record> selectedRecords = new ArrayList<>();
        for (Record record : records) {
            if (record.isSelected()) {
                selectedRecords.add(record);
            }
        }
        return selectedRecords;
    }

    public void deleteSelectedRecords() {

        for (int i = records.size() - 1; i >= 0; i--) {  // Start from the end of the list
            Record record = records.get(i);

            if (record.isSelected()) {

                deleteRecordWithoutNotify(i);

            }
        }
        notifyDataSetChanged();
    }


    // This method checks if all records are selected to handle button text changes
    public boolean areAllSelected() {
        for (Record record : records) {
            if (!record.isSelected()) {
                return false; // If any record is not selected, return false
            }
        }
        return true; // All records are selected
    }

    private void toPlaybackActivity(int position) {
        Intent intent = new Intent(context, PlayBackActivity.class);
        intent.putExtra("recordId", records.get(position).getId());
        intent.putExtra("recordPath", records.get(position).getFilePath());
        intent.putExtra("recordName", records.get(position).getFilename());
        context.startActivity(intent);
    }

    private void toUploadActivity(int position) {
        Intent intent = new Intent(context, UploadActivity.class);
        intent.putExtra("recordId", records.get(position).getId());
        intent.putExtra("recordPath", records.get(position).getFilePath());
        intent.putExtra("recordName", records.get(position).getFilename());
        context.startActivity(intent);
    }

    private void toSearchAudioActivity(int position) {
        Intent intent = new Intent(context, SearchAudioActivity.class);
        intent.putExtra("recordId", records.get(position).getId());
        intent.putExtra("recordPath", records.get(position).getFilePath());
        intent.putExtra("recordName", records.get(position).getFilename());
        context.startActivity(intent);
    }

    private void toTrimAudioActivity(int position) {
        Intent intent = new Intent(context, TrimAudioActivity.class);
        intent.putExtra("record", records.get(position));
        context.startActivity(intent);
    }


    @SuppressLint({"ResourceType", "NonConstantResourceId"})
    private void showPopupMenu(View anchorView, int position) {
        PopupMenu popupMenu = new PopupMenu(context, anchorView);
        popupMenu.getMenuInflater().inflate(R.layout.record_item_popup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getTitle().toString()) {
                case "Delete":
                    deleteRecord(position);
                    return true;
                case "Edit":
                    showRenameFileDialog(position);
                    return true;
                case "Convert Format":
                    toUploadActivity(position);
                    return true;
                case "Search in file":
                    toSearchAudioActivity(position);
                    return true;
                case "Share":
                    shareRecord(position);
                    return true;
                case "Remove silence":
                    System.out.println("remove silence");
                    AudioAPI.removeSilence(context, records.get(position).getFilePath());
                    return true;
                case "Reduce noise":
                    AudioAPI.reduceNoise(context, records.get(position).getFilePath());
                    return true;
                case "Info":
                    accessRecordInfo(position);
                    return true;
                case "Trim audio":
                    toTrimAudioActivity(position);
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.show();
    }

    private void accessRecordInfo(int position) {
        Record record = records.get(position);
        String name = record.getFilename();

        Intent intent = new Intent(context, DetailsActivities.class);
        intent.putExtra("recordName", name);
        context.startActivity(intent);
    }

    //sd
    private void shareRecord(int position) {
        Record record = records.get(position);
        File file = new File(record.getFilePath());

        if (!file.exists()) {
            Toast.makeText(context, "File does not exist", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("audio/*"); // MIME type for audio
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(shareIntent, "Share Audio File"));
        } catch (IllegalArgumentException e) {
            Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show();
        }
    }


    private void deleteRecordWithoutNotify(int position) {
        int recordId = records.get(position).getId();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        databaseHelper.updateDeletedState(recordId, 1);
        records.remove(position);

    }

    private void deleteRecord(int position) {
        int recordId = records.get(position).getId();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        databaseHelper.updateDeletedState(recordId, 1);
        records.remove(position);
        notifyDataSetChanged();
    }

    private void showRenameFileDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_rename_file, null);
        EditText editTextFileName = dialogView.findViewById(R.id.editTextFileName);
        editTextFileName.setText(records.get(position).getFilename());
        builder.setView(dialogView);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newFileName = editTextFileName.getText().toString().trim();
                if (!newFileName.isEmpty()) {
                    // Update file name in the list
                    records.get(position).setFilename(newFileName);
                    Utilities.changeFileName(newFileName + GlobalConstants.FORMAT_M4A, records.get(position).getFilePath(), context);
                    records.get(position).setFilePath(context.getExternalFilesDir(null).getAbsolutePath() + "/" + newFileName + GlobalConstants.FORMAT_M4A);
                    notifyDataSetChanged();
                    // Update file name in the database
                    updateFileNameInDatabase(position, records.get(position).getId(), newFileName);
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateFileNameInDatabase(int position, int recordId, String newFileName) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        int rowsAffected = databaseHelper.updateFileName(recordId, newFileName);

        if (rowsAffected > 0) {
            notifyDataSetChanged();
        } else {
            Toast.makeText(context, "Failed to update filename", Toast.LENGTH_SHORT).show();
        }
    }

    private void setBookmarkIcon(ImageButton bookmarkButton, int bookmarked) {
        if (bookmarked == 1) {
            bookmarkButton.setImageResource(R.drawable.baseline_bookmark_48);
        } else {
            bookmarkButton.setImageResource(R.drawable.bookmark_border_24);
        }
    }

    private void updateBookmarkState(int recordId, int bookmarked) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        int rowsAffected = databaseHelper.updateBookmarkState(recordId, bookmarked);
        if (rowsAffected > 0) {
            Toast.makeText(context, "Bookmark state updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Failed to update bookmark state", Toast.LENGTH_SHORT).show();
        }
    }
}