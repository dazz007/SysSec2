/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sigma;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
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
    private BigInteger x;
    private BigInteger g;
    private BigInteger p;
    private BigInteger sessionKey;
    private static SecretKey skeyForMAC;

    private BigInteger valueOtherParty;
    public Client(KeyPairGenerator keyGen){
        try {
            this.keyPair = keyGen.generateKeyPair();
            KeyFactory kfactory = KeyFactory.getInstance("DiffieHellman");
            DHPublicKeySpec kspec = (DHPublicKeySpec) kfactory.getKeySpec(this.keyPair.getPublic(),
                    DHPublicKeySpec.class);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public PrivateKey getPrivateKey(){
        return keyPair.getPrivate();
    }
    
    public PublicKey getPublicKey(){
        return keyPair.getPublic();
    }
    
    public void generateMac(BigInteger valueOtherParty) throws NoSuchAlgorithmException{
        
        if(skeyForMAC == null){
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacMD5");
             keyGen.init(new SecureRandom(valueOtherParty.multiply(x).toByteArray()));
             skeyForMAC = keyGen.generateKey();
        }
        
        try {
            this.mac = Mac.getInstance(skeyForMAC.getAlgorithm());
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Error during creating MAC");
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            mac.init(skeyForMAC);
        } catch (InvalidKeyException ex) {
            System.out.println("Error during initialization MAC");
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public byte[] returnMAC(){
        return mac.doFinal();
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
}
