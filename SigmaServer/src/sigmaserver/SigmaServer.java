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

enum e_state {

    GETG, GETP, GETGPOWX, SENDALL, GETALL, END
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
    public ProcessClass processClass;

    public SigmaServer(int port) throws IOException {
        processClass = new ProcessClass();
        this.serverSocket = new ServerSocket(port);
        clientSocket = serverSocket.accept();
        ois = new ObjectInputStream(clientSocket.getInputStream());
        oos = new ObjectOutputStream(clientSocket.getOutputStream());
    }

    public void setG() throws IOException, ClassNotFoundException {
        processClass.setG((BigInteger) ois.readObject());
        System.out.println("Wyslano G: " + processClass.getG());
    }

    public void setP() throws IOException, ClassNotFoundException {
        processClass.setP((BigInteger) ois.readObject());
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
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
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
                    ss.sendG();
                    state = e_state.END;
                default:
                    break;
            }
        }
    }

}
