package com.example.se2_einzelbeispiel;

public class ClientConnectionThread implements Runnable{


    private String messageToServer;
    private String serverHostname;
    private int serverPort;

    public ClientConnectionThread(String messageToServer, String serverHostname, int serverPort){
        this.messageToServer = messageToServer;
        this.serverHostname = serverHostname;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {

    }


    private void openConnection(){

    }


}
