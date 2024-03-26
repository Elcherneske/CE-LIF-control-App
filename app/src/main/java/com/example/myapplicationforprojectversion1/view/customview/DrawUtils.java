package com.example.myapplicationforprojectversion1.view.customview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.example.myapplicationforprojectversion1.model.model.ChartData;

import java.util.List;

public class DrawUtils {
    private Canvas canvas;
    private Paint paint;
    private int canvasWidth; //坐标轴宽度(padding已经减去)
    private int canvasHeight; //坐标轴高度(padding已经减去)
    private final int padding = 100; //坐标轴与View边界的一个gap
    private int data_range = 5000;//绘制数据的时候和标识纵坐标轴的时候有用
    private int data_min = 0;

    private List<ChartData> data;//理论上用一个类去表示data更好一点
    private static DrawUtils drawUtils;

    //线程安全的单例模式
    public static DrawUtils getInstance(){
        //先判断对象是否已经实例过，没有实例化过才进入加锁代码
        if(drawUtils==null){
            //类对象加锁
            synchronized (DrawUtils.class){
                if(drawUtils==null){
                    drawUtils=new DrawUtils();
                }
            }
        }
        return drawUtils;
    }


    public void setRange(int range){
        this.data_range=range;
    }
    public void setMin(int min){this.data_min = min;}
    public int getRange(){return this.data_range;}
    public int getMin(){return this.data_min;}




    public void drawChart(Canvas canvas,Paint paint,int canvasWidth,int canvasHeight,List<ChartData>data){
        //initialize
        this.data=data;
        this.canvas=canvas;
        this.canvasHeight=canvasHeight;
        this.canvasWidth=canvasWidth;
        this.paint=paint;
        drawBackgroud();
    }

    private void drawBackgroud(){
        drawAxis();
        drawData();
    }









    private void drawAxis(){
        final int gap=5;
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        Path path=new Path();
        path.moveTo(padding,canvasHeight+padding);
        path.lineTo(canvasWidth+padding,canvasHeight+padding);
        path.rLineTo(-10,-10);
        path.moveTo(canvasWidth+padding,canvasHeight+padding);
        path.rLineTo(-10,10);
        path.moveTo(padding,canvasHeight+padding);
        path.lineTo(padding,padding);
        path.rLineTo(10,10);
        path.moveTo(padding,padding);
        path.rLineTo(-10,10);

        path.moveTo(padding,canvasHeight+padding);
        for(int i=1;i<gap;i++){
            path.moveTo(padding,padding+canvasHeight-canvasHeight/(gap) * i);
            path.rLineTo(10,0);

        }

        for(int i=1;i<gap;i++){
            String string = String.valueOf(data_min+data_range/gap * i);
            float stringWidth = paint.measureText(string);
            float stringDown = (Math.abs(fontMetrics.ascent)-fontMetrics.descent)/2;
            canvas.drawText(string,padding-stringWidth-10,padding+canvasHeight-canvasHeight/(gap) * i+stringDown,paint);
        }

        canvas.drawPath(path,paint);
    }










    private void drawData(){
        if(data==null) return;

        int data_size = data.size();

        float x_space = (float)(canvasWidth)/data_size;

        float front_x=0;
        float front_y=0;
        float x=0;
        float y=0;
        Path path= new Path();

        for(int i=0;i<data_size;i++){
            x = padding + (x_space)*i;
            if(data.get(i).getData()-data_min>0){
                y = canvasHeight+padding-(((float)(data.get(i).getData())-(float) data_min)/(float)data_range)*((float)canvasHeight);
            }
            else{
                y = canvasHeight+padding;
            }


            if(i==0){
                path.moveTo(x,y);
            }
            else{
                path.quadTo(front_x,front_y,x,y);
            }

            front_x=x;
            front_y=y;

        }

        canvas.drawPath(path,paint);

    }















}
