package com.example.ccproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import static com.example.ccproject.MainActivity.s;

public class CompanyDetailsActivity extends AppCompatActivity {
    EditText editTextCompanyName;
    String companyName;
    Button getDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_details);

        editTextCompanyName = (EditText)findViewById(R.id.editTextCompanyName);
        getDetails = (Button)findViewById(R.id.btnGetStudentDetailsForCompany) ;

        getDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                companyName = editTextCompanyName.getText().toString().trim();
                BackgroundTask b1 = new BackgroundTask();
                String message = "StudentDetails";
                b1.execute(message);

            }
        });
    }

    class BackgroundTask extends AsyncTask<String,Void,Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                int shared = MainActivity.shared;
                String message = strings[0];

                DataOutputStream dout=new DataOutputStream(s.getOutputStream());
                dout.writeUTF(message);

                //Sending company name in encrypted format to obtain its placed students data.
                companyName = encrypt(companyName,shared);
                dout.writeUTF(companyName);

                DataInputStream din = new DataInputStream(s.getInputStream());

                Log.d("LLL","IIII");
                String total_str = din.readUTF();
                total_str = decrypt(total_str,shared);
                int total = Integer.parseInt(total_str);
                Log.d("total",Integer.toString(total));

                for(int i=0;i<total;i++)
                {
                    String prn = din.readUTF();
                    String studentName = din.readUTF();
                    String branch = din.readUTF();

                    prn = decrypt(prn,shared);
                    studentName = decrypt(studentName,shared);
                    branch = decrypt(branch,shared);
                    Log.d("RECEIVED",prn+" "+studentName+" "+branch);  //To be added to listview
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
