package com.ashesi.delalivorgbe.arsqc;

import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Kwabena on 1/24/2017.
 */

public class txtWriter implements Runnable {

    private static final int MAX_LINES =200;
    private int line_number;
    private Float gravityX;
    private Float gravityY;
    private Float gravityZ;
    private Float accuracy;
    private String currentDateTimeString;
    private String androidId;
    private String phoneModel;
    private String phoneName;
    private String fileExtension;
    private String filename;

    public txtWriter(String phoneId){
        gravityX = 0.0F;
        gravityY = 0.0F;
        gravityZ = 0.0F;
        accuracy = 0.0F;
        phoneModel = "";
        phoneName = "";
        phoneModel = android.os.Build.MODEL;
        phoneName = android.os.Build.BRAND;
        fileExtension = ".txt";
        androidId = phoneId;
        currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        currentDateTimeString = currentDateTimeString.replace(':','_');
        filename = ""+androidId+"_"+phoneName+"_"+phoneModel+"_"+currentDateTimeString+
                fileExtension;

    }

    public void run (){
        while (true) {
           // write();
        }
    }


    public void startClassification(String fileName){
        ClassifierService classify = new ClassifierService(fileName);
        new Thread(classify).start();
    }

    public static boolean canWriteOnExternalStorage(){
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            // Log.v("sTag", "Yes, can write to external storage.");
            return true;
        }
        return false;
    }

    public void write(Float x, Float y, Float z, Float speed, Double longitude, Double latitude,
                            String vehicleClass, String vehicleAge, String vehicleCondition){

        Boolean checkExternal = canWriteOnExternalStorage();

        if(checkExternal==true){
            File sdcard= Environment.getExternalStorageDirectory();
            File dir = new File(sdcard.getAbsolutePath()+"/ARSQC/rawData");

            try {
                if(!dir.exists()){
                    dir.mkdirs();
                }

                File file = new File(dir,filename);

                if(!file.exists()){
                    file.createNewFile();
                }

                //Have to edit this to contain the given time on
                if(line_number>=MAX_LINES){
                    line_number=0;
                    //Toast.makeText(this, "Creating new file", Toast.LENGTH_SHORT).show();
                    startClassification(filename);
                    currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                    currentDateTimeString = currentDateTimeString.replace(':','_');
                    filename = ""+androidId+"_"+phoneName+"_"+phoneModel+"_"+currentDateTimeString+
                            fileExtension;
                    file = new File(dir,filename);
                    file.createNewFile();
                }

                FileOutputStream out = new FileOutputStream(file,true);
                OutputStreamWriter osw = new OutputStreamWriter(out);

                Long tsLong = System.currentTimeMillis();
                String ts = tsLong.toString();
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyy");
                String formattedDate = df.format(c.getTime());

                String line = formattedDate+" "+ts +
                        " " + x.toString() + " " + y.toString() + " " + z.toString() +
                        " " + gravityX + " " + gravityY + " " + gravityZ +
                        " " + speed.toString() + " " + accuracy.toString() +
                        " " + longitude.toString() + " " + latitude.toString() + "\n";

                osw.append(line);
                osw.close();
                out.close();
                line_number++;
            }catch(Exception e){
                System.err.println("File not found");
            }
        }
       /* else{
            try {
                FileOutputStream fileout=openFileOutput(filename, Context.MODE_APPEND);
                OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);

                Long tsLong = System.currentTimeMillis();
                String ts = tsLong.toString();

                String line = ts+
                        "|"+x.toString()+"|"+y.toString()+"|"+z.toString()+
                        "|"+gravityX+"|"+gravityY+"|"+gravityZ+
                        "|"+ speed.toString()+"|"+accuracy.toString()+
                        "|"+longitude.toString()+"|"+latitude.toString()+"|"+vehicleClass+
                        "|"+vehicleAge+"|"+vehicleCondition+"\n";
                outputWriter.write(line);
                outputWriter.close();

    //            txtView.setText(vehicleAge + "\n" +
    //                    vehicleCondition + "\n" +
    //                    vehicleClass+"\n");

                //display file saved message
    //            Toast.makeText(getBaseContext(), "File saved successfully!",
    //                    Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

    }
}
