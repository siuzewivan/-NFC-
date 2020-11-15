package com.example.mainactivity;

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

public class Activity2 extends AppCompatActivity implements View.OnClickListener {

    private NfcAdapter nfcAdapter;
    TextView textbd;
    Button btnAddStud, btnRead, btnClear, btnUpd, btnDel, btnAddSub, btnReadSub, btnClearSub;
    EditText etName, etEmail, etId, etSub;
    int flag = 0;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main2);

        textbd = (TextView)findViewById(R.id.textbd);


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


        btnAddStud = (Button)findViewById(R.id.btnAddStud);
        btnAddStud.setOnClickListener(this);

        btnClearSub = (Button)findViewById(R.id.btnClearSub);
        btnClearSub.setOnClickListener(this);

        btnAddSub = (Button)findViewById(R.id.btnAddSub);
        btnAddSub.setOnClickListener(this);

        btnRead = (Button)findViewById(R.id.btnRead);
        btnRead.setOnClickListener(this);

        btnReadSub = (Button)findViewById(R.id.btnReadSub);
        btnReadSub.setOnClickListener(this);

        btnClear = (Button)findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);

        btnUpd = (Button)findViewById(R.id.btnUpd);
        btnUpd.setOnClickListener(this);

        btnDel = (Button)findViewById(R.id.btnDel);
        btnDel.setOnClickListener(this);

        etName = (EditText)findViewById(R.id.etName);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etId = (EditText)findViewById(R.id.etId);
        etSub = (EditText)findViewById(R.id.etSub);

        dbHelper = new DBHelper(this);
    }


    public void onClick(View v){
        String name;
        String email = etEmail.getText().toString();
        String id = etId.getText().toString();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        switch (v.getId()){

            case R.id.btnAddSub:
                name = etSub.getText().toString();

                String selection = "name = ?";
                String[] selectionArgs = new String[] { name };
                Cursor c = db.query("subject", null, selection, selectionArgs, null, null, null);
                String sub = "";
                if(c.moveToFirst()){
                    do{
                        sub = c.getString(c.getColumnIndex("_id"));
                    }while (c.moveToNext());
                }
                c.close();

                int len = name.length();
                if ((sub == "") && (len != 0)) {
                contentValues.put(DBHelper.KEY_NAME, name);
                database.insert(DBHelper.TABLE_SUBJECT, null, contentValues);
                    Toast.makeText(this,
                            "Регистрация предмета прошла успешно",
                            Toast.LENGTH_SHORT).show();
                } else Toast.makeText(this,
                        "Такой предмет уже существует либо поле ввода пустое. Регистрация не состоялась",
                        Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnClearSub:
                database.delete(DBHelper.TABLE_SUBJECT, null, null);
                database.delete(DBHelper.TABLE_DATESTUDENT, null, null);
                break;

            case R.id.btnAddStud:
               if (flag == 1) {
                   selection = "chipId = ?";
                   selectionArgs = new String[]{id};
                   c = db.query("contacts", null, selection, selectionArgs, null, null, null);
                   String studid = "";
                   if (c.moveToFirst()) {
                       do {
                           studid = c.getString(c.getColumnIndex("_id"));
                       } while (c.moveToNext());
                   }
                   c.close();

                   String prov = etId.getText().toString();
                   name = etName.getText().toString();
                    len = name.length();

                   if (studid == "" && len != 0) {
                       contentValues.put(DBHelper.KEY_NAME, name);
                       contentValues.put(DBHelper.KEY_CHIPID, id);
                       database.insert(DBHelper.TABLE_CONTACTS, null, contentValues);
                       Toast.makeText(this,
                               "Регистрация студента прошла успешно",
                               Toast.LENGTH_SHORT).show();
                   } else Toast.makeText(this,
                           "Студент с таким тегом уже существует либо есть пустые поля. Регистрация не состоялась.",
                           Toast.LENGTH_SHORT).show();
               } else Toast.makeText(this,
                       "Тег не найден. Регистрация не состоялась.",
                       Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnRead:
                Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
                textbd.setText("");
                if (cursor.moveToFirst()){
                    int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
                    int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
                    int emailIndex = cursor.getColumnIndex(DBHelper.KEY_CHIPID);
                        do{
                            Log.d("mLog", "ID = " + cursor.getInt(idIndex) + ", name - " + cursor.getString(nameIndex) + ", chipid = " + cursor.getString(emailIndex));
                            textbd.setText(textbd.getText() + "\n" + "ID = " + cursor.getInt(idIndex) + ", name - " + cursor.getString(nameIndex) + ", chipid = " + cursor.getString(emailIndex));

                        } while (cursor.moveToNext());
                } else
                    Log.d("mLog", "0 rows");

                cursor.close();
                break;


            case R.id.btnReadSub:
                cursor = database.query(DBHelper.TABLE_SUBJECT, null, null, null, null, null, null);
                textbd.setText("");
                if (cursor.moveToFirst()){
                    int idIndex = cursor.getColumnIndex("_id");
                    int nameIndex = cursor.getColumnIndex("name");
                    do{
                        Log.d("mLog", "ID = " + cursor.getInt(idIndex) + ", name - " + cursor.getString(nameIndex));
                        textbd.setText(textbd.getText() + "\n" + "ID = " + cursor.getInt(idIndex) + ", name - " + cursor.getString(nameIndex));
                    } while (cursor.moveToNext());
                } else
                    Log.d("mLog", "0 rows");

                cursor.close();
                break;



            case R.id.btnClear:
                database.delete(DBHelper.TABLE_CONTACTS, null, null);
                break;

            case R.id.btnUpd:
                if (id.equalsIgnoreCase("")){
                    break;
                }
                contentValues.put(DBHelper.KEY_CHIPID, email);
                //contentValues.put(DBHelper.KEY_NAME, name);
                int updCount = database.update(DBHelper.TABLE_CONTACTS, contentValues, DBHelper.KEY_ID + "= ?", new String[] {id});
                Log.d("mLog", "update rows count = " + updCount);
                break;
        }
        dbHelper.close();
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
        // Intent intent = getIntent();
        String action = intent.getAction();

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            Toast.makeText(this,
                    "onResume() - Обнаружено действие",
                    Toast.LENGTH_SHORT).show();

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if(tag == null){
                Toast.makeText(this,
                        "тег = null",
                        Toast.LENGTH_SHORT).show();
            }else{
                String tagInfo = "";
                byte[] tagId = tag.getId();
                for(int i=0; i<tagId.length; i++){
                    tagInfo += Integer.toHexString(tagId[i] & 0xFF) + " ";
                }
                flag = 1;
                etId.setText(tagInfo);
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
