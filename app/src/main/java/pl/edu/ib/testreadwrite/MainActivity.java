package pl.edu.ib.testreadwrite;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_WRITE = 10;
    private boolean canWriteToFile = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Based on:
        // https://developer.android.com/training/permissions/requesting#java

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {


            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE);

            // MY_PERMISSIONS_REQUEST_WRITE is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        } else {
            // Permission has already been granted
            canWriteToFile = true;
        }


    }// onCreate


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    canWriteToFile = true;

                } else {
                    // permission denied
                    // functionality that depends on this permission.
                    canWriteToFile = false;

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    protected void onSaveFileClick(View view) {

        if (canWriteToFile) {

            EditText edStart = (EditText) findViewById(R.id.edStart);
            EditText edStop = (EditText) findViewById(R.id.edStop);
            EditText edSampFreq = (EditText) findViewById(R.id.edSampFreq);
            TextView tvFileContent = (TextView) findViewById(R.id.savedFileContent);

            double tStart = 0;
            double tStop = 0;
            double sampFreq = 0;
            boolean success = false;


            try {

                tStart = Double.parseDouble(edStart.getText().toString());
                tStop = Double.parseDouble(edStop.getText().toString());
                sampFreq = Double.parseDouble(edSampFreq.getText().toString());
                success = true;
            } catch (NumberFormatException e) {


                Toast.makeText(getApplicationContext(),
                        "Can't  save - invalid data",
                        Toast.LENGTH_LONG).show();
            }


            if (success) {
                saveToFile(calculateSinus(tStart, tStop, sampFreq), "/TEST/", "test1.txt");

                String text = readFromFile("/TEST/", "test1.txt");
                tvFileContent.setText(text);

            }
            ;
        } else {

            Toast.makeText(getApplicationContext(),
                    "You don't have WRITE permission",
                    Toast.LENGTH_LONG).show();

        }

    }


    private void saveToFile(ArrayList<Double> data, String folder, String fileName) {


        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + folder);
        dir.mkdirs();
        File file = new File(dir, fileName);

        String test = file.getAbsolutePath();
        Log.i("My", "FILE LOCATION: " + test);


        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);


            for (int i = 0; i < data.size(); i++) {
                pw.println(data.get(i));
            }

            pw.flush();
            pw.close();
            f.close();


            Toast.makeText(getApplicationContext(),

                    "Data saved",

                    Toast.LENGTH_LONG).show();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i("My", "******* File not found *********");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String readFromFile(String folder, String fileName) {

        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + folder);
        File file = new File(dir, fileName);

        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i("My", "******* File not found *********");
        } catch (IOException e) {
            //You'll need to add proper error handling here
        } finally {
            return text.toString();
        }


    }


    private ArrayList<Double> calculateSinus(double t1, double t2, double fs) {

        double dt = 1. / fs;
        int n = (int) ((t2 - t1) / dt) + 1;
        double time = t1;


        ArrayList<Double> sinValues = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            sinValues.add(Math.sin(time));
            time += dt;
        }

        return sinValues;


    }


}
