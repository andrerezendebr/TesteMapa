package com.example.andre.testemapa;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.*;

/**
 * Create by andre on 22/01/15.
 */
public class TreadEnviaServidor extends AsyncTask<String,String,String>
{

    // @Override
    protected String onPostExecute(Void result) {
        // TODO Auto-generated method stub
        super.onPostExecute(valueOf(result));

        Log.d("Mensagem", "Server message is ");
        return " ";
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
    }
    /*
    @Override
    protected String doInBackground(String... params) {
        try
        {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost method = new HttpPost(params[0]);
            HttpResponse response = httpclient.execute(method);
            HttpEntity entity = response.getEntity();
            if(entity != null){
                return EntityUtils.toString(entity);
            }
            else{
                return "No string.";
            }
        }
        catch(Exception e){
            return "Network problem";
        }

    }
    */
    @Override
    protected String doInBackground(String... params) {
        SendPostToServer();
        return "sas";
    }


    double longitude=0.0;
    double latitude=0.0;
    double mph=0.0;
    String currTime="";

    ///////////////////////////////////////////////
    // Envia valores para o site
    // http://localhost/ChildMonitor/Util/SendGlobalPosition.php?id=12&Lat=-12.34&Long=-43.09&TimeStamp=%222000-11-11+13%3A23%3A02%22&Velocity=3
    public void SendPostToServer()
    {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://childmonitor.ngrok.com/ChildMonitor/Util/SendGlobalPosition.php");
        try {
            // Add your data

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("id", "12"));
            nameValuePairs.add(new BasicNameValuePair("Lat", Double.toString(latitude)));
            nameValuePairs.add(new BasicNameValuePair("Long", Double.toString(longitude)));
            nameValuePairs.add(new BasicNameValuePair("TimeStamp", currTime));
            nameValuePairs.add(new BasicNameValuePair("Velocity", Double.toString(mph)));


            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

            Log.d(response.toString()+"log_tag", "5 - /////////////////////////////////////////////////////////");

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            Log.d("log_tag", "Problema ClientProtocolException");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.d("log_tag", "Problema IOException");
        }
    }

}