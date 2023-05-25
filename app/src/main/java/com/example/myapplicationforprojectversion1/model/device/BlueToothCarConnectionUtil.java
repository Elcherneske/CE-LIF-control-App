package com.example.myapplicationforprojectversion1.model.device;

public class BlueToothCarConnectionUtil {
    public final static String SERVICE_UUID = "0000ff26-0000-1000-8000-00805f9b34fb";
    public final static String WRITE_UUID = "0000ff68-0000-1000-8000-00805f9b34fb";
    public final static String READ_UUID = "0000ff69-0000-1000-8000-00805f9b34fb";
    public final static String PASSWORD = "AT+SEND151811@";

    public static byte[] A2_COMMAND1 = {(byte)0xFD,(byte)0x02,
            (byte)0x10,(byte)0x00,
            (byte)0x00,(byte)0x00,
            (byte)0x00,(byte)0x00,
            (byte)0xDF};

}
