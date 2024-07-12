/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package newpackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yusuf.dur
 */
public class Client extends Thread {

    Socket socket;
    NameScreen nameScreen;
    AppScreen appScreen;

    OutputStream output;
    InputStream input;
    boolean isListening;
    String userName;

    public Client(Socket socket, NameScreen nameScreen) throws IOException, InterruptedException {
        this.socket = socket;

        this.nameScreen = nameScreen;

        this.output = socket.getOutputStream();
        this.input = socket.getInputStream();

        isListening = false;
        userName = "";

    }

    public void Start() {
        this.isListening = true;
        this.start();
    }

    public void Stop() throws InterruptedException {
        try {
            SendMessage("Quit");
            this.isListening = false;
            this.output.close();
            this.input.close();
            this.socket.close();
        } catch (IOException ex) {
            System.out.println("kapatma hatası");
            System.out.println(this.socket.getInetAddress().toString() + ":" + this.socket.getPort() + "-> stoped");

        }
    }

    public void SendMessage(String s) throws IOException, InterruptedException {
//        Thread.sleep(20);
        // message format    ===>    "_[messageCode]_[message]"
        s = ("_" + s);
        this.output.write(s.getBytes());
        this.output.flush();
    }

//    public static void main(String[] args) throws IOException, InterruptedException {
//        NameScreen s = new NameScreen();
//        s.setVisible(true);
//    }
    @Override
    public void run() {
        try {
            System.out.println("client calismaya basladi ");
            while (this.isListening) {

                int byteSize = input.read(); //blocking     
                byte bytes[] = new byte[byteSize];
                input.read(bytes);

                // take input properly
                String s = new String(bytes, StandardCharsets.UTF_8);
                s = s.trim();
                System.out.println(s);

                if (s.charAt(0) == 'n') {                               // take the other clients names 
//                    appScreen.u.removeAll();
                    s = s.substring(1, s.length());
                    System.out.println("Tum client elemanlar : " + s + "  : " + s.length());

                    String[] sArr;
                    sArr = s.split("_");
                    ArrayList<String> arr = new ArrayList<>();
                    Collections.addAll(arr, sArr);

                    System.out.println("clietn username detect " + nameScreen.client.userName == null);
                    for (String string : arr) {
                        if (string.equals(nameScreen.client.userName)) {
                            arr.remove(userName);
                        }
                    }
                    nameScreen.appScreen.usersListModel.removeAllElements();
                    nameScreen.appScreen.usersListModel.addAll(arr);
                    nameScreen.appScreen.u.setModel(nameScreen.appScreen.usersListModel);

                }
                if (s.charAt(0) == 'r') {
                    System.out.println("Tum room elemanlar : " + s + "  : " + s.length());
                    Thread.sleep(100);// room update for other clients 
                    s = s.substring(1, s.length());
                    String[] arr;
                    arr = s.split("_");
//                    System.out.println(arr);
                    nameScreen.appScreen.r.setListData(arr);

                }

                if (s.charAt(0) == 'B') {                                       // TAKE BROADCAST FOR YOUR ROOM
                    // arr[0] is room name of message // arr[1] is who send the message to that room // arr[2] is message                                               
                    s = s.substring(1, s.length());
                    String[] arr;
                    arr = s.split("_");

                    for (RoomScreen connectedRoom : nameScreen.appScreen.roomsForClient) {  // SEND MESSAGE TO CORRECT ROOM OF CLIENTS (rest of the client who is in room )
                        if ((connectedRoom.name).equals(arr[0])) {

//                            String currentText = connectedRoom.messageArea.getText();
//                            currentText = currentText.concat("\\r\\n" + arr[1] + " => " + arr[2] + "\\r\\n");
//
//                            connectedRoom.messageArea.setText(currentText);  
                                connectedRoom.messageArea.append( arr[1] + " => " + arr[2] + "\n");
                            break;
                        }

                    }
//                    Thread.sleep(100);

                }

            }

        } catch (IOException ex) {
            try {
                this.Stop();
            } catch (InterruptedException ex1) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex1);
            }

            System.out.println(this.socket.getInetAddress().toString() + ":" + this.socket.getPort() + "->  ");
            //Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);

        } catch (InterruptedException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
