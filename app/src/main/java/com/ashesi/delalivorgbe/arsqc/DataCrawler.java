package com.ashesi.delalivorgbe.arsqc;
/*
 * @author delalivorgbe
 * modified by Kwabena Boohene
 * March 2014
 *
 * This class traverses a text file containing data collected from the field.
 * It stores each datapoint in an array index for further manipulation
 *
 */

import com.ashesi.delalivorgbe.arsqc.DataPoints;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;

public class DataCrawler{

    PrintWriter printWriter;
    BufferedReader br;
    String currentLine;


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

            while ((currentLine = br.readLine()) != null) {

                String[] splits = currentLine.split(" ");
                //Have to change these headings
        /*
         * Data stored in splits array
         * index - data
         * *******************************
         * 0 - timestamp
         * 1 - timestamp
         * 2 - x-axis acceleration
         * 3 - z-axis acceleration
         * 4 - y-axis acceleration
         * 5 - x-axis linear acceleration
         * 6 - z-axis linear acceleration
         * 7 - y-axis linear acceleration
         * 8 - latitude
         * 9 - longitude
         * *******************************
         */

                points.addLog(parseDbl(splits[2]), parseDbl(splits[3]), parseDbl(splits[4]), parseDbl(splits[10]), parseDbl(splits[11]));
            }

        } catch (IOException e) {
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

