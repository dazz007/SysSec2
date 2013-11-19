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
    
    public void schnorrSignature(){
        int zzLength = 512/8; 
        byte[] zz = random.fetch(null, zzLength); 
        BigInteger r = new BigInteger(zz).mod(q);
        byte[] u = g.modPow(r, p).toByteArray();
        
        byte[] hu = ByteUtil.append(M, u, M.length, u.length);
        hash.init(PRIVATE_BITS_KEY_SIZE); 
        hash.update(hu, hu.length);
        byte[] zero = new byte[1];
        byte[] resume = hash.getHash(null);
        BigInteger e = new BigInteger(ByteUtil.append(zero, resume, 1, resume.length)).mod(q); // mod q é o segredo!
        BigInteger s = r.subtract(x.multiply(e)).mod(q);
        
        return new BigInteger[]{e, s};
    }
}
