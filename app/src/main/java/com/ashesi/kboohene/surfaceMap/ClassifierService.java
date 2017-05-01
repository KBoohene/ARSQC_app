package com.ashesi.kboohene.surfaceMap;

import android.os.Environment;

import java.io.File;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import static com.ashesi.kboohene.surfaceMap.MainActivity.canWriteOnExternalStorage;

/**
 * @author Kwabena Boohene on 1/9/2017.
 * Performs road quality classification
 */

public class ClassifierService implements Runnable {

    private double [][] exFeatures;
    private PrintWriter printer;
    private String roadGrade, filename;
    private double class1,class2;
    private FeatureExtractor extractor;

    //Constructor of the class
    public ClassifierService(String name){
        filename=name;

    }

    //Runs as soon as the thread starts
    public void run() {
        extractor = new FeatureExtractor(filename);
        runClassification();
    }



    // Good vs Bad/Fair theta values
    public double classify1(double val1, double z_mean, double z_var,
                            double z_D, double z_peak, double z_trough){

        double theta1, theta2, theta3, theta4, theta5, theta6;

        theta1=38.388669;
        theta2=-1.658378;
        theta3=4.517424;
        theta4=-23.416034;
        theta5=0.305482;
        theta6=-1.711021;

        double result = (val1*theta1)+(z_mean*theta2)+(z_var*theta3)
                +(z_D*theta4)+(z_peak*theta5)+(z_trough*theta6);

        return result;
    }


    //Bad vs Good/Fair theta values
    public double classify2(double val1, double z_mean, double z_var,
                            double z_D, double z_peak, double z_trough){

        double theta1, theta2, theta3, theta4, theta5, theta6;

        theta1=-0.641961;
        theta2=-1.009224;
        theta3=-3.973086;
        theta4=17.536464;
        theta5=-0.565364;
        theta6=0.681880;

        double result = (val1*theta1)+(z_mean*theta2)+(z_var*theta3)
                +(z_D*theta4)+(z_peak*theta5)+(z_trough*theta6);

        return result;
    }


    //Determines the grade of road
    public String grade(double classify1, double classify2){
        String grade=" ";
        if((classify1<0.5)&&(classify2>0.5)){
            grade="Good";
        }
        else if((classify1>=0.5)&&(classify2<0.5)){
            grade="Bad";
        }
        else{
            grade="Fair";
        }
        return grade;
    }


    //Sigmoid function of probability
    public double sigmoid(double prob){
        prob = (1/(1+Math.pow(Math.E,(-1*prob))));
        return prob;
    }


    //Writes grade along with required parameters to a text file
    public void runClassification(){
        exFeatures = extractor.extract();
        Boolean checkExternal = canWriteOnExternalStorage();
        if(checkExternal==true) {
            File sdcard = Environment.getExternalStorageDirectory();
            File dir = new File(sdcard.getAbsolutePath() + "/SurfaceMap/Classification");


            try {

                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File file = new File(dir, filename);

                if (!file.exists()) {
                    file.createNewFile();
                }


                printer = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));

                for (int i = 0; i < exFeatures.length; i++) {
                    class1 = sigmoid(classify1(1, exFeatures[i][0], exFeatures[i][1],
                            exFeatures[i][2], exFeatures[i][3], exFeatures[i][4]));

                    class2 = sigmoid(classify2(1, exFeatures[i][0], exFeatures[i][1],
                            exFeatures[i][2], exFeatures[i][3], exFeatures[i][4]));

                    roadGrade = grade(class1, class2);

                    //RoadGrade-Latitude-Longitude-Class1_Value-Class2_Value-Path-
                    printer.println(roadGrade + "," + exFeatures[i][5] + "," + exFeatures[i][6]+
                            ","+class1+","+class2+","+exFeatures[i][7]);
                }

                printer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
