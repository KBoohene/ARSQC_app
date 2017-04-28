package com.ashesi.kboohene.surfaceMap;

/**
 * @author Kwabena Boohene January 2017
 * Adapted from Delali Vorgbe March 2014
 * Main class that performs recording of text file data
 */
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import android.content.Intent;
import android.os.Environment;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.provider.Settings.Secure;


public class MainActivity extends AppCompatActivity implements SensorEventListener, LocationListener {

    private static final String TAG = "MainActivity.java";
    private static final int MAX_LINES =2000;

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Sensor senRotationVestor;

    private Float accelerationX;
    private Float accelerationY;
    private Float accelerationZ;
    private Float gravityX;
    private Float gravityY;
    private Float gravityZ;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private FileOutputStream outputStream;
    private OutputStreamWriter outputWriter;
    private Double longitude;
    private Double latitude;
    private Float accuracy;
    private Float speed;
    private TextView txtView;
    private ToggleButton toggle;

    private Calendar c;
    private SimpleDateFormat sdf;
    private String sdfString;
    private String timeStamp;
    private String currentDateTimeString;
    private String filename;
    private String fileExtension;
    private boolean locationLocked;
    private String androidId;
    private Button uploadFileButton;
    private String phoneModel;
    private String phoneName;
    private int sensorDelay;
    private int line_number;

    private Boolean checkExternal;
    private TextView noticeView;
    private File sdcard;
    private File dir;
    static final int READ_BLOCK_SIZE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        locationLocked = false;

        longitude = 0.0;
        latitude = 0.0;
        speed = 0.0F;
        accuracy = 0.0F;

        accelerationX = 0.0F;
        accelerationY = 0.0F;
        accelerationZ = 0.0F;
        gravityX = 0.0F;
        gravityY = 0.0F;
        gravityZ = 0.0F;

        txtView = (TextView) findViewById(R.id.testX);
        toggle = (ToggleButton) findViewById(R.id.start_stop_toggle);
        noticeView=(TextView) findViewById(R.id.noticeView);
        uploadFileButton = (Button)findViewById(R.id.upload);
        sensorDelay = SensorManager.SENSOR_DELAY_UI;


        phoneModel = "";
        phoneName = "";

        phoneModel = android.os.Build.MODEL;
        phoneName = android.os.Build.BRAND;

        c = Calendar.getInstance();
        sdfString = "dd-MM-yy";
        sdf = new SimpleDateFormat(sdfString);
        timeStamp = sdf.format(c.getTime());
        fileExtension = ".txt";

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        senRotationVestor = senSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        androidId = Secure.getString(this.getContentResolver(),Secure.ANDROID_ID);
        currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        currentDateTimeString = currentDateTimeString.replace(':','_');
        filename = ""+androidId+"_"+phoneName+"_"+phoneModel+"_"+currentDateTimeString+
                fileExtension;
        checkExternal = canWriteOnExternalStorage();
        sdcard=Environment.getExternalStorageDirectory();
        dir = new File(sdcard.getAbsolutePath()+"/SurfaceMap/rawData");

    }

    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    // Accelerometer blocks
    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // TODO Auto-generated method stub
        Sensor mySensor = sensorEvent.sensor;

        //changed condition from locationLocked
        if(locationLocked){
            if (mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                Float x = sensorEvent.values[0];
                Float y = sensorEvent.values[1];
                Float z = sensorEvent.values[2];

                writeToFile(x, y, z, speed, longitude, latitude);
                noticeView.setVisibility(View.VISIBLE);
                noticeView.setText("Recording");

                txtView.setText("x-linear: "+x.toString() + "\n y-linear: " + y.toString() +
                        "\n z-linear: " + z.toString()+ "\n speed: " + speed.toString()+
                        "\n longitude: " + longitude.toString()+ "\n latitude: " + latitude.toString()+
                        "\n x-gravity: "+ gravityX.toString() + "\n y-gravity: " + gravityY.toString() +
                        "\n z-gravity: " + gravityZ.toString());
            }

            if (mySensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                gravityX = sensorEvent.values[0];
                gravityY = sensorEvent.values[1];
                gravityZ = sensorEvent.values[2];
            }
        }

    }

    // Location GPS blocks
    @Override
    public void onLocationChanged(Location location) {

        longitude =location.getLongitude();
        latitude=location.getLatitude();
        speed = location.getSpeed();
        accuracy = location.getAccuracy();


        if(locationLocked==false){
            locationLocked = true;
            Toast.makeText(getBaseContext(), "location locked", Toast.LENGTH_LONG).show();
        }

        String str = "Latitude: "+location.getLatitude()+" Longitude: "+location.getLongitude();

        //Toast.makeText(getBaseContext(), str, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getBaseContext(), "Gps is turned off. Turn on and try again", Toast.LENGTH_LONG).show();
        locationLocked = false;
        toggle.setChecked(false);
    }

    @Override
    public void onProviderEnabled(String provider) {

        /******** Called when User on Gps  *********/

        //Toast.makeText(getBaseContext(), "Gps turned on ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    //Checks if data can be written on external storage
    public static boolean canWriteOnExternalStorage(){
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
           // Log.v("sTag", "Yes, can write to external storage.");
            return true;
        }
        return false;
    }

    //Writes raw data to text file
    public void writeToFile(Float x, Float y, Float z, Float speed, Double longitude, Double latitude){

        if(checkExternal==true){

            try {
                if(!dir.exists()){
                    dir.mkdirs();
                }

                File file = new File(dir,filename);

                if(!file.exists()){
                    file.createNewFile();
                    writeStart(file);
                }


                if(line_number>=MAX_LINES){
                    line_number=0;
                    //Toast.makeText(this, "Creating new file", Toast.LENGTH_SHORT).show();
                    startClassification(filename);
                    Toast.makeText(this, "Finished Classifying", Toast.LENGTH_SHORT).show();
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

                String line = formattedDate+","+ts +
                        "," + x.toString() + "," + y.toString() + "," + z.toString() +
                        "," + gravityX + "," + gravityY + "," + gravityZ +
                        "," + speed.toString() + "," + accuracy.toString() +
                        "," + longitude.toString() + "," + latitude.toString() + "\n";
                System.out.println(line);
                osw.append(line);
                osw.close();
                out.close();
                line_number++;
            }catch(Exception e){
                System.err.println("File not found");
            }
        }


    }

    //Toggles the start recording button
    public void startStopToggleClicked(View view) {
        // Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            senSensorManager.registerListener(this, senAccelerometer , sensorDelay);
            senSensorManager.registerListener(this, senRotationVestor, sensorDelay);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);


            if(!locationLocked){
                Toast.makeText(getBaseContext(), "Getting GPS lock. Might take a while", Toast.LENGTH_LONG).show();
            }


        } else {
            noticeView.setText("Paused Recording");
            senSensorManager.unregisterListener(this);

        }
    }

    //Performs classification when the end button is clicked
    public void endButtonClicked(View view){


        try {

            File sdcard=Environment.getExternalStorageDirectory();
            File dir = new File(sdcard.getAbsolutePath()+"/SurfaceMap/rawData");

            if (!dir.exists()) {
                dir.mkdirs();
            }
            System.out.println(dir.getName());

            File file = new File(dir, filename);

            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream out = new FileOutputStream(file, true);
            OutputStreamWriter osw = new OutputStreamWriter(out);
            String line ="==================End===================\n";
            osw.append(line);
            osw.close();
            out.close();


        }catch(Exception e){
            System.err.println("File not found");
        }

        startClassification(filename);
        toggle.setChecked(false);
        senSensorManager.unregisterListener(this);

        Toast.makeText(getBaseContext(), "Recording Ended",
                Toast.LENGTH_SHORT).show();


        noticeView.setText("Ended Recording");

        Toast.makeText(this, "Finished Classifying", Toast.LENGTH_SHORT).show();

        currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        currentDateTimeString = currentDateTimeString.replace(':','_');
        filename = ""+androidId+"_"+phoneName+"_"+phoneModel+"_"+currentDateTimeString+
                fileExtension;
    }

    //Writes 'START' to the beginning of a text file
    public void writeStart(File file){
        try{
            FileOutputStream out = new FileOutputStream(file,true);
            OutputStreamWriter osw = new OutputStreamWriter(out);

            String line ="===================Start=========================\n";

            osw.append(line);
            osw.close();
            out.close();
            line_number++;
        }catch(Exception e){
            System.err.println("File not found");
        }
    }

    //Checks if file directory is empty
    public boolean fileDirectoryIsEmpty(){
        return(fileList().length==0);
    }

    // Start the upload background service
    public void startUploadService(){
        startService(new Intent(this, UploadService.class));
    }


    //non-critical functions. utility testing
    public void testButton(View v){
        startUploadService();
    }

    //Starts the classification background service
    public void startClassification(String fileName){
        ClassifierService classify = new ClassifierService(fileName);
        new Thread(classify).start();
    }

}
