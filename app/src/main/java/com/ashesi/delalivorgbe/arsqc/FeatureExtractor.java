package com.ashesi.delalivorgbe.arsqc;

/**
* @author: Kwabena Boohene
* February 2017
* This class extracts the features required for classification
* */
import android.os.Environment;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.FileReader;




public class FeatureExtractor{
    private BufferedReader br;
    private String currentLine;
    private int numWindows;
    private String fileName="";
    private DataCrawler crawler = new DataCrawler();
    private DataPoints testDataPoints = new DataPoints();

    public FeatureExtractor(String name){
        fileName=name;
    }

    // Gets file location to classify
    File sdcard= Environment.getExternalStorageDirectory();
    File directory = new File(sdcard.getAbsolutePath()+"/ARSQC/rawData");
    String testDataInputFile;

    FeatureComputer testDataComputer = new FeatureComputer(testDataPoints);

    //Computes the number of windows to be used
    public void getNumWindows (){
        int count= 0;
        int requiredLines=0;
        try {

            testDataInputFile = new File(directory,fileName)+"";
            br = new BufferedReader(new FileReader(testDataInputFile));

            while ((currentLine = br.readLine()) != null) {
                count++;
                if(count%4==0){
                    requiredLines=count;
                }
            }
            br.close();
        }catch (IOException e) {
           e.printStackTrace();
            //Log.d("NumWin",e.getMessage());
        }
        //Check integer division
        int numSecs = requiredLines/4;
        numWindows=numSecs/10;
       // String win = Integer.toString(numWindows);
       // Log.d("NumWin",win);
    }


//Extracts data into multidimensional array

    public double [][] extract (){
        getNumWindows();
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

        //Deletes raw file data
        File rawfile = new File(directory,fileName);
        rawfile.delete();

        return features;

    }


}
