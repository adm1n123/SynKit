package com.example.synkit;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javafx.scene.paint.Color;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.geometry.Insets;
import javafx.scene.control.Alert.AlertType;

import java.awt.*;
import java.io.*;
import java.net.URI;

public class HelloApplication extends Application {
    static VBox root;
    static Scene scene;
    static Stage stage;
    static Tab upstreamTab;
    static Tab module1Tab;
    static Tab module2Tab;
    static TabPane leftTabPane;
    static TabPane rightTabPane;
    static ScrollPane upstreamScroll;
    static ScrollPane module1Scroll;
    static ScrollPane module2Scroll;

    @Override
    public void start(Stage stage) throws IOException {
        try {
            HelloApplication.stage = stage;
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
    }

    public static void main(String[] args) {
        launch();
    }

    void load() {
        MenuBar menuBar = createMenuBar();
        root.getChildren().add(menuBar);
        SplitPane splitPane = createSplitPane();
        root.getChildren().add(splitPane);
        VBox.setVgrow(splitPane, Priority.ALWAYS); // Make the scrollpane expand
        splitPane.setMaxHeight(Double.MAX_VALUE); // Allow the scrollpane to grow without limit
    }

    SplitPane createSplitPane() {
        leftTabPane = new TabPane();
        rightTabPane = new TabPane();
        leftTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        rightTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        upstreamTab = new Tab("Upstream", new Label("Nothing to show"));
        module1Tab = new Tab("module1"  , new Label("Nothing to show"));
        module2Tab = new Tab("module2" , new Label("Nothing to show"));

        leftTabPane.getTabs().add(upstreamTab);
        rightTabPane.getTabs().addAll(module1Tab, module2Tab);

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(leftTabPane, rightTabPane);

        return splitPane;
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
        upstreamScroll = new ScrollPane();
        module1Scroll = new ScrollPane();
        module2Scroll = new ScrollPane();

        upstreamTab.setContent(upstreamScroll);
        module1Tab.setContent(module1Scroll);
        module2Tab.setContent(module2Scroll);

        UpstreamModel um = new UpstreamModel();
        um.parseCommits(files[0]);

        ModuleModel mm1 = new ModuleModel();
        if(files[1] != null) {
            mm1.parseCommits(files[1]);
            setIndex(um, mm1, 0);
        }
        ModuleModel mm2 = new ModuleModel();
        if(files[2] != null) {
            mm2.parseCommits(files[2]);
            setIndex(um, mm2, 1);
        }

        displayUpstreamCommits(um, upstreamScroll);

        if(files[1] != null) {
            displayModuleCommits(mm1, module1Scroll);
        }
        if(files[2] != null) {
            displayModuleCommits(mm2, module2Scroll);
        }


    }

    void setIndex(UpstreamModel um, ModuleModel mm, int idx) {
        float count = mm.commits.size();
        boolean[] check = new boolean[mm.commits.size()];
        for(int i = 0; i < um.commits.size(); i++) {
            for(int j = 0; j < count; j++) {
                if(check[j]) continue;
                if(um.commits.get(i).message.equals(mm.commits.get(j).message)) {
                    check[j] = true;
                    um.commits.get(i).synced = true;
                    um.indices.get(i).module = idx;
                    um.indices.get(i).Vval = j/count;
                }
            }
        }
    }

    void displayUpstreamCommits(UpstreamModel um, ScrollPane pane) {
        VBox vbPane = new VBox();
        pane.setContent(vbPane);

        for (int i = 0; i < um.commits.size(); i++) {
            final int ii = i;
            Commit c = um.commits.get(i);
//            System.out.println(um.commits.get(i).toString());
            GridPane gp = new GridPane();
            gp.setPadding(new Insets(20, 5, 20, 5));
            gp.setVgap(3);
            gp.setHgap(3);
            gp.setAlignment(Pos.BASELINE_LEFT);


            Label date = new Label();
            Label author = new Label();
            Hyperlink message = new Hyperlink();
            Text desc = new Text();

            date.setText(c.date);
            author.setText(c.author);
            message.setText(c.message);
//            message.setText(c.message.substring(0, Math.min(c.message.length(), 64)));

            Button cherry = new Button("Cherry-Pick");
            cherry.setUserData(0);
            Button skip = new Button("Skip");
            skip.setUserData(0);
            Button synced = new Button("Synced");
            Button copy = new Button("Copy");
            Button show = new Button("Show");


            gp.add(date, 0, 0);
            GridPane.setColumnSpan(date, 7);
            gp.add(skip, 7, 0);
            gp.add(synced, 8, 0);
            gp.add(cherry, 9, 0);
            gp.add(copy, 10, 0);
            gp.add(show, 11, 0);

            gp.add(message, 0, 1);
            GridPane.setColumnSpan(message, 12);
            gp.add(author, 0, 2);
            GridPane.setColumnSpan(author, 12);

            vbPane.getChildren().add(gp);

            if (c.synced) {
                synced.setStyle("-fx-background-color: green;");
            }

            skip.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if ((Integer)skip.getUserData() == 0) {
                        skip.setStyle("-fx-background-color: green;");
                        skip.setUserData(1);
                    } else {
                        skip.setStyle(null);
                        skip.setUserData(0);
                    }
                }
            });

            cherry.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if ((Integer)cherry.getUserData() == 0) {
                        cherry.setStyle("-fx-background-color: green;");
                        cherry.setUserData(1);
                    } else {
                        cherry.setStyle(null);
                        cherry.setUserData(0);
                    }
                }
            });


            copy.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    ClipboardContent content = new ClipboardContent();
                    content.putString(c.ID);
                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    clipboard.setContent(content);
                }
            });

            message.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    try {
                        // Open a URL in the default browser
                        Desktop.getDesktop().browse(new URI("https://github.com/Samsung/TizenRT/commit/"+c.ID)); // Replace with your desired URL
                    } catch (IOException | java.net.URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            });

            if (um.indices.get(ii).module != -1) {
                show.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        SingleSelectionModel<Tab> selectionModel = rightTabPane.getSelectionModel();
                        if (um.indices.get(ii).module == 0) {
                            selectionModel.select(0);
                            module1Scroll.setVvalue(um.indices.get(ii).Vval);
                        } else {
                            selectionModel.select(1);
                            module2Scroll.setVvalue(um.indices.get(ii).Vval);
                        }

                    }
                });
            }
        }

    }
    void displayModuleCommits(ModuleModel mm, ScrollPane pane) {
        VBox vbPane = new VBox();
        pane.setContent(vbPane);

        for (int i = 0; i < mm.commits.size(); i++) {
            Commit c = mm.commits.get(i);
//            System.out.println(mm.commits.get(i).toString());
            GridPane gp = new GridPane();
            gp.setPadding(new Insets(20, 5, 20, 5));
            gp.setVgap(3);
            gp.setHgap(3);
            gp.setAlignment(Pos.CENTER_LEFT);


            Label date = new Label();
            Label author = new Label();
            Hyperlink message = new Hyperlink();
            Text desc = new Text();

            date.setText(c.date);
            author.setText(c.author);
            message.setText(c.message);
//            message.setText(c.message.substring(0, Math.min(c.message.length(), 64)));


            gp.add(date, 0, 0);
            GridPane.setColumnSpan(date, 7);

            gp.add(message, 0, 1);
            GridPane.setColumnSpan(message, 12);
            gp.add(author, 0, 2);
            GridPane.setColumnSpan(author, 12);

            vbPane.getChildren().add(gp);
        }
    }
}