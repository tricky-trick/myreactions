package com.denyszaiats.myreactions;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.denyszaiats.myreactions.drawer.DrawCircle;
import com.denyszaiats.myreactions.drawer.DrawLine;
import com.denyszaiats.myreactions.drawer.DrawRect;
import com.denyszaiats.myreactions.drawer.DrawView;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

public class ChooseColorFragment extends Fragment {

    private Helper helper;
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
    private ImageView buttonRefresh;
    private LinkedList<Integer> usedCoordinates;
    private LinkedList<DrawRect> listCreatedViews;
    private LinkedList<DrawLine> listCreatedLines;
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
    private CountDownTimer cT;
    private int maxX;
    private int maxY;
    private int highlevel;

    public ChooseColorFragment(){}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        context = container.getContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        editor = prefs.edit();
        helper = new Helper();
        boolean isChecked = prefs.getBoolean(Constants.CHOOSE_COLOR_FRAGMENT + "_CHECKED", false);
        editor.putString(Constants.FRAGMENT_NAME, Constants.CHOOSE_COLOR_FRAGMENT);
        editor.commit();
        if(!isChecked) {
            Intent i = new Intent(context,
                    GuideModalActivity.class);
            startActivity(i);
        }

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
        buttonRefresh = (ImageView) rootView.findViewById(R.id.buttomRefresh);
        textHighScore = (TextView) rootView.findViewById(R.id.textColorHighscores);
        colorMap = new HashMap<Integer, Integer>();
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runGame();
            }
        });

        size = helper.getShapeStartSize(context);
        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                score = 0;
                level = 1;
                timeAppearing = 4;
                countShapes = 2;
                size = helper.getShapeStartSize(context);
                runGame();
            }
        });

        nextLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runGame();
            }
        });

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView msg = new TextView(getActivity());
                msg.setText("Do You really want to start new game?");
                msg.setPadding(20, 10, 20, 10);
                msg.setGravity(Gravity.CENTER);
                msg.setTextSize(20);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirmation");
                builder.setView(msg);

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        if(cT != null) {
                            cT.cancel();
                        }
                        editor.putBoolean(Constants.COLOR_IS_FINISHED, true);
                        level = 1;
                        size = helper.getShapeStartSize(context);
                        score = 0;
                        timeAppearing = 4;
                        countShapes = 2;
                        runGame();
                    }

                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        highscore = prefs.getInt(Constants.COLOR_HIGHSCORE,0);
        highlevel = prefs.getInt(Constants.COLOR_HIGHLEVEL,0);
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
            textHighScore.setVisibility(View.VISIBLE);
            buttonRefresh.setVisibility(View.VISIBLE);
            textColorTimer.setText("Level done!");
            textLevel.setText("Level " + String.valueOf(tempLevel));
            textColorScore.setText("Score: " + String.valueOf(tempScore));
            textHighScore.setText("High score: " + String.valueOf(highscore));
            level = tempLevel;
            score = tempScore;
        }
        return rootView;
    }

    private void runGame(){
        //setPaddingArea();
        removeView();
        editor.putBoolean(Constants.COLOR_IS_CLICKABLE, true);
        editor.commit();
        if(timeAppearing<=30) {
            timeAppearing = level + timeAppearing;
        }
        countShapes = level + countShapes;
        size = helper.getShapeSize(level, size, context);
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
        textHighScore.setVisibility(View.VISIBLE);
        buttonRefresh.setVisibility(View.VISIBLE);
        textLevel.setText("Level " + String.valueOf(level));
        initShapes();
        if(level > 1) {
            if (score >= 30 * level) {
                textColorScore.setTextColor(Color.WHITE);
            } else {
                textColorScore.setTextColor(Color.RED);
            }
        }

        cT = new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                int va = (int) ((millisUntilFinished % 60000) / 1000);
                textColorTimer.setText("00:" + String.format("%02d", va));
            }

            public void onFinish() {
                //areaColorAppear.setVisibility(View.INVISIBLE);
                editor.putBoolean(Constants.COLOR_IS_CLICKABLE,false);

                if(score >= 30 * level){
                    nextLevelButton.setVisibility(View.VISIBLE);
                    level++;
                    timeAppearing = 4;
                    countShapes = 2;
                    size = helper.getShapeStartSize(context);
                    editor.putBoolean(Constants.COLOR_IS_FINISHED,false);
                    textColorTimer.setText("Level done!");
                    highscore = prefs.getInt(Constants.COLOR_HIGHSCORE,0);
                    if(score>highscore) {
                        highscore = score;
                        textHighScore.setText("High score: " + String.valueOf(score));
                    }
                    if(level>highlevel) {
                        highlevel = level;
                        editor.putInt(Constants.COLOR_HIGHLEVEL, level);
                    }
                    editor.putInt(Constants.COLOR_HIGHSCORE, highscore);
                    editor.putInt(Constants.COLOR_TEMP_LEVEL, level);
                    editor.putInt(Constants.COLOR_TEMP_SCORE, score);

                }
                else {
                    textColorTimer.setText("Game over");
                    buttonRefresh.setVisibility(View.INVISIBLE);
                    tryAgainButton.setVisibility(View.VISIBLE);
                    highscore = prefs.getInt(Constants.COLOR_HIGHSCORE,0);
                    if(level>highlevel) {
                        highlevel = level;
                        editor.putInt(Constants.COLOR_HIGHLEVEL, level);
                    }
                    if(score>highscore) {
                        highscore = score;
                        textHighScore.setText("High score: " + String.valueOf(score));
                    }

                    editor.putInt(Constants.COLOR_HIGHSCORE, highscore);

                    editor.putBoolean(Constants.COLOR_IS_FINISHED,true);
                    level = 1;
                    timeAppearing = 4;
                    countShapes = 2;
                    score = 0;
                    size = helper.getShapeStartSize(context);
                }
                AlphaAnimation animation= new AlphaAnimation(0.0f, 1.0f);
                animation.setDuration(500);
                areaViewAppear.startAnimation(animation);
                editor.commit();

            }
        };
        cT.start();
    }

    private void drawShapes(){
        //removeGrid();
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
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    rectSize - pxFromDp(2), rectSize - pxFromDp(2)
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

        //removeGrid();
        setPaddingArea();
        //drawGrid();


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
        drawCircleNominative.setSideSize(pxFromDp(40));
        drawCircleNominative.setX(areaColorAppear.getWidth()/2 - pxFromDp(25));
        drawCircleNominative.setY(0);
        drawCircleNominative.setBackgroundColor(colorMap.get(nominativeColor));
        areaColorAppear.addView(drawCircleNominative);

        AlphaAnimation animation= new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(timeAppearing * 300);
        areaViewAppear.startAnimation(animation);

    }

    private void calculateResults(View v){
        ColorDrawable color = (ColorDrawable) v.getBackground();
        if (color.getColor() == colorMap.get(nominativeColor)){
            score += 10;
            if(score>highscore) {
                highscore = score;
                textHighScore.setText("High score: " + String.valueOf(score));
            }
        }
        if(level > 1) {
            if (score >= 30 * level) {
                textColorScore.setTextColor(Color.WHITE);
            } else {
                textColorScore.setTextColor(Color.RED);
            }
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
        drawShapes();
    }

    private void setPaddingArea(){
        int paddingX = (areaViewAppear.getWidth() - maxX)/2;
        int paddingY = (areaViewAppear.getHeight()- maxY)/2;

        areaViewAppear.setPadding(paddingX, paddingY, paddingX, paddingY);
    }

    private void generateMapCoordinates(){
        int generatedSize = pxFromDp(size);
        rectSize = generatedSize;
        maxX = 0;
        maxY = 0;
        int k = 1;
        mapCoord = new HashMap<Integer, String>();
        for (int i = 0; i< areaViewAppear.getWidth(); i++){
            for (int j = 0; j< areaViewAppear.getHeight(); j++){
                if ((i%generatedSize == 0) && (j%generatedSize==0)){
                    if (((i + generatedSize) < areaViewAppear.getWidth()) && ((j + generatedSize) < areaViewAppear.getHeight())) {
                        if((i+generatedSize) > maxX) {
                            maxX = (i+generatedSize);
                        }
                        if((j+generatedSize) > maxY) {
                            maxY = (j+generatedSize);
                        }
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

    private void drawGrid(){
        listCreatedLines = new LinkedList<DrawLine>();
        for(Map.Entry<Integer, String> map: mapCoord.entrySet()){
            if(map.getValue().toString().startsWith("0,")){
                DrawLine lineView = new DrawLine(context);
                lineView.setBackgroundColor(getResources().getColor(R.color.silver));
                int point = Integer.parseInt(map.getValue().split(",")[1]);
                lineView.setStartPoint(new Point(0, point));
                lineView.setEndPoint(new Point(areaView.getWidth(), point));
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        areaView.getWidth(), 5
                );
                lineView.setLayoutParams(params);
                lineView.setX(0);
                lineView.setY(point);
                listCreatedLines.add(lineView);
            }
            if(map.getValue().toString().endsWith(",0")){
                DrawLine lineView = new DrawLine(context);
                lineView.setBackgroundColor(getResources().getColor(R.color.silver));
                int point = Integer.parseInt(map.getValue().split(",")[0]);
                lineView.setStartPoint(new Point(point, 0));
                lineView.setEndPoint(new Point(point, areaView.getHeight()));
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        5, areaView.getHeight()
                );
                lineView.setLayoutParams(params);
                lineView.setY(0);
                lineView.setX(point);
                listCreatedLines.add(lineView);
            }
        }

        DrawLine lineViewX = new DrawLine(context);
        lineViewX.setBackgroundColor(getResources().getColor(R.color.silver));
        lineViewX.setStartPoint(new Point(maxX, 0));
        lineViewX.setEndPoint(new Point(maxX, areaView.getHeight()));
        RelativeLayout.LayoutParams paramsX = new RelativeLayout.LayoutParams(
                5, areaView.getHeight()
        );
        lineViewX.setLayoutParams(paramsX);
        lineViewX.setY(0);
        lineViewX.setX(maxX - 3);
        listCreatedLines.add(lineViewX);

        DrawLine lineViewY = new DrawLine(context);
        lineViewY.setBackgroundColor(getResources().getColor(R.color.silver));
        lineViewY.setStartPoint(new Point(maxY, 0));
        lineViewY.setEndPoint(new Point(maxY, areaView.getWidth()));
        RelativeLayout.LayoutParams paramsY = new RelativeLayout.LayoutParams(
                areaView.getWidth(), 5
        );
        lineViewY.setLayoutParams(paramsY);
        lineViewY.setY(maxY - 3);
        lineViewY.setX(0);
        listCreatedLines.add(lineViewY);


        for(DrawView line: listCreatedLines){
            areaViewAppear.addView(line);
        }
    }

    private void removeGrid(){
        if (listCreatedLines != null) {
            for (DrawView line : listCreatedLines) {
                if(line != null)
                    areaViewAppear.removeView(line);
            }
        }
        areaView.setPadding(0,0,0,0);
    }


}
