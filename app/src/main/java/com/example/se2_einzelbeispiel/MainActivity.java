package com.example.se2_einzelbeispiel;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button sendRequestBtn = findViewById(R.id.send_button);
        sendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Info",sendRequestBtn.getText()+" Button gedrückt");
                Log.d("Info","Thread wird erstellt");

                Thread myNetworkThread = new Thread() {
                    @Override
                    public void run() {
                        writeMatrToServer();
                    }
                };
                Log.d("Info","Thread wurde erstellt");
                Log.d("Info","Thread wird ausgeführt");
                myNetworkThread.start();
                Log.d("Info","Thread ist fertig");
            }
        });
    }


    private void  writeMatrToServer() {

            //get the text from the field an check if not empty
            String matrNr = getValueFromTextField();
            //now check
            if(!checkMatrNr(matrNr)) {
                printmessage("Bitte geben Sie eine Matrikelnummer ein", "Hinweis");
                return;
            }
            //if everything is okay with the number then we start the connection
            try {

            Socket socket = createConnection();

            if(socket == null || !socket.isConnected()) {
                throw new Exception("Fehler beim Herstellen der Verbindung");
            }


            //writeToServer(socket, matrNr);

            //everything went fine.. now wait for response;
            Log.d("Info", "Matrikelnumber has been send to server");

             BufferedWriter writer= new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream()));

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            writer.write(matrNr);
            writer.flush();
            // writer.close();


            String response = reader.readLine();

            Log.d("Info", "reponse:"+response);
            //set Response to field
            writeToResponseField(response);


        }
        catch (Exception ex){
            Log.d("Error","MainActivity.createconnection"+ex.getMessage());
            printmessage(ex.getMessage(), "Fehler");

        }
    }


    private void writeToResponseField(String message){
            TextView response = findViewById(R.id.serverResponse_textView);
            response.setText(message);
    }

    private void writeToServer(Socket socket , String message){

            BufferedWriter writer;

            try{
                writer= new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));

            writer.write(message);
            writer.flush();
           // writer.close();

        }
        catch (Exception ex){
            Log.d("Error","MainActivity.createconnection"+ex.getMessage());
            printmessage(ex.getMessage(), "Fehler");
        }
    }

    private Socket createConnection(){
        Socket socket;
        try{
        socket = new Socket(getString(R.string.server_name),getResources().getInteger(R.integer.server_port));
        Log.d("Info","Server connection to host established");
        return socket;
        }
        catch (Exception ex) {
            Log.d("Error", "MainActivity.createconnection" + ex.getMessage());
            printmessage(ex.getMessage(), "Fehler");
        }
        return null;
    }


    private String getValueFromTextField(){
        EditText matrNrEditText =  findViewById(R.id.matrikelNumber_editText);
        String matrNr = matrNrEditText.getText().toString();
        return matrNr;
        //check if empty and if numeric string
    }

    private boolean checkMatrNr(String matrNr){
        if(matrNr.length() <= 0) return false;
        //we know that it must be a numeric string because of input field constraints
        return true;
    }

    private void printmessage(String message, String Title){
        //AlertDialog.Builder messageBox = new AlertDialog.Builder(this);
        //messageBox.setMessage(message);
        //messageBox.setTitle(Title);
        //messageBox.show();
        return;
    }
}