/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sigmaclient;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pawel
 */
public class ProcessClass {
    private BigInteger p;
    private BigInteger g;
    private BigInteger x;
    private KeyPair keyPair;
    private BigInteger GpowXotherSide;
    private BigInteger GpowX;
    
    public void generatePandG(){
        int gV = 73;
        int pV = 47;
        this.p = new BigInteger(Integer.toString(pV));
        this.g = new BigInteger(Integer.toString(gV));
        
        int bitLength = 512; // 512 bits
        SecureRandom rnd = new SecureRandom();
        this.p = BigInteger.probablePrime(bitLength, rnd);
        this.g = BigInteger.probablePrime(bitLength, rnd);
    }
    
    public BigInteger getG(){
        return this.g;
    }
    
    public BigInteger getP(){
        return this.p;
    }
    
    public void setG(BigInteger g){
        this.g = g;
    }
    
    public void setP(BigInteger p){
        this.p = p;
    }
    
    public void setPandG(BigInteger g, BigInteger p){
        this.g = g;
        this.p = p;
    }
    
    public void computeGpowX(){
        int bitLength = 512; // 512 bits
        SecureRandom rnd = new SecureRandom();
        this.x = BigInteger.probablePrime(bitLength, rnd);
        GpowX = g.modPow(this.x, p);
    }
    
    public void setGpowXotherSide(BigInteger GpowXotherSide) {
        this.GpowXotherSide = GpowXotherSide;
    }
    
    public void setGpowX(BigInteger GpowX) {
        this.GpowX = GpowX;
    }
    

    public BigInteger getGpowXotherSide() {
        return GpowXotherSide;
    }

    public BigInteger getGpowX() {
        return GpowX;
    }
    
    public void generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException{
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        keyGen.initialize(1024,random);
        keyPair = keyGen.generateKeyPair();
    }
    
    public PublicKey getPublicKey(){
        return keyPair.getPublic();
    }
    
    private PrivateKey getPrivateKey(){
        return keyPair.getPrivate();
    }
}
