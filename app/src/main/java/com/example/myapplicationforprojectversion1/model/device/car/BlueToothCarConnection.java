package com.example.myapplicationforprojectversion1.model.device.car;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.util.Log;

import com.example.myapplicationforprojectversion1.view.Activities.CELIF.generator.ConnectionActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BlueToothCarConnection implements CarConnection{

    private static BlueToothCarConnection carConnection;
    public boolean isConnected = false;
    private boolean isFind = false;
    private BluetoothAdapter adapter;
    private String deviceInformation;
    private List<BluetoothDevice>devices = new ArrayList<>();
    private BluetoothLeScanner scanner;
    private BluetoothGatt gatt;
    private BluetoothGattService service = null;
    private BluetoothGattCharacteristic writeCharacteristic = null;
    private BluetoothGattCharacteristic readCharacteristic = null;
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                try{
                    gatt.discoverServices();  //跳转到发现蓝牙服务
                }catch(SecurityException e){}
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            try {
                formService();
                formCharacteristic();
                writeCharacteristic.setValue(BlueToothCarConnectionUtil.PASSWORD);
                gatt.writeCharacteristic(writeCharacteristic);
                isConnected = true;
            }
            catch (SecurityException e){}
            catch (Exception e){}

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }
    };
    private ScanCallback leCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            try{
                BluetoothDevice device = result.getDevice();
                String deviceName = device.getName();
                if (!devices.contains(device) && deviceName!=null) {  //判断是否已经添加
                    devices.add(device);
                    if(deviceName.equals(deviceInformation)){
                        scanner.stopScan(leCallback);
                        synchronized (BlueToothCarConnection.this){
                            isFind = true;
                            connectCar();
                        }
                    }
                }
            }
            catch(SecurityException e){}



        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };
    private ConnectionActivity activity; //connect界面，这个变量用于在connect界面显示连接失败





    public static BlueToothCarConnection formInstance(BluetoothAdapter adapter, String deviceInformation,ConnectionActivity activity){
        if(carConnection == null){
            synchronized (BlueToothCarConnection.class){
                if(carConnection==null){
                    carConnection = new BlueToothCarConnection(adapter,deviceInformation,activity);
                }
            }
        }
        return carConnection;
    }

    public static BlueToothCarConnection getInstance(){
        return carConnection;
    }


    //上位activity的服务接口，用于操控蓝牙引脚开关
    public void activate(int port, int mode){
        try{
            writeCharacteristic.setValue(BlueToothCarConnectionUtil.A2_COMMAND1);
            gatt.writeCharacteristic(writeCharacteristic);
        }
        catch (SecurityException e){}
        catch (Exception e){}

    }
















    //private function
    private BlueToothCarConnection(BluetoothAdapter adapter, String deviceInformation,ConnectionActivity activity){
        this.adapter = adapter;
        this.deviceInformation = deviceInformation;
        this.activity = activity;
        initBlueTooth();
    }

    private void initBlueTooth()
    {
        initAdapter();

        startScan();
        stopScan();
    }


    private void initAdapter()
    {
        if(!adapter.isEnabled()){
            try{
                adapter.enable();
            }
            catch (SecurityException e){}
        }
    }

    private void startScan()
    {
        scanner = adapter.getBluetoothLeScanner();
        try {
            scanner.startScan(leCallback);
        }
        catch (SecurityException e){
            Log.e("loge:  ",e.getLocalizedMessage());
        }
        catch (Exception e){}
    }

    private void stopScan()
    {
        new Thread(){
            @Override
            public void run(){
                try{
                    sleep(20000);
                    if(isFind == false){
                        scanner.stopScan(leCallback);
                        activity.myHandler.sendEmptyMessage(1);
                    }
                }
                catch (SecurityException e){ }
                catch (Exception e){ }
            }
        }.start();
    }

    private void connectCar()
    {
        for(int i=0;i<devices.size();i++){
            BluetoothDevice device = devices.get(i);
            String deviceName;
            try{
                deviceName = device.getName();
                if(deviceName!=null && deviceName.equals(deviceInformation)){//不判断null会抛出异常
                    gatt = device.connectGatt(activity,true,mGattCallback);
                }
            }
            catch (SecurityException e) {
            }

        }
    }

    private void formService(){
        List<BluetoothGattService> services;
        services = gatt.getServices();
        for(int i=0;i<services.size();i++){
            if(services.get(i).getUuid().equals(UUID.fromString(BlueToothCarConnectionUtil.SERVICE_UUID))){
                service = services.get(i);
                break;
            }
        }
    }
    private void formCharacteristic(){
        List<BluetoothGattCharacteristic> chara_list;
        chara_list = service.getCharacteristics();
        for(int i=0;i<chara_list.size();i++){
            if(chara_list.get(i).getUuid().equals(UUID.fromString(BlueToothCarConnectionUtil.WRITE_UUID))){
                writeCharacteristic = chara_list.get(i);
            }
            if(chara_list.get(i).getUuid().equals(UUID.fromString(BlueToothCarConnectionUtil.READ_UUID))){
                readCharacteristic = chara_list.get(i);
            }
        }
    }



}
