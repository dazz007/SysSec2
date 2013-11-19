/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sigma;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.DHPublicKeySpec;
import org.bouncycastle.crypto.macs.CFBBlockCipherMac;

/**
 *
 * @author Pawel
 */
public class Client {
    private KeyPair keyPair;
    private Mac mac;
    private BigInteger gPOWxMODp;

    private String message;


    private BigInteger x;
    private BigInteger g;
    private BigInteger p;
    private BigInteger sessionKey;
    private static SecretKey skeyForMAC;
    public byte[] realSig;
    public byte[] data;

    private BigInteger valueOtherParty;
    public Client(KeyPairGenerator keyGen) throws NoSuchAlgorithmException, NoSuchProviderException{
            this.keyPair = keyGen.generateKeyPair();
    }
    
    public PrivateKey getPrivateKey(){
        return keyPair.getPrivate();
    }
    
    public PublicKey getPublicKey(){
        return keyPair.getPublic();
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public void generateMac(BigInteger valueOtherParty, String msg) throws NoSuchAlgorithmException, UnsupportedEncodingException{
        this.valueOtherParty = valueOtherParty;
        this.message = msg;
        if(skeyForMAC == null){
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacMD5");
             keyGen.init(new SecureRandom(valueOtherParty.multiply(x).toByteArray()));
             skeyForMAC = keyGen.generateKey();
        }
        
            this.mac = Mac.getInstance(skeyForMAC.getAlgorithm());

        try {
            mac.init(skeyForMAC);
        } catch (InvalidKeyException ex) {
            System.out.println("Error during initialization MAC");
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        byte [] bytes = this.message.getBytes();
        mac.update(bytes);
    }
    
    public byte[] returnMAC(){
        return mac.doFinal();
    }
    
    
    public BigInteger getgPOWxMODp() {
        return gPOWxMODp;
    }

    public void setgPOWxMODp(BigInteger gPOWxMODp) {
        this.gPOWxMODp = gPOWxMODp;
    }
    
    public void createGpowXmodP(BigInteger g, BigInteger p){
        int bitLength = 512; // 512 bits
        SecureRandom rnd = new SecureRandom();
        this.g = g;
        this.p = p;
        this.x = BigInteger.probablePrime(bitLength, rnd);
        gPOWxMODp = g.modPow(this.x, p);
    }
    
    public BigInteger returnGpowXmodP(){
        
        return this.gPOWxMODp;
    }
    
    public void createSessionKey(BigInteger valueOtherParty){
        this.sessionKey = valueOtherParty.modPow(this.x, this.p);
    }
    
    public BigInteger getSessionKey() {
        return sessionKey;
    }
    
    public void signTheValues() throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, NoSuchProviderException, IOException{
        Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
        dsa.initSign(this.getPrivateKey());
        data = gPOWxMODp.add(valueOtherParty).toByteArray();
        dsa.update(data);
        this.realSig = dsa.sign();
    }
    
    public boolean verifyMAC(byte [] MAC, String message) throws NoSuchAlgorithmException, UnsupportedEncodingException{
        Mac local_mac = Mac.getInstance(skeyForMAC.getAlgorithm());
        try {
            local_mac.init(skeyForMAC);
        } catch (InvalidKeyException ex) {
            System.out.println("Error during initialization MAC");
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        byte [] bytes = message.getBytes();
        local_mac.update(bytes);
        byte[] mac_final = local_mac.doFinal();
        if(Arrays.equals(mac_final, MAC)){
            System.out.println("Weryfikacja MACa przebigła pomyślnie");
            return true;
        }else{
            System.out.println("MAC jest nie prawidłowy");
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
    
    public boolean verify(PublicKey pk, 
                          byte [] data, 
                          byte [] signedData, 
                          String id, 
                          byte[] MAC, 
                          String message, 
                          BigInteger gPOWxModp )
                                throws  NoSuchAlgorithmException, 
                                        NoSuchProviderException, 
                                        InvalidKeyException, 
                                        SignatureException,
                                        UnsupportedEncodingException{
        if(verifySignature(pk,data,signedData,id)){
            if(verifyMAC(MAC,message)){
                System.out.println("Wszystko gra!");
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

//    void generateMac(BigInteger returnGpowXmodP) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
}
