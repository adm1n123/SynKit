package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class Main extends Application {
	static VBox root;
	Scene scene;
	@Override
	public void start(Stage primaryStage) {
		try {
			root = new VBox();
			scene = new Scene(root,1400,900);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			scene.getStylesheets().add(getClass().getResource("dark-theme.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setTitle("SynKit");
			load();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
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
        leftTabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        rightTabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        
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
        MenuItem importMenuItem = new MenuItem("Import");
        MenuItem exportMenuItem = new MenuItem("Export");
        CheckMenuItem theme = new CheckMenuItem("Light");
        fileMenu.getItems().addAll(newMenuItem, importMenuItem, exportMenuItem, theme);
        
        theme.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                scene.getStylesheets().remove(getClass().getResource("dark-theme.css").toExternalForm());
            } else {
                scene.getStylesheets().add(getClass().getResource("dark-theme.css").toExternalForm());
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
}
