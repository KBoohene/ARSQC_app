package com.ashesi.kboohene.surfaceMap;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author Delali Vorgbe on November 2015
 * modified by Kwabena Boohene January 2017
 */
public class UploadService extends Service {

    private Calendar c;
    private SimpleDateFormat sdf;
    private String sdfString;
    private String timeStamp;
    private String fileExtension;
    private File [] filesToUpload;

    public UploadService() {
        c = Calendar.getInstance();
        sdfString = "dd-MM-yy-ss";
        sdf = new SimpleDateFormat(sdfString);
        timeStamp = sdf.format(c.getTime());
        fileExtension = ".txt";

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        //Toast.makeText(this, "Service was Created. Not started", Toast.LENGTH_LONG).show();
        System.out.println("Service was Created. Not started");
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        // Perform your long running operations here.
        //Toast.makeText(this, "Service Started \n About to start uploads", Toast.LENGTH_LONG).show();
        System.out.println("Service Started \n About to start uploads");
        queueUploads();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        //Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        System.out.println("Service Destroyed");
    }


    public String getFileTimestamp(String fileName){
        return fileName.substring(fileName.length()-(sdfString.length()+fileExtension.length()),
                fileName.length()-fileExtension.length());
    }

    //Checks if the directory is empty
    public boolean fileDirectoryIsEmpty(){
        return(fileList().length==0);
    }


    //Posts file to the required server
    public void postFile(File fileToUpload){


        System.out.println("About to upload " + fileToUpload.getName());

        OkHTTPAsync fileUploadHandler = new OkHTTPAsync(fileToUpload);
        System.out.println(("File Size: "+fileToUpload.length()/1024));
        System.out.println("File name: "+fileToUpload.getName());

        try {
            fileUploadHandler.execute(fileToUpload);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*private File getFileAtIndex(int index){
        return new File(getFilesDir() + "/" + fileList()[index]);
    }*/

    /*public int getNumberOfFilesInDirectory(){
        return fileList().length;
    }*/

    //Lists the number of files in the directory
    public int getNumberOfFilesInDirectory(){
        int numberOfFiles;
        File dir = new File(Environment.getExternalStorageDirectory()
                + "/SurfaceMap/Classification");
        filesToUpload = dir.listFiles();

        if(dir.listFiles()==null){
            numberOfFiles=0;
        }
        else{
            numberOfFiles=filesToUpload.length;
        }

        return numberOfFiles;
    }

    //Returns a specific file at a given index
    private File getFileAtIndex(int index){
        return  filesToUpload[index];
    }

    public String getTodayTimestamp(){
        return timeStamp;
    }

    //Queues the files to be uploaded
    private boolean queueUploads(){

        Toast.makeText(getBaseContext(), "Number of files to upload: "+getNumberOfFilesInDirectory(),
                Toast.LENGTH_SHORT).show();
        boolean completedUploads=false;

        for(int i=0; i<getNumberOfFilesInDirectory(); i++){
            System.out.println("Trying "+getFileAtIndex(i).getName());
            if(!(getFileTimestamp(getFileAtIndex(i).getName()).equals(getTodayTimestamp()))){
                Log.d("Upload Message","Not equal to today. Uploading");
                postFile(getFileAtIndex(i));
                completedUploads=true;
            }else{
               Log.d("Upload Message","Equal to today. Not uploading");
                postFile(getFileAtIndex(i));
                completedUploads=false;
            }
        }

        if(completedUploads==true){
            Toast.makeText(getBaseContext(), "Finished file uploads",
                    Toast.LENGTH_SHORT).show();
            System.out.println("Finished file uploads");
            stopSelf();
        }
        else{
            Toast.makeText(getBaseContext(), "No File uploaded",
                    Toast.LENGTH_SHORT).show();
        }

        return false;
    }
}
