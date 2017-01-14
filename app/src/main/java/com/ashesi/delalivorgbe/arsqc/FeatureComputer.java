/*
 * @author delalivorgbe
 * March 2014
 *
 * This class defines the window size and computes required
 * features from the dataset
 *
 */
package com.ashesi.delalivorgbe.arsqc;
import java.util.*;
public class FeatureComputer{

    int windowSize;
    double[][] window;
    int xCurrentPos, yCurrentPos, zCurrentPos;
    int xDivisor,yDivisor,zDivisor;
    final int xRow, yRow, zRow;
    ArrayList<Double> longi = new ArrayList<Double>();
    ArrayList<Double> lat = new ArrayList<Double> ();

    //New datapoints object
    DataPoints dataPoints;

    public FeatureComputer(DataPoints dp){
        dataPoints = dp;
        windowSize = 40;
        window = new double[3][windowSize];
        xDivisor = yDivisor = zDivisor = 0;
        xCurrentPos = yCurrentPos = zCurrentPos = 0;
        xRow = 0;
        yRow = 1;
        zRow = 2;
    }


  /*
   * Method to create timed window of 10secs
   * Sampling rate of accelerometer is 4Hz
   */

    public void window(){
        xDivisor = yDivisor = zDivisor = 0;

        for(int i=0;i<window.length;i++){ //window.length is 40
            for(int j=0;j<window[0].length;j++){

                if(i==0 && xCurrentPos < dataPoints.getX().size()){
                    window[i][j] = dataPoints.getX().get(xCurrentPos);
                    xCurrentPos++;
                    xDivisor++;
                }

                if(i==1 && yCurrentPos < dataPoints.getY().size()){
                    window[i][j] = dataPoints.getY().get(yCurrentPos);
                    yCurrentPos++;
                    yDivisor++;
                }

                if(i==2 && zCurrentPos < dataPoints.getZ().size()){
                    window[i][j] = dataPoints.getZ().get(zCurrentPos);
                    zCurrentPos++;
                    zDivisor++;
                }

            }
        }

    }


    //Stores the required longitudes
    public ArrayList<Double> computeLong(){
        for (int i=0;i<dataPoints.longLog.size();i++){
            if((i%40)== 0){
                longi.add(dataPoints.longLog.get(i));
            }
        }
        return longi;
    }


    //Stores the required latitude
    public ArrayList<Double> computeLat(){
        for (int i=0;i<dataPoints.latLog.size();i++){
            if((i%40)== 0){
                lat.add(dataPoints.latLog.get(i));
            }
        }
        return lat;
    }



    /*
     * Compute the mean value of specified axial reading
     */
    public double computeXMean(){
        return computeMean(xDivisor,xRow);
    }

    public double computeYMean(){
        return computeMean(yDivisor,yRow);
    }

    public double computeZMean(){
        return computeMean(zDivisor,zRow);
    }


    /*
     * Compute the variance of stated axial reading in each window
     */
    public double computeZVariance(){
        double variance = 0.0;
        double [] difference = new double[windowSize];

        for(int j=0;j<zDivisor;j++){
            difference[j] = window[2][j] - computeZMean();
            difference[j] = Math.pow(difference[j],2);
            variance += difference[j];
        }

        variance = variance/zDivisor;
        return variance;
    }



    public double computeXVariance(){
        double variance = 0.0;
        double [] difference = new double[windowSize];

        for(int j=0;j<xDivisor;j++){
            difference[j] = window[2][j] - computeXMean();
            difference[j] = Math.pow(difference[j],2);
            variance += difference[j];
        }

        variance = variance/xDivisor;
        return variance;
    }


    public double computeYVariance(){
        double variance = 0.0;
        double [] difference = new double[windowSize];

        for(int j=0;j<zDivisor;j++){
            difference[j] = window[2][j] - computeYMean();
            difference[j] = Math.pow(difference[j],2);
            variance += difference[j];
        }

        variance = variance/yDivisor;
        return variance;
    }



    /*
     * Compute the standard deviation of stated axial reading in each window
     */
    public double computeZStandardDeviation(){
        return Math.sqrt(computeZVariance());
    }


    public double computeYStandardDeviation(){
        return Math.sqrt(computeYVariance());
    }


