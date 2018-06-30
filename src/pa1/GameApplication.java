package pa1;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.util.Duration;

import units.*;
import terrain.*;



public class GameApplication extends Application 
{
	// ============================
	// Section: JavaFX GUI Elements
	// ============================

	// Note: Please play the game in full-screen.
	// Resolution, Tiles and Offset for Rendering Unit ID on bottom-right.
	private static final int RESOLUTION_TOTAL_WIDTH = 800;
	private static final int RESOLUTION_TOTAL_HEIGHT = 600;
	private static final int RESOLUTION_GAMEPLAY_WIDTH = 640;
	private static final int RESOLUTION_GAMEPLAY_HEIGHT = 480;
	public static final int TILE_WIDTH = 32;
	public static final int TILE_HEIGHT = 32;
	private static final int UNIT_TEXT_WIDTH_OFFSET = 20;
	private static final int UNIT_TEXT_HEIGHT_OFFSET = 28;
	
	// Scene and Stage
	private static final int SCENE_NUM = 4;
	private static final int SCENE_WELCOME = 0;
	private static final int SCENE_STARTGAME = 1;
	private static final int SCENE_GAMEPLAY = 2;
	private static final int SCENE_GAMEOVER = 3;
	private static final String[] SCENE_TITLES = {"Welcome", "Start Game", "Game Play", "Game Over"};
	private Scene[] scenes = new Scene[SCENE_NUM];
	private Stage stage;
	
	// Part 1: paneWelcome
	private Label lbMenuTitle;
	private Button btNewGame, btBackgroundMusic, btQuit;

	// Part 2: paneGameStart
	private Canvas canvasGameStart;
	private Button btLoadTerrainMap, btLoadPlayersAndUnits, btStartGame, btQuitToMenu;
	private Label lbMapPosition, lbTileInfo, lbUnitDetails;
	private ListView<String> listViewUnit;
	private ObservableList<String> listViewUnitItems = FXCollections.observableArrayList();
	private String listViewSelectedUnit = "";
	private File selectedPlayersAndUnits;

	// Part 3: paneGamePlay
	private static final int NUM_LAYERS = 4;
	private Canvas[] canvasGamePlayLayers = new Canvas[NUM_LAYERS];
	private static final int TERRAIN_LAYER = 0;
	private static final int UNIT_LAYER = 1;
	private static final int TEXT_LAYER = 2;
	private static final int RANGE_INDICATOR_LAYER = 3;
	private static final int TOP_LAYER = NUM_LAYERS - 1;
	private Button btGamePlayQuitToMenu;
	private Label lbCurrentTurn, lbGamePlayInfo;
	private ListView<VBox> listGameUnit;
	private ObservableList<VBox> listViewGamePlayUnitInfoItems = FXCollections.observableArrayList();

	// Part 4: paneGameOver
	private Label lbGameOver;
	private Button btExitToMenu, btGameOverQuitGame;
	
	
	
	//==========================================
	// Section: Control and Rendering Attributes
	//==========================================
	
	// MediaPlayer and Background Music
	private MediaPlayer backgroundMusic;
	private boolean isBackgroundMusicEnabled = false;

	// GameEngine and GameMap
	public static GameEngine gameEngine = new GameEngine();
	public static GameMap gameMap = new GameMap();
	
	// Animation Thread Handles.
	private ArrayList<Thread> animThreads = new ArrayList<>();
	
	// GamePlay Global Variables.
	private Unit gamePlaySelectedUnit = null;
	private boolean isSelectedUnitAttackPhase = false;
	private static final Color UNIT_READY_TEXT_COLOR = Color.WHITE;
	private static final Color UNIT_DONE_TEXT_COLOR = Color.SILVER;
	private static final Color MOVEMENT_RANGE_INDICATOR_COLOR = Color.WHITE;
	private static final Color ATTACK_RANGE_INDICATOR_COLOR = Color.RED;
	
	
	
	// ===============================
	// Section: Content Panes Creation
	// ===============================
	// Hint: This section is similar to Lab 10.
	
	// TODO: Create a pane for scene "Welcome".
	// You need to have BorderPane, VBox, Label and 3 Buttons.
	private Pane paneWelcome() 
	{
		lbMenuTitle = new Label("Java Warfare Game");
		btNewGame = new Button("New Game");
		btQuit = new Button("Quit");
		btBackgroundMusic = new Button("Enable Background Music");

		lbMenuTitle.getStyleClass().add("menu-title");
		btNewGame.getStyleClass().add("menu-button");
		btQuit.getStyleClass().add("menu-button");
		btBackgroundMusic.getStyleClass().add("menu-button");

		VBox container = new VBox(20);
		container.getChildren().addAll(lbMenuTitle, btNewGame, btBackgroundMusic, btQuit);
		container.setAlignment(Pos.CENTER);

		BorderPane pane = new BorderPane();
		pane.setCenter(container);
		return pane;
	}

	// TODO: Create a pane for scene "GameStart".
	// You need to have a BorderPane with one VBox on the left and one VBox on the center.
	// In VBox on the left, 4 Buttons, 1 Label, 1 ListView<String> are required.
	// In VBox on the center, 1 Canvas is required.
	// Suggestion: Set the size of ListView to width:150 height:200.
	// Suggestion: Set the size of Canvas based on RESOLUTION_GAMEPLAY_WIDTH/HEIGHT.
	private Pane paneStartGame() 
	{
		canvasGameStart = new Canvas(150, 200);
		btLoadTerrainMap = new Button("Load Map");
		btLoadPlayersAndUnits = new Button("Load Units");
		btStartGame = new Button("Start Game");
		btQuitToMenu = new Button("Quit to Menu");
		listViewUnit = new ListView<String>();
		lbMapPosition = new Label("");
		lbTileInfo = new Label("");
		lbUnitDetails = new Label("");

		btLoadTerrainMap.getStyleClass().add("menu-button");
		btLoadPlayersAndUnits.getStyleClass().add("menu-button");
		btStartGame.getStyleClass().add("menu-button");
		btQuitToMenu.getStyleClass().add("menu-button");
		
		listViewUnit.setPrefSize(150, 200);
		listViewUnit.setItems(listViewUnitItems);
		
		VBox containerCanvas = new VBox(20);
		containerCanvas.getChildren().addAll(canvasGameStart);
		containerCanvas.setAlignment(Pos.CENTER);

		VBox containerCtrl = new VBox(20);
		containerCtrl.getChildren().addAll(btLoadTerrainMap, lbMapPosition, lbTileInfo, btLoadPlayersAndUnits, new Label("Units"),
				listViewUnit, lbUnitDetails, btStartGame, btQuitToMenu);
		containerCtrl.setAlignment(Pos.CENTER);
		containerCtrl.setPadding(new Insets(10, 10, 10, 10));

		BorderPane pane = new BorderPane();
		pane.setCenter(containerCanvas);
		pane.setLeft(containerCtrl);
		return pane;
		
	}
 
	// TODO: Create a pane for scene "GamePlay".
	// You need to have a BorderPane, StackPane, multiple Canvases, Label of Current Turn, Label of GamePlay Info and a Button.
	// Label of Current Turn is on top of the other Label, it shows the current turn (e.g. Red/Blue)
	// Label of GamePlayInfo shows the property of the selectedUnit.
	// The content of the Label will be updated later.
	// Set the content of the Label to an empty string for now.
	// -----
	// The Canvases will be stacked on top of each other. They will be used for rendering the GamePlay.
	// With separate layers, we can focus on only redrawing the things that changed. Don't need to redraw the whole GamePlay graphics.
	// StackPane is used to stack the Canvases on top of each other with stackPane.getChildren().add(canvas);
	// StackPane displays like a Stack, the canvas added last will be displayed on top.
	private Pane paneGamePlay() 
	{
		lbCurrentTurn = new Label("");
		lbGamePlayInfo = new Label("");
		btGamePlayQuitToMenu = new Button("Quit to Menu");
		VBox container = new VBox(20);
		container.setAlignment(Pos.CENTER);
		container.getChildren().addAll(lbCurrentTurn, lbGamePlayInfo, btGamePlayQuitToMenu);
		
		
		StackPane sPane = new StackPane();
		
		for ( int i = 0; i < 4; i++ ) {
			canvasGamePlayLayers[i] = new Canvas(canvasGameStart.getWidth(), canvasGameStart.getHeight());
			sPane.getChildren().add(canvasGamePlayLayers[i]);
		}
		listGameUnit = new ListView<VBox>();		
		listGameUnit.setPrefSize(100, 400);
		listGameUnit.setItems(listViewGamePlayUnitInfoItems);
		
		
		BorderPane bPane = new BorderPane();
		bPane.setCenter(sPane);
		bPane.setBottom(container);
		BorderPane.setAlignment(container, Pos.CENTER);
		bPane.setRight(listGameUnit);
		return bPane;
	}

	// TODO: Create a pane for scene "GameOver".
	// You need to have a BorderPane, VBox, 1 Label and 2 Buttons.
	// Initiate the Label with empty string (i.e. "") for this moment.
	// It is used to display who is the winner later.
	private Pane paneGameOver() 
	{
		lbGameOver = new Label("");
		btExitToMenu = new Button("Exit to Menu");
		btGameOverQuitGame = new Button("Quit Game");

		btGameOverQuitGame.getStyleClass().add("menu-title");
		btExitToMenu.getStyleClass().add("menu-title");

		VBox container = new VBox(20);
		container.getChildren().addAll(lbGameOver, btExitToMenu, btGameOverQuitGame);
		container.setAlignment(Pos.CENTER);

		BorderPane pane = new BorderPane();

		pane.setCenter(container);
		return pane;
	}
	
	
	
	// ======================================================
	// Section: Event Handlers (Naming convention: handle___)
	// ======================================================
	// Hint: This section is similar to Lab 11.
	
	private void handleBackgroundMusic()
	{
		if (!isBackgroundMusicEnabled) 
		{
			backgroundMusic.play();
			isBackgroundMusicEnabled = true;
			btBackgroundMusic.setText("Disable Background Music");
		} 
		
		else 
		{
			backgroundMusic.stop();
			isBackgroundMusicEnabled = false;
			btBackgroundMusic.setText("Enable Background Music");
		}
	}
	
	private void handleNewGame() 
	{
		putSceneOnStage(SCENE_STARTGAME);
	}
	
	// TODO: Show an Alert asking the User to confirm Exiting with YES/NO Buttons.
	// Call Platform.exit() to exit the game.
	private void handleExitGame()
	{
		  // Pop up an alert asking "Do you want to exit this game?"
		  Alert alert = new Alert(AlertType.CONFIRMATION, "Do you want to exit this game?", ButtonType.YES, ButtonType.NO);
		  // Show the alert until receiving a click on the buttons
		  alert.showAndWait();
		  // Exit the application if player clicks "Yes"
		  if (alert.getResult() == ButtonType.YES) {
		      Platform.exit();
		  }
	}
	
	// TODO: Load Terrain Map from chosen textfile.
	// Use a FileChooser for the User to select the textfile.
	// Render Terrain Map onto canvasGameStart.
	// Catch and display IOExceptions with showErrorDialog(). These IOExceptions no longer stop the game.
	// -----
	// If this method is called multiple times, unload the old Terrain Map then load the new one.
	// If Players and Units are loaded, reset the starting location of all Units, and also reset listViewUnitItems.
	// Also render the new Terrain Map.
	private void handleLoadMap() 
	{	
		// Setup the FileChooser similar to TODO2
		FileChooser fileChooser = new FileChooser();

		fileChooser.setTitle("Load Map");

		fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("Text Files", "*.txt"));
		
		File selectedFile = fileChooser.showOpenDialog(stage);

		if (selectedFile != null) {
			try {
				if (gameMap.isLoaded())
					gameMap.unloadTerrainMap();
				gameMap.loadTerrainMap(selectedFile);
			}
			catch (IOException e) {
				//e.getMessage();
				showErrorDialog(e.getMessage());
			}
		}
		
/*		if (gameEngine.isLoaded())
		{

			gameEngine.unloadPlayersAndUnits();
			
			
			try {
			gameEngine.loadPlayersAndUnits(selectedPlayersAndUnits);
			clearLayer(canvasGameStart);
			updateListViewUnitItems();
			}
			catch (IOException e) {
				showErrorDialog(e.getMessage());
			}
		}*/
		
		if(gameEngine.isLoaded()){
			for (Player player:gameEngine.getPlayers()){
				for(Unit unit:player.getUnits()){
					Terrain terrain = gameMap.getTerrainAtLocation(unit.getLocationX(), unit.getLocationY());
					terrain.unoccupy();
					renderTerrainTile(canvasGameStart, terrain, unit.getLocationX(), unit.getLocationY());
					unit.resetStartingLocation();
				}
			}
			updateListViewUnitItems();
		}
		//System.out.println("I'm ok");
		
		canvasGameStart.setWidth(gameMap.terrainMapToCanvasX(gameMap.getWidth()));
		canvasGameStart.setHeight(gameMap.terrainMapToCanvasX(gameMap.getHeight()));
		for ( int i = 0; i < 4; i++ ) {
			canvasGamePlayLayers[i].setWidth(gameMap.terrainMapToCanvasX(gameMap.getWidth()));
			canvasGamePlayLayers[i].setHeight(gameMap.terrainMapToCanvasX(gameMap.getHeight()));
		}
		
		
		renderTerrainMap(canvasGameStart);
		
