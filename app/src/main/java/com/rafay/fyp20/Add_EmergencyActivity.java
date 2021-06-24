package com.rafay.fyp20;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Add_EmergencyActivity extends Activity {
    EditText editText1,editText10;
    TextView btn1;
    Button addPhone;
    Toast toast;
    TextView toast_text;
    Typeface toast_font;
    LayoutInflater inflater;
    View layout;
    int flag=1;
    DBEmergency db;
    FirebaseAuth firebaseAuth;
    String email="";
    private static final int CONTACT_PERMISSION_CODE = 1;
    private static final int CONTACT_PICK_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_add);
        firebaseAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = firebaseAuth.getCurrentUser();
        email=user.getEmail();
        addPhone = (Button)findViewById(R.id.contactsAdd);
        toast_font = Typeface.createFromAsset(getAssets(), "AvenirNextLTPro-Cn.otf");
        inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) this.findViewById(R.id.toast));
        toast_text = (TextView) layout.findViewById(R.id.tv);
        toast = new Toast(this.getApplicationContext());
        editText1 = (EditText)findViewById(R.id.add_name);
        editText10=(EditText)findViewById(R.id.add_phone);
        editText10.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        btn1=(TextView)findViewById(R.id.text_add);
        db=new DBEmergency(this);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "AvenirNextLTPro-UltLtCn.otf");
        editText1.setTypeface(custom_font);
        editText10.setTypeface(custom_font);
        btn1.setTypeface(custom_font, Typeface.BOLD);
        //Toast variables initialisation
        toast_text.setTypeface(toast_font);
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText1.getText().toString().length()==0)
                {
                    toast_text.setText("Enter Name");
                    toast.show();
                }
                else if(editText10.toString().length()==0)
                {
                    toast_text.setText("Enter phone number");
                    toast.show();
                }
                else {
                    addContact(editText1.getText().toString(), editText10.getText().toString());
//                    String text = db.addContact(new EmerContact(editText1.getText().toString(),editText10.getText().toString(),email));
//                    toast_text.setText(text);
//                    toast.show();
//                    Intent intent = new Intent(Add_EmergencyActivity.this, MyEmerContActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
                }
            }
        });

        addPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkContactPermission()){
                    //permission granted, pick contact
                    pickContactIntent();
                }
                else {
                    //permission not granted, request
                    requestContactPermission();
                }

            }
        });



    }


    private void addContact(String name, String num){
        String text = db.addContact(new EmerContact(name,num,email));
        toast_text.setText(text);
        toast.show();
        Intent intent = new Intent(Add_EmergencyActivity.this, MyEmerContActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    private boolean checkContactPermission(){
        //check if contact permission was granted or not
        boolean result = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS) == (PackageManager.PERMISSION_GRANTED
        );

        return result;  //true if permission granted, false if not
    }

    private void requestContactPermission(){
        //permissions to request
        String[] permission = {Manifest.permission.READ_CONTACTS};

        ActivityCompat.requestPermissions(this, permission, CONTACT_PERMISSION_CODE);
    }

    private void pickContactIntent(){
        //intent to pick contact
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, CONTACT_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String contactName = "";
        String contactNumber = "";
        //handle intent results
        if (resultCode == RESULT_OK){
            //calls when user click a contact from list

            if (requestCode == CONTACT_PICK_CODE){
                //contactTv.setText("");

                Cursor cursor1, cursor2;

                //get data from intent
                Uri uri = data.getData();

                cursor1 = getContentResolver().query(uri, null, null, null, null);

                if (cursor1.moveToFirst()){
                    //get contact details
                    String contactId = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts._ID));
                    contactName = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String contactThumnail = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                    String idResults = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    int idResultHold = Integer.parseInt(idResults);

                    //contactTv.append("ID: "+contactId);
                    //contactTv.append("\nName: "+contactName);

                    if (idResultHold == 1){
                        cursor2 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+contactId,
                                null,
                                null
                        );
                        //a contact may have multiple phone numbers
                        while (cursor2.moveToNext()){
                            //get phone number
                            contactNumber = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            //set details

                            //contactTv.append("\nPhone: "+contactNumber);
                            //before setting image, check if have or not

                        }
                        cursor2.close();
                    }
                    cursor1.close();

                }
                addContact(contactName, contactNumber);

            }

        }
        else {
            //calls when user click back button | don't pick contact

        }
    }

}