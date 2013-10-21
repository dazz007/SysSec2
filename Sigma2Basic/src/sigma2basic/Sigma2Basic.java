/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sigma2basic;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.MacSpi;
import javax.crypto.SecretKey;
/**
 *
 * @author Pawel
 */
public class Sigma2Basic {
    private BigInteger g;
    private BigInteger x;
    private BigInteger y;
    private BigInteger p;
    private static BigInteger publicKeyA;
    private static BigInteger publicKeyB;
    private static Mac macA;
    private static Mac macB;
    private static KeyGenerator keyGenA;
    private static KeyGenerator keyGenB;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // Get q and p.
            int gV = 73;
            int pV = 47;
            int xV = 53;
            int yV = 81;
            BigInteger p = new BigInteger(Integer.toString(pV));
            BigInteger g = new BigInteger(Integer.toString(gV));
            BigInteger x = new BigInteger(Integer.toString(xV));
            BigInteger y = new BigInteger(Integer.toString(yV));
            
            int bitLength = 512; // 512 bits
            SecureRandom rnd = new SecureRandom();
            p = BigInteger.probablePrime(bitLength, rnd);
            g = BigInteger.probablePrime(bitLength, rnd);
            
            //Making public key for A.
            publicKeyA = g.modPow(x, p);
            
            //Making public key for B.
            publicKeyB = g.modPow(y, p);
            
            //Making Mac for A.
            keyGenA = KeyGenerator.getInstance("HmacMD5");
            keyGenA.init(new SecureRandom(publicKeyA.toByteArray()));
            SecretKey privateKeyA = keyGenA.generateKey();
            System.out.println("KeyGen: " + privateKeyA);
            macA = Mac.getInstance(privateKeyA.getAlgorithm());
            try {
                macA.init(privateKeyA);
            } catch (InvalidKeyException ex) {
                Logger.getLogger(Sigma2Basic.class.getName()).log(Level.SEVERE, null, ex);
            }
            macA.doFinal();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Sigma2Basic.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
