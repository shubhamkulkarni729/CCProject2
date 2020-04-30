package com.example.ccproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import static com.example.ccproject.MainActivity.s;


public class AllCompaniesActivity extends AppCompatActivity {

    private ArrayList<HashMap<String,String>>list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_companies);

        String message = new String("Companies");
        BackgroundTask b1 = new BackgroundTask();
        Log.d("AA","mm");
        b1.execute(message);
    }

    class BackgroundTask extends AsyncTask<String,Void,Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                int shared = MainActivity.shared;
                String message = strings[0];

                DataOutputStream dout=new DataOutputStream(s.getOutputStream());

                dout.writeUTF(message);
                Log.d("NN","hh");

                DataInputStream din = new DataInputStream(s.getInputStream());

                Log.d("LLL","IIII");
                String total_str_encrypted = din.readUTF();
                String total_str_decrypted = decrypt(total_str_encrypted,shared);
                int total = Integer.parseInt(total_str_decrypted);
                Log.d("total",Integer.toString(total));

                for(int i=0;i<total;i++)
                {
                    String companyName = din.readUTF();
                    String salary = din.readUTF();
                    String studentsPlaced = din.readUTF();

                    companyName = decrypt(companyName,shared);
                    salary = decrypt(salary,shared);
                    studentsPlaced = decrypt(studentsPlaced,shared);
                    Log.d("RECEIVED",companyName+" "+studentsPlaced+" "+salary);  //To be added to listview
                }
            }
            catch(Exception e){

            }

            return null;
        }
    }

    private String decrypt(String data, int sharedValue)
    {
        String decryptedData = "";
        String[] splitData = data.split("!!");
        for (int i=0 ; i<splitData.length;i++)
        {
            char c = (char)(Integer.parseInt(splitData[i])-sharedValue);
            decryptedData = decryptedData.concat(Character.toString(c));
        }

        return decryptedData;
    }

    private String encrypt (String data, int sharedValue){
        String encryptedData = "";
        for (int i=0;i<data.length();i++)
        {
            Character character = new Character(data.charAt(i));
            int value = (int)character + sharedValue;
            encryptedData = encryptedData.concat(Integer.toString(value));
            encryptedData = encryptedData.concat("!!");
        }

        return encryptedData;
    }
}
