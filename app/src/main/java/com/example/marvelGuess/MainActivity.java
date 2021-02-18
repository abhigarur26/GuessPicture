package com.example.marvelGuess;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    Button option1;
    Button option2;
    Button option3;
    Button option4;
    String[] options = new String[4];
    ImageView celeb;
    String numLess;
    String mycode;
    List<String> imgURL = new ArrayList<String>();
    List<String> imgName = new ArrayList<String>();
   Random imgSrcRndm = new Random();
   int r =0;
   int lca = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        celeb = (ImageView) findViewById(R.id.celeb);
        option1 = (Button) findViewById(R.id.option1);
        option2 = (Button) findViewById(R.id.option2);
        option3 = (Button) findViewById(R.id.option3);
        option4 = (Button) findViewById(R.id.option4);

        CodeProcessor();
       ImageProcessor();


    }

    public void inputAnswer(View view) {
        if(view.getTag().toString().equals(Integer.toString(lca))){
            Toast.makeText(getApplicationContext(), "Correct !", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "Wrong ! It was " + imgName.get(r), Toast.LENGTH_SHORT).show();
        }
        ImageProcessor();

    }



        public void CodeProcessor(){
            CodeDownloader codeDownld = new CodeDownloader();
            try {
                mycode = codeDownld.execute("https://comicvine.gamespot.com/profile/theoptimist/lists/top-100-marvel-characters/32199/").get();
                String[] splitcode = mycode.split("<a href=\"/cassie-lang/4005-40516/\">");
                String splitt = splitcode[0];
                String[] splitcode2 = splitt.split("<div class=\"profile-title-hold\">");
                String finalSplit = splitcode2[1];

                Pattern p = Pattern.compile("img src=\"(.*?)\" />");
                Matcher m = p.matcher(finalSplit);
                while (m.find()) {

                    imgURL.add(m.group(1));
                }
                p = Pattern.compile("<h3>(.*?)</h3>");
                m = p.matcher(finalSplit);

                while (m.find()) {
                    numLess = m.group(1).replaceAll("[0123456789.]","");
                    imgName.add(numLess);
                }
                Log.i("name", imgName.toString());
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (ExecutionException ex) {
                ex.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        public void ImageProcessor(){
            r = imgSrcRndm.nextInt(90);
        ImageDownloader imgDownld = new ImageDownloader();
            try {
                Bitmap myImageName = imgDownld.execute(imgURL.get(r)).get();
                celeb.setImageBitmap(myImageName);
                lca = imgSrcRndm.nextInt(4);
                int incorrectAnswer;
                for(int i=0; i<4; i++){
                    if(i == lca){
                        options[i] = imgName.get(r);
                    }else{
                        incorrectAnswer = imgSrcRndm.nextInt(imgName.size());
                        while (incorrectAnswer==r){
                            incorrectAnswer = imgSrcRndm.nextInt(imgName.size());
                        }
                        options[i] = imgName.get(incorrectAnswer);
                    }
                }
                option1.setText(options[0]);
                option2.setText(options[1]);
                option3.setText(options[2]);
                option4.setText(options[3]);

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        public class CodeDownloader extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... urls) {
                String code = "";
                URL pageurl;
                HttpURLConnection urlConnection = null;
                try {
                    pageurl = new URL(urls[0]);
                    urlConnection = (HttpURLConnection) pageurl.openConnection();
                    InputStream inputStream = urlConnection.getInputStream();
                    InputStreamReader codeReader = new InputStreamReader(inputStream);
                    int data = codeReader.read();
                    while (data != -1) {
                        char current = (char) data;
                        code += current;
                        data = codeReader.read();
                    }
                    return code;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... imgUrls) {
            try{

                URL imageUrl = new URL(imgUrls[0]);
                HttpURLConnection imgUrlConnection = (HttpURLConnection) imageUrl.openConnection();
                imgUrlConnection.connect();
                InputStream imgInputStream = imgUrlConnection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(imgInputStream);
                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    }
