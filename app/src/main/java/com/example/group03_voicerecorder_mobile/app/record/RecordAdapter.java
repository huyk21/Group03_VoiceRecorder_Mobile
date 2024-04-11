package com.example.group03_voicerecorder_mobile.app.record;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.app.audio_player.PlayBackActivity;
import com.example.group03_voicerecorder_mobile.data.database.DatabaseHelper;

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
        return null;
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

    private void toPlaybackActivity(int position) {
        Intent intent = new Intent(context, PlayBackActivity.class);
        intent.putExtra("recordPath", records.get(position).getFilePath());
        context.startActivity(intent);
    }

    @SuppressLint({"ResourceType", "NonConstantResourceId"})
    private void showPopupMenu(View anchorView, int position) {
        PopupMenu popupMenu = new PopupMenu(context, anchorView);
        popupMenu.getMenuInflater().inflate(R.layout.record_item_popup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
//            System.out.println(item.getTitle());
            switch (item.getTitle().toString()) {
                case "Delete":
                    deleteRecord(position);
                    return true;
                case "Edit":
                    showRenameFileDialog(position);
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.show();
    }
//sd
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
                    notifyDataSetChanged();
                    // Update file name in the database
                    updateFileNameInDatabase(records.get(position).getId(), newFileName);
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void updateFileNameInDatabase(int recordId, String newFileName) {
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