package com.ashesi.kboohene.surfaceMap;

import android.content.Context;
import android.os.AsyncTask;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Created by delalivorgbe on 11/1/15.
 */
public class OkHTTPAsync extends AsyncTask<File, Integer, String> {

    private int fileNum, numberOfFiles;
    private File fileToUpload;

    private Context appContext;

    public MainActivity mActivity;


    public OkHTTPAsync(File upFile, MainActivity activity){
        mActivity = (MainActivity)activity;
        fileToUpload = upFile;
        appContext = ApplicationContextProvider.getContext();
    }

    public OkHTTPAsync(File upFile, int number, int numFiles){
        fileToUpload = upFile;
        appContext = ApplicationContextProvider.getContext();
        fileNum=number;
        numberOfFiles=numFiles;
    }

    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");


    private OkHttpClient getClient() {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(10, TimeUnit.MINUTES);
        client.setReadTimeout(10, TimeUnit.MINUTES);
        return client;
    }


    @Override
    protected String doInBackground(File... params){

        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("fileToUpload", fileToUpload.getName(),
                        RequestBody.create(MEDIA_TYPE_MARKDOWN, fileToUpload))
                .build();

        Request request = new Request.Builder()
                //.url("http://cs.ashesi.edu.gh/~kwabena.boohene/ARSQC_server/file_download.php")
                .url("http://192.168.13.157/ARSQC_server/file_download.php")
                .post(requestBody)
                .build();

        Response response = null;
        try {
            response = getClient().newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String responseBody = response.body().string();
            System.out.println(responseBody);

            if(responseBody.equals("Successful")){
                System.out.println("successfully uploaded " + fileToUpload.getName());

                if(fileToUpload.delete()){
                    System.out.println("successfully deleted " + fileToUpload.getName());
                }else{
                    System.out.println("Delete failed "+ fileToUpload.getName());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if(fileNum==numberOfFiles){
        msgConfirm confirm = new msgConfirm();
        confirm.execute();
        }
    }

    private class msgConfirm extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String response="false";
            try {
                URL url = new URL("http://192.168.13.157/ARSQC_server/file_download.php?confirm=1");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                String line;
                StringBuilder sb= new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                while ((line=br.readLine()) != null) {
                    sb.append(line);
                    response=sb.toString();
                }
                br.close();
            }
            catch(Exception ex){
                ex.printStackTrace();
            }

            return null;
        }
    }
}
