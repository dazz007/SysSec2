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

enum e_state{ SENDG, SENDP, SENDGPOX, GETALL, SENDALL, END};
/**
 *
 * @author Pawel
 */
public class SigmaClient {

    public Socket clientSocket;
    public ObjectOutputStream oos;
    public ObjectInputStream ois;
    private ProcessClass processClass;
    /**
     * @param host
     * @param args the command line arguments
     */
    public SigmaClient(String host, int portNumber) throws IOException{
        this.processClass = new ProcessClass();
        this.processClass.generatePandG();
        this.clientSocket = new Socket(host, portNumber);
        this.oos = new ObjectOutputStream(this.clientSocket.getOutputStream());
        this.ois = new ObjectInputStream(this.clientSocket.getInputStream());
    }
    
    public void sendG() throws IOException{
        this.oos.writeObject(this.processClass.getG());
        this.oos.flush();
        System.out.println("Wysłano G");
    }
    
    public void sendP() throws IOException{
        this.oos.writeObject(this.processClass.getP());
        this.oos.flush();
        System.out.println("Wysłano P");
    }
    
    public void sendGpowX() throws IOException{
        this.processClass.generatePandG();
        this.oos.writeObject(this.processClass.getGpowX());
        this.oos.flush();
        System.out.println("Wysłano G pow X");
    }
    
    public void getG() throws IOException, ClassNotFoundException{
        System.out.println((BigInteger) ois.readObject());
    }
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        e_state state = e_state.SENDG;
        String hostName = "localhost";
        int portNumber = 4444;
        SigmaClient sc = new SigmaClient(hostName, portNumber);
//        ProcessClass processClass = new ProcessClass();
//        processClass.generatePandG();
//        Socket clientSocket = new Socket(hostName, portNumber);
//        ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
        
        while(!state.equals(e_state.END)){
            switch(state){
                case SENDG:
                    sc.sendG();
//                    oos.writeObject(processClass.getG());
//                    oos.flush();
//                    System.out.println("Wysłano G");
                    state = e_state.SENDP;
                    break;
                case SENDP:
                    sc.sendP();
//                    oos.writeObject(processClass.getP());
//                    oos.flush();
//                    System.out.println("Wysłano P");
                    state = e_state.SENDGPOX;
                    break;
                case SENDGPOX:
                    sc.sendGpowX();
//                    processClass.computeGpowX();
//                    oos.writeObject(processClass.getGpowX());
//                    oos.flush();
//                    System.out.println("Wysłano P");
                    state = e_state.GETALL;
                    break;
                case GETALL:
                    sc.getG();
                    state = e_state.END;
                    break;
                default:
                    System.out.println("Nothing");
            }
        }
    }

}
