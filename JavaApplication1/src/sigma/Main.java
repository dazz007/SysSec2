package sigma;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

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
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException, IOException {
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
        System.out.println("Wysyłanie klucza eferycznego od alicji do boba");
        bob.createSessionKey(alice.returnGpowXmodP());
        System.out.println("Bob generuje MAC");
        bob.generateMac(alice.returnGpowXmodP(), "Jakiś tam message Boba");
        System.out.println("Bob podpisuje sobie klucze eferyczne");
        bob.signTheValues();
        
        System.out.println("Klucz sesyjny boba: "+bob.getSessionKey());
        System.out.println("Bob wysyla podpis, MACa i klucz eferyczny");
        System.out.println("Alice weryfikuje dane od Boba"); 
       
        if(alice.verify(bob.getPublicKey(), bob.data, bob.realSig, "Bob", bob.returnMAC(), bob.getMessage(), bob.getgPOWxMODp())){
            System.out.println("Wszystko się zgadza, teraz Alice wysyła dane do Boba");
            System.out.print("Alice robi klucz sesyjny poprzez otrzymanie od boba g^y mod p");
            alice.createSessionKey(bob.returnGpowXmodP());
            System.out.println("Klucz sesyjny alice: "+alice.getSessionKey());
            alice.generateMac(bob.returnGpowXmodP(), "Jakiś tam message Alice");
            alice.signTheValues();
        }else{
            System.out.println("Weryfikacja nie przebiegła prawidłowo");
        }
        
    }
    
}
