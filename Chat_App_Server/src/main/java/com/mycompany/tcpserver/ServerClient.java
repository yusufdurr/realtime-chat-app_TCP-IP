/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tcpserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yusuf.dur
 */
public class ServerClient extends Thread { // Player

    Server server;
    Socket socket;
    OutputStream output;
    InputStream input;
    boolean isListening;
    String userName;
    public ArrayList<Room> rooms;

    public ServerClient(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.output = socket.getOutputStream();
        this.input = socket.getInputStream();
        this.isListening = false;
        this.rooms = new ArrayList<>();
        //System.out.println(this.socket.getInetAddress().toString() + ":" + this.socket.getPort() + "-> connected");
        //Frm_Server.lst_clients_model.addElement(this.socket.getInetAddress().toString() + ":" + this.socket.getPort());
    }

    public void Start() {
        isListening = true;
        start();
    }

    public void Stop() {
        try {
            isListening = false;
            output.close();
            input.close();
            socket.close();
            server.RemoveClient(this);

        } catch (IOException ex) {
            System.out.println("kapatma hatası");
            System.out.println(socket.getInetAddress().toString() + ":" + socket.getPort() + "-> stoped");

        }
    }

    public void SendMessage(String s) throws IOException {
        // message format    ===>    "_[messageCode]_[message]"
        s = ("_" + s);
        this.output.write(s.getBytes());
        this.output.flush();
    }

    @Override
    public void run() {
        try {
            while (isListening) {

                int byteSize = input.read(); //blocking
                byte bytes[] = new byte[byteSize];
                input.read(bytes);
                String s = new String(bytes, StandardCharsets.UTF_8);
                System.out.println(this.socket.getInetAddress().toString() + ":" + socket.getPort() + "-> message reacted serverclient");

                s = s.trim();
                System.out.println(s);

//                if (s.charAt(0) == 'C') {  // Connect
                server.AddClient(this);
//                    Thread.sleep(50);
//                    server.SendRoomCreationBroadcast();    // when room created connected other clients be aware 
//                }

                if (s.charAt(0) == 'U') {   // Username
                    //server.rooms.add(new Room("ChatRoom1", this.server));  // add default chat room by hand 
                    s = s.substring(1, s.length());
                    this.userName = s;

                    server.SendClientConnectionBroadcast(this);    // when client connected other clients be aware 
                    Thread.sleep(100);
                }

                if (s.charAt(0) == 'Q') {  // Quit
                    if(server.clients.contains(this)){
                        server.clients.remove(this);
                    }
                    this.userName = "";
                    this.isListening = false;

                    server.SendClientConnectionBroadcast(this);    // when client connected other clients be aware 
                    Thread.sleep(100);

                    // find rooms of this client and remove it 
                    this.rooms.clear();
                    for (Room room : server.rooms) {
                        if (room.clients.contains(this)) {
                            room.clients.remove(this);
                        }
                    }

//                    for (Room room : rooms) {
//                        System.out.println("room" + room + "  ");
//                        for (ServerClient client : room.clients) {
//                            System.out.print("client" + client + " ");
//                        }
//                    }
                }

                if (s.charAt(0) == 'r') {                       // room create from client
                    s = s.substring(1, s.length());
                    server.CreateRoom(s, server);
                    server.SendRoomCreationBroadcast();
                    System.out.println("serverda " + server.rooms.size() + " tane oda var");
                }

                if (s.charAt(0) == 'J') {                           // client connecting to  room
                    Thread.sleep(100);
                    s = s.substring(1, s.length());

                    for (Room room : server.rooms) {
                        if ((s).equals(room.name)) {
                            // add this client to room clients
                            room.clients.add(this);

                            System.out.println("default odada kaç kişi var " + room.clients.size());
                            // add room to this client roomList
                            this.rooms.add(room);

                            System.out.println("clientın kaç odası var " + rooms.size());
                            break;
                        }
                    }
//                    Thread.sleep(200);
                }
                if (s.charAt(0) == 'L') {                           // client left the  room
                    System.out.println("BİRİLERİ ODADAN ÇIKTI ");
                    Thread.sleep(100);
                    s = s.substring(1, s.length());

                    for (Room room : server.rooms) {
                        if ((s).equals(room.name)) {
                            // remove this client from room clients
                            room.clients.remove(this);

                            System.out.println("default odada kaç kişi var " + room.clients.size());
                            // add room to this client roomList
                            this.rooms.remove(room);

                            for (Room room1 : this.rooms) {

                            }

                            System.out.println("clientın kaç odası var " + rooms.size());
                            break;
                        }
                    }
//                    Thread.sleep(200);
                }

                if (s.charAt(0) == 'm') {                       // client message 
                    Thread.sleep(100);
                    s = s.substring(1, s.length());
                    String[] arr;
                    arr = s.split("_");        // arr[0] is received roomName   ,   arr[1] is received message to send other clients

                    // find room and send client message to other room members 
                    for (Room room : server.rooms) {
                        if ((arr[0]).equals(room.name)) {
//                            if (room.clients.contains(this)) {

                            for (ServerClient client : room.clients) {
                                // prevent send message the same client =)
                                if (!client.userName.equals(this.userName)) {
                                    client.SendMessage("B" + room.name + "_" + this.userName + "_" + arr[1]);           // SEND BROADCAST TO ROOM
                                    Thread.sleep(25);
                                }
                            }
                            break;
//                            }

                        }
                    }
                    Thread.sleep(50);

                }

            }

        } catch (IOException ex) {
            this.Stop();
            System.out.println(socket.getInetAddress().toString() + ":" + socket.getPort() + "->  ");
            //Logger.getLogger(ServerClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NegativeArraySizeException ex) {
            this.Stop();
            server.RemoveClient(this);
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
