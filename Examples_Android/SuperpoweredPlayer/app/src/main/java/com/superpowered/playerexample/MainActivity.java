package com.superpowered.playerexample;

import android.support.v7.app.AppCompatActivity;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.content.Context;
import android.widget.Button;
import android.view.View;
import android.util.Log;
import android.os.Bundle;
import android.widget.SeekBar;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {


    ScDsp scDsp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the device's sample rate and buffer size to enable
        // low-latency Android audio output, if available.
        String samplerateString = null, buffersizeString = null;
        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            samplerateString = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
            buffersizeString = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
        }
        if (samplerateString == null) samplerateString = "48000";
        if (buffersizeString == null) buffersizeString = "480";
        int samplerate = Integer.parseInt(samplerateString);
        int buffersize = Integer.parseInt(buffersizeString);

        // Files under res/raw are not zipped, just copied into the APK.
        // Get the offset and length to know where our file is located.
        AssetFileDescriptor fd = getResources().openRawResourceFd(R.raw.track);
        int fileOffset = (int) fd.getStartOffset();
        int fileLength = (int) fd.getLength();
        try {
            fd.getParcelFileDescriptor().close();
        } catch (IOException e) {
            Log.e("PlayerExample", "Close error.");
        }
        String path = getPackageResourcePath();         // get path to APK package
        System.loadLibrary("PlayerExample");    // load native library
        StartAudio(samplerate, buffersize);             // start audio engine
        OpenFile(path, fileOffset, fileLength);         // open audio file from APK
        // If the application crashes, please disable Instant Run under Build, Execution, Deployment in preferences.


        scDsp = new ScDsp();

        // TODO Set HL test result
        float[] frequencies = {2000.0f, 3000.0f, 4000.0f, 6000.0f};
        float[] leftValues = {40.0f, 50.0f, 60.0f, 70.0f};
        float[] rightValues = {50.0f, 60.0f, 70.0f, 80.0f};
        scDsp.setAudiogram(4, frequencies, leftValues, rightValues);

        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                scDsp.setParameter(0, (float)progress / 100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    // Handle Play/Pause button toggle.
    public void PlayerExample_PlayPause(View button) {
        TogglePlayback();
        playing = !playing;
        Button b = findViewById(R.id.playPause);
        b.setText(playing ? "Pause" : "Play");
    }

    @Override
    public void onPause() {
        super.onPause();
        onBackground();
    }

    @Override
    public void onResume() {
        super.onResume();
        onForeground();
    }

    protected void onDestroy() {
        super.onDestroy();
        Cleanup();
    }

    // Functions implemented in the native library.
    private native void StartAudio(int samplerate, int buffersize);

    private native void OpenFile(String path, int offset, int length);

    private native void TogglePlayback();

    private native void onForeground();

    private native void onBackground();

    private native void Cleanup();

    private boolean playing = false;
}
