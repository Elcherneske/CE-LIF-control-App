package com.example.myapplicationforprojectversion1.presenter;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplicationforprojectversion1.model.model.ChartData;
import com.example.myapplicationforprojectversion1.model.model.DataContainer;
import com.example.myapplicationforprojectversion1.model.model.DataProvider;
import com.example.myapplicationforprojectversion1.view.Activities.CELIF.GeneratorActivity;
import com.example.myapplicationforprojectversion1.view.Activities.CELIF.Views;
import com.example.myapplicationforprojectversion1.view.Activities.Interface.UIHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class DataPresenter {
    private UIHolder uiHolder;
    private DataProvider dataProvider;
    private final int default_size = 20000;
    private int size = 20000;
    private int swift = 0;

    //构造函数
    public DataPresenter(UIHolder uiHolder){
        this.uiHolder=uiHolder;
        dataProvider = new DataContainer(uiHolder);
    }

    public void getData()
    {
       List<ChartData> data = dataProvider.getData(size,swift);
        uiHolder.showData(data);
    }

    public void stopAll()
    {
        dataProvider.stopAll();
    }

    public void beginAll()
    {
        dataProvider.beginAll();
    }

    //debug用
    public void sendMessage(String message){this.uiHolder.showMessage(message);}




    //以下函数都是用于图表界面实时显示对应参数的设置
    public void setSwift(int swift){
        if(swift<0){
            this.swift = 0;
            return;
        }

        if(dataProvider.getDataPoolSize()<size){
            this.swift = 0;
            return;
        }
        else if (dataProvider.getDataPoolSize()<=size+swift){
            this.swift = dataProvider.getDataPoolSize() - size;
        }
        else{
            this.swift = swift;
        }
    }

    public void setSize(int size){
        if(size<250){
            this.size = 250;
        }
        if(size>default_size){
            this.size = default_size;
        }
        this.size = size;
    }

    public int getDefault_size(){return this.default_size;}

    public void magnifySwift(){
        setSwift(this.swift + this.size / 2);
    }

    public void shrinkSwift(){
        setSwift(this.swift - this.size / 2);
    }

    public void magnifySize(){
        setSize(this.size*2);
    }

    public void shrinkSize(){
        setSize(this.size/2);
    }

    public void setDefaultSize(){
        setSize(default_size);
    }

}