		//System.out.println("hi");

	}
	
	// TODO: Load Players and Units from chosen textfile.
	// Use a FileChooser for the User to select the textfile.
	// Update the loaded Players and Units in listViewUnitItems. 
	// Catch and display IOExceptions with showErrorDialog(). These IOExceptions no longer stop the game.
	// -----
	// If this method is called multiple times, unload the old set of Players and Units then load the new set.
	// If any Units were placed onto the Terrain Map, remember to reset their starting locations and remove them from canvasGameStart rendering.
	private void handleLoadPlayersAndUnits() 
	{
		// 1) Create a FileChooser object
		FileChooser fileChooser = new FileChooser();

		// 2) Set the title of the FileChooser to "Load Unit" (use .setTitle(string))
		fileChooser.setTitle("Load Unit");
		// 3) Restrict the extension of the file to only .txt
		//    (use .getExtensionFilters() to get the list of extensions allow)
		//	  (use .addAll(ExtensionFilter) to add a new restriction)
		//    (put a new file ExtensionFilter by new FileChooser.ExtensionFilter("Text files", "*.txt"))
		fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("Text Files", "*.txt"));
		
		
		// 4) Get the File object from the FileChooser object
		//    (use FileChooser object.showOpenDialog(stage) which returns the file chosen or null if no file is chosen)
		selectedPlayersAndUnits = fileChooser.showOpenDialog(stage);



		// 5) If the file is not null, call loadPlayersAndUnits from GameEngine
		//    Update the method to read the new file format correctly
		if (selectedPlayersAndUnits != null) {
			try {
				if(gameEngine.isLoaded()){
					for (Player player:gameEngine.getPlayers()){
						for(Unit unit:player.getUnits()){
							if(unit.getLocationX() != -1 && unit.getLocationY() != -1){
								Terrain terrain = gameMap.getTerrainAtLocation(unit.getLocationX(), unit.getLocationY());
								terrain.unoccupy();
								renderTerrainTile(canvasGameStart, terrain, unit.getLocationX(), unit.getLocationY());
								renderTerrainTile(canvasGamePlayLayers[TERRAIN_LAYER], terrain, unit.getLocationX(), unit.getLocationY());
								unit.resetStartingLocation();
							}
						}
					}
					gameEngine.unloadPlayersAndUnits();
					listViewUnitItems.clear();
				}
				gameEngine.loadPlayersAndUnits(selectedPlayersAndUnits);
			}
			catch (IOException e) {
				showErrorDialog(e.getMessage());
			}
		}
		updateListViewUnitItems();
	}
	
	private void handleGameStartButton() 
	{
		if (!gameMap.isLoaded()) 
		{
			showErrorDialog("Game Map is not loaded.");
			return;
		}

		if (!gameEngine.isLoaded()) 
		{
			showErrorDialog("Players and Units are not loaded.");
			return;
		}

		if (listViewUnitItems.size() > 0) 
		{
			showErrorDialog("Please place all Units on the Terrain Map.");
			return;
		}
		
		clearLayer(canvasGameStart);
		lbMapPosition.setText("");
		
		updateCurrentTurnLabel();
		updateListViewGamePlayUnitInfo();
		renderInitGamePlayCanvas();
		
		putSceneOnStage(SCENE_GAMEPLAY);
	}
	
	private void handleCanvasGameStartMouseMovement(double canvasX, double canvasY)
	{
		if (gameMap.isLoaded())
		{
			lbMapPosition.setText(String.format("Map Position: %d %d", gameMap.canvasToTerrainMapX(canvasX), gameMap.canvasToTerrainMapY(canvasY)));
		}
	}
	
	// TODO: Place loaded Units from the listView onto the Terrain Map.
	private void handleCanvasGameStartMouseClick(double canvasX, double canvasY) 
	{
		if (gameMap.isLoaded()) 
		{
			if (!listViewSelectedUnit.equals("")) 
			{
				char unitId = listViewSelectedUnit.charAt(0); // Get the ID of selectedUnit.
				
				Unit selectedUnit = null;
				// ================================================
				// TODO: 1) Find the selectedUnit from the Players.
				for (Player p : gameEngine.getPlayers())
				{
					selectedUnit = p.getUnitById(unitId);
					if (selectedUnit != null)
						break;
				}
				// ================================================

				if (selectedUnit != null)
				{
					// terrainMapX and terrainMapY contain the coordinates of where the User clicked on the Terrain Map.
					int terrainMapX = gameMap.canvasToTerrainMapX(canvasX);
					int terrainMapY = gameMap.canvasToTerrainMapY(canvasY);
					
					// TODO 2): Check to make sure that the Terrain Map location is not blocked (by other Unit or by MOVEMENT_COST -1).
					if (gameMap.getTerrainAtLocation( terrainMapX, terrainMapY).isBlocked() == false) 
					{
						listViewUnit.getSelectionModel().clearSelection();
						listViewUnitItems.remove(listViewSelectedUnit);

						// TODO 3): Set the Starting Location of the Unit. Also render it and its Unit Text onto canvasGameStart.
						selectedUnit.setStartingLocation(terrainMapX, terrainMapY);
						renderUnit(canvasGameStart, selectedUnit);
						renderUnitText(canvasGameStart, selectedUnit);
						
						//renderUnit(canvasGamePlayLayers[UNIT_LAYER], selectedUnit);
						//renderUnitText(canvasGamePlayLayers[TEXT_LAYER], selectedUnit);
					} 
					
					else 
					{
						// TODO 4): Otherwise, showErrorDialog() that "Target Location is blocked."
						showErrorDialog("Target Location is blocked");
						return;
					}
				}
				// ================================================================

				listViewSelectedUnit = "";
			} 
		}
	}
	
	private void handleCanvasGamePlayMouseClick(double canvasX, double canvasY) 
	{
		int terrainX = gameMap.canvasToTerrainMapX(canvasX);
		int terrainY = gameMap.canvasToTerrainMapY(canvasY);
		
		if (gamePlaySelectedUnit == null) 
		{
			processGamePlayWithoutUnitSelected(terrainX, terrainY);
		} 
				
		else 
		{
			processGamePlayWithUnitSelected(terrainX, terrainY);
		}
	}
	
	
	
	// =======================================================================
	// Section: Event Handler Initializers (Naming convention: init___Handler)
	// =======================================================================
	// Note: This section is similar to Lab 11.

	private void initEventHandlers() 
	{
		initBackgroundMusicHandler();
		initWelcomeSceneHandler();
		initStartGameSceneHandler();
		initGamePlaySceneHandler();
		initGameOverHandler();
	}
	
	private void initBackgroundMusicHandler() 
	{
		//backgroundMusic = new MediaPlayer(new Media(new File("src/audio/landius.mp3").toURI().toString()));
		
		// From local path to URL:
		// Reference: https://stackoverflow.com/questions/34859603/mediaplayer-in-jar
		backgroundMusic = new MediaPlayer(new Media(getClass().getResource("/audio/landius.mp3").toExternalForm()));
		
		//backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE); // cycleCount is how many times to loop. However, internal bug with javafx, won't loop forever.
		
		// Instead, create a new thread to manually Loop from the beginning every time the music ends.
		backgroundMusic.setOnEndOfMedia(new Runnable()
		{
			public void run()
			{
				backgroundMusic.seek(Duration.ZERO); // Loop from the beginning.
			}
		});
	}
	
	// TODO: Connect the handle___() methods in scene "Welcome".
	// There are 3 Buttons in scene "Welcome".
	private void initWelcomeSceneHandler() 
	{
		btNewGame.setOnAction(e -> handleNewGame());
		btQuit.setOnAction(e -> handleExitGame());
		btBackgroundMusic.setOnAction(e -> handleBackgroundMusic());
	}

	private void initStartGameSceneHandler() 
	{
		listViewUnit.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() 
		{
			public void changed(ObservableValue<? extends String> ov, String old_val, String new_val) 
			{
				if (new_val != null) 
				{
					listViewSelectedUnit = new_val;
				}
			}
		});
		
		// ==============================
		// TODO: Connect the handle___() methods to "Load Terrain Map", "Load Players and Units", and "Start Game" Buttons.
		// Also connect the handle___() method to canvasGameStart, which places Units onto the Terrain Map when the User Mouse-Clicks.
		// "Quit to Menu" Button handle___() method as well as canvasGameStart.setOnMouseMoved() has already been setup as a code reference.
		btLoadTerrainMap.setOnAction(e -> handleLoadMap());
		btLoadPlayersAndUnits.setOnAction(e -> handleLoadPlayersAndUnits());
		btStartGame.setOnAction(e -> handleGameStartButton());
		canvasGameStart.setOnMouseClicked(e -> handleCanvasGameStartMouseClick(e.getX(), e.getY()));
		
		canvasGameStart.setOnMouseMoved(e -> handleCanvasGameStartMouseMovement(e.getX(), e.getY()));
			
		btQuitToMenu.setOnAction(e ->
		{
			gameEngine.unloadPlayersAndUnits();
			gameMap.unloadTerrainMap();
			clearLayer(canvasGameStart);
			lbMapPosition.setText("");
			listViewUnitItems.clear();
			putSceneOnStage(SCENE_WELCOME);
		});
		// ===================================================
	}
	
	// TODO: Connect the handle___() methods to scene "GamePlay".
	// Use the Top Layer of canvasGamePlayLayers to setup onMouseClicked() with its handle___() method.
	// Remember to call stopGamePlay() when clicked on "Quit To Menu" Button before going back to scene "Welcome".
	private void initGamePlaySceneHandler()
	{
		btGamePlayQuitToMenu.setOnAction(e -> {stopGamePlay(); 			
												//gameEngine.unloadPlayersAndUnits();
												//gameMap.unloadTerrainMap();
												//clearLayer(canvasGameStart);
												//lbMapPosition.setText("");
												//listViewUnitItems.clear();
												putSceneOnStage(SCENE_WELCOME);
												});
		canvasGamePlayLayers[TOP_LAYER].setOnMouseClicked(e -> handleCanvasGamePlayMouseClick(e.getX(), e.getY()));
		
	}
	
	// TODO: Connect the handle___() methods to scene "GameOver".
	// There are 2 Buttons, "Exit to Menu" and "Quit Game".
	private void initGameOverHandler()
	{
		btExitToMenu.setOnAction(e -> {stopGamePlay(); 			
										//gameEngine.unloadPlayersAndUnits();
										//gameMap.unloadTerrainMap();
										//clearLayer(canvasGameStart);
										//lbMapPosition.setText("");
										//listViewUnitItems.clear();
										putSceneOnStage(SCENE_WELCOME);});
		btGameOverQuitGame.setOnAction(e -> handleExitGame());
	}

	
	
	// ====================================
	// Section: Text and UI-Related Methods
	// ====================================
	
	// Shows an Error Message as a GUI pop-up.
	private void showErrorDialog(String errorMessage) 
	{
		Alert alert = new Alert(AlertType.ERROR, errorMessage);
		alert.showAndWait();
	}
	
	// Update the Current Turn Label to show which Player's turn is it right now.
	private void updateCurrentTurnLabel() 
	{
		lbCurrentTurn.setText(gameEngine.getCurrentPlayer().getName() + "'s Turn");
	}
	
	// Update the Game Over Label to show the Winner (or a Draw).
	private void updateGameOverLabel() 
	{
		for (Player player:gameEngine.getPlayers()) 
		{
			if (player.hasUnitsRemaining()) 
			{
				lbGameOver.setText("Game Over: " + player.getName() + " Wins!");
				return;
			}
		}
		
		lbGameOver.setText("Game Over: All Players Eliminated! Draw!");
	}
	
	// Updates listViewUnitItems in GameStart.
	private void updateListViewUnitItems() 
	{
		listViewUnitItems.clear();
		for (Player player:gameEngine.getPlayers()) 
		{
			for (Unit unit:player.getUnits()) 
			{
				if (unit instanceof Archer) 
				{
					listViewUnitItems.add(unit.getId() + ": " + player.getName() + "'s " + "Archer");
				} 
				
				else if (unit instanceof Cavalry) 
				{
					listViewUnitItems.add(unit.getId() + ": " + player.getName() + "'s " + "Cavalry");
				} 
				
				else if (unit instanceof Infantry) 
				{
					listViewUnitItems.add(unit.getId() + ": " + player.getName() + "'s " + "Infantry");
				} 
				
				else if (unit instanceof Pikeman) 
				{
					listViewUnitItems.add(unit.getId() + ": " + player.getName() + "'s " + "Pikeman");
				}
			}
		}
	}	

	// Updates listViewGamePlayUnitInfoItems in GamePlay.
	// Also shows the Heal Button for each Ready Unit of the current Player.
	private void updateListViewGamePlayUnitInfo() 
	{
		listViewGamePlayUnitInfoItems.clear();
		Player currentPlayer = gameEngine.getCurrentPlayer();
		
		for (Unit unit:currentPlayer.getUnits()) 
		{
			if (unit.isReady() && unit.isAlive()) 
			{
				Button btHeal = new Button("Heal");
				btHeal.setOnAction((event) -> 
				{
					// Only process the Heal button when not in-between the Selected Unit's Movement/Attack Phase.
					if (gamePlaySelectedUnit == null)
					{
						unit.heal(); 
						unit.endTurn();
						renderUnitText(canvasGamePlayLayers[TEXT_LAYER], unit);
						updateListViewGamePlayUnitInfo();
						checkPlayerTurn();
					}
				});
				
				VBox newItem = new VBox(new Label(unit.getId() + ""), new Label("Health: " + unit.getHealth()), btHeal);
				listViewGamePlayUnitInfoItems.add(newItem);
			}
		}
	}
	
	
	
	// ==================================
	// Section: Rendering-Related Methods
	// ==================================
	
	// Renders "image" at position "(terrainMapX, terrainMapY)" on canvas "layer".
	private void renderImageTile(Canvas layer, Image image, int terrainMapX, int terrainMapY)
	{
		layer.getGraphicsContext2D().drawImage(image, gameMap.terrainMapToCanvasX(terrainMapX), gameMap.terrainMapToCanvasY(terrainMapY));
	}
	
	// Clears the rendering at position "(terrainMapX, terrainMapY)" on canvas "layer". 
	// Cleared rendering becomes transparent, so layers at the bottom can be seen.
	private void clearTile(Canvas layer, int terrainMapX, int terrainMapY)
	{
		layer.getGraphicsContext2D().clearRect(gameMap.terrainMapToCanvasX(terrainMapX), gameMap.terrainMapToCanvasY(terrainMapY), TILE_WIDTH, TILE_HEIGHT);
	}
	
	// Clears the whole layer to become transparent.
	private void clearLayer(Canvas layer)
	{
		layer.getGraphicsContext2D().clearRect(0, 0, layer.getWidth(), layer.getHeight());
	}
	
	// TODO: Render the Terrain Image of "terrain" onto "layer" at position "(terrainMapX, terrainMapY)".
	// You may call renderImageTile().
	private void renderTerrainTile(Canvas layer, Terrain terrain, int terrainMapX, int terrainMapY)
	{
		renderImageTile(layer, terrain.getImage(), terrainMapX, terrainMapY);
	}
	
	// TODO: Render the whole Terrain Map onto the given canvas layer.
	// You may call renderTerrainTile().
	private void renderTerrainMap(Canvas layer)
	{
		for (int y = 0; y < gameMap.getHeight() ; y++) {
			for (int x = 0; x < gameMap.getWidth()  ; x++) {
				renderTerrainTile(layer, gameMap.getTerrainAtLocation(x, y), x, y);
			}
		}
	}
	
	// TODO: Render the Unit onto the given canvas layer. Hint: Unit keeps track of its location.
	// You may call renderImageTile().
	private void renderUnit(Canvas layer, Unit unit)
	{
		renderImageTile(layer, unit.getImage(), unit.getLocationX(), unit.getLocationY());
	}
	
	// TODO: Render the Unit ID of the given Unit onto the given canvas layer.
	// Use UNIT_READY_TEXT_COLOR and UNIT_DONE_TEXT_COLOR for Ready and Done Unit respectively.
	// Use setStroke(color), then strokeText() to draw the text.
	// Add UNIT_TEXT_WIDTH_OFFSET and UNIT_TEXT_HEIGHT_OFFSET to render the text at bottom-right corner of the Unit.
	private void renderUnitText(Canvas layer, Unit unit)
	{
		if(unit.isAlive()){
			if(unit.isReady()){
				layer.getGraphicsContext2D().setStroke(UNIT_READY_TEXT_COLOR);
			}else{ 
				layer.getGraphicsContext2D().setStroke(UNIT_DONE_TEXT_COLOR);
			}
			layer.getGraphicsContext2D().strokeText(String.valueOf(unit.getId()), gameMap.terrainMapToCanvasX(unit.getLocationX())+UNIT_TEXT_WIDTH_OFFSET, gameMap.terrainMapToCanvasY(unit.getLocationY())+UNIT_TEXT_HEIGHT_OFFSET);
		}

	}
	
	// TODO: Render the Movement Range and Attack Range Indicator Tiles.
	// rangeMap is centered on the Unit's Position. Convert to terrainMap coordinates with attackMapToTerrainMap() and movementMapToTerrainMap() in Unit.
	// No need to check if terrainMap coordinates out-of-range, already handled by GameMap.
	// rangeMap is true if that Terrain Tile is within range, false otherwise.
	// -----
	// Use setFill() to set the Indicator Tile fill color, and fillRect() to draw the Indicator Tile.
	// Use setStroke() to set the Indicator Tile solid outline color, and strokeRect() to draw the Indicator Tile solid outline.
	// Remember to call fillRect() before strokeRect(), so that the solid outline is drawn on top of the filled Indicator Tile.
	// Hint: For transparency effect, use Color.color(red, green, blue, transparency). [0, 1.0], zero is fully transparent, 1.0 is fully opaque/solid.
	// Hint: renderColor has getRed(), getGreen(), getBlue() methods.
	private void renderRangeIndicator(Canvas layer, Unit unit, boolean[][] rangeMap, Color renderColor)
	{
		GraphicsContext gc =layer.getGraphicsContext2D();
		gc.setFill(Color.color(renderColor.getRed(),renderColor.getGreen(),renderColor.getBlue(),0.4));
		gc.setStroke(Color.color(renderColor.getRed(),renderColor.getGreen(),renderColor.getBlue(),0.75));
		
/*		for (int x = 0 ; x < rangeMap.length ; x ++)
			for (int y = 0 ; y < rangeMap[x].length; y ++) {
				if (rangeMap[x][y] == true) {
					gc.setFill(Color.color(renderColor.getRed(),renderColor.getGreen(),renderColor.getBlue(),0.4));
					gc.setStroke(Color.color(renderColor.getRed(),renderColor.getGreen(),renderColor.getBlue(),0.75));
					
					//gc.fillRect(gameMap.terrainMapToCanvasX( unit.attackMapToTerrainMapX(x)) - GameApplication.TILE_WIDTH, gameMap.terrainMapToCanvasY( unit.attackMapToTerrainMapX(y)) -GameApplication.TILE_HEIGHT, GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT);
					//gc.strokeRect(unit.attackMapToTerrainMapX(x) - GameApplication.TILE_WIDTH, unit.attackMapToTerrainMapX(y) -GameApplication.TILE_HEIGHT, GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT);
					gc.fillRect(gameMap.terrainMapToCanvasX(unit.getLocationX()) - gameMap.terrainMapToCanvasX((unit.getAttackRange() - x)) , gameMap.terrainMapToCanvasY(unit.getLocationY() - (unit.getAttackRange() - y)) , GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT);
					gc.strokeRect(gameMap.terrainMapToCanvasX(unit.getLocationX() - (unit.getAttackRange() - x)) , gameMap.terrainMapToCanvasY(unit.getLocationY() - (unit.getAttackRange() - y)) , GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT);
					
				}
			}*/
		
		int terrainMapX = 0;
		int terrainMapY = 0;
		for(int row = 0; row < rangeMap.length; row++){
			for(int col = 0; col < rangeMap[0].length; col++){
				boolean flag = rangeMap[row][col];
				if(flag){
					if(renderColor == MOVEMENT_RANGE_INDICATOR_COLOR){
						terrainMapX = unit.movementMapToTerrainMapX(col);
						terrainMapY = unit.movementMapToTerrainMapY(row);
					}else if(renderColor == ATTACK_RANGE_INDICATOR_COLOR){
						terrainMapX = unit.attackMapToTerrainMapX(col);
						terrainMapY = unit.attackMapToTerrainMapY(row);
					}
					gc.fillRect(gameMap.terrainMapToCanvasX(terrainMapX), gameMap.terrainMapToCanvasY(terrainMapY), TILE_WIDTH, TILE_HEIGHT);
					gc.strokeRect(gameMap.terrainMapToCanvasX(terrainMapX), gameMap.terrainMapToCanvasY(terrainMapY), TILE_WIDTH, TILE_HEIGHT);
				}
			}
		}

		
		
		
		
		
	}
	
	// TODO: Render the starting Terrain Map, Units, Unit ID at the beginning of GamePlay.
	// Also start the Water Tile Animations.
	// Remember to render to the correct layer in canvasGamePlayLayers.
	private void renderInitGamePlayCanvas() 
	{
		renderTerrainMap(canvasGamePlayLayers[TERRAIN_LAYER]);
		for (Player player:gameEngine.getPlayers()){
			for(Unit unit:player.getUnits()){
				renderUnit(canvasGamePlayLayers[UNIT_LAYER], unit);
				renderUnitText(canvasGamePlayLayers[TEXT_LAYER], unit);
			}
		}
		animateWaterTiles(canvasGamePlayLayers[TERRAIN_LAYER]);
	}
	
	// TODO: Create a new Thread for each Water Tile to manage the Water Animations.
	// You may use renderImageTile().
	// Remember to store each Thread in animThreads, so that we have a way to shutdown the Threads at the end of the game.
	// Although it is not necessary, you may create your own helper methods/classes for this.
	// Hint: Refer to Lab 12. Especially, remember to use Platform.runLater(). Otherwise, JavaFX will crash.
	// -----
	// Hint: A workaround for Compile Error "Local variable defined in an enclosing scope must be final or effectively final".
	// Create a final copy of the local variable in one scope-level outside.
	// Example:
	// for (int x = 0; x < gameMap.getWidth(); x++)
	// {
	// 		final int TERRAIN_MAP_X = x;
	//
	//		Thread waterAnimThread = new Thread(new Runnable()
	//		{
	//			private final int LOCATION_X = TERRAIN_MAP_X; // Using "LOCATION_X = x;" here will trigger the Compile Error mentioned above.
	//
	//			@Override
	//			public void run()
	//			{
	//				// Animation code here, refer to Lab 12.
	//			}
	//		});
	//		waterAnimThread.start();
	// }
	private void animateWaterTiles(Canvas layer)
	{
		
/*			//while (true) {
				//final int index = i ;
				for (int x = 0 ; x < gameMap.getWidth() ; x ++) {
					final int TERRAIN_MAP_X = x;
					for (int y = 0; y < gameMap.getHeight() ; y ++) {
						final int TERRAIN_MAP_Y = y;
						if (gameMap.getTerrainAtLocation(x, y) instanceof Water) {
							Runnable thread = new Thread(() -> {clearTile(layer, TERRAIN_MAP_X, TERRAIN_MAP_Y); renderImageTile(layer, ((Water)gameMap.getTerrainAtLocation(TERRAIN_MAP_X, TERRAIN_MAP_Y)).getAnimFrame(index),TERRAIN_MAP_X, TERRAIN_MAP_Y);  });
							
							//animThreads.add(thread);
							//animThreads.add(Platform.runLater(thread));
							
						}
					}
					

				//}
				//Platform.runLater(() -> {
				//});
			}*/
		for(int height = 0; height < gameMap.getHeight(); height++){
			for(int width = 0; width < gameMap.getWidth(); width++){
				if(gameMap.getTerrainAtLocation(width, height) instanceof Water){
					final int TERRAIN_MAP_X = width;
					final int TERRAIN_MAP_Y = height;
					
					Thread waterAnimThread = new Thread(new Runnable(){
						private final int x = TERRAIN_MAP_X;
						private final int y = TERRAIN_MAP_Y;
						private final Water water = (Water)gameMap.getTerrainAtLocation(x, y);
						private int number = 0;
						
						@Override
						public void run(){
							try{
								while(true){
									Platform.runLater(() -> renderImageTile(layer, water.getAnimFrame(number), x, y));
									++number;
									number %= 4;
									Thread.sleep(Water.ANIM_TIME_PER_FRAME);
								}
							}catch(InterruptedException ex){}
						}
					});
					animThreads.add(waterAnimThread);
					waterAnimThread.start();
				}
			}
		}
		 
		
		
		

	}
	
	
	
	// =======================================================================
	// Section: Gameplay-related Methods (Mostly cal======ls to GameEngine and/or GameMap)
	// =============================================================================
	
	private void processGamePlayWithoutUnitSelected(int terrainMapX, int terrainMapY) 
	{
		if (gameMap.getTerrainAtLocation(terrainMapX, terrainMapY).isOccupied())
		{
			Unit clickedUnit = gameMap.getTerrainAtLocation(terrainMapX, terrainMapY).getOccupyingUnit();
			lbGamePlayInfo.setText(clickedUnit.toString());
			
			// Check if this Unit can be selected for Movement. If yes, then checkPathfinding and renderMovementRangeIndicator.
			if (gameEngine.getCurrentPlayer().hasUnitWithId(clickedUnit.getId()) && clickedUnit.isReady())
			{
				gamePlaySelectedUnit = clickedUnit;
				gameMap.checkPathfinding(gamePlaySelectedUnit); 
				renderRangeIndicator(canvasGamePlayLayers[RANGE_INDICATOR_LAYER], gamePlaySelectedUnit, gamePlaySelectedUnit.getMovementMap(), MOVEMENT_RANGE_INDICATOR_COLOR);
			}
		}
	}

	private void processGamePlayWithUnitSelected(int terrainMapX, int terrainMapY) 
	{
		clearLayer(canvasGamePlayLayers[RANGE_INDICATOR_LAYER]); // Always clear previous Range Indicators first.
		
		// Attack Phase.
		if (isSelectedUnitAttackPhase)
		{
			int attackMapX = gamePlaySelectedUnit.terrainMapToAttackMapX(terrainMapX);
			int attackMapY = gamePlaySelectedUnit.terrainMapToAttackMapY(terrainMapY);
			boolean[][] attackMap = gamePlaySelectedUnit.getAttackMap();
			
			// If user clicked an invalid Attack Target, skip this part and assume just End Turn without Attacking.
			if ((((attackMapX >= 0) && (attackMapY >= 0)) && ((attackMapX < attackMap.length) && (attackMapY < attackMap.length))) 
					&& (attackMap[attackMapY][attackMapX] == true))
			{
				Terrain targetTile = gameMap.getTerrainAtLocation(terrainMapX, terrainMapY);
				
				if (targetTile.isOccupied())
				{
					Unit targetUnit = targetTile.getOccupyingUnit();
					
					if (gameEngine.isEnemyUnit(targetUnit))
					{
						gamePlaySelectedUnit.attackUnit(targetUnit);
						
						if (!gamePlaySelectedUnit.isAlive())
						{
							clearTile(canvasGamePlayLayers[UNIT_LAYER], gamePlaySelectedUnit.getLocationX(), gamePlaySelectedUnit.getLocationY());
							clearTile(canvasGamePlayLayers[TEXT_LAYER], gamePlaySelectedUnit.getLocationX(), gamePlaySelectedUnit.getLocationY());
						}
						
						if (!targetUnit.isAlive())
						{
							clearTile(canvasGamePlayLayers[UNIT_LAYER], targetUnit.getLocationX(), targetUnit.getLocationY());
							clearTile(canvasGamePlayLayers[TEXT_LAYER], targetUnit.getLocationX(), targetUnit.getLocationY());
						}
					}
				}
			}
			
			if (gamePlaySelectedUnit.isAlive())
			{
				gamePlaySelectedUnit.endTurn();
				renderUnitText(canvasGamePlayLayers[TEXT_LAYER], gamePlaySelectedUnit);
			}
			
			isSelectedUnitAttackPhase = false;
			updateListViewGamePlayUnitInfo();
			lbGamePlayInfo.setText(gamePlaySelectedUnit.toString());
			gamePlaySelectedUnit = null;
			
			checkPlayerTurn();
		}

		// Movement Phase.
		else
		{			
			int movementMapX = gamePlaySelectedUnit.terrainMapToMovementMapX(terrainMapX);
			int movementMapY = gamePlaySelectedUnit.terrainMapToMovementMapY(terrainMapY);
			boolean[][] movementMap = gamePlaySelectedUnit.getMovementMap();
			
			if ((((movementMapX >= 0) && (movementMapY >= 0)) && ((movementMapX < movementMap.length) && (movementMapY < movementMap.length)))
					&& (movementMap[movementMapY][movementMapX] == true))
			{
				int unitPreviousLocationX = gamePlaySelectedUnit.getLocationX();
				int unitPreviousLocationY = gamePlaySelectedUnit.getLocationY();
				
				gamePlaySelectedUnit.move(terrainMapX, terrainMapY);
				
				clearTile(canvasGamePlayLayers[UNIT_LAYER], unitPreviousLocationX, unitPreviousLocationY);
				clearTile(canvasGamePlayLayers[TEXT_LAYER], unitPreviousLocationX, unitPreviousLocationY);
				renderUnit(canvasGamePlayLayers[UNIT_LAYER], gamePlaySelectedUnit);	
				renderUnitText(canvasGamePlayLayers[TEXT_LAYER], gamePlaySelectedUnit);
				
				if (gameEngine.isEnemyUnitInRange(gamePlaySelectedUnit))
				{
					isSelectedUnitAttackPhase = true;
					renderRangeIndicator(canvasGamePlayLayers[RANGE_INDICATOR_LAYER], gamePlaySelectedUnit, gamePlaySelectedUnit.getAttackMap(), ATTACK_RANGE_INDICATOR_COLOR);
				}
				else
				{
					gamePlaySelectedUnit.endTurn();
					updateListViewGamePlayUnitInfo();
					renderUnitText(canvasGamePlayLayers[TEXT_LAYER], gamePlaySelectedUnit);
					lbGamePlayInfo.setText(gamePlaySelectedUnit.toString());
					gamePlaySelectedUnit = null;
	
					checkPlayerTurn();
				}
			}
			
			else
			{
				lbGamePlayInfo.setText(gamePlaySelectedUnit.toString());
				gamePlaySelectedUnit = null; // Deselect the Unit if clicked on a Tile that can't Move to.
			}
		}
	}
	
	private void checkPlayerTurn()
	{
		if (gameEngine.isGameOver()) 
		{
			// Find out who is the Winner.
			updateGameOverLabel();
			
			// Stop GamePlay and cleanup.
			stopGamePlay();
			
			// Move to the Game Over screen.
			putSceneOnStage(SCENE_GAMEOVER);
			
			return;
		}
		
		if (!gameEngine.getCurrentPlayer().hasReadyUnits()) 
		{
			gameEngine.nextPlayerTurn();
			updateCurrentTurnLabel();
			updateListViewGamePlayUnitInfo();
			
			for (Unit unit:gameEngine.getCurrentPlayer().getUnits())
			{
				if (unit.isAlive() && unit.isReady())
				{
					renderUnitText(canvasGamePlayLayers[TEXT_LAYER], unit);
				}
			}
			
			return;
		}
	}
	
	private void stopGamePlay()
	{
		//System.out.println("Stopping Gameplay...");
		
		// Stop all Animation Threads.
		for (Thread animThread:animThreads)
		{
			animThread.interrupt();
		}
		animThreads.clear();
		
		// Clear all the Canvases.
		canvasGameStart.getGraphicsContext2D().clearRect(0, 0, canvasGameStart.getWidth(), canvasGameStart.getHeight());
		for (int i = 0; i < NUM_LAYERS; i++)
		{
			canvasGamePlayLayers[i].getGraphicsContext2D().clearRect(0, 0, canvasGamePlayLayers[i].getWidth(), canvasGamePlayLayers[i].getHeight());
		}
		
		// Clear GamePlay Info label.
		lbGamePlayInfo.setText("");
		
		// Clear GameEngine and GameMap.
		gameEngine.unloadPlayersAndUnits();
		gameMap.unloadTerrainMap();
	}
	
	
	
	//===========================================
	// Section: JavaFX Application Initialization
	//===========================================
	// Note: launch(args) will call init() and start(). Platform.exit() will call stop().
	
	private void initScenes() 
	{
		scenes[SCENE_WELCOME] = new Scene(paneWelcome(), 400, 500);
		scenes[SCENE_STARTGAME] = new Scene(paneStartGame(), RESOLUTION_TOTAL_WIDTH, RESOLUTION_TOTAL_HEIGHT);
		scenes[SCENE_GAMEPLAY] = new Scene(paneGamePlay(), RESOLUTION_TOTAL_WIDTH, RESOLUTION_TOTAL_HEIGHT);
		scenes[SCENE_GAMEOVER] = new Scene(paneGameOver(), RESOLUTION_TOTAL_WIDTH, RESOLUTION_TOTAL_HEIGHT);

		for (int i = 0; i < SCENE_NUM; i++)
		{
			scenes[i].getStylesheets().add("menu_and_css/styles.css"); // share stylesheet for all scenes
		}
	}

	private void putSceneOnStage(int sceneID) 
	{
		// ensure the sceneID is valid
		if (sceneID < 0 || sceneID >= SCENE_NUM)
		{
			return;
		}

		stage.hide();
		stage.setTitle(SCENE_TITLES[sceneID]);
		stage.setScene(scenes[sceneID]);
		stage.show();
	}

	@Override
	public void init()
	{
		initScenes();
		initEventHandlers();
	}
	
	@Override
	public void start(Stage primaryStage)
	{
		stage = primaryStage;
		putSceneOnStage(SCENE_WELCOME);
	}

	@Override
	public void stop()
	{
		if (isBackgroundMusicEnabled)
		{
			backgroundMusic.stop();
		}
	}
	
	public static void main(String[] args) 
	{
		launch(args);
	}
}
