package com.example.synkit;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class UploadView {
    
    void showUploadView(List<String> stylesheets) {
        Stage stage = new Stage();
        VBox root = new VBox();
        Scene scene = new Scene(root, 500, 300);
        scene.getStylesheets().addAll(stylesheets);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("Upload");

        Label upstreamLbl = new Label("Upstream");
        Label module1Lbl = new Label("Module1");
        Label module2Lbl = new Label("Module2");
        Label upstreamLbl_h = new Label("");
        Label module1Lbl_h = new Label("");
        Label module2Lbl_h = new Label("");

        Button upstreamBtn = new Button("Browse");
        Button module1Btn = new Button("Browse");
        Button module2Btn = new Button("Browse");

        GridPane gridPane = new GridPane();
        root.getChildren().add(gridPane);

        Label dummy = new Label("  ");
        Label lbl = new Label(" Run below command to get the git log history:");
        Label cmd = new Label(" > git --no-pager log HEAD ^commit_hash > log.txt   ");
        Button copy = new Button("Copy");

        GridPane bottomGP = new GridPane();
        root.getChildren().add(bottomGP);
        bottomGP.add(dummy, 0, 0);
        bottomGP.add(lbl, 0, 1);
        bottomGP.add(cmd, 0, 2);
        bottomGP.add(copy, 8, 2);

        gridPane.setMinSize(400, 200);

        //Setting the padding
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        //Setting the vertical and horizontal gaps between the columns
        gridPane.setVgap(20);
        gridPane.setHgap(10);

        //Setting the Grid alignment
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(upstreamLbl, 0,0);
        gridPane.add(upstreamBtn, 1, 0);
        gridPane.add(upstreamLbl_h, 2, 0);
        gridPane.add(module1Lbl, 0,1);
        gridPane.add(module1Btn, 1, 1);
        gridPane.add(module1Lbl_h, 2, 1);
        gridPane.add(module2Lbl, 0,2);
        gridPane.add(module2Btn, 1, 2);
        gridPane.add(module2Lbl_h, 2, 2);

        final File[] uploadedFiles = new File[3];

        upstreamBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String userHome = System.getProperty("user.home")+"\\Downloads";
                File userHomeDir = new File(userHome);

                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialDirectory(userHomeDir);
                fileChooser.setTitle("Upstream Commits");
                File selectedFile = fileChooser.showOpenDialog(stage);
                upstreamLbl_h.setText(selectedFile.getName());
                uploadedFiles[0] = selectedFile;
            }
        });
        module1Btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String userHome = System.getProperty("user.home")+"\\Downloads";
                File userHomeDir = new File(userHome);
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialDirectory(userHomeDir);
                fileChooser.setTitle("Module1 Commits");
                File selectedFile = fileChooser.showOpenDialog(stage);
                module1Lbl_h.setText(selectedFile.getName());
                uploadedFiles[1] = selectedFile;
            }
        });
        module2Btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String userHome = System.getProperty("user.home")+"\\Downloads";
                File userHomeDir = new File(userHome);
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialDirectory(userHomeDir);
                fileChooser.setTitle("Module2 Commits");
                File selectedFile = fileChooser.showOpenDialog(stage);
                module2Lbl_h.setText(selectedFile.getName());
                uploadedFiles[2] = selectedFile;
            }
        });

        ToggleButton done = new ToggleButton ("Done");
        gridPane.add(done, 0, 4);
        done.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(uploadedFiles[0] == null && uploadedFiles[1] == null && uploadedFiles[2] == null) { // TODO: replace first && with ||
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Info");
                    alert.setHeaderText("Please select files");
                    alert.setContentText("Upstream and Module1/Module2 files are required");
                    alert.showAndWait();
                } else {
                    stage.close();
                    new HelloApplication().populateCommits(uploadedFiles);
                }
            }
        });

        Button clear = new Button("Clear");
        gridPane.add(clear, 1, 4);
        clear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Arrays.fill(uploadedFiles, null);
                upstreamLbl_h.setText("");
                module1Lbl_h.setText("");
                module2Lbl_h.setText("");
            }
        });

        copy.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ClipboardContent content = new ClipboardContent();
                content.putString("git --no-pager log HEAD ^commit_hash > log.txt");
                Clipboard clipboard = Clipboard.getSystemClipboard();
                clipboard.setContent(content);
            }
        });



    }
}
