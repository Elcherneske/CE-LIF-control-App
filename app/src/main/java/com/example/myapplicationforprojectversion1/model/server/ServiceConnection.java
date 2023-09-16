package com.example.myapplicationforprojectversion1.model.server;

import com.example.myapplicationforprojectversion1.model.model.ChartData;

import java.util.List;

public interface ServiceConnection {
    void sendData(List<ChartData> data);
}
