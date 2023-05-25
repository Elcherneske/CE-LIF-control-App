package com.example.myapplicationforprojectversion1.view.ParameterClass;

import com.example.myapplicationforprojectversion1.model.Modbus.ModbusRtuMaster;

import java.util.List;

public interface Parameter {
    String formString();
    String getFileName();
    String getInformation();//写入cvs格式中使用的
    List<byte[]> formByteParameter(ModbusRtuMaster modbusRtuMaster);

}
