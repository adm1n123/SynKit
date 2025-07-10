package com.example.synkit;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.List;

public class ConnectSSHView {
    Session session = null;
    String conanDir = null;

    void showSSHSettings(List<String> stylesheets) {
        Stage stage = new Stage();
        VBox root = new VBox();
        Scene scene = new Scene(root, 500, 300);
        scene.getStylesheets().addAll(stylesheets);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("Connect");


        Label userLabel = new Label("User:");
        userLabel.setStyle("-fx-font-weight: bold;");
        Label addressLabel = new Label("Host:");
        addressLabel.setStyle("-fx-font-weight: bold;");
        Label portLabel = new Label("Port:");
        portLabel.setStyle("-fx-font-weight: bold;");
        Label passLabel = new Label("Password:");
        passLabel.setStyle("-fx-font-weight: bold;");
        Label dirLabel = new Label("conan dir:");
        dirLabel.setStyle("-fx-font-weight: bold;");

        TextField userField = new TextField("test");
        userField.setPromptText("User ID");
        TextField addressField = new TextField("127.0.0.1");
        addressField.setPromptText("IP Address");
        TextField portField = new TextField("22");
        portField.setPromptText("SSH Port");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        TextField dirField = new TextField("~/projects/conan");
        dirField.setPromptText("Conan Root dir");

        Button connectBtn = new Button("Connect");

        GridPane gridPane = new GridPane();
        root.getChildren().add(gridPane);

        gridPane.setMinSize(400, 200);

        //Setting the padding
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        //Setting the vertical and horizontal gaps between the columns
        gridPane.setVgap(20);
        gridPane.setHgap(10);

        //Setting the Grid alignment
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(userLabel, 0,0);
        gridPane.add(userField, 1, 0);
        gridPane.add(addressLabel, 0,1);
        gridPane.add(addressField, 1, 1);
        gridPane.add(portLabel, 0,2);
        gridPane.add(portField, 1, 2);
        gridPane.add(passLabel, 0,3);
        gridPane.add(passField, 1, 3);
        gridPane.add(dirLabel, 0,4);
        gridPane.add(dirField, 1, 4);
        gridPane.add(connectBtn, 1, 5);


        connectBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String user = userField.getText();
                String ip = addressField.getText();
                int port = Integer.parseInt(portField.getText());
                String pass = passField.getText();
                String dir = dirField.getText();
                if (user == null || ip == null || pass == null || dir == null) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Info");
                    alert.setHeaderText("Please enter fields");
                    alert.setContentText("All field are required");
                    alert.showAndWait();
                } else {
                    if (connect(user, ip, port, pass, dir)) {
                        stage.close();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Error");
                        alert.setHeaderText("Failed to connect");
                        alert.setContentText("SSH connection failed");
                        alert.showAndWait();
                    }
                }

            }
        });
    }



    public boolean connect(String user, String host, int port, String password, String dir) {
        conanDir = dir;
        try {
            JSch jsch = new JSch();

            session = jsch.getSession(user, host, port);
            session.setPassword(password);

            session.setConfig("StrictHostKeyChecking", "no");

            System.out.println("Establishing Connection...");
            session.connect();
            System.out.println("Connection established.");
            return true;

        } catch (JSchException e) {
            System.err.println("JSchException: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public String executeCommand(String command) {
        StringBuilder output = new StringBuilder();
        ChannelExec channel = null;
        try {
            // Open a new channel to execute commands
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("cd "+conanDir+";"+command);
            channel.setInputStream(null);
            channel.setErrStream(System.err); //  Redirect error stream to standard error

            //  Connect to the channel
            channel.connect();

            //  Read the output of the command
            InputStream in = channel.getInputStream();
            byte[] buffer = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(buffer, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    output.append(new String(buffer, 0, i));
                    System.out.println(new String(buffer, 0, i));
                }
                if (channel.isClosed()) {
                    System.out.println("Exit Status: " + channel.getExitStatus());
                    break;
                }
                Thread.sleep(100);
            }

            System.out.println("Command execution complete.");

        } catch (JSchException e) {
            System.err.println("JSchException: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
        return output.toString();
    }
}
