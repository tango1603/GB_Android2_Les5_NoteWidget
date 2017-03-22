package com.example.ilya.gb_android2_les5_notewidget.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.ilya.gb_android2_les5_notewidget.Controller;
import com.example.ilya.gb_android2_les5_notewidget.R;
import com.example.ilya.gb_android2_les5_notewidget.models.tables.TblNotes;
import com.example.ilya.gb_android2_les5_notewidget.models.tables.TblNotes_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

public class Note extends AppCompatActivity {

    private static final String TAG = "DEBUGGG";
    private EditText noteName;
    private EditText noteText;
    private Intent mIntent;
    private String nodeNameStr;
    private String nodeTextStr;
    private int position = -10;
    private Controller controller;


    private void findEditNode(int nodePosition) {
        if (nodePosition != -10) {
            TblNotes queryTbl = SQLite.select()
                    .from(TblNotes.class)
                    .where(TblNotes_Table.listPosition.eq(nodePosition))
                    .querySingle();
            nodeNameStr = queryTbl.getNoteName();
            nodeTextStr = queryTbl.getNoteText();
        } else return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        noteName = (EditText) findViewById(R.id.etName);
        noteText = (EditText) findViewById(R.id.etText);
        noteName.setFocusableInTouchMode(true);
        noteName.setFocusable(true);
        noteName.setCursorVisible(true);
        mIntent = getIntent();
        position = mIntent.getIntExtra(NoteList.NOTE_POSITION, -10);
        findEditNode(position);
        controller = new Controller(this);
    }

    @Override
    protected void onResume() {
        noteName.setText(nodeNameStr);
        noteText.setText(nodeTextStr);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.menu_save: {
                if (position == -10) {
                    if (noteName.getText().toString() != "") {
                        controller.addElement(noteName.getText().toString(), noteText.getText().toString());
                        setResult(RESULT_OK, intent);
                    } else {
                        setResult(RESULT_CANCELED, intent);
                    }
                } else {
                    if (noteName.getText().toString() != "") {
                        if (!noteName.getText().toString().isEmpty() && position != -10) {
                            controller.editElement(position, noteName.getText().toString(), noteText.getText().toString());
                            setResult(RESULT_OK, intent);
                        }
                    } else {
                        setResult(RESULT_CANCELED, intent);
                    }
                }
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

