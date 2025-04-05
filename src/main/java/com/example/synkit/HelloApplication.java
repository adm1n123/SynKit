package com.example.synkit;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class HelloApplication extends Application {
    static VBox root;
    Scene scene;
    Stage stage;
    @Override
    public void start(Stage stage) throws IOException {
        try {
            this.stage = stage;
            root = new VBox();
            scene = new Scene(root,1400,900);
            scene.getStylesheets().add(String.valueOf(getClass().getResource("application.css")));
            scene.getStylesheets().add(String.valueOf(getClass().getResource("dark-theme.css")));
            stage.setScene(scene);
            stage.show();
            stage.setTitle("SynKit");

            load();
        } catch(Exception e) {
            e.printStackTrace();
        }
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
//        stage.setTitle("Hello!");
//        stage.setScene(scene);
//        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    void load() {

        MenuBar menuBar = createMenuBar();
        root.getChildren().add(menuBar);
        GridPane gridPane = createGridPane();
        root.getChildren().add(gridPane);


    }
    GridPane createGridPane() {
        GridPane gridPane = new GridPane();
        ColumnConstraints leftCol = new ColumnConstraints();
        leftCol.setPercentWidth(50);

        ColumnConstraints rightCol = new ColumnConstraints();
        rightCol.setPercentWidth(50);
        gridPane.getColumnConstraints().addAll(leftCol, rightCol);


        TabPane leftTabPane = new TabPane();
        TabPane rightTabPane = new TabPane();
        leftTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        rightTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab upstream = new Tab("Upstream", new Label("Nothing to show"));
        Tab module1 = new Tab("module1"  , new Label("Nothing to show"));
        Tab module2 = new Tab("module2" , new Label("Nothing to show"));

        leftTabPane.getTabs().add(upstream);
        rightTabPane.getTabs().addAll(module1, module2);

        gridPane.add(leftTabPane, 0, 0);
        gridPane.add(rightTabPane, 1, 0);

        ScrollPane upstream_scroll = new ScrollPane();
        ScrollPane module1_scroll = new ScrollPane();
        ScrollPane module2_scroll = new ScrollPane();


        return gridPane;
    }


    MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        menuBar.getMenus().add(fileMenu);
        MenuItem newMenuItem = new MenuItem("New");
        MenuItem uploadMenuItem = new MenuItem("Upload");
        MenuItem importMenuItem = new MenuItem("Import");
        MenuItem exportMenuItem = new MenuItem("Export");
        CheckMenuItem theme = new CheckMenuItem("Light");
        fileMenu.getItems().addAll(newMenuItem, uploadMenuItem, importMenuItem, exportMenuItem, theme);

        theme.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                scene.getStylesheets().remove(String.valueOf(getClass().getResource("dark-theme.css")));
            } else {
                scene.getStylesheets().add(String.valueOf(getClass().getResource("dark-theme.css")));
            }
        });

        uploadMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                UploadView uv = new UploadView();
                uv.showUploadView(stage.getScene().getStylesheets());
            }
        });

        Menu syncMenu = new Menu("Sync");
        menuBar.getMenus().add(syncMenu);
        MenuItem statsMenuItem = new MenuItem("Statistics");
        MenuItem reportMenuItem = new MenuItem("Report");
        syncMenu.getItems().addAll(statsMenuItem, reportMenuItem);

        Menu helpMenu = new Menu("Help");
        menuBar.getMenus().add(helpMenu);
        MenuItem welcomeMenuItem = new MenuItem("Welcome");
        MenuItem aboutMenuItem = new MenuItem("About");
        helpMenu.getItems().addAll(welcomeMenuItem, aboutMenuItem);



        return menuBar;

    }

    void populateCommits(File[] files) {
        for (int i = 0; i < 3; i++) {
            System.out.println(files[i].getName());
        }
    }

}