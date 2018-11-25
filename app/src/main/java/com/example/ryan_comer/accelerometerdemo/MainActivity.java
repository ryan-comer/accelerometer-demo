package com.example.ryan_comer.accelerometerdemo;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import static android.provider.Telephony.Carriers.PORT;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private int deviceID;

    // Get a handler that can be used to post to the main thread
    Handler mainHandler;

    TextView accelerometerText;

    private float lastX, lastY, lastZ;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    private float collisionThreshold = 5.0f;

    private RequestQueue m_RequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainHandler = new Handler(Looper.getMainLooper());

        accelerometerText = findViewById(R.id.accelerometer_data);

        // Register callback for accelerometer
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            // Success, we have an accelerometer

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        }else{
            // Fail, we don't have an accelerometer
        }

        // Create the request queue
        m_RequestQueue = Volley.newRequestQueue(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    // Clean up the accelerometer values
    private void displayCleanValue(){
        String cleanText = "X: 0.0\n" +
                "Y: 0.0\n" +
                "Z: 0.0";

        accelerometerText.setText(cleanText);
    }

    private void clearText(){
        accelerometerText.setText("");
    }

    private void displayCurrentValues(){
        StringBuilder sb = new StringBuilder();
        sb.append("dX: ").append(Float.toString(deltaX)).append("\n");
        sb.append("dY: ").append(Float.toString(deltaY)).append("\n");
        sb.append("dZ: ").append(Float.toString(deltaZ)).append("\n");

        sb.append("\n");

        sb.append("X: ").append(Float.toString(lastX)).append("\n");
        sb.append("Y: ").append(Float.toString(lastY)).append("\n");
        sb.append("Z: ").append(Float.toString(lastZ)).append("\n");

        accelerometerText.setText(sb.toString());
    }

    // Callback for when the sensor values change
    // Use this to update the UI for the accelerometer
    @Override
    public void onSensorChanged(SensorEvent event) {
        // Update the deltas
        deltaX = (lastX - event.values[0]);
        deltaY = (lastY - event.values[1]);
        deltaZ = (lastZ - event.values[2]);

        lastX = event.values[0];
        lastY = event.values[1];
        lastZ = event.values[2];

        // Check for collision
        if(collisionDetected()){
            accelerometerText.setText("Collision Detected!");

            // Send the socket message
            Thread thread = new Thread(){
                @Override
                public void run() {
                    try {
                        Date date = new Date();
                        long timeMilli = date.getTime();
                        sendHTTPPost("device" + deviceID + "=" + timeMilli);
                    }catch (Exception e){

                    }
                }
            };

            thread.start();

            // Clear the text after a while
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Thread.sleep(2000);
                    }catch (Exception e){

                    }

                    // Clear the text
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            clearText();
                        }
                    });
                }
            }).start();
        }
    }

    public static String getParamsString(Map<String, String> params)
            throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }

    // Send a message that device one is connected
    public void OnDeviceOneButtonClick(View view){
        deviceID = 1;

        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    sendHTTPPost("connected=device1");
                }catch (Exception e){

                }
            }
        };

        thread.start();
    }

    // Send a message that device two is connected
    public void OnDeviceTwoButtonClick(View view){
        deviceID = 2;

        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    sendHTTPPost("connected=device2");
                }catch (Exception e){

                }
            }
        };

        thread.start();
    }

    public void sendHTTPPost(String params) throws IOException, UnknownHostException, ClassNotFoundException, InterruptedException {
        String HOST = "192.168.1.107";
        int PORT = 8000;

        Socket socket = new Socket(HOST, PORT);

        PrintWriter out = new PrintWriter(socket.getOutputStream());

        out.print(params);
        out.close();
    }

    public void OnGoogleClick(View view){
        String url ="http://www.google.com";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        // Add the request to the RequestQueue.
        m_RequestQueue.add(stringRequest);
    }

    private void sendCollisionMessage(){

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // Check the accelerometer values to detect if a collision happened
    private Boolean collisionDetected(){
        // Compare the acceleration to the threshold
        if(deltaX > collisionThreshold || deltaY > collisionThreshold || deltaZ > collisionThreshold){
            return true;
        }

        return false;
    }
}
