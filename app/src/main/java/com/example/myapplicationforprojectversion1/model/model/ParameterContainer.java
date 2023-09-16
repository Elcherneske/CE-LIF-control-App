package com.example.myapplicationforprojectversion1.model.model;

import com.example.myapplicationforprojectversion1.model.device.BlueToothServiceConnection;
import com.example.myapplicationforprojectversion1.model.server.ServerConnection;
import com.example.myapplicationforprojectversion1.presenter.ParameterPresenter;
import com.example.myapplicationforprojectversion1.view.ParameterClass.CELIFParameter;
import com.example.myapplicationforprojectversion1.view.ParameterClass.ParameterGenerator;

import java.sql.Time;

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
            try {
                Thread.sleep(10000);
            }
            catch (Exception e){}
            CELIFParameter celifParameter = (CELIFParameter) parameter.getParameter();
            int voltage_2 = celifParameter.getVoltage_2();
            int voltage_1 = celifParameter.getVoltage_1();
            parameter.setCELIF_parameter_voltage_1(voltage_2);
            parameter.setCELIF_parameter_voltage_2(voltage_1);
            deviceConnection.sendParameter(parameter);
            parameter.setCELIF_parameter_voltage_1(voltage_1);
            parameter.setCELIF_parameter_voltage_2(voltage_2);
        }
        else{
            presenter.sendMessage("parameter set fail");
        }

        if(serverConnection!=null && serverConnection.isConnected()){
            serverConnection.sendParameter(parameter);
        }
    }
}
