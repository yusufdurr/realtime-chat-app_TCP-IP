/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tcpserver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 *
 * @author yusuf.dur
 */
public class Room extends Thread {

    public Server server;
    public String name;
    public ArrayList<ServerClient> clients;
    
    public Room(String name , Server server) throws IOException {
        this.name = name;
        this.server = server;
        this.clients = new ArrayList<>();
    }

    @Override
    public void run() {
        while (true) {
            //room codes
                      
            
        }
    }
}

