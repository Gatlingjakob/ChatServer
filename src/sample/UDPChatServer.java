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
    private static String[] userList; //Platform.runLater()


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
            datagramSocket = new DatagramSocket(PORT);
        } catch (SocketException sockEx) {
            System.out.println("Unable to open port!");
            System.exit(1);
        }
        handleClient();

    }

    private static void handleClient(){

        try{
            String messageIn, messageOut;
            int numMessages = 0;
            InetAddress clientAddress = null;
            int clientPort;
            boolean gotUsername = false;
            String username = "blank";

            if (gotUsername == false) {
                buffer = new byte[256];
                inPacket = new DatagramPacket(buffer, buffer.length);

                datagramSocket.receive(inPacket);

                clientAddress = inPacket.getAddress();

                clientPort = inPacket.getPort();

                messageIn = new String(inPacket.getData(), 0, inPacket.getLength());

                username = messageIn;
                System.out.println("Username received from [ User "  + username
                        + " Port: " + clientPort + " IP: " + clientAddress + " ]");
                messageOut = "New Username: "  + messageIn;

                outPacket = new DatagramPacket(messageOut.getBytes(), messageOut.length(), clientAddress, clientPort);

                datagramSocket.send(outPacket);

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
