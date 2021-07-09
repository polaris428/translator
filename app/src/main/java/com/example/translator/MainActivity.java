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

                tts.setPitch(1.5f); //1.5톤 올려서
                tts.setSpeechRate(1.0f); //1배속으로 읽기
                tts.speak(totalSpeak, TextToSpeech.QUEUE_FLUSH, null);



            }
        });

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName()); // 여분의 키
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR"); // 언어 설정

        // 버튼 클릭 시 객체에 Context와 listener를 할당

        RecognitionListener listener = new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                // 말하기 시작할 준비가되면 호출
                Toast.makeText(getApplicationContext(),"음성인식 시작",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBeginningOfSpeech() {
                // 말하기 시작했을 때 호출
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                // 입력받는 소리의 크기를 알려줌
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                // 말을 시작하고 인식이 된 단어를 buffer에 담음
            }

            @Override
            public void onEndOfSpeech() {
                // 말하기를 중지하면 호출
            }

            @Override
            public void onError(int error) {
                // 네트워크 또는 인식 오류가 발생했을 때 호출
                String message;

                switch (error) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        message = "오디오 에러";
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        message = "클라이언트 에러";
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        message = "퍼미션 없음";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        message = "네트워크 에러";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        message = "네트웍 타임아웃";
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        message = "찾을 수 없음";
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        message = "RECOGNIZER 가 바쁨";
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        message = "서버가 이상함";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        message = "말하는 시간초과";
                        break;
                    default:
                        message = "알 수 없는 오류임";
                        break;
                }

                Toast.makeText(getApplicationContext(), "에러 발생 : " + message,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(Bundle results) {
                // 인식 결과가 준비되면 호출
                // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줌
                ArrayList<String> matches =
                        results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                for(int i = 0; i < matches.size() ; i++){
                    input.setText(matches.get(i));
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                // 부분 인식 결과를 사용할 수 있을 때 호출
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                // 향후 이벤트를 추가하기 위해 예약
            }
        };



        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this); // 새 SpeechRecognizer 를 만드는 팩토리 메서드
                mRecognizer.setRecognitionListener(listener); // 리스너 설정
                mRecognizer.startListening(intent); // 듣기 시작
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
                        // Papago는 3번에서 만든 자바 코드이다.
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
                    text1.setText("영어");
                    text2.setText("한국어");

                }else{
                    language= "Korean";
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");
                    text1.setText("한국어");
                    text2.setText("영어");
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