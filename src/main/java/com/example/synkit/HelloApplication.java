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

import com.example.synkit.ConnectSSHView.CommandStatus;

public class HelloApplication extends Application {

    static String KERNEL_DIR = "Kernel";
    static String TR_UTILS_DIR = "TR_Utils";
    static String KERNEL_SYNC = "kernel_sync";
    static String TR_UTILS_SYNC = "tr_utils_sync";
    static String KERNEL_BASE = "main";
    static String TR_UTILS_BASE = "main";
    static boolean sync_init = true;

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
            scene = new Scene(root,1400,768);
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

        upstreamTab = new Tab("Public", new Label("Nothing to show"));
        module1Tab = new Tab(KERNEL_DIR  , new Label("Nothing to show"));
        module2Tab = new Tab(TR_UTILS_DIR , new Label("Nothing to show"));

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
        MenuItem connectMenuItem = new MenuItem("Connect");
        MenuItem uploadMenuItem = new MenuItem("Upload");
        MenuItem importMenuItem = new MenuItem("Import");
        MenuItem exportMenuItem = new MenuItem("Export");
        CheckMenuItem theme = new CheckMenuItem("Light");
        fileMenu.getItems().addAll(newMenuItem, connectMenuItem, uploadMenuItem, importMenuItem, exportMenuItem, theme);

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
        connectMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ConnectSSHView uv = new ConnectSSHView();
                uv.showSSHSettings(stage.getScene().getStylesheets());
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
        mm1.moduleNo = 1;
        if(files[1] != null) {
            mm1.parseCommits(files[1]);
            setIndex(um, mm1, 0);
        }
        ModuleModel mm2 = new ModuleModel();
        mm2.moduleNo = 2;
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
                    um.commits.get(i).synced = Commit.Sync.YES;
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
            gp.setHgap(5);
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
            Button cherry2 = new Button("TR");
            cherry2.setUserData(0);
            Button skip = new Button("Skip");
            skip.setUserData(0);
            Button synced = new Button("Synced");
            Button copy = new Button("Copy");
            Button show = new Button("Show");
            show.setDisable(true);  // disabled and enable only when synced.

            HBox cherryBox = new HBox();
            cherryBox.getChildren().addAll(cherry, cherry2);

            gp.add(date, 0, 0);
            GridPane.setColumnSpan(date, 7);
            gp.add(skip, 7, 0);
            gp.add(synced, 8, 0);
            gp.add(cherryBox, 9, 0);
            gp.add(copy, 10, 0);
            gp.add(show, 11, 0);

            gp.add(message, 0, 1);
            GridPane.setColumnSpan(message, 12);
            gp.add(author, 0, 2);
            GridPane.setColumnSpan(author, 12);

            vbPane.getChildren().add(gp);

            if (c.synced == Commit.Sync.YES) {
                synced.setStyle("-fx-background-color: green;");
//                synced.setDisable(true);
                skip.setDisable(true);
                cherry.setDisable(true);
                cherry2.setDisable(true);
                show.setDisable(false);
            }