    public double computeXStandardDeviation(){
        return Math.sqrt(computeXVariance());
    }


    /*
     * Compute the highest peak recorded stated axial reading in each window
     */
    public double highestZPeak(){
        double zPeak = window[2][0];

        for(int j=0;j<window[0].length;j++){
            if(window[2][j]>zPeak)
                zPeak = window[2][j];
        }
        return zPeak;
    }


    /*
     * Compute the lowest trough recorded stated axial reading in each window
     */
    public double lowestZTrough(){
        double zTrough = window[2][0];

        for(int j=0;j<window[0].length;j++){
            if(window[2][j]<zTrough)
                zTrough = window[2][j];
        }
        return zTrough;
    }




    /*
     * Compute successive troughs
     */
    public double[] findTroughs(){
        double [] troughs = new double[20];
        int tCount = 1;

        for(int i=1;i<window[0].length-1;i++){
            if(window[zRow][i-1] > window[zRow][i] && window[zRow][i+1] > window[zRow][i]){
                troughs[tCount] = window[zRow][i];
                tCount++;
            }
        }

        troughs[0] = tCount;
        return troughs;
    }



    /*
     * Compute the various peaks and troughs from each window of data
     */
    public ArrayList<Double> findPeaksNTroughs(){
        ArrayList<Double> peaksNTroughs = new ArrayList<Double>();

        peaksNTroughs.add(window[zRow][0]);

        for(int i=1;i<window[0].length-1;i++){
            if(window[zRow][i-1] <= window[zRow][i] && window[zRow][i+1] < window[zRow][i]){
                peaksNTroughs.add(window[zRow][i]);
            }


            if(window[zRow][i-1] >= window[zRow][i] && window[zRow][i+1] > window[zRow][i]){
                peaksNTroughs.add(window[zRow][i]);
            }
        }

        if(window[zRow][zDivisor-1] != peaksNTroughs.get(peaksNTroughs.size()-1))
            peaksNTroughs.add(window[zRow][zDivisor-1]);

        return peaksNTroughs;
    }



  /*
   * Compute the difference between successive peaks and troughs
   */

    public ArrayList<Double> meanPTDifference(){
        ArrayList<Double> difference = new ArrayList<Double>();
        ArrayList<Double> peaksNTroughs = findPeaksNTroughs();

        if(peaksNTroughs.get(0)>peaksNTroughs.get(1)){
            for(int i=0;i<peaksNTroughs.size();i+=2){

                if(i>0)
                    difference.add(peaksNTroughs.get(i) - peaksNTroughs.get(i-1));

                if(i<peaksNTroughs.size()-1)
                    difference.add(peaksNTroughs.get(i) - peaksNTroughs.get(i+1));

            }
        }

        else{
            for(int i=1;i<peaksNTroughs.size();i+=2){

                if(i>0)
                    difference.add(peaksNTroughs.get(i) - peaksNTroughs.get(i-1));

                if(i<peaksNTroughs.size()-1)
                    difference.add(peaksNTroughs.get(i) - peaksNTroughs.get(i+1));
            }
        }

        return difference;
    }


    /*
     * Compute the mean of the difference between successive peaks and troughs
     */
    public double computeZPTMean(){
        double mean = 0.0;

        for(int j=0;j<meanPTDifference().size();j++){
            mean += meanPTDifference().get(j);
        }
        mean = mean/meanPTDifference().size();
        return mean;
    }


    /*
     * Compute the variance of the difference between successive peaks and troughs
     */
    public double computeZPTVariance(){
        double variance = 0.0;
        double [] difference = new double[windowSize];

        for(int j=0;j<zDivisor;j++){
            difference[j] = window[2][j] - computeZPTMean();
            difference[j] = Math.pow(difference[j],2);
            variance += difference[j];
        }

        variance = variance/zDivisor;
        return variance;
    }


    /*
     * Compute the standard deviation of the difference between successive peaks and troughs
     */
    public double computeZPTStandardDev(){
        return Math.sqrt(computeZPTVariance());
    }


    /*
     * Helper method for computing mean
     */
    private double computeMean(int divisor, int row){
        double mean = 0.0;

        for(int j=0;j<divisor;j++){
            mean += window[row][j];
        }
        mean = mean/divisor;
        return mean;
    }

}
