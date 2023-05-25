package com.example.myapplicationforprojectversion1.view.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.myapplicationforprojectversion1.model.model.ChartData;

import java.util.ArrayList;
import java.util.List;


/*
public class ChartView extends SurfaceView implements SurfaceHolder.Callback{
    private int canvasWidth;
    private int canvasHeight;
    private final int padding=100;
    private Paint paint;
    private SurfaceHolder surfaceHolder;
    private ExecutorService threadPool;
    private Timer timer;

    private List<ChartData> data = new ArrayList<ChartData>();


    //form the ChartView
    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initializeView();
        initializePaint();
    }


    //the three function that the SurfaceView must complete


    //initialize: get the width and height
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.canvasWidth=getWidth()-2*padding;
        this.canvasHeight=getHeight()-2*padding;


        startTimer();
    }



    //when the View be changed(don't used)
    @Override
    public void surfaceChanged(SurfaceHolder holder,int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }












    public void stopTimer(){
        timer.cancel();
    }
    public void beginTimer(){
        startTimer();
    }


    public void setData(List<ChartData> data)
    {
        this.data=data;
    }





    private void startTimer(){
        timer=new Timer();
        TimerTask task= new TimerTask() {
            @Override
            public void run() {

                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        Canvas canvas = surfaceHolder.lockCanvas();
                        DrawUtils.getInstance().drawChart(canvas,paint,canvasWidth,canvasHeight,data);
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                });


            }
        };
        timer.schedule(task,0,20);
    }


    //get the callback object and built the threadpool
    private void initializeView(){
        surfaceHolder=getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setKeepScreenOn(true);

        threadPool= Executors.newCachedThreadPool();
    }

    //set the paint of the View
    private void initializePaint(){
        this.paint=new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setColor(Color.WHITE);
        this.paint.setStrokeWidth(2);
        this.paint.setStyle(Paint.Style.STROKE);//只绘制轮廓
        //other style
        this.paint.setTypeface(Typeface.DEFAULT);
        this.paint.setTextSize(35);
    }






    //notice: the SurfaceView doesn't need to use onDraw function
    //notice: the SurfaceView doesn't need to use onSizeChanged function

}
*/








public class ChartView extends SurfaceView implements SurfaceHolder.Callback{
    private int canvasWidth;
    private int canvasHeight;
    private final int padding=100;
    private Paint paint;
    private SurfaceHolder surfaceHolder;

    private List<ChartData> data = new ArrayList<ChartData>();


    //form the ChartView
    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initializeView();
        initializePaint();
    }


    //the three function that the SurfaceView must complete


    //initialize: get the width and height
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.canvasWidth=getWidth()-2*padding;
        this.canvasHeight=getHeight()-2*padding;

    }

    //when the View be changed(don't used)
    @Override
    public void surfaceChanged(SurfaceHolder holder,int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }



    public void rangeExpand(){
        int range = DrawUtils.getInstance().getRange();
        if(range+500 > 5000){
            range = 5000;
        }
        else{
            range += 500;
        }
        DrawUtils.getInstance().setRange(range);
    }

    public void rangeShrink(){
        int range = DrawUtils.getInstance().getRange();
        if(range - 500 <500){
            range = 500;
        }
        else{
            range -= 500;
        }
        DrawUtils.getInstance().setRange(range);
    }

    public void setDefaultRange(){
        DrawUtils.getInstance().setRange(5000);
        DrawUtils.getInstance().setMin(0);
    }


    public void upSwift(){
        int min = DrawUtils.getInstance().getMin();
        int range = DrawUtils.getInstance().getRange();
        if(min+range/2 >= 5000-range) {
            min = 5000-range;
        }
        else{
            min += range/2;
        }
        DrawUtils.getInstance().setMin(min);
    }

    public void downSwift(){
        int min = DrawUtils.getInstance().getMin();
        int range = DrawUtils.getInstance().getRange();
        if(min-range/2 < 0 ) {
            min = 0;
        }
        else{
            min -= range/2;
        }
        DrawUtils.getInstance().setMin(min);
    }







    public void setData(List<ChartData> data)
    {
        this.data=data;
        Canvas canvas = surfaceHolder.lockCanvas();
        if(canvas==null) return;
        canvas.drawRGB(0,0,0);
        DrawUtils.getInstance().drawChart(canvas,paint,canvasWidth,canvasHeight,data);
        surfaceHolder.unlockCanvasAndPost(canvas);
    }




    //get the callback object
    private void initializeView(){
        surfaceHolder=getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setKeepScreenOn(true);

    }

    //set the paint of the View
    private void initializePaint(){
        this.paint=new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setColor(Color.WHITE);
        this.paint.setStrokeWidth(2);
        this.paint.setStyle(Paint.Style.STROKE);//只绘制轮廓
        //other style
        this.paint.setTypeface(Typeface.DEFAULT);
        this.paint.setTextSize(35);
    }






    //notice: the SurfaceView doesn't need to use onDraw function
    //notice: the SurfaceView doesn't need to use onSizeChanged function

}

















