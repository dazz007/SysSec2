/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sigmaclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import process.Data2Send;
import process.Process;
enum e_state{ SENDG, SENDP, SENDGPOX, GETSCHNORR, GETALL, SENDALL, END};
/**
 *
 * @author Pawel
 */
public class SigmaClient {

    public Socket clientSocket;
    public ObjectOutputStream oos;
    public ObjectInputStream ois;
    private Process processClass;
    /**
     * @param host
     * @param args the command line arguments
     */
    public SigmaClient(String host, int portNumber) throws IOException{
        this.processClass = new Process();
        this.processClass.generatePandG();
        this.clientSocket = new Socket(host, portNumber);
        this.oos = new ObjectOutputStream(this.clientSocket.getOutputStream());
        this.ois = new ObjectInputStream(this.clientSocket.getInputStream());
    }
    
    public void sendG() throws IOException, NoSuchAlgorithmException{
        this.oos.writeObject(this.processClass.getG());
        this.oos.flush();
        System.out.println("Wysłano G od klienta do serwera");
    }
    
    public void sendP() throws IOException{
        this.oos.writeObject(this.processClass.getP());
        this.oos.flush();
        System.out.println("Wysłano P od klienta do serwera");
    }
    
    public void sendGpowX() throws IOException{
        this.processClass.computeGpowX();
        this.oos.writeObject(this.processClass.getGpowX());
        this.oos.flush();
        System.out.println("Wysłano G pow X od klienta do serwera");
    }
    
    public void getSchnorrData() throws IOException, ClassNotFoundException, NoSuchAlgorithmException{
        this.processClass.setSchnorrDataFromOtherSide((BigInteger[]) ois.readObject());
        if(this.processClass.verifySchnorr()){
            System.out.println("SCHNORR PO STRONIE KLIENTA ZOSTAŁ ZWERYFIKOWANY!");
        }
    }
    
    public void getAllData() throws IOException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException{
        this.processClass.setD2sotherside((Data2Send) ois.readObject());
        if(this.processClass.verify()){
            processClass.generateKeyPair();
            processClass.generateMac("Wiadomosc od strony klienta blallaldsksladj!");
            processClass.signTheValues();
            oos.writeObject(this.processClass.getData2Send());
            oos.flush();
        }else{
            
            oos.writeObject(this.processClass.badVerification());
            oos.flush();
        }
    }
    
    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        e_state state = e_state.SENDG;
        String hostName = "localhost";
        int portNumber = 4444;
        SigmaClient sc = new SigmaClient(hostName, portNumber);
        
        while(!state.equals(e_state.END)){
            switch(state){
                case SENDG:
                    sc.sendG();
                    state = e_state.SENDP;
                    break;
                case SENDP:
                    sc.sendP();
                    state = e_state.SENDGPOX;
                    break;
                case SENDGPOX:
                    sc.sendGpowX();
                    state = e_state.GETSCHNORR;
                    break;
                case GETSCHNORR:
                    sc.getSchnorrData();
                    state = e_state.GETALL;
                    break;
                case GETALL:
                    sc.getAllData();
                    state = e_state.END;
                    break;
                default:
                    System.out.println("Nothing");
            }
        }
    }

}
