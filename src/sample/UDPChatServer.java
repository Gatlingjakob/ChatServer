package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * Created by jakob on 18/02/2017.
 */

public class UDPChatServer extends Application {


    // Fields
    public static final int PORT = 1234;
    private static DatagramSocket datagramSocket;
    private static DatagramPacket inPacket, outPacket;
    private static byte[] buffer;
    private static int alive;
     //Platform.runLater()



    // JavaFX
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Set the stage to display content
        TextArea textarea = new TextArea();
        textarea.setMinWidth(900);
        textarea.setMinHeight(600);

        Button btn = new Button();
        btn.setOnAction(e -> {
            textarea.appendText("Hello");
        });

        primaryStage.setTitle("Server(UDP)_Test01");
        Pane root = new Pane();
        ScrollPane scroll = new ScrollPane(textarea);
        btn.setLayoutX(300);
        btn.setLayoutY(300);
        Pane pane2 = new Pane(btn);
        root.getChildren().addAll(scroll, pane2);
        primaryStage.setScene(new Scene(root, 900, 600));

        // primaryStage.setScene(new Scene(root, 300, 300));
        primaryStage.show();
    }
    public static void main(String[] args) {



        System.out.println("Opening port... \n");
        try {
            // Open Port
            datagramSocket = new DatagramSocket(PORT);
        } catch (SocketException sockEx) {
            System.out.println("Unable to open port!");
            System.exit(1);
        }
        handleClient();

    }

    // add New User to list of users (kan godt være at denne metode er overflødig)
    public static ArrayList<String> addUsername (String user, ArrayList<String> list){
        list.add(user);
        return list;
    }

    private static void handleClient(){
        ArrayList<String> userList = new ArrayList<>();
        try{
            // Message from Client to Server and message from Server to Client respectively
            String messageIn, messageOut;
            // Number of sent messages for this Client
            int numMessages = 0;
            // IP-Address for this Client
            InetAddress clientAddress = null;
            // Port for this Client
            int clientPort;
            boolean gotUsername = false;
            String username = "blank";

            // If (username hasn't been successfully retrieved yet){}
            if (gotUsername == false) {

                buffer = new byte[256];
                // Readying ingoing packet with a byte array to store data passed from Client
                inPacket = new DatagramPacket(buffer, buffer.length);

                //Server receives Packet from Client
                datagramSocket.receive(inPacket);

                clientAddress = inPacket.getAddress();

                clientPort = inPacket.getPort();

                // Convert data passed from Client to String-type
                messageIn = new String(inPacket.getData(), 0, inPacket.getLength());

                // set Username for this Client
                username = messageIn;
                addUsername(username, userList);

                // Info about new User printed in the Server Console
                System.out.println("New user has joined the server! [ User: "  + username
                        + " Port: " + clientPort + " IP: " + clientAddress + " ] ");

                // Outgoing message to Client
                messageOut = "New Username: "  + messageIn;

                // "Load" outgoing Packet with specified data
                outPacket = new DatagramPacket(messageOut.getBytes(), messageOut.length(), clientAddress, clientPort);

                // Send Packet to Client
                datagramSocket.send(outPacket);

                System.out.println("Users on this Server: " + userList); //iterér igennem userList når
                // bruger går offline -> find tilsvarende brugernavn og fjern fra listen -->
                // for each user in userList { if(userList.get(i) == username){ userList.remove(i); }}

                // Username successfully generated
                gotUsername=true;



            }

            do {
                buffer = new byte[256];
                inPacket = new DatagramPacket(buffer, buffer.length);

                datagramSocket.receive(inPacket);

                clientAddress = inPacket.getAddress();

                clientPort = inPacket.getPort();

                messageIn = new String(inPacket.getData(), 0, inPacket.getLength());

                System.out.println("Message received from [ User "  + username
                        + " Port: " + clientPort + " IP: " + clientAddress + " ]");
                numMessages++;
                messageOut = "[" + username + "] - " +"Message " + numMessages + ": " + messageIn;

                outPacket = new DatagramPacket(messageOut.getBytes(), messageOut.length(), clientAddress, clientPort);

                datagramSocket.send(outPacket);

            }
            while(true);

        }
        catch (IOException ioEx)
        {
            ioEx.printStackTrace();
        }
        finally {
            System.out.println("\n* Closing Connection... *");
            datagramSocket.close();
        }

    }

}
