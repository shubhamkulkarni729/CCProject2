package com.example.ccproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    static Socket s;
    static int shared;
    static int connected;

    private Button btnAllCompanies;
    private Button btnCompanyDetails;
    private Button btnDownloadRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(connected==0) {
            BackgroundTask b1 = new BackgroundTask();
            b1.execute();
        }

        btnAllCompanies = (Button)findViewById(R.id.btnAllCompanies);
        btnCompanyDetails = (Button)findViewById(R.id.btnCompanyDetails);
        btnDownloadRecords = (Button)findViewById(R.id.btnDownloadRecords);

        btnAllCompanies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connected==1) {
                    Intent intent = new Intent(MainActivity.this, AllCompaniesActivity.class);
                    startActivity(intent);
                    //finish();
                }
            }
        });

        btnCompanyDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connected==1) {
                    Intent intent = new Intent(MainActivity.this, CompanyDetailsActivity.class);
                    startActivity(intent);
                    //finish();
                }
            }
        });

        btnDownloadRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connected==1) {
                    try {
                        BackgroundTask1 b1 = new BackgroundTask1();
                        b1.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    class BackgroundTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                s = new Socket("192.168.43.104",6000);

                DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                DataInputStream din = new DataInputStream(s.getInputStream());

                BigInteger P = BigInteger.valueOf((long)761);
                BigInteger g = BigInteger.valueOf((long)6);

                int a = (int)(Math.random() * (761));
                Log.d("a",Integer.toString(a));

                BigInteger A_Big = (g.pow(a)).mod(P);
                Log.d("A_Big",A_Big.toString());

                dout.writeLong(A_Big.longValue());
                dout.flush();

                long B_Long = din.readLong();
                Log.d("B_Long",Long.toString(B_Long));

                BigInteger B_Big = BigInteger.valueOf(B_Long);
                BigInteger shared_Big = (B_Big.pow(a)).mod(P);
                shared = shared_Big.intValue();
                Log.d("SHARED",Long.toString(shared));
                connected = 1;
            } catch (IOException e) {
                e.printStackTrace();
                connected = 0;
            }
            return null;
        }
    }

    class BackgroundTask1 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                byte[] mybytearray = new byte[1024];
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                dout.writeUTF("Records");

                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/CloudStorage");
                dir.mkdirs();
                File file = new File(dir, "records.pdf");
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                int bytesRead = dis.read(mybytearray, 0, mybytearray.length);
                bos.write(mybytearray, 0, bytesRead);
                bos.close();

            }
            catch (Exception e){

            }
            return null;
        }
    }


}
