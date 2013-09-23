package com.eazy.communication;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-9-18
 * Time: 上午10:11
 * To change this template use File | Settings | File Templates.
 */
public class ReadSerial extends Thread{
    private SerialBuffer ComBuffer;
    private InputStream ComPort;
    /**
     *
     * Constructor
     *
     * @param SB The buffer to save the incoming messages.
     * @param Port The InputStream from the specific serial port.
     *
     */
    public ReadSerial(SerialBuffer SB, InputStream Port)
    {
        ComBuffer = SB;
        ComPort = Port;
    }
    public void run()
    {
        int c;
        try
        {
            while (true)
            {
                c = ComPort.read();
                ComBuffer.PutChar(c);

                System.out.println("receiv:"+ ComBuffer.GetMsg(10));
            }


        } catch (IOException e) {}
    }
}
