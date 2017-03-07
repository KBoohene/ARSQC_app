package com.ashesi.delalivorgbe.arsqc;
/**
 * @author delalivorgbe March 2014
 * modified by Kwabena Boohene January 2017
 *
 *
 * This class organizes data by axes for easy retrieval
 *
 */


import java.util.*;
import java.io.IOException;

public class DataPoints{

    ArrayList<Double> xLog;
    ArrayList<Double> yLog;
    ArrayList<Double> zLog;
    ArrayList<Double> longLog;
    ArrayList<Double> latLog;

    public DataPoints(){
        xLog = new ArrayList<Double>();
        yLog = new ArrayList<Double>();
        zLog = new ArrayList<Double>();
        longLog = new ArrayList<Double>();
        latLog = new ArrayList<Double>();

    }

    public void addLog(Double x, Double y, Double z, Double lg, Double lat){
        xLog.add(x);
        yLog.add(y);
        zLog.add(z);
        longLog.add(lg);
        latLog.add(lat);
    }

    public void addLog(Double x, Double y, Double z){
        xLog.add(x);
        yLog.add(y);
        zLog.add(z);
    }

    public ArrayList<Double> getX(){
        return xLog;
    }

    public ArrayList<Double> getY(){
        return yLog;
    }

    public ArrayList<Double> getZ(){
        return zLog;
    }

    public void print(){
        System.out.print(xLog);
        System.out.print(yLog);
        System.out.print(zLog);
    }

    public void printX(){
        System.out.print(xLog);
    }

    public void printY(){
        System.out.print(yLog);
    }

    public void printZ(){
        System.out.print(zLog);
    }

}