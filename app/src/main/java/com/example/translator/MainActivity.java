package com.example.translator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Intent intent;
    SpeechRecognizer mRecognizer;

    TextToSpeech tts;

    TextView text1;
    TextView text2;
    TextView changedText;
    ImageButton Change;
    EditText input;
    Button voice;
    Button hedset;
    String language = "Korean";
    final int PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        buttonClickListenr();
        if(Build.VERSION.SDK_INT >= 23){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO},PERMISSION);
        }

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=android.speech.tts.TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });



        hedset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String totalSpeak = changedText.getText().toString();

                tts.setPitch(1.5f); //1.5??? ?????????
                tts.setSpeechRate(1.0f); //1???????????? ??????
                tts.speak(totalSpeak, TextToSpeech.QUEUE_FLUSH, null);



            }
        });

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName()); // ????????? ???
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR"); // ?????? ??????

        // ?????? ?????? ??? ????????? Context??? listener??? ??????

        RecognitionListener listener = new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                // ????????? ????????? ??????????????? ??????
                Toast.makeText(getApplicationContext(),"???????????? ??????",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBeginningOfSpeech() {
                // ????????? ???????????? ??? ??????
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                // ???????????? ????????? ????????? ?????????
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                // ?????? ???????????? ????????? ??? ????????? buffer??? ??????
            }

            @Override
            public void onEndOfSpeech() {
                // ???????????? ???????????? ??????
            }

            @Override
            public void onError(int error) {
                // ???????????? ?????? ?????? ????????? ???????????? ??? ??????
                String message;

                switch (error) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        message = "????????? ??????";
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        message = "??????????????? ??????";
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        message = "????????? ??????";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        message = "???????????? ??????";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        message = "????????? ????????????";
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        message = "?????? ??? ??????";
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        message = "RECOGNIZER ??? ??????";
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        message = "????????? ?????????";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        message = "????????? ????????????";
                        break;
                    default:
                        message = "??? ??? ?????? ?????????";
                        break;
                }

                Toast.makeText(getApplicationContext(), "?????? ?????? : " + message,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(Bundle results) {
                // ?????? ????????? ???????????? ??????
                // ?????? ?????? ArrayList??? ????????? ?????? textView??? ????????? ?????????
                ArrayList<String> matches =
                        results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                for(int i = 0; i < matches.size() ; i++){
                    input.setText(matches.get(i));
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                // ?????? ?????? ????????? ????????? ??? ?????? ??? ??????
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                // ?????? ???????????? ???????????? ?????? ??????
            }
        };



        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this); // ??? SpeechRecognizer ??? ????????? ????????? ?????????
                mRecognizer.setRecognitionListener(listener); // ????????? ??????
                mRecognizer.startListening(intent); // ?????? ??????
            }
        });


        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                new Thread(){
                    @Override
                    public void run() {
                        String word = input.getText().toString();
                        // Papago??? 3????????? ?????? ?????? ????????????.
                        Papagotranslator papago = new Papagotranslator();
                        String resultWord;

                        if(language.equals("Korean")){
                            resultWord= papago.getTranslation(word,"ko","en");
                        }else{
                            resultWord= papago.getTranslation(word,"en","ko");
                        }

                        Bundle papagoBundle = new Bundle();
                        papagoBundle.putString("resultWord",resultWord);



                        Message msg = papago_handler.obtainMessage();
                        msg.setData(papagoBundle);
                        papago_handler.sendMessage(msg);


                    }
                }.start();

            }
        });


    }


    private void init(){
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        Change = findViewById(R.id.Change);
        input = findViewById(R.id.input);
        voice=findViewById(R.id.voice);
        hedset=findViewById(R.id.hedset);
        changedText = findViewById(R.id.changedText);
        voice=findViewById(R.id.voice);
    }

    @SuppressLint("HandlerLeak")
    Handler papago_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String resultWord = bundle.getString("resultWord");
            changedText.setText(resultWord);
        }
    };

    private void buttonClickListenr(){

        Change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(language.equals("Korean")){
                    language= "English";
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"en-US");
                    text1.setText("??????");
                    text2.setText("?????????");

                }else{
                    language= "Korean";
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");
                    text1.setText("?????????");
                    text2.setText("??????");
                }
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(tts != null){
            tts.stop();
            tts.shutdown();
            tts = null;
        }

        if(mRecognizer!=null){
            mRecognizer.destroy();
            mRecognizer.cancel();
            mRecognizer = null;
        }
    }





}