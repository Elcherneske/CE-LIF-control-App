package com.example.myapplicationforprojectversion1.model.model;

import android.widget.Toast;

import com.example.myapplicationforprojectversion1.model.device.BlueToothServiceConnection;
import com.example.myapplicationforprojectversion1.model.server.ServerConnection;
import com.example.myapplicationforprojectversion1.view.Activities.Interface.MessageShower;
import com.example.myapplicationforprojectversion1.view.Activities.Interface.ParameterProvider;
import com.example.myapplicationforprojectversion1.view.ParameterClass.CELIFParameter;
import com.example.myapplicationforprojectversion1.view.ParameterClass.Parameter;

public class ParameterContainer implements ParameterHolder {
    private BlueToothServiceConnection deviceConnection;
    private ServerConnection serverConnection;
    private ParameterProvider activity;
    private CSVFileUtil fileUtil;

    public ParameterContainer(ParameterProvider activity){
        deviceConnection = BlueToothServiceConnection.getInstance();
        serverConnection = ServerConnection.getInstance();
        this.activity = activity;
        this.fileUtil = CSVFileUtil.getInstance();
    }


    @Override
    public void sendParameter(Parameter parameter) {
        if(deviceConnection!=null){

            deviceConnection.sendParameter(parameter);

            this.fileUtil.write(parameter.getInformation());

            new Thread(){
                @Override
                public void run(){
                    try {
                        sleep(20000);
                    }
                    catch (Exception e){}
                    CELIFParameter celifParameter = new CELIFParameter((CELIFParameter) parameter) ;
                    int voltage_2 = celifParameter.getVoltage_2();
                    int voltage_1 = celifParameter.getVoltage_1();
                    celifParameter.setVoltage_1(voltage_2);
                    celifParameter.setVoltage_2(0);
                    deviceConnection.sendParameter(celifParameter);
                }
            }.start();
        }
        else{
            activity.showMessage("send parameter fail\n");
        }

        if(serverConnection!=null && serverConnection.isConnected()){
            serverConnection.sendParameter(parameter);
        }
    }
}



