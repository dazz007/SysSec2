package sigma;

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
    public static void main(String[] args) {
        System.out.println("Tworzenie generatora g");
        generator = new Sigma();
        alice = new Client(generator.getKPGen());
        bob = new Client(generator.getKPGen());
        System.out.println("Prywatny klucz alicji: "+alice.getPrivateKey());
        System.out.println("Publiczny klucz alicji: "+alice.getPublicKey());
        System.out.println("Prywatny klucz boba: "+bob.getPrivateKey());
        System.out.println("Publiczny klucz boba: "+bob.getPublicKey());
    }
    
}
