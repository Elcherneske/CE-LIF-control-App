package com.example.myapplicationforprojectversion1.view.Activities.Interface;

import com.example.myapplicationforprojectversion1.model.model.ChartData;
import com.example.myapplicationforprojectversion1.view.ParameterClass.ParameterGenerator;

import java.util.List;

public interface UIHolder {

    void showData(List<ChartData> data);

    ParameterGenerator provideParameter();

    String provideDir();

    void showMessage(String message);

}
