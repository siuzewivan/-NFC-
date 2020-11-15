package com.example.mainactivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;

import android.nfc.NfcAdapter;
import android.os.Vibrator;
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


public class Activity3 extends AppCompatActivity {

    final String LOG_TAG = "myLogs";

    private NfcAdapter nfcAdapter;
    TextView textbd;
    Spinner spinner;
    DBHelper dbHelper;
    EditText etDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main3);

       dbHelper = new DBHelper(this);

        spinner = (Spinner) findViewById(R.id.spinner);
        etDate = (EditText) findViewById(R.id.etDate);
        textbd = (TextView)findViewById(R.id.textbd3) ;

        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);
        etDate.setText(dateText);
        //etDate.setEnabled(false);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter == null){
            Toast.makeText(this,
                    "NFC NOT supported on this devices!",
                    Toast.LENGTH_LONG).show();
            finish();
        }else if(!nfcAdapter.isEnabled()){
            Toast.makeText(this,
                    "NFC NOT Enabled!",
                    Toast.LENGTH_LONG).show();
            finish();
        }


        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query("subject", new String[]{"_id", "name"}, null, null, null, null, null);

        spinner = (Spinner) findViewById(R.id.spinner);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, cursor, new String[] {"name"}, new int[] {android.R.id.text1});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
       // int name = cursor.getCount();
        //cursor.close();

    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);

    }


    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        String date;
        ContentValues contentValues = new ContentValues();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        SQLiteDatabase databaseread = dbHelper.getReadableDatabase();

        //получение тега
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
          //  Toast.makeText(this,
          //          "Обнаружено действие",
          //          Toast.LENGTH_SHORT).show();

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if(tag == null){
                Toast.makeText(this,
                        "onResume() - Тег = null",
                        Toast.LENGTH_SHORT).show();
            }else{
                String tagInfo = "";
                byte[] tagId = tag.getId();
                for(int i=0; i<tagId.length; i++){
                    tagInfo += Integer.toHexString(tagId[i] & 0xFF) + " ";
                }
                //получение тега


//запись в бд
                long elem = spinner.getSelectedItemId();
                Cursor itemText = (Cursor) spinner.getSelectedItem();
                String subject = itemText.getString(1);

                String selection = "name = ?";
                String[] selectionArgs = new String[] { subject };
                Cursor c = databaseread.query("subject", null, selection, selectionArgs, null, null, null);
                String subjectid = "";
                if(c.moveToFirst()){
                    do{
                        subjectid = c.getString(c.getColumnIndex("_id"));
                    }while (c.moveToNext());
                }
                c.close();

                selection = "chipId = ?";
                selectionArgs = new String[] { tagInfo };
                c = databaseread.query("contacts", null, selection, selectionArgs, null, null, null);
                String studentid = "";
                if(c.moveToFirst()){
                    do{
                        studentid = c.getString(c.getColumnIndex("_id"));
                    }while (c.moveToNext());
                }
                c.close();

                if (studentid == ""){
                    Toast.makeText(this,
                            "Студент не найден. Регистрация не состоялась",
                            Toast.LENGTH_SHORT).show();

                    long mills = 1500L;
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (vibrator.hasVibrator()) {
                        vibrator.vibrate(mills);
                    }

                    Toast.makeText(this,
                            "Студент не найден. Регистрация не состоялась",
                            Toast.LENGTH_SHORT).show();
                } else {

                    date = etDate.getText().toString();

                    selection = "chipId = ? and subject = ? and date = ?";
                    selectionArgs = new String[] { studentid, subjectid, date };
                    c = databaseread.query("date", null, selection, selectionArgs, null, null, null);
                    String result = "";
                    if(c.moveToFirst()){
                        do{
                            result = c.getString(c.getColumnIndex("_id"));
                        }while (c.moveToNext());
                    }
                    c.close();

                    if (result != "") {
                        Toast.makeText(this,
                                "Такая запись уже есть",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        contentValues.put(DBHelper.KEY_DATE, date);
                        contentValues.put(DBHelper.KEY_CHIPID, studentid);
                        contentValues.put(DBHelper.KEY_SUBJECT, subjectid);
                        database.insert(DBHelper.TABLE_DATESTUDENT, null, contentValues);
//запись в бд

                        Toast.makeText(this,
                                "Успешная операция",
                                Toast.LENGTH_SHORT).show();
                        //просмотр бд
                        Cursor cursor = database.query(DBHelper.TABLE_DATESTUDENT, null, null, null, null, null, null);
                        textbd.setText("");
                        if (cursor.moveToFirst()) {
                            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
                            int dateIndex = cursor.getColumnIndex(DBHelper.KEY_DATE);
                            int iddIndex = cursor.getColumnIndex(DBHelper.KEY_CHIPID);
                            int subIndex = cursor.getColumnIndex(DBHelper.KEY_SUBJECT);
                            do {
                                textbd.setText(textbd.getText() + "\n" + "ID = " + cursor.getInt(idIndex) + ", subjectID - " + cursor.getString(subIndex) + ", StudentID = " + cursor.getString(iddIndex) + ", date = " + cursor.getString(dateIndex));

                            } while (cursor.moveToNext());
                        } else
                            Log.d("mLog", "0 rows");

                        cursor.close();
//просмотр бд

                    }
                }
            }
        }else{
            Toast.makeText(this,
                    "onResume() : " + action,
                    Toast.LENGTH_SHORT).show();
        }


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
