package com.vedam.xylophone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;


public class MainActivity extends AppCompatActivity {
    private SoundPool soundPool;
    private int sound1, sound2, sound3, sound4, sound5, sound6,sound7;
    private int sound3StreamId;
    SharedPreferences sharedpreferences;
    String hostAddress;
    MqttAndroidClient client;
    static String topic="setColor";
    private Boolean connectionLocalStatus=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(6)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        }

        sound1 = soundPool.load(this, R.raw.note1, 1);
        sound2 = soundPool.load(this, R.raw.note2, 1);
        sound3 = soundPool.load(this, R.raw.note3, 1);
        sound4 = soundPool.load(this, R.raw.note4, 1);
        sound5 = soundPool.load(this, R.raw.note5, 1);
        sound6 = soundPool.load(this, R.raw.note6, 1);
        sound7 = soundPool.load(this, R.raw.note7, 1);

        sharedpreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        setConfigurations();

        if(isNetworkAvailable()){
            isConnectedToLocalServer();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, ServerActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void playSound(View v) {
        switch (v.getId()) {
            case R.id.button_sound1:
                soundPool.play(sound1, 1, 1, 0, 0, 1);
                //soundPool.pause(sound3StreamId);
                soundPool.autoPause();
                publishOnSetLocal(getString(R.string.color1));
                break;
            case R.id.button_sound2:
                soundPool.play(sound2, 1, 1, 0, 0, 1);
                publishOnSetLocal(getString(R.string.color2));
                break;
            case R.id.button_sound3:
                sound3StreamId = soundPool.play(sound3, 1, 1, 0, 0, 1);
                publishOnSetLocal(getString(R.string.color3));
                break;
            case R.id.button_sound4:
                soundPool.play(sound4, 1, 1, 0, 0, 1);
                publishOnSetLocal(getString(R.string.color4));
                break;
            case R.id.button_sound5:
                soundPool.play(sound5, 1, 1, 0, 0, 1);
                publishOnSetLocal(getString(R.string.color5));
                break;
            case R.id.button_sound6:
                soundPool.play(sound6, 1, 1, 0, 0, 1);
                publishOnSetLocal(getString(R.string.color6));
                break;
            case R.id.button_sound7:
                soundPool.play(sound7, 1, 1, 0, 0, 1);
                publishOnSetLocal(getString(R.string.color7));
                break;
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setConfigurations(){
        hostAddress = sharedpreferences.getString("host1","192.168.1.14:1883");
    }
    private Boolean isConnectedToLocalServer(){

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://" + hostAddress,
                clientId);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                     Toast.makeText(MainActivity.this, "Connected to Server", Toast.LENGTH_SHORT).show();
                    //  ToastMessage("Connected to Local",PosToast);
                    connectionLocalStatus = true;

                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("error",exception.getLocalizedMessage());
                    Toast.makeText(MainActivity.this, "Not Connected to Server", Toast.LENGTH_SHORT).show();
                        connectionLocalStatus = false;
                    // ToastMessage("Local not connected",NegToast);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return connectionLocalStatus;
    }

    private void publishOnSetLocal(String payload){


        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);

        } catch (UnsupportedEncodingException | MqttException e) {
            Log.d("publishError:Set:",e.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }
}