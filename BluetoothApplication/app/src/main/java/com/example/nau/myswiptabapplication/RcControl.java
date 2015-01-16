package com.example.nau.myswiptabapplication;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by Nay Aung Kyaw on 7/25/2014.
 */
public class RcControl {
    static int rcValues[] = new int[]{1500, 1500, 1500, 1000, 1000, 1000, 1000, 1000};
    public static final int MSP_SET_RAW_RC = 200;
    public static final int MSP_RC = 105;


    public int[] getRcValues() {
        return rcValues;
    }

    public void setRcValues(int[] rcValues) {
        this.rcValues = rcValues;
    }

    public void setThrottleValue(int throttleValue)
    {
        this.rcValues[3] = throttleValue;
    }
    public void setPitchValue(int value)
    {
        this.rcValues[1] = value;
    }

    public void setRollValue(int value)
    {
        this.rcValues[0] = value;
    }
    public void setYawValue(int value)
    {
        this.rcValues[2] = value;
    }

public List<Byte> getMSPCode()
{
    return SendRequestMSP_SET_RAW_RC(rcValues);
}

    public List<Byte> getArmCode() {

        rcValues[0] = 1500;
        rcValues[1] = 1500;
        rcValues[2] = 1500;
        rcValues[3] = 1000;
        rcValues[4] = 1000;
        rcValues[5] = 1000;
        rcValues[6] = 1000;
        rcValues[7] = 2000;

        return SendRequestMSP_SET_RAW_RC(rcValues);
    }

    public List<Byte>  getDisarmCode() {
        for (int i = 0; i < 3; i++) rcValues[i] = 1500;
        for (int i = 3; i < 8; i++) rcValues[i] = 1000;
        return SendRequestMSP_SET_RAW_RC(rcValues);
    }



    public List<Byte> SendRequestMSP_SET_RAW_RC(int[] channels8) {
        ArrayList<Character> payload = new ArrayList<Character>();
        for (int i = 0; i < 8; i++) {
            payload.add((char) (channels8[i] & 0xFF));
            payload.add((char) ((channels8[i] >> 8) & 0xFF));
        }

        return requestMSP(MSP_SET_RAW_RC, payload.toArray(new Character[payload.size()]));
    }
    final String MSP_HEADER = "$M<";


    public List<Byte> requestMSP(int msp, Character[] payload) {

        if (msp < 0) {
            return null;
        }
        List<Byte> bf = new LinkedList<Byte>();
        for (byte c : MSP_HEADER.getBytes()) {
            bf.add(c);
        }

        byte checksum = 0;
        byte pl_size = (byte) ((payload != null ? (int) (payload.length) : 0) & 0xFF);
        bf.add(pl_size);
        checksum ^= (pl_size & 0xFF);

        bf.add((byte) (msp & 0xFF));
        checksum ^= (msp & 0xFF);

        if (payload != null) {
            for (char c : payload) {
                bf.add((byte) (c & 0xFF));
                checksum ^= (c & 0xFF);
            }
        }
        bf.add(checksum);
        return (bf);
    }

}
