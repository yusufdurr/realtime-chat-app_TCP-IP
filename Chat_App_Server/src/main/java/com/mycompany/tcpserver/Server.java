/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tcpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yusuf.dur
 */
public class Server extends Thread {

    int port;
    TCPServer serverWindow;
    ServerSocket serverSocket;
    boolean isListening;
    ArrayList<ServerClient> clients;
    ArrayList<Room> rooms;

    //Thread listenThread;
    public Server(int port, TCPServer serverWindow) throws IOException, InterruptedException {
        try {
            this.port = port;
            this.serverWindow = serverWindow;
            this.serverSocket = new ServerSocket(port);
            this.isListening = false;
            this.clients = new ArrayList<>();
            this.rooms = new ArrayList<>();

            //this.listenThread= new Thread();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Listen() {
        this.isListening = true;
        this.start();
        System.out.println("Server startted...");

    }

    public void Stop() {
        this.isListening = false;
        try {
            serverSocket.close();
            for (ServerClient client : clients) {
                client.socket.close();
                RemoveClient(client);
            }
        } catch (IOException e) {
            System.out.println("Error closing server socket: " + e.getMessage());
        }

    }

    public void AddClient(ServerClient serverClient) {
        if (!clients.contains(serverClient)) {
            this.clients.add(serverClient);
        }
        System.out.println("Client Added...");

        // print arraylist in one line 
        System.out.print("clients:  ");
        System.out.print("[ ");
        for (int i = 0; i < clients.size(); i++) {
            System.out.print(clients.get(i).socket.getPort());
            if (i != clients.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.print("  ]");
        System.out.println("");

        serverWindow.lst_clients_model.removeAllElements();
        for (int i = 0; i < clients.size(); i++) {
            serverWindow.lst_clients_model.addElement("player  " + clients.get(i).socket.getPort());
        }
    }

    public void RemoveClient(ServerClient serverClient) {
        this.clients.remove(serverClient);
        System.out.println("Client removed...");

        // add users on list in server frame 
        serverWindow.lst_clients_model.removeAllElements();
        for (int i = 0; i < clients.size(); i++) {
            serverWindow.lst_clients_model.addElement("player  " + clients.get(i).socket.getPort());
        }

        System.out.print("clients:  ");
        System.out.print("[ ");
        for (int i = 0; i < clients.size(); i++) {
            System.out.print(clients.get(i).socket.getPort());
            if (i != clients.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("");
    }

    public void SendClientConnectionBroadcast(ServerClient c) throws IOException, InterruptedException {
        Thread.sleep(100);
        String names = "";
        for (ServerClient client : clients) {
            names = names.concat(client.userName + "_");
            System.out.println("olusan kisi listesi " + names);
        }
        names = names.substring(0, names.length() - 1);

        for (ServerClient client : clients) {
            client.SendMessage("n" + names);
            Thread.sleep(25);
        }
        System.out.println("Broadcast message send...");
    }
    
    //  
    public void SendRoomCreationBroadcast() throws IOException, InterruptedException {
        Thread.sleep(100);
        String names = "";
        for (Room room : rooms) {
            names = names.concat(room.name + "_");
        }
        names = names.substring(0, names.length() - 1);
        System.out.println(names);
        for (ServerClient client : clients) {
            client.SendMessage("r" + names);
            Thread.sleep(25);
        }
        System.out.println("Broadcast message send...");
    }

    // Create room in server 
    public void CreateRoom(String name, Server server) throws IOException, InterruptedException {
        Room r = new Room(name, server);
        if (!rooms.contains(r)) {
            rooms.add(r);
        }

        System.out.println("Rooms : ");
        for (Room room : rooms) {
            System.out.print(room.name + "  ");
        }

        serverWindow.lst_rooms_model.addElement(name);

//        serverWindow.lst_rooms_model.removeAllElements();
//        for (int i = 0; i < rooms.size(); i++) {
//            serverWindow.lst_rooms_model.addElement("player  " + clients.get(i).socket.getPort());
//        }
//        Thread.sleep(200);
//        SendRoomCreationBroadcast();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        TCPServer serverScreen = new TCPServer();
        serverScreen.setVisible(true);
    }

    public void run() {
        try {
            rooms.add(new Room("ChatRoom1", this));
            serverWindow.lst_rooms_model.addElement("ChatRoom1");

            while (this.isListening) {
                try {
                    Socket clientSocket = this.serverSocket.accept(); //blocking
                    System.out.println("Client connected...");
                    ServerClient player = new ServerClient(this, clientSocket);
                    this.AddClient(player);
//                    player.SendMessage("");
                    player.Start();

                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
