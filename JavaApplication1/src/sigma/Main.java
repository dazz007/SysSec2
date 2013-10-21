package sigma;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Pawel
 */
public class Main {
    public static Sigma generator;
    public static Client alice;
    public static Client bob;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.out.println("Tworzenie generatora g");
        generator = new Sigma();
        alice = new Client(generator.getKPGen());
        bob = new Client(generator.getKPGen());
        System.out.println("Prywatny klucz alicji: "+alice.getPrivateKey());
        System.out.println("Publiczny klucz alicji: "+alice.getPublicKey());
        System.out.println("Prywatny klucz boba: "+bob.getPrivateKey());
        System.out.println("Publiczny klucz boba: "+bob.getPublicKey());
        System.out.println("Tworzenie g^x mod p, dla obu partii");
        alice.createGpowXmodP(generator.returnG(), generator.returnP());
        bob.createGpowXmodP(generator.returnG(), generator.returnP());
        System.out.println("Wysyłanie g^x mod p od alicji do boba");
        bob.createSessionKey(alice.returnGpowXmodP());
        System.out.println("Klucz sesyjny boba: "+bob.getSessionKey());
        
        System.out.println("Bob generuje MAC");
        bob.generateMac(alice.returnGpowXmodP());
        
        System.out.print("Alice robi klucz sesyjny poprzez otrzymanie od boba g^y mod p");
        alice.createSessionKey(bob.returnGpowXmodP());
        System.out.println("Klucz sesyjny alice: "+alice.getSessionKey());
    }
    
}
