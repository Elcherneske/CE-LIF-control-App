package com.example.myapplicationforprojectversion1.model.model;

import com.example.myapplicationforprojectversion1.model.device.BlueToothServiceConnection;
import com.example.myapplicationforprojectversion1.model.service.ServerConnection;
import com.example.myapplicationforprojectversion1.presenter.ParameterPresenter;
import com.example.myapplicationforprojectversion1.view.ParameterClass.ParameterGenerator;

public class ParameterContainer implements ParameterHolder {
    private BlueToothServiceConnection deviceConnection;
    private ServerConnection serverConnection;
    private ParameterPresenter presenter;

    public ParameterContainer(ParameterPresenter presenter){
        this.presenter = presenter;
        deviceConnection = BlueToothServiceConnection.getInstance();
        serverConnection = ServerConnection.getInstance();
    }

    @Override
    public void sendParameter(ParameterGenerator parameter) {
        if(deviceConnection!=null){
            deviceConnection.sendParameter(parameter);
        }
        else{
            presenter.sendMessage("parameter set fail");
        }

        if(serverConnection!=null && serverConnection.isConnected()){
            serverConnection.sendParameter(parameter);
        }
    }
}
