/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sigmaserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.SignatureException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import process.Data2Send;
import process.Process;

enum e_state {

    GETG, GETP, GETGPOWX, SENDSIGN, SENDALL, GETALL, END
};

/**
 *
 * @author Pawel
 */
public class SigmaServer {

    public ServerSocket serverSocket;
    public Socket clientSocket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    
    public Process processClass;

    public SigmaServer(int port) throws IOException {
        processClass = new Process();
        this.serverSocket = new ServerSocket(port);
        clientSocket = serverSocket.accept();
        ois = new ObjectInputStream(clientSocket.getInputStream());
        oos = new ObjectOutputStream(clientSocket.getOutputStream());
    }

    public void setG() throws IOException, ClassNotFoundException {
        processClass.setG((BigInteger) ois.readObject());
        System.out.println("Wyslano G: " + processClass.getG());
    }

    public void setP() throws IOException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchProviderException {
        processClass.setP((BigInteger) ois.readObject());
        processClass.computeGpowX();
        processClass.generateKeyPair();
        System.out.println("Wyslano P: " + processClass.getP());
    }

    public void setGpowXOtherSide() throws IOException, ClassNotFoundException {
        processClass.setGpowXotherSide((BigInteger) ois.readObject());
        System.out.println("Wysłano g pow x");
    }
    
    public void sendG() throws IOException, ClassNotFoundException {
        oos.writeObject(this.processClass.getG());
        oos.flush();
//        System.out.println("Wyslano G: " + processClass.getG());
    }

    public void sendMacAndSignature() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException, IOException{
        processClass.generateMac("Wiadomosc od strony servera!");
        processClass.signTheValues();
        oos.writeObject(this.processClass.getData2Send());
        oos.flush();
    }
    
    public void sendMac() throws IOException{
        oos.writeObject(this.processClass.getMAC());
        oos.flush();
    }
    
    public void getAllData() throws IOException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException{
        this.processClass.setD2sotherside((Data2Send) ois.readObject());
        if(this.processClass.verify()){
            processClass.generateMac("Wiadomosc od strony klienta blallaldsksladj!");
            processClass.signTheValues();
            oos.writeObject(this.processClass.getData2Send());
            oos.flush();
        }else{
            oos.writeObject(this.processClass.badVerification());
            oos.flush();
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, NoSuchProviderException {
        e_state state = e_state.GETG;
        int portNumber = 4444;
        SigmaServer ss = new SigmaServer(portNumber);
        while (!state.equals(e_state.END)) {
            switch (state) {
                case GETG:
                    ss.setG();
                    state = e_state.GETP;
                    break;
                case GETP:
                    ss.setP();
                    
                    state = e_state.GETGPOWX;
                    break;
                case GETGPOWX:
                    ss.setGpowXOtherSide();
                    ss.sendMacAndSignature();
                    state = e_state.GETALL;
                    break;
                case GETALL:
                    ss.getAllData();
                    state = e_state.END;
                    break;
                default:
                    break;
            }
        }
    }

}
