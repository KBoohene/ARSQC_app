package com.ashesi.kboohene.surfaceMap;

/**
 * @author: Kwabena Boohene
 * February 2017
 * This class extracts the features required for classification
 * Adapted from Feature Extraction class created by Delali Vorgbe (2014)
* */
import android.os.Environment;

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
    private boolean start=false, end=false;

    //Constructor of the class
    public FeatureExtractor(String name){
        fileName=name;
    }

    // Gets file location to classify
    File sdcard= Environment.getExternalStorageDirectory();
    File directory = new File(sdcard.getAbsolutePath()+"/SurfaceMap/rawData");
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

                if((currentLine.contains("Start")!=true)||(currentLine.contains("End")!=true))
                {
                    count++;
                }

                if(currentLine.contains("Start")==true){
                    start=true;
                    System.out.println("Start exists");
                }
                else if(currentLine.contains("End")==true){
                    System.out.println("End exists");
                    end=true;
                }

                if(count%4==0){
                    requiredLines=count;
                }

                }

            br.close();
        }catch (IOException e) {
           e.printStackTrace();
        }
        //Check integer division
        System.out.println("Number of lines: "+requiredLines);
        int numSecs = requiredLines/4;
        numWindows=numSecs/10;

    }


//Extracts data into multidimensional array

    public double [][] extract (){
        getNumWindows();
        crawler.read(testDataPoints,testDataInputFile);
        testDataComputer.computeLong();

        double [][] features = new double[numWindows][8];
        for(int i=0;i<numWindows;i++){
            testDataComputer.window();
            features[i][0]=testDataComputer.computeZMean();
            features[i][1]=testDataComputer.computeZVariance();
            features[i][2]=testDataComputer.computeZStandardDeviation();
            features[i][3]=testDataComputer.highestZPeak();
            features[i][4]= testDataComputer.lowestZTrough();
            features[i][5]=testDataComputer.computeLong().get(i);
            features[i][6]=testDataComputer.computeLat().get(i);

            //Adds the start and end points path value
            if(i==0){
                if(start==true){
                    System.out.println("Start Exists");
                    features[i][7]=1;
                }
                else{features[i][7]=2;}
            }
            else if(i==(numWindows-1)){
                if(end==true) {
                    features[i][7] = 3;
                    System.out.println("End exists");
                }
            }
            else {
                features[i][7]=2;
            }
        }

        //Deletes raw file data
        File rawfile = new File(directory,fileName);
        if(rawfile.exists()){
            rawfile.delete();
            System.out.println("File deleted");
        }
        else{
            System.out.println("File not deleted");
        }


        return features;

    }


}
