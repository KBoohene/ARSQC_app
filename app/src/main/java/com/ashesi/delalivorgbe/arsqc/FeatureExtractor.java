package com.ashesi.delalivorgbe.arsqc;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.FileReader;




public class FeatureExtractor{
    BufferedReader br;
    String currentLine;
    int numWindows = 95;
    String fileName="";
    DataCrawler crawler = new DataCrawler();
    DataPoints testDataPoints = new DataPoints();

    public FeatureExtractor(String name){
        fileName=name;
    }

    // Find method to find file location

        File sdcard= Environment.getExternalStorageDirectory();
        String  testDataInputFile = new File(sdcard.getAbsolutePath()+"/ARSQC/rawData")+fileName;


    FeatureComputer testDataComputer = new FeatureComputer(testDataPoints);



    //Computes the number of windows to be used
//Check the division. Very important;
    public void numWindows (){
        int count= 0;
        int requiredLines=0;
        try {
            br = new BufferedReader(new FileReader(testDataInputFile));

            while ((currentLine = br.readLine()) != null) {
                count++;
                if(count%4==0){
                    requiredLines=count;
                }
            }

        }catch (IOException e) {
            e.printStackTrace();
        }
        //Check integer division
        int numSecs = requiredLines/4;
        numWindows=numSecs/10;
    }


//Extracts data into multidensional array

    public double [][] extract (){
        crawler.read(testDataPoints,testDataInputFile);
        testDataComputer.computeLong();

        double [][] features = new double[numWindows][7];
        for(int i=0;i<numWindows;i++){
            testDataComputer.window();
            features[i][0]=testDataComputer.computeZMean();
            features[i][1]=testDataComputer.computeZVariance();
            features[i][2]=testDataComputer.computeZStandardDeviation();
            features[i][3]=testDataComputer.highestZPeak();
            features[i][4]= testDataComputer.lowestZTrough();
            features[i][5]=testDataComputer.computeLong().get(i);
            features[i][6]=testDataComputer.computeLat().get(i);

        }
        return features;
    }


}
