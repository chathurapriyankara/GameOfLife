package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class Main extends Application {
	private static final int CELL_SIZE = 10;
	private static final int HEIGHT = 1000;
	private static final int WIDTH = 1300;
	private int[][] board = new int[50][50];
	private boolean isStopped = false;

	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane mainContainer = new BorderPane();
			HBox buttonContainer = new HBox();
			StackPane stackPane = new StackPane();
			ScrollPane scrollPane = new ScrollPane();
			Canvas canvas = new Canvas();
			Button btnStart = new Button("Start");
			Button btnStop = new Button("Stop");
			btnStart.setOnMouseClicked(e -> {
				isStopped = false;
				startGamePlay(new GameController(), canvas);
			});
			btnStop.setOnMouseClicked(e -> {
				isStopped = true;
			});
			canvas.setOnMouseClicked(e -> {
				addLiveCellOnClick(e.getX(), e.getY(), canvas, board);
			});
			buttonContainer.setPadding(new Insets(10, 10, 10, 10));
			buttonContainer.setSpacing(10);
			buttonContainer.setAlignment(Pos.CENTER);
			buttonContainer.getChildren().add(btnStart);
			buttonContainer.getChildren().add(btnStop);
			scrollPane.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
			scrollPane.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
			stackPane.getChildren().add(canvas);
			scrollPane.setContent(stackPane);
			scrollPane.setFitToWidth(true);
			scrollPane.setFitToHeight(true);
			buttonContainer.toFront();

			mainContainer.setTop(buttonContainer);
			mainContainer.setCenter(scrollPane);

			Scene scene = new Scene(mainContainer, WIDTH, HEIGHT);
			primaryStage.setScene(scene);
			primaryStage.show();
			drawBoard(board, canvas);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//SINCE THE CANVAS CAN GROW INFINITELY THIS METHOD WILL GENERATE AN ERROR WHEN IT TRIES TO EXCEED THE MAXIMUM TEXTURE SIZE SUPPORTED BY THE GRAPHICS HARDWARE IN YOUR COMPUTER!!!!
	//You can run this method without any problem until around 10 million pixels (this is really a large value).
	//If you really want to exceed that limit, add the following arguments under VM arguments in Eclipse to bypass the video card and to increase the heap size
	//-Dprism.order=sw
	//-Xmx5096m
	private void drawBoard(int[][] board, Canvas canvas) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		canvas.setWidth((board[0].length) * CELL_SIZE);
		canvas.setHeight((board.length) * CELL_SIZE);
		//Start drawing rectangle from 1,1, not from 0,0
		for (int r = 1; r < board.length - 1; r++) {
			for (int c = 1; c < board[0].length - 1; c++) {
				if (board[r][c] == 1) {
					gc.setFill(Color.DARKGREEN);
				} else {
					gc.setFill(Color.BLACK);
				}
				gc.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
			}
		}
	}

	private void addLiveCellOnClick(double x, double y, Canvas canvas, int[][] board) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		int xPos = (int) (x / CELL_SIZE);
		int yPos = (int) (y / CELL_SIZE);
		if (yPos != 0 && xPos != 0 && yPos != board.length - 1 && xPos != board[0].length - 1) {
			if (board[yPos][xPos] == 0) {
				gc.setFill(Color.DARKGREEN);
				board[yPos][xPos] = 1;
			} else {
				gc.setFill(Color.BLACK);
				board[yPos][xPos] = 0;
			}
			gc.fillRect(xPos * CELL_SIZE, yPos * CELL_SIZE, CELL_SIZE, CELL_SIZE);
		}
	}

	//Builds the animation using a thread.
	private void startGamePlay(GameController controller, Canvas canvas) {
		Task<Void> task = new Task<>() {
			@Override
			public Void call() throws Exception {
				while (!isStopped) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							board = controller.runGame(board);
							drawBoard(board, canvas);
						}
					});
					Thread.sleep(200);
				}
				return null;
			}
		};
		new Thread(task).start();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
