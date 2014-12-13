package com.denyszaiats.myreactions;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
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
    private DrawCircle drawCircleNominative;
    private RelativeLayout areaView;
    private RelativeLayout areaViewAppear;
    private RelativeLayout areaColorAppear;
    private RelativeLayout resultsArea;
    private TextView textColorTimer;
    private TextView textColorScore;
    private LinkedList<Integer> usedCoordinates;
    private LinkedList<DrawRect> listCreatedViews;
    private int rectSize;

    private float rectX;
    private float rectY;
    private HashMap<Integer, Integer> colorMap;
    private Integer nominativeColor;
    private int results;
    private int genIndexColorRect;
    private LinkedList<Integer> listColor;
    private HashMap<Integer, String> mapCoord;

    public ChooseColorFragment(){}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        context = container.getContext();

        final View rootView = inflater.inflate(R.layout.fragment_choose_color, container, false);

        areaView = (RelativeLayout) rootView.findViewById(R.id.areaChooseColor);
        areaViewAppear = (RelativeLayout) rootView.findViewById(R.id.areaChooseColorAppear);
        areaColorAppear = (RelativeLayout) rootView.findViewById(R.id.areaColorAppear);
        buttonStart = (Button) rootView.findViewById(R.id.startButtonChooseColor);
        tryAgainButton = (Button) rootView.findViewById(R.id.tryAgainButtonChooseColor);
        resultsArea = (RelativeLayout) rootView.findViewById(R.id.resultsArea);
        textColorTimer = (TextView) rootView.findViewById(R.id.textTimerColor);
        textColorScore = (TextView) rootView.findViewById(R.id.textScoreColor);
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
                runGame();
            }
        });

        return rootView;
    }

    private void runGame(){
        colorMap.put(1, Color.BLACK);
        colorMap.put(2, Color.BLUE);
        colorMap.put(3, Color.RED);
        colorMap.put(4, Color.GREEN);
        colorMap.put(5, Color.YELLOW);
        colorMap.put(6, Color.MAGENTA);
        colorMap.put(7, Color.CYAN);
        colorMap.put(8, Color.GRAY);
        textColorScore.setText("0");
        tryAgainButton.setVisibility(View.INVISIBLE);
        buttonStart.setVisibility(View.INVISIBLE);
        resultsArea.setVisibility(View.VISIBLE);
        areaColorAppear.setVisibility(View.VISIBLE);
        initShapes();

        CountDownTimer cT = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                int va = (int) ((millisUntilFinished % 60000) / 1000);
                textColorTimer.setText("00:" + String.format("%02d", va));
            }

            public void onFinish() {
                textColorTimer.setText("Finish!");
                removeView();
                areaColorAppear.setVisibility(View.INVISIBLE);
                tryAgainButton.setVisibility(View.VISIBLE);
                results = 0;

            }
        };
        cT.start();
    }

    private void drawShapes(){
        listCreatedViews = new LinkedList<DrawRect>();
        listColor = new LinkedList<Integer>();
        usedCoordinates = new LinkedList<Integer>();
        generateMapCoordinates();
        for (int i = 0; i<mapCoord.size(); i++) {
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
                    calculateResults(v);
                }
            });
        }

        nominativeColor = listColor.get(generateRandomInteger(0,listColor.size()-1,new Random()));

        drawCircleNominative= new DrawCircle(context);
        drawCircleNominative.setLayoutParams(new RelativeLayout.LayoutParams(pxFromDp(50), pxFromDp(50)));
        drawCircleNominative.setRadius(pxFromDp(25));
        drawCircleNominative.setX(areaColorAppear.getWidth()/2 - pxFromDp(25));
        drawCircleNominative.setY(0);
        drawCircleNominative.setBackgroundColor(colorMap.get(nominativeColor));
        areaColorAppear.addView(drawCircleNominative);

        AlphaAnimation animation= new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(10000);
        areaViewAppear.startAnimation(animation);

    }

    private void calculateResults(View v){
        ColorDrawable color = (ColorDrawable) v.getBackground();
        if (color.getColor() == colorMap.get(nominativeColor)){
            results++;
        }
        else {

        }
        textColorScore.setText(String.valueOf(results));
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
        int generatedSize = pxFromDp(20);//generateRandomInteger(150, 200, random);
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