            skip.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if (c.synced != Commit.Sync.NO || c.cherryPick != Commit.Status.NONE) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Info");
                        alert.setHeaderText("Existing Sync/Cherry-Pick status");
                        alert.setContentText("Please reset above status first");
                        alert.showAndWait();
                        return;
                    }
                    if (!c.skip) {
                        skip.setStyle("-fx-background-color: orange;");
                        c.skip = true;
                    } else {
                        skip.setStyle(null);
                        c.skip = false;
                    }
                }
            });

            synced.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (c.synced == Commit.Sync.YES)
                        return;
                    if (c.skip || c.cherryPick != Commit.Status.NONE) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Info");
                        alert.setHeaderText("Existing Skip/Cherry-Pick status");
                        alert.setContentText("Please reset above status first");
                        alert.showAndWait();
                        return;
                    }
                    if (c.synced == Commit.Sync.NO) {
                        synced.setStyle("-fx-background-color: orange;");
                        c.synced = Commit.Sync.MARK;
                    } else if (c.synced == Commit.Sync.MARK) {
                        synced.setStyle(null);
                        c.synced = Commit.Sync.NO;
                    }
                }
            });

            cherry.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    cherryPick(c, false, cherry, cherry2);

                }
            });

            cherry2.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    cherryPick(c, true, cherry, cherry2);
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
        }

    }

    void cherryPick(Commit c, boolean pressedCherry2, Button btnCherry, Button btnCherry2) {
        if (c.skip || c.synced != Commit.Sync.NO) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Info");
            alert.setHeaderText("Existing Skip/Synced status");
            alert.setContentText("Please reset above status first");
            alert.showAndWait();
            return;
        }

        if (ConnectSSHView.session == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Info");
            alert.setHeaderText("No SSH connection");
            alert.setContentText("Please connect to remote.");
            alert.showAndWait();
            return;
        }

        if (c.cherryPick == Commit.Status.NONE) {
            ConnectSSHView ssh = new ConnectSSHView();
            String repo = !pressedCherry2? KERNEL_DIR: TR_UTILS_DIR;
            String cmdCD = "cd "+repo+";";
            String cmdReset = "git reset --hard HEAD;";
            String cmdCheckout = "git checkout "+ (!pressedCherry2? KERNEL_SYNC: TR_UTILS_SYNC) +";";
            String cmdCherry = "git cherry-pick "+c.ID+";";
            String cmdAbort = "git cherry-pick --abort";

            if(sync_init) {
                System.out.println("Creating Sync branches");
                ssh.executeCommand("cd "+KERNEL_DIR+";"+cmdReset+"git checkout "+KERNEL_BASE+"; git checkout -b "+KERNEL_SYNC); // create branch
                ssh.executeCommand("cd "+KERNEL_DIR+";git checkout "+KERNEL_SYNC+"; git reset --hard "+KERNEL_BASE);    // update branch
                ssh.executeCommand("cd "+TR_UTILS_DIR+";"+cmdReset+"git checkout "+TR_UTILS_BASE+"; git checkout -b "+TR_UTILS_SYNC); // create branch
                ssh.executeCommand("cd "+TR_UTILS_DIR+";git checkout "+TR_UTILS_SYNC+"; git reset --hard "+TR_UTILS_BASE); // update branch
                sync_init = false;
                System.out.println("Sync branches ready.");
                System.out.println("In "+KERNEL_DIR+" module "+KERNEL_SYNC+" checkout from "+KERNEL_BASE);
                System.out.println("In "+TR_UTILS_DIR+" module "+TR_UTILS_SYNC+" checkout from "+TR_UTILS_BASE);
            }

            ssh.executeCommand(cmdCD+cmdReset+cmdCheckout);
            CommandStatus cmdStatus = ssh.executeCommand(cmdCD+cmdCherry);

            if (cherrySuccess(cmdStatus)) {
                if (!pressedCherry2) {
                    c.cherryPick = Commit.Status.SUCCESS;
                    btnCherry.setStyle("-fx-background-color: green;");
                } else {
                    c.cherryPick = Commit.Status.SUCCESS;
                    c.cherry2 = true;
                    btnCherry.setStyle("-fx-background-color: green;");
                    btnCherry2.setStyle("-fx-background-color: green;");
                }
            } else {
                ssh.executeCommand(cmdCD+cmdAbort);
                if (!pressedCherry2) {
                    c.cherryPick = Commit.Status.FAILED;
                    btnCherry.setStyle("-fx-background-color: red;");
                } else {
                    c.cherryPick = Commit.Status.FAILED;
                    c.cherry2 = true;
                    btnCherry.setStyle("-fx-background-color: red;");
                    btnCherry2.setStyle("-fx-background-color: red;");
                }
            }

        } else if (c.cherryPick == Commit.Status.SUCCESS || c.cherryPick == Commit.Status.MANUAL) {
            if (!c.cherry2) {   // kernel
                if (commitExist(c, KERNEL_DIR)) { // commit already exist
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Info");
                    alert.setHeaderText("Already cherry-picked");
                    alert.setContentText("Remove commit from "+KERNEL_DIR+" to change status");
                    alert.showAndWait();
                } else {
                    c.cherryPick = Commit.Status.NONE;
                    btnCherry.setStyle(null);
                }
            } else { // TR_Utils
                if (commitExist(c, TR_UTILS_DIR)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Info");
                    alert.setHeaderText("Already cherry-picked");
                    alert.setContentText("Remove commit from "+TR_UTILS_DIR+" to change status");
                    alert.showAndWait();
                } else {
                    c.cherryPick = Commit.Status.NONE;
                    c.cherry2 = false;
                    btnCherry.setStyle(null);
                    btnCherry2.setStyle(null);
                }
            }
        } else if (c.cherryPick == Commit.Status.FAILED) {
            if (!c.cherry2) {
                if (commitExist(c, KERNEL_DIR)) { // commit already exist
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Info");
                    alert.setHeaderText("Found manual cherry-pick in "+KERNEL_DIR);
                    alert.showAndWait();
                    c.cherryPick = Commit.Status.MANUAL;
                    btnCherry.setStyle("-fx-background-color: orange;");
                } else {
                    c.cherryPick = Commit.Status.NONE;
                    btnCherry.setStyle(null);
                }
            } else {
                if (commitExist(c, TR_UTILS_DIR)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Info");
                    alert.setHeaderText("Found manual cherry-pick in "+TR_UTILS_DIR);
                    alert.showAndWait();
                    c.cherryPick = Commit.Status.MANUAL;
                    btnCherry.setStyle("-fx-background-color: orange;");
                    btnCherry2.setStyle("-fx-background-color: orange;");
                } else {
                    c.cherryPick = Commit.Status.NONE;
                    c.cherry2 = false;
                    btnCherry.setStyle(null);
                    btnCherry2.setStyle(null);
                }
            }
        }
    }

    boolean commitExist(Commit c, String dir) {

        return false;
    }

    boolean cherrySuccess(CommandStatus cmdStatus) {
        if (cmdStatus.exitStatus == 0) { // success
            if (cmdStatus.output.contains("file changed,")) {
                return true;
            }
        } else if (cmdStatus.exitStatus == 1) {
            if (cmdStatus.output.contains("nothing to commit, working tree clean")) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Changes already exist");
                alert.setContentText("Please mark as synced.");
                alert.showAndWait();
            } else if (cmdStatus.output.contains("\nCONFLICT")) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("Merge Conflict");
                alert.setContentText("After resolving click button again to mark as manual cherry-pick");
                alert.showAndWait();
            }
        } else if (cmdStatus.exitStatus == 128) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Commit not found.");
            alert.showAndWait();
            return false;
        }
        return false;
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

            message.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    try {
                        // Open a URL in the default browser
                        String module = mm.moduleNo == 1?KERNEL_DIR: TR_UTILS_DIR;
                        Desktop.getDesktop().browse(new URI("https://github.ecodesamsung.com/TizenRT/"+module+"/commit/"+c.ID)); // Replace with your desired URL
                    } catch (IOException | java.net.URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}