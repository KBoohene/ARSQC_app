package com.ashesi.kboohene.surfaceMap;
/**
 * @author delalivorgbe March 2014
 * modified by Kwabena Boohene January 2017
 * This class traverses a text file containing data collected from the field.
 * It stores each datapoint in an array index for further manipulation
 *
 */

import java.io.FileReader;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;

public class DataCrawler{

    PrintWriter printWriter;
    BufferedReader br;
    String currentLine;

    //Constructor
    public DataCrawler(){
        currentLine = null;
        printWriter = null;
        br = null;
    }


  /*
   * Reads a string file containing acceleration data and
   * stores them as Datapoint objects
   */

    public void read(DataPoints points, String file){
        try {
            br = new BufferedReader(new FileReader(file));
            boolean checkEquals;
            while ((currentLine = br.readLine()) != null) {
                    checkEquals =currentLine.contains("=");

                if (checkEquals==false) {

                    String[] splits = currentLine.split(",");

                    //Have to change these headings
                    /*
                     * Data stored in splits array
                     * index - data
                     * *******************************
                     * 0 - x-axis linear acceleration
                     * 1 - z-axis linear acceleration
                     * 2 - y-axis linear acceleration
                     * 3 - latitude
                     * 4 - longitude
                     * *******************************
                     */

                    points.addLog(parseDbl(splits[2]), parseDbl(splits[3]), parseDbl(splits[4]), parseDbl(splits[10]), parseDbl(splits[11]));
                }
            }
            }catch(IOException e){
                e.printStackTrace();
            }

        finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    public void write(){
    /*
     try {
     printWriter = new PrintWriter(new BufferedWriter(new FileWriter("x.txt", true)));
     printWriter.println(splits[2]);
     printWriter.close();
     } catch (IOException e) {
     e.printStackTrace();
     }  */
    }


    static double parseDbl(String b){
        return Double.parseDouble(b);
    }

}

