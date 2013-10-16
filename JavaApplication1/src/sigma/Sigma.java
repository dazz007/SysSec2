/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sigma;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import javax.crypto.spec.DHParameterSpec;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.security.SecureRandom;
/**
 *
 * @author Pawel
 */
public class Sigma {
    private DHParameterSpec dhParams;
    private KeyPairGenerator keyGen;
    
    public Sigma(){
        int gV = 73;
        int pV = 47;
        BigInteger p = new BigInteger(Integer.toString(pV));
        BigInteger g = new BigInteger(Integer.toString(gV));
        
        int bitLength = 512; // 512 bits
        SecureRandom rnd = new SecureRandom();
        p = BigInteger.probablePrime(bitLength, rnd);
        g = BigInteger.probablePrime(bitLength, rnd);
        
        this.dhParams = new DHParameterSpec(g, p);
        try {
            this.keyGen = KeyPairGenerator.getInstance("DiffieHellman");
            this.keyGen.initialize(dhParams);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Sigma.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidAlgorithmParameterException ex) {
            Logger.getLogger(Sigma.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public KeyPairGenerator getKPGen(){
        return this.keyGen;
    }
}
