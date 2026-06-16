package com.example.voicelock;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.accessibility.AccessibilityEvent;
import java.util.ArrayList;

public class VoiceLockService extends AccessibilityService {
    private SpeechRecognizer speechRecognizer;
    private AudioManager audioManager;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        initializeRecognizer();
    }

    private void initializeRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override public void onReadyForSpeech(Bundle params) {}
            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEndOfSpeech() {}
            @Override public void onError(int error) {
                handler.postDelayed(() -> startListening(), 3000);
            }
            @Override public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && matches.contains("lock screen")) {
                    performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN);
                }
                startListening();
            }
            @Override public void onPartialResults(Bundle partialResults) {}
            @Override public void onEvent(int eventType, Bundle params) {}
        });
        startListening();
    }

    private void startListening() {
        // Check if user turned it off via the Switch
        boolean isEnabled = getSharedPreferences("Settings", MODE_PRIVATE).getBoolean("is_enabled", true);
        if (!isEnabled) return;

        // Check if phone is busy (Call/Media)
        if (audioManager.isMusicActive() ||
                audioManager.getMode() == AudioManager.MODE_IN_CALL ||
                audioManager.getMode() == AudioManager.MODE_IN_COMMUNICATION) {
            handler.postDelayed(() -> startListening(), 5000);
            return;
        }

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizer.startListening(intent);
    }

    @Override public void onAccessibilityEvent(AccessibilityEvent event) {}
    @Override public void onInterrupt() { if (speechRecognizer != null) speechRecognizer.destroy(); }
}