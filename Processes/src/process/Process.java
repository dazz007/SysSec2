/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package process;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;

/**
 *
 * @author Pawel
 */
public class Process {
   private BigInteger p;
   private BigInteger p_schnr;
   private BigInteger q_schnr;
   private BigInteger a_schnr;
   private BigInteger secret_key_schnr;
   private BigInteger public_key_schnr;
   
   private BigInteger p_schnr_other_side;
   private BigInteger q_schnr_other_side;
   private BigInteger a_schnr_other_side;
   private BigInteger public_key_schnr_other_side;
   private BigInteger e_other_side;
   private BigInteger s_other_side;
   private BigInteger message_other_side;
    private BigInteger g;
    private BigInteger x;
    private KeyPair keyPair;
    private BigInteger GpowXotherSide;
    private BigInteger GpowX;
    private String msg;
    private Mac mac;
    private SecretKey skeyForMAC;
    public byte[] data;
    public byte[] realSig;
    private Data2Send d2s;
    private Data2Send d2sotherside;
    
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
    
    public void generateMac(String msg) throws NoSuchAlgorithmException, InvalidKeyException{
        this.msg = msg;
        if(skeyForMAC == null){
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacMD5");
            keyGen.init(new SecureRandom(GpowXotherSide.modPow(this.x, p).toByteArray()));
            skeyForMAC = keyGen.generateKey();
        }
        this.mac = Mac.getInstance(skeyForMAC.getAlgorithm());

        mac.init(skeyForMAC);

        byte [] bytes = this.msg.getBytes();
        mac.update(bytes);
    }
    
    public byte[] getMAC(){
        return mac.doFinal();
    }
    
    public void signTheValues() throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, NoSuchProviderException, IOException{
        Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
        dsa.initSign(this.getPrivateKey());
        data = GpowX.add(GpowXotherSide).toByteArray();
        dsa.update(data);
        this.realSig = dsa.sign();

    }
    
    public Data2Send getData2Send(){
        d2s = new Data2Send(data, realSig, msg, getMAC(),GpowX,keyPair.getPublic(), "Klient");
        return d2s;
    }
    
    public Data2Send badVerification(){
        d2s = new Data2Send(false);
        return d2s;
    }
    
    public boolean verifyMAC(byte [] MAC, String message) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException{
        if(skeyForMAC == null){
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacMD5");
            keyGen.init(new SecureRandom(GpowXotherSide.modPow(this.x, p).toByteArray()));
            skeyForMAC = keyGen.generateKey();
        }
        
        Mac local_mac = Mac.getInstance(skeyForMAC.getAlgorithm());

        local_mac.init(skeyForMAC);

        byte [] bytes = message.getBytes();
        local_mac.update(bytes);
        byte[] mac_final = local_mac.doFinal();
        if(Arrays.equals(mac_final, MAC)){
            System.out.println("Weryfikacja MACa przebigła pomyślnie");
            System.out.println("Wiadomość brzmi "+message);
            return true;
        }else{
            System.out.println("MAC jest nie prawidłowy");
            return false;
        }
    }

    public Data2Send getD2sotherside() {
        return d2sotherside;
    }

    public void setD2sotherside(Data2Send d2sotherside) {
        this.d2sotherside = d2sotherside;
        this.GpowXotherSide = d2sotherside.getGpowX();
    }
    
    public boolean verify() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException, UnsupportedEncodingException{
         if(verifySignature(d2sotherside.getPk(),d2sotherside.getData(),d2sotherside.getRealSig(),d2sotherside.getId())){
            if(verifyMAC(d2sotherside.getMac(),d2sotherside.getMsg())){
                System.out.println("Wszystko gra!");
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
    
    public boolean verifySignature(PublicKey pk, 
                                   byte [] data, 
                                   byte [] signedData, 
                                   String id) 
                                            throws NoSuchAlgorithmException, 
                                            NoSuchProviderException, 
                                            InvalidKeyException, 
                                            SignatureException{
        Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
        dsa.initVerify(pk);
        dsa.update(data);
         if(dsa.verify(signedData) == true){
            System.out.println("Podpis od "+id+" się zgadza");
            
            return true;
        }else{
            System.out.println("Podpis od "+id+" się nie zgadza");
            return false;
        }
    }
    
    
    public void setSchnorrDataFromOtherSide(BigInteger[] param){
        public_key_schnr_other_side = param[0];
        q_schnr_other_side = param[1];
        p_schnr_other_side = param[2];
        a_schnr_other_side = param[3];
        e_other_side = param[4];
        s_other_side = param[5];
        message_other_side = param[6];
    }
    
    public BigInteger[] initialiseSchnorrKeys(boolean stat) throws NoSuchAlgorithmException{
        
        if(!stat){
            return new BigInteger[]{new BigInteger("-1"),
                new BigInteger("-1"),new BigInteger("-1"),new BigInteger("-1"),new BigInteger("-1"),new BigInteger("-1"),new BigInteger("-1")};
        }
        final int pV = 73;
        SecureRandom rnd = new SecureRandom();
        final int q_ditis = 512;
        final int p_min_digit = 511;
        final int dig_extra = 64;
        this.q_schnr = BigInteger.probablePrime(q_ditis, rnd);
        
        boolean foundPrime = false;
        //calculate p_schnr such that p_schnr-1|q_schnr
        while(!foundPrime){
            BigInteger temp = new BigInteger(dig_extra, rnd);
            this.p_schnr = temp.multiply(q_schnr).add(BigInteger.ONE);
            if(p_schnr.bitLength() > p_min_digit && p_schnr.isProbablePrime(1)){
                foundPrime = true;
            }
        }
        //calculate a_schnr such that a_schnr^q_schnr mod p_schnr = 1
        a_schnr = new BigInteger("1");
        BigInteger exp = p_schnr.subtract(BigInteger.ONE ).divide(q_schnr);
        while(a_schnr.equals(BigInteger.ONE)){
            BigInteger t = new BigInteger(p_schnr.bitLength()+1, rnd).add(BigInteger.ONE);
            if(t.compareTo(p_schnr)!= - 1)
                continue;
            a_schnr = t.modPow(exp, p_schnr);
        }
        
        System.out.println("A to: "+a_schnr);

        do{
            secret_key_schnr = new BigInteger(q_ditis,rnd);
        }while(secret_key_schnr.compareTo(q_schnr.subtract(BigInteger.ONE)) == 1);
        System.out.println("Q: "+q_schnr);
        System.out.println("SK: "+secret_key_schnr);
        public_key_schnr = a_schnr.modPow(secret_key_schnr, p_schnr);
        
        
        final int bits = dig_extra/8;
        BigInteger r = new BigInteger(bits, rnd).mod(q_schnr);
        BigInteger k = a_schnr.modPow(r, p_schnr);
        BigInteger message = GpowX.add(GpowXotherSide).add(k);
        
        MessageDigest digester = MessageDigest.getInstance("MD5");
        byte[] byte_msg = message.toByteArray();
        digester.update(byte_msg);
        byte[] digest = digester.digest();
        BigInteger e = new BigInteger(digest).mod(q_schnr);
        BigInteger s = r.subtract(secret_key_schnr.multiply(e)).mod(q_schnr);
        return new BigInteger[]{public_key_schnr,q_schnr,p_schnr,a_schnr,e,s,GpowX.add(GpowXotherSide)};
    }
    
    public BigInteger[] signSchnorr(String msg) throws NoSuchAlgorithmException{
        this.msg = msg;
        int dig_extra = 64/8;
        SecureRandom rnd = new SecureRandom();
        BigInteger r = new BigInteger(dig_extra, rnd).mod(q_schnr);
        BigInteger k = a_schnr.modPow(r, p_schnr);
        String message = this.msg + k.toString();
        MessageDigest digester = MessageDigest.getInstance("MD5");
        byte[] byte_msg = message.getBytes();
        digester.update(byte_msg);
        byte[] digest = digester.digest();
        BigInteger e = new BigInteger(digest).mod(q_schnr);
        BigInteger s = r.subtract(secret_key_schnr.multiply(e)).mod(q_schnr);
        return new BigInteger[]{e,s};
    }
    
    public boolean verifySchnorr() throws NoSuchAlgorithmException{
        if(a_schnr_other_side.compareTo(new BigInteger("-1")) == 0
                && s_other_side.compareTo(new BigInteger("-1")) == 0
                && p_schnr_other_side.compareTo(new BigInteger("-1")) == 0
                && e_other_side.compareTo(new BigInteger("-1")) == 0
                && message_other_side.compareTo(new BigInteger("-1")) == 0
                && q_schnr_other_side.compareTo(new BigInteger("-1")) == 0){
            System.out.println("Weryfikacja po drugiej stronie nie przebiegła pomyślnie");
            return false;
        }
        BigInteger u = a_schnr_other_side.modPow(s_other_side, p_schnr_other_side);
        BigInteger factor = public_key_schnr_other_side.modPow(e_other_side, p_schnr_other_side);
        u = u.multiply(factor).mod(p_schnr_other_side);
        BigInteger message = message_other_side.add(u);
//        String message = this.msg + u.toString();
        MessageDigest digester = MessageDigest.getInstance("MD5");
        byte[] byte_msg = message.toByteArray();
        digester.update(byte_msg);
        byte[] digest = digester.digest();
        BigInteger ev = new BigInteger(digest).mod(q_schnr_other_side);
        
        if (ev.equals(e_other_side)){
            System.out.println("Podpis Schnorra zweryfikowany");
            return true;
        }else{
           System.out.println("NIE DZIAŁA SCHNORR");
           return false;
        }
    } 
}
