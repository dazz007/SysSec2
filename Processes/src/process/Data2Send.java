/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package process;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.PublicKey;

/**
 *
 * @author Pawel
 */
public class Data2Send implements Serializable {
    public byte[] data;
    public BigInteger GpowX;
    public PublicKey pk;
    public String id;
    public boolean status;
    public byte[] realSig;
    private String msg;
    private byte[] mac;
    
    public Data2Send(byte[] data, byte[] realSig, String msg, byte[] mac, BigInteger gpowx, PublicKey pk, String id){
        this.data = data;
        this.realSig = realSig;
        this.msg = msg;
        this.mac = mac;
        this.GpowX = gpowx;
        this.pk = pk;
        this.id = id;
        this.status = true;
    }
    
    public Data2Send(boolean status){
        this.status = false;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getRealSig() {
        return realSig;
    }

    public void setRealSig(byte[] realSig) {
        this.realSig = realSig;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public byte[] getMac() {
        return mac;
    }

    public void setMac(byte[] mac) {
        this.mac = mac;
    }
    
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
    
    public BigInteger getGpowX() {
        return GpowX;
    }

    public void setGpowX(BigInteger GpowX) {
        this.GpowX = GpowX;
    }

    public PublicKey getPk() {
        return pk;
    }

    public void setPk(PublicKey pk) {
        this.pk = pk;
    }
}
