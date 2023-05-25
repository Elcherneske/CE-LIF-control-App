package com.example.myapplicationforprojectversion1.model.model;

import java.util.List;

public interface DataProvider {
    List<ChartData> getData(int size);
    List<ChartData> getData(int size,int swift);
    int getDataPoolSize();
    void stopAll();
    void beginAll();
}
