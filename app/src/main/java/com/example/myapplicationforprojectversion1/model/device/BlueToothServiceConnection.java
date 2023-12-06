package com.example.myapplicationforprojectversion1.model.device;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.widget.Toast;

import com.example.myapplicationforprojectversion1.model.Modbus.ModbusRtuMaster;
import com.example.myapplicationforprojectversion1.model.model.ByteCircleBuffer;
import com.example.myapplicationforprojectversion1.model.model.ChartData;
import com.example.myapplicationforprojectversion1.view.Activities.CELIF.Views;
import com.example.myapplicationforprojectversion1.view.Activities.Interface.MessageShower;
import com.example.myapplicationforprojectversion1.view.ParameterClass.Parameter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BlueToothServiceConnection implements DeviceConnection{

    private static BlueToothServiceConnection connection = null;
    private BluetoothAdapter adapter;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream out;
    private BufferedInputStream in_stream;
    private ModbusRtuMaster modbusRtuMaster;
    private int time = 0;
    private int time_step = 10;
    private Thread listen_thread;
    private Thread process_thread;
    private Thread connect_thread;
    private Thread reconnect_thread;
    private boolean thread_flag = false;
    private boolean connect_flag = false;
    private MessageShower activity;
    private List<ChartData> data = new ArrayList<ChartData>();//data的buffer，用于存储从底层程序传来的数据，等待上层程序fetch
    private ByteCircleBuffer circleBuffer = new ByteCircleBuffer(2048);//自己写的buffer类，用于快速处理数据


    //formInstance， 需要在activity中先调用form然后再使用
    public static BlueToothServiceConnection formInstance(BluetoothAdapter adapter,String deviceInformation){
        if(connection==null){
            synchronized (BlueToothServiceConnection.class){
                if(connection==null){
                    connection = new BlueToothServiceConnection(adapter,deviceInformation);
                }
            }
        }
        return connection;
    }

    //DEBUG版的form
    public static BlueToothServiceConnection formInstance(BluetoothAdapter adapter, String deviceInformation, MessageShower activity){
        if(connection==null){
            synchronized (BlueToothServiceConnection.class){
                if(connection==null){
                    connection = new BlueToothServiceConnection(adapter,deviceInformation);
                }
            }
        }
        connection.activity = activity;
        return connection;

    }
    public static BlueToothServiceConnection getInstance(){
        return connection;
    }


    public boolean isConnect()
    {
//        return this.socket.isConnected();
        try {
            byte[] test = {};
            synchronized (this){
                if(out == null){
                    return false;
                }
                out.write(test);
                out.flush();
                connect_flag = true;
            }
            return true;
        }
        catch (IOException e) {
            try {
                synchronized (this){
                    socket.close();
                    connect_flag = false;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    public void setActivity(MessageShower activity){
        this.activity = activity;
    }


    //向底层驱动程序传输参数，用modbus方式编码
    public void sendParameter(Parameter parameter){
        List<byte[]> byteParameter = parameter.formByteParameter(modbusRtuMaster);
        for(int i=0;i<byteParameter.size();i++){
            try {
                synchronized (out){
                    out.write(byteParameter.get(i));
                    out.flush();
                }
                //modbus需要回应，所以这里应该要sleep，但是设计硬件的时候切掉了回应，因此不需要接收
            }
            catch (Exception e) {
                //this.activity.showMessage(e.getMessage());
            }
        }

    }


    public List<ChartData> fetchAllData()
    {
        List<ChartData> result = new ArrayList<>(data);
        synchronized (data){
            data.clear();
        }
        return result;
    }
    public List<ChartData> fetchData(int size){
        List<ChartData> result = new ArrayList<>();
        synchronized (data){
            for(int i=0;i<size;i++){
                if(this.data.isEmpty()){
                    break;
                }
                result.add(this.data.get(0));
                this.data.remove(0);
            }
        }
        return result;
    }


    public void sendBeginMessage()
    {
        //重新初始化buffer和响应的time标识
        data = new ArrayList<ChartData>();
        circleBuffer = new ByteCircleBuffer(2048);
        this.time = 0;
        //重新初始化thread
        this.thread_flag = true;
        initThread(); //这里需要重新刷新thread，否则第二次连接无法重新开启上一次的thread
        this.listen_thread.start();
        this.process_thread.start();
        this.reconnect_thread.start();

        //send begin message to device
        try{
            byte[] item = modbusRtuMaster.writeSingleRegister(1, 9, 1);
            //TODO：这里还需要开启电源
            synchronized (out){
                out.write(item);
                out.flush();
            }
        }
        catch (Exception e){
            Toast.makeText(Views.getInstance().getShowActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public void sendStopMessage() {
        //send end message to device
        try{
            byte[] item = modbusRtuMaster.writeSingleRegister(1, 9, 2);
            //TODO：这里还得关闭电源
            synchronized (out){
                out.write(item);
                out.flush();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        this.thread_flag = false;//结束thread
        try {
            this.listen_thread.join();
            this.process_thread.join();
            this.reconnect_thread.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }














    //private function

    //ctor
    private BlueToothServiceConnection(BluetoothAdapter adapter,String deviceInformation) {
        this.adapter = adapter;
        modbusRtuMaster = new ModbusRtuMaster();
        initBlueTooth(deviceInformation);
    }
    private void initBlueTooth(String deviceInformation) {
        initBlueToothAdapter();
        getDevice(deviceInformation);
        initSocket();
        initThread();
    }
    private void initBlueToothAdapter() {
        //获取蓝牙适配器
        try{
            if (this.adapter != null){
                // 蓝牙已打开
                if (this.adapter.isEnabled()){}
                else{
                    this.adapter.enable();
                }
            }
        }
        catch (SecurityException e){
            e.printStackTrace();
        }
    }
    private void getDevice(String deviceInformation) {
        try{
            Set<BluetoothDevice> deviceSet = this.adapter.getBondedDevices();
            List<BluetoothDevice> deviceList = new ArrayList<>();
            if (deviceSet.size() > 0) {
                for (BluetoothDevice device : deviceSet) {
                    deviceList.add(device);
                }
            }

            for(int i = 0 ; i < deviceList.size() ; i++){
                if(deviceList.get(i).getName().equals(deviceInformation)){
                    this.device = deviceList.get(i);
                    break;
                }
            }
        }
        catch (SecurityException e){
            e.printStackTrace();
        }
    }

    private void initSocket() {
        this.connect_thread = new Thread(){
            @Override
            public void run(){
                try {
                    synchronized (BlueToothServiceConnection.class){
                        socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                        socket.connect();
                        out = socket.getOutputStream();
                        in_stream = new BufferedInputStream(socket.getInputStream());
                        connect_flag = true;
                    }
                }
                catch (SecurityException | IOException e){
                    e.printStackTrace();
                }
            }
        };
        this.connect_thread.start();
    }

    private void initThread() {
        //接收数据的线程，接受到底层驱动程序发来的数据之后，装入this.circlebuffer中
        this.listen_thread = new Thread(){
            @Override
            public void run(){
                byte[] listen_buff = new byte[2048];
                int listen_len;
                try{
                    while(thread_flag && connect_flag){
                        sleep(10);
                        if((listen_len = in_stream.read(listen_buff,0,2048))!=1){
                            synchronized (circleBuffer){
                                circleBuffer.push(listen_buff,listen_len);
                            }
                        }
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        };

        //处理线程，用于处理circle buffer中的数据，将其中有用的value提取出来
        this.process_thread = new Thread(){
            @Override
            public void run()
            {
                byte[] process_buff;
                int value;
                int read_size;
                try{
                    while(thread_flag && connect_flag){
                        sleep(10);
                        boolean check = false;
                        synchronized (circleBuffer){
                            if(circleBuffer.get_size()<16){ //8个byte为一组数据，至少需要两组数据来实现数据头部对齐
                                continue;
                            }

                            //对齐数据块头 并读取第一个数据
                            process_buff = new byte[8];
                            byte[] temp = circleBuffer.pop(8);
                            int index = 0;
                            while(((int)(temp[index++]) & 0xff) != 0xA0);
                            index--;
                            int remain_size = index;
                            for(;index<8;index++){
                                process_buff[index - remain_size] = temp[index];
                            }
                            temp = circleBuffer.pop(remain_size);
                            for(int i=0;i<remain_size;i++){
                                process_buff[8-index+i] = temp[i];
                            }

                            if(check_frame(process_buff[0],process_buff[1])==true){
                                value = ((int)process_buff[5]&0x00ff);
                                value = (value<<8)+((int)process_buff[4]&0x00ff);
                                value = value* 4000/(int)0xffff;
                                ChartData singleData = new ChartData(value,time);
                                time += time_step;
                                synchronized (data){
                                    data.add(singleData);
                                }
                            }
                            else{
                                time += time_step;
                            }


                            //读取后续数据
                            read_size = (circleBuffer.get_size()/8)*8;
                            process_buff = circleBuffer.pop(read_size);
                            check = true;
                        }
                        if (check == true){
                            //将从circle buffer中获取的原始数据处理为我们需要的数据
                            for(int i=0;i<read_size/8;i++){
                                if(check_frame(process_buff[i*8 + 0],process_buff[i*8 + 1])==true){
                                    value = ((int)process_buff[i*8 + 5]&0x00ff);
                                    value = (value<<8)+((int)process_buff[i*8 + 4]&0x00ff);
                                    value = value* 4000/(int)0xffff;    //最大值设为4k
                                    ChartData singleData = new ChartData(value,time);
                                    time += time_step;
                                    synchronized (data){
                                        data.add(singleData);
                                    }
                                }
                                else{
                                    time += time_step;
                                }
                            }
                        }
                    }
                }
                catch (Exception e){
                    //activity.showMessage(e.getMessage());
                }
            }
        };


        this.reconnect_thread = new Thread(){
            @Override
            public void run(){
                while(thread_flag){
                    try{
                        sleep(10000);
                    } catch (InterruptedException e) {
                        //activity.showMessage(e.getMessage());
                    }
                    if(!isConnect()){
                        //activity.showMessage("start reconnect");
                        //先清理一下目前的thread
                        try {
                            listen_thread.join();
                            process_thread.join();
                        } catch (Exception e) {
                            //activity.showMessage(e.getMessage());
                        }
                        initSocket();
                        try {
                            connect_thread.join();
                            if(!isConnect()) continue;
                            initThread();
                            listen_thread.start();
                            process_thread.start();
                            reconnect_thread.start();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                    else{
                        //activity.showMessage("connective");
                    }
                }
                //activity.showMessage("reconnected");

            }

        };
    }

    private boolean check_frame(byte frame1, byte frame2){
        boolean result = true;
        if(((int)(frame1) & 0xff) != 0xA0) result = false;
        if(((int)(frame2) & 0xff) != 0xA1) result = false;
        return result;
    }


}




















