package com.example.charlie.eegeo_programmingtest;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


        //a handle to the application's resources
        private Resources resources;
        //a string to output the contents of the files to LogCat
        private String output;

        private TextView text;

        public List<FeatureHolder> featureHolders = new ArrayList<FeatureHolder>();

        public FeatureKDTree FeatureTree;

        /** Called when the activity is first created. */
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            //get the application's resources
            resources = getResources();

            text = (TextView)findViewById(R.id.basicText);

            try
            {
                //Load the file from assets folder - don't forget to INCLUDE the extension
                LoadFile("problem_small.txt");
                //output to LogCat
                //Log.d("outputtest", output);
               // new StringReader(output);
                //text.setText(featureHolders.size()+"");

                FeatureTree = new FeatureKDTree(featureHolders);

                text.setText(FeatureTree.findMostIsolated(featureHolders).name);



            }
            catch (IOException e)
            {
                //display an error toast message
                Toast toast = Toast.makeText(this, "File: not found!", Toast.LENGTH_LONG);
                toast.show();
            }
        }

        //load file from apps res/raw folder or Assets folder
    public void LoadFile(String fileName) throws IOException
    {
        //Create a InputStream to read the file into
        InputStream iS;


            //get the file as a stream
            iS = resources.getAssets().open(fileName);


        //create a buffer that has the same size as the InputStream
        byte[] buffer = new byte[iS.available()];
        //read the text file as a stream, into the buffer
        iS.read(buffer);
        //create a output stream to write the buffer into
        ByteArrayOutputStream oS = new ByteArrayOutputStream();
        //write this buffer to the output stream
        oS.write(buffer);
        //Close the Input and Output streams
        oS.close();
        iS.close();
        String currLine;
        BufferedReader reader = new BufferedReader(
                new StringReader(oS.toString()));

        try {
            while ((currLine = reader.readLine()) != null) {

                if (currLine.length() > 0){
                    //System.out.println(currLine.charAt(0));

                   // Log.d("LINETEST",currLine);

                   String[] parts = currLine.split(" ");
                    featureHolders.add(new FeatureHolder(parts[0], Double.parseDouble(parts[1]),Double.parseDouble(parts[2])));
                }

            }

        } catch(IOException e) {
            e.printStackTrace();
        }
        //return the output stream as a String
        //return oS.toString();
    }



}

