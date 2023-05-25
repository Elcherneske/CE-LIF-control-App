package com.example.myapplicationforprojectversion1.model.model;

public class ChartData {
    private int data;
    private String name;
    private int time;//ms


    public ChartData(int data,int time){
        this.data = data;
        this.time = time;
        name="default";
    }


    public ChartData(int data,String name,int time){
        this.data=data;
        this.name=name;
        this.time = time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public void setData(int data) {
        this.data = data;
    }

    public int getData() {
        return data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
