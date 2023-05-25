package com.example.myapplicationforprojectversion1.model.device;

import com.example.myapplicationforprojectversion1.model.model.ChartData;
import com.example.myapplicationforprojectversion1.view.ParameterClass.ParameterGenerator;

import java.util.List;

public interface DeviceConnection {

    void sendParameter(ParameterGenerator parameter);

    List<ChartData> fetchAllData();

    void sendBeginMessage();

    void sendStopMessage();
}
