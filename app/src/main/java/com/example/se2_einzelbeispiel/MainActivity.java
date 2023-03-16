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

    private static int ASCIINumberToLoweCaseCharacterOffset = 48;
    private static int ASCIIZeroToSpecialCharacterOffset = 58;

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

        final Button calculateButton = findViewById(R.id.calculate_button);

        calculateButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Log.d("Info", "Calculate button clicked" );
                Log.d("Info","Updating Matrikelnumber");
                calculateAndUpdate(true);
            }

        });
    }


    private void  writeMatrToServer() {

            //get the text from the field an check if not empty
            String matrNr = getMatrikelNumberFromField();
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

    /**
     * We assume that:
     *  - change every SECOND number means => In a String which consists of characters 0..n-1 that every odd index is changed
     *  -- SINCE we dont haven´t been given a nice conversion (the number zero can occur, is changed to what??)
     *  -- and given the list of matrikelnumbers we´ve seen in the email about the EINZELGESPRÄCHE
     *  -- WE CONCLUDE that zero can only occur as first number (index = 0 , not translated)
     *  -- AND even if zero was to be translated .. it is not clear from the exercise if zero would to be translated
     *  -- after nine (in a sense of 1-> a ...., 9->i , 0->j) or it is excluded because the enumeration starts at 1 (which
     *  -- is order-whise above zero und we can decude that zero therefore can be ignored
     *
     *  Solution: We introduce a parameter so that we can decide wether to ignore 0 or translate it in the above stated manner
     */
    private void calculateAndUpdate(boolean translateZeros){

        String matrNr = getMatrikelNumberFromField();
        matrNr =  changedOddIndecesToLowerCaseLetters(matrNr, translateZeros);
        updateMatrikelNumberField(matrNr);

    }

    private String changedOddIndecesToLowerCaseLetters(String matrNr, boolean translateZeros){
        char workingVariable[] = matrNr.toCharArray();
        for(int i = 1; i < matrNr.length(); i+=2){
            //check if its a zero in ascii which has the int value 48
            if(workingVariable[i] == 48){
                //check bool variable if we should ignore zeros or cover according to assumption
                workingVariable[i] += (translateZeros ? 1  : 0) * ASCIIZeroToSpecialCharacterOffset;
            }
            else {
                workingVariable[i] += ASCIINumberToLoweCaseCharacterOffset;
            }
        }
        return new String(workingVariable);
    }

    private EditText getMatrikelNumberField(){
        return findViewById(R.id.matrikelNumber_editText);
    }
    private void updateMatrikelNumberField(String newMatrikelNumber){
        getMatrikelNumberField().setText(newMatrikelNumber);
    }
    private String getMatrikelNumberFromField(){

        String matrNr = getMatrikelNumberField().getText().toString();
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