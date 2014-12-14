package com.denyszaiats.myreactions;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.denyszaiats.myreactions.drawer.DrawCircle;
import com.denyszaiats.myreactions.drawer.DrawRect;
import com.denyszaiats.myreactions.drawer.DrawView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

public class ChooseColorFragment extends Fragment {

    private Context context;
    private Button buttonStart;
    private Button tryAgainButton;
    private Button nextLevelButton;
    private DrawCircle drawCircleNominative;
    private RelativeLayout areaView;
    private RelativeLayout areaViewAppear;
    private RelativeLayout areaColorAppear;
    private RelativeLayout scoreArea;
    private TextView textColorTimer;
    private TextView textColorScore;
    private TextView textLevel;
    private TextView textHighScore;
    private LinkedList<Integer> usedCoordinates;
    private LinkedList<DrawRect> listCreatedViews;
    private int rectSize;
    private SharedPreferences prefs;
    private float rectX;
    private float rectY;
    private HashMap<Integer, Integer> colorMap;
    private Integer nominativeColor;
    private int score;
    private  int highscore;
    private int genIndexColorRect;
    private int level = 1;
    private int timeAppearing = 4;
    private int countShapes = 2;
    private int size = 105;
    private LinkedList<Integer> listColor;
    private HashMap<Integer, String> mapCoord;
    private SharedPreferences.Editor editor;

    public ChooseColorFragment(){}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        context = container.getContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        editor = prefs.edit();
        final View rootView = inflater.inflate(R.layout.fragment_choose_color, container, false);

        areaView = (RelativeLayout) rootView.findViewById(R.id.areaChooseColor);
        areaViewAppear = (RelativeLayout) rootView.findViewById(R.id.areaChooseColorAppear);
        areaColorAppear = (RelativeLayout) rootView.findViewById(R.id.areaColorAppear);
        buttonStart = (Button) rootView.findViewById(R.id.startButtonChooseColor);
        nextLevelButton = (Button) rootView.findViewById(R.id.nextLevelButtonChooseColor);
        tryAgainButton = (Button) rootView.findViewById(R.id.tryAgainButtonChooseColor);
        scoreArea = (RelativeLayout) rootView.findViewById(R.id.resultsArea);
        textColorTimer = (TextView) rootView.findViewById(R.id.textTimerColor);
        textColorScore = (TextView) rootView.findViewById(R.id.textScoreColor);
        textLevel = (TextView) rootView.findViewById(R.id.textLevel);
        textHighScore = (TextView) rootView.findViewById(R.id.textColorHighscores);
        colorMap = new HashMap<Integer, Integer>();
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runGame();
            }
        });

        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                score = 0;
                level = 1;
                timeAppearing = 4;
                countShapes = 2;
                size = 100;
                runGame();
            }
        });

        nextLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runGame();
            }
        });

        highscore = prefs.getInt(Constants.COLOR_HIGHSCORE,0);
        textHighScore.setText("High score: " + String.valueOf(highscore));

        boolean isFinished = prefs.getBoolean(Constants.COLOR_IS_FINISHED, true);
        if (!isFinished) {
            int tempScore = prefs.getInt(Constants.COLOR_TEMP_SCORE, 0);
            int tempLevel = prefs.getInt(Constants.COLOR_TEMP_LEVEL, 0);
            tryAgainButton.setVisibility(View.INVISIBLE);
            nextLevelButton.setVisibility(View.VISIBLE);
            buttonStart.setVisibility(View.INVISIBLE);
            scoreArea.setVisibility(View.VISIBLE);
            areaColorAppear.setVisibility(View.VISIBLE);
            textLevel.setVisibility(View.VISIBLE);
            textColorScore.setVisibility(View.VISIBLE);
            textColorTimer.setVisibility(View.VISIBLE);
            textColorTimer.setText("Finish!");
            textLevel.setText("Level " + String.valueOf(tempLevel));
            textColorScore.setText("Score: " + String.valueOf(tempScore));
            level = tempLevel;
            score = tempScore;
        }
        return rootView;
    }

    private void runGame(){
        removeView();
        editor.putBoolean(Constants.COLOR_IS_CLICKABLE, true);
        editor.commit();
        if(timeAppearing<=30) {
            timeAppearing = level + timeAppearing;
        }
        countShapes = level + countShapes;
        if((size - level * 5)>=30) {
            size = size - level * 5;
        }
        else {
            size = 30;
        }
        colorMap.put(1, Color.BLACK);
        colorMap.put(2, Color.BLUE);
        colorMap.put(3, Color.RED);
        colorMap.put(4, Color.GREEN);
        colorMap.put(5, Color.YELLOW);
        colorMap.put(6, Color.MAGENTA);
        colorMap.put(7, Color.CYAN);
        colorMap.put(8, Color.GRAY);
        textColorScore.setText("Score: " + String.valueOf(score));
        tryAgainButton.setVisibility(View.INVISIBLE);
        nextLevelButton.setVisibility(View.INVISIBLE);
        buttonStart.setVisibility(View.INVISIBLE);
        scoreArea.setVisibility(View.VISIBLE);
        areaColorAppear.setVisibility(View.VISIBLE);
        textLevel.setVisibility(View.VISIBLE);
        textLevel.setText("Level " + String.valueOf(level));
        initShapes();

        CountDownTimer cT = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                int va = (int) ((millisUntilFinished % 60000) / 1000);
                textColorTimer.setText("00:" + String.format("%02d", va));
            }

            public void onFinish() {
                textColorTimer.setText("Finish!");
                //areaColorAppear.setVisibility(View.INVISIBLE);
                editor.putBoolean(Constants.COLOR_IS_CLICKABLE,false);

                if(score >= 10*level){
                    nextLevelButton.setVisibility(View.VISIBLE);
                    level++;
                    timeAppearing = 4;
                    countShapes = 2;
                    size = 100;
                    editor.putBoolean(Constants.COLOR_IS_FINISHED,false);
                }
                else {
                    tryAgainButton.setVisibility(View.VISIBLE);
                    highscore = prefs.getInt(Constants.COLOR_HIGHSCORE,0);
                    if(score > highscore){
                        highscore = score;
                    }
                    textHighScore.setText("High score: " + String.valueOf(highscore));
                    editor.putInt(Constants.COLOR_HIGHSCORE, highscore);
                    editor.putBoolean(Constants.COLOR_IS_FINISHED,true);
                    level = 1;
                    timeAppearing = 4;
                    countShapes = 2;
                    score = 0;
                    size = 100;
                    AlphaAnimation animation= new AlphaAnimation(0.0f, 1.0f);
                    animation.setDuration(500);
                    areaViewAppear.startAnimation(animation);
                }
                editor.putInt(Constants.COLOR_TEMP_LEVEL, level);
                editor.putInt(Constants.COLOR_TEMP_SCORE, score);
                editor.commit();
            }
        };
        cT.start();
    }

    private void drawShapes(){
        listCreatedViews = new LinkedList<DrawRect>();
        listColor = new LinkedList<Integer>();
        usedCoordinates = new LinkedList<Integer>();
        generateMapCoordinates();
        if(countShapes>=mapCoord.size()){
            countShapes = mapCoord.size();
        }
        for (int i = 0; i < countShapes; i++) {
            generateShapesParameters();
            DrawRect drawRect = new DrawRect(context);
            //drawRect.setLayoutParams(new ViewGroup.LayoutParams(rectSize, rectSize), );
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    rectSize, rectSize
            );
            params.setMargins(5,5,5,5);
            drawRect.setLayoutParams(params);
            drawRect.setSideSize(rectSize);
            Random randColorRect = new Random();
            if(listColor.size()<colorMap.size()) {
                do {
                    genIndexColorRect = generateRandomInteger(1, colorMap.size(), randColorRect);
                }
                while (listColor.contains(genIndexColorRect));
            }
            else{
                genIndexColorRect = generateRandomInteger(1, colorMap.size(), randColorRect);
            }
            drawRect.setBackgroundColor(colorMap.get(genIndexColorRect));
            drawRect.setX(rectX);
            drawRect.setY(rectY);
            listCreatedViews.add(drawRect);
            listColor.add(genIndexColorRect);
        }

        for (DrawRect drawRect: listCreatedViews) {
            do{areaViewAppear.addView(drawRect);}
            while (areaViewAppear.indexOfChild(drawRect) == -1);
            drawRect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isClickable = prefs.getBoolean(Constants.COLOR_IS_CLICKABLE, true);
                    if (isClickable)
                        calculateResults(v);
                }
            });
        }

        nominativeColor = listColor.get(generateRandomInteger(0,listColor.size()-1,new Random()));

        drawCircleNominative= new DrawCircle(context);
        drawCircleNominative.setLayoutParams(new RelativeLayout.LayoutParams(pxFromDp(40), pxFromDp(40)));
        drawCircleNominative.setRadius(pxFromDp(25));
        drawCircleNominative.setX(areaColorAppear.getWidth()/2 - pxFromDp(25));
        drawCircleNominative.setY(0);
        drawCircleNominative.setBackgroundColor(colorMap.get(nominativeColor));
        areaColorAppear.addView(drawCircleNominative);

        AlphaAnimation animation= new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(timeAppearing * 1000);
        areaViewAppear.startAnimation(animation);

    }

    private void calculateResults(View v){
        ColorDrawable color = (ColorDrawable) v.getBackground();
        if (color.getColor() == colorMap.get(nominativeColor)){
            score++;
        }
        textColorScore.setText("Score: " + String.valueOf(score));
        initShapes();
        System.gc();
    }

    private int pxFromDp(float dp)
    {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    private void initShapes(){
        removeView();
        //generateShapesParameters();
        drawShapes();
    }

    private void generateMapCoordinates(){
        //Random random = new Random();
        int generatedSize = pxFromDp(size);//generateRandomInteger(150, 200, random);
        rectSize = generatedSize;

        int k = 1;
        mapCoord = new HashMap<Integer, String>();
        for (int i = 0; i< areaViewAppear.getWidth(); i++){
            for (int j = 0; j< areaViewAppear.getHeight(); j++){
                if ((i%generatedSize == 0) && (j%generatedSize==0)){
                    if (((i + generatedSize) < areaViewAppear.getWidth()) && ((j + generatedSize) < areaViewAppear.getHeight())) {
                        mapCoord.put(k, i + "," + j);
                        k++;
                    }
                }
            }
        }
    }

    private void generateShapesParameters(){
        Random randomNumberRect = new Random();
        int randomGeneratedNumberRect;
        do{randomGeneratedNumberRect = generateRandomInteger(1, mapCoord.size(), randomNumberRect);}
        while(usedCoordinates.contains(randomGeneratedNumberRect));

        rectX = Float.parseFloat(mapCoord.get(randomGeneratedNumberRect).split(",")[0]);
        rectY = Float.parseFloat(mapCoord.get(randomGeneratedNumberRect).split(",")[1]);

        usedCoordinates.add(randomGeneratedNumberRect);
    }


    private int generateRandomInteger(int aStart, int aEnd, Random aRandom){
        if (aStart > aEnd) {
            int tempEnd = aEnd;
            int tempStart = aStart;

            aEnd = tempStart;
            aStart = tempEnd;
        }
        long range = (long)aEnd - (long)aStart + 1;
        long fraction = (long)(range * aRandom.nextDouble());
        return (int)(fraction + aStart);
    }

    private void removeView(){
        if(listCreatedViews != null) {
            for (DrawView view : listCreatedViews) {
                if (view != null) {
                    areaViewAppear.removeView(view);
                }
            }
        }
    }


}
