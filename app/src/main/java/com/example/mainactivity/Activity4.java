package com.example.mainactivity;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;

import android.nfc.NfcAdapter;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;


import android.content.Intent;
import android.nfc.Tag;

import android.app.PendingIntent;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Activity4 extends AppCompatActivity {
    TextView bdtext;
    Spinner spinner, spinnerdate;
    DBHelper dbHelper;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main4);
        dbHelper = new DBHelper(this);
        btn = (Button) findViewById(R.id.butres);
        bdtext = (TextView)findViewById(R.id.textbd2) ;
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query("subject", new String[]{"_id", "name"}, null, null, null, null, null);

        spinner = (Spinner) findViewById(R.id.spinner2);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, cursor, new String[] {"name"}, new int[] {android.R.id.text1});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        spinnerdate = (Spinner) findViewById(R.id.spinner4);
        Cursor cursordate = database.rawQuery("select distinct date from date", null);
        String arrdate[] = new String[cursordate.getCount()];
        cursordate.moveToFirst();
                for (int i =0; i<arrdate.length; i++) {
                    arrdate[i] = cursordate.getString(0);
                    cursordate.moveToNext();
                }
                ArrayAdapter<String> adp =new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, arrdate);
                spinnerdate.setAdapter(adp);
    }

    public void ButtonResult(View view)
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        bdtext.setText("");
        Cursor itemText = (Cursor) spinner.getSelectedItem();
        String subject = itemText.getString(1);
   //     Cursor itemText1 = (Cursor) spinnerdate.getSelectedItem();
       String date1 = spinnerdate.getSelectedItem().toString();

        String selection = "name = ?";
        String[] selectionArgs = new String[] { subject };
        Cursor c = db.query("subject", null, selection, selectionArgs, null, null, null);
        String subjectid = "";
        if(c.moveToFirst()){
            do{
                subjectid = c.getString(c.getColumnIndex("_id"));
            }while (c.moveToNext());
        }
        c.close();



        selection = "subject = ? and date = ?";
        selectionArgs = new String[] { subjectid, date1 };
        c = db.query("date", null, selection, selectionArgs, null, null, null);
        if(c.moveToFirst()){
            do{
                Cursor cs = db.query("contacts", null, "_id = ?", new String[] { c.getString(c.getColumnIndex("chipId")) }, null, null, null);
                if(cs.moveToFirst()) {
                    do {
                        bdtext.setText(bdtext.getText() + "\n" + "Студент " + cs.getString(cs.getColumnIndex("name")) + " был на занятии - " + subject + ", которое было - " + c.getString(c.getColumnIndex("date")));// + ", name = " + c.getString(c.getColumnIndex("name")));
                    } while (cs.moveToNext());
                    cs.close();
                }
            }while (c.moveToNext());
        }
        c.close();
    }

    @Override
        protected void onStart() {
            super.onStart();
        }

        @Override
        protected void onResume() {
            super.onResume();
        }


        @Override
        protected void onPause() {
            super.onPause();
        }

        @Override
        protected void onStop() {
            super.onStop();
        }

        @Override
        protected void onRestart() {
            super.onRestart();
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
        }
}
