package com.example.myapplicationforprojectversion1.model.server;

import com.example.myapplicationforprojectversion1.model.model.ChartData;
import com.example.myapplicationforprojectversion1.view.ParameterClass.Parameter;

import java.io.BufferedWriter;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ServerConnection{
    private Socket socket;
    private PrintWriter out = null;
    private boolean isConnect = false;
    private final int port = 10023;

    static private ServerConnection service = null;



    static public ServerConnection formInstance(String ip)
    {
        if(service==null){
            synchronized (ServerConnection.class){
                if(service==null){
                    service = new ServerConnection(ip);
                }
            }
        }
        return service;
    }

    static public ServerConnection getInstance()
    {
        return service;
    }



    //构造对象，建立通讯
    public ServerConnection(String ip)
    {
        new Thread(){
            @Override
            public void run()
            {
                synchronized (this){
                    try {
                        socket = new Socket(ip,port);
                        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8")),true);
                        isConnect = true;
                    }
                    catch (Exception e){
                        isConnect = false;
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void sendParameter(Parameter parameter)
    {
        new Thread(){
            @Override
            public void run()
            {
                synchronized (out){
                    String output = parameter.formString();
                    out.println(output);
                }


            }
        }.start();
    }

    public void sendValues(List<ChartData> datas)
    {
        new Thread(){
            @Override
            public void run()
            {
                synchronized (out)
                {
                    String output = "";
                    for(int i = 0; i < datas.size(); i++)
                    {
                        output = output + "value" + " " + datas.get(i).getTime() + " " + datas.get(i).getData();
                        out.println(output);
                        output = "";
                    }
                }

            }
        }.start();
    }

    public void sendBeginMessage()
    {
        new Thread(){
            @Override
            public void run()
            {
                synchronized (out){
                    String output = "begin";
                    out.println(output);
                }

            }
        }.start();
    }

    public void sendStopMessage()
    {
        new Thread(){
            @Override
            public void run()
            {
                synchronized (out){
                    String output = "end";
                    out.println(output);
                }

            }
        }.start();
    }

    public void sendCloseMessage()
    {
        new Thread(){
            @Override
            public void run()
            {
                synchronized (out)
                {
                    String output = "close";
                    out.println(output);
                }

            }
        }.start();
    }

    public boolean isConnected()
    {
        if(socket != null){
            return this.socket.isConnected();
        }
        return false;
    }



}



