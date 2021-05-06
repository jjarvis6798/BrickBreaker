//	JarvisJoshBB.java
// 	Josh Jarvis
// 	Assignment #8
// 	05/04/2021
// 	Ball breaker Game

import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import java.util.Random;

public class JarvisJoshBB extends Application
{
    @FXML
    private Circle ball;

    @FXML
    private Rectangle paddle;

    @FXML
    private Pane pane;

    @FXML
    private Rectangle[][] brick = new Rectangle[4][11];

    @FXML
    private Label lblLives;

    @FXML
    private Label lblScore;

    @FXML
    private Label lblLoser;

    private int lives = 3;
    private int numBricks = 0;
    private int score = 0;
    private double speedMod = 1;

    public void initialize()
    {
		Random rand = new Random();
		int color;

		for(int i = 0; i < 4; i++)
		{
			for(int j = 0; j < 11; j++)
			{
				Rectangle r = new Rectangle();
				r.setWidth(50);
				r.setHeight(20);
				r.setLayoutX(10 + (50 * j));
				r.setLayoutY(5 + (20 * i));
				r.setVisible(true);
				r.setStrokeWidth(1);
				r.setStroke(Color.BLACK);
				r.setFill(Color.BLUE);

				color = rand.nextInt(4);
				switch(color)
				{
					case 0:
						r.setFill(Color.RED);
						break;
					case 1:
						r.setFill(Color.YELLOW);
						break;
					case 2:
						r.setFill(Color.BLUE);
						break;
					case 3:
						r.setFill(Color.GREEN);
						break;
				}

				numBricks += 1;
				pane.getChildren().add(r);
				brick[i][j] = r;
			}
		}
		lblLives.setText("Lives: 3");
		lblScore.setText("Score: 0");

		Timeline timelineAnimation = new Timeline(
			new KeyFrame(Duration.millis(10),
				new EventHandler<ActionEvent>()
				{
					double dx = 0;
					double dy = 0;
					boolean isPlaying = false;

					@Override
					public void handle(final ActionEvent e)
					{
						ball.setLayoutX(ball.getLayoutX() + dx);
						ball.setLayoutY(ball.getLayoutY() + dy);
						Bounds bounds = pane.getBoundsInLocal();

						if(numBricks == 0) //cleared all bricks
						{
							dx = 0;
							dy = 0;
							ball.setLayoutX(285);
							ball.setLayoutY(328);
							speedMod += 0.1;
							paddle.setWidth(paddle.getWidth() * .95);
							reloadBricks();
							isPlaying = false;
						}

						if(hitRightOrLeftEdge(bounds))
						{
							dx *= -1;
						}

						if(hitTop(bounds))
						{
							dy *= -1;
						}

						if(hitBottom(bounds))
						{
							lives -= 1;
							lblLives.setText("Lives: " + lives);
							dx = 0;
							dy = 0;
							ball.setLayoutX(285);
							ball.setLayoutY(328);
							isPlaying = false;

							if(lives == 0)
							{
								lblLoser.setVisible(true);
							}
						}

						if(hitPaddleLeft())
						{
							dx = -5 * speedMod;
							dy *= -1;
						}
						else if(hitPaddleRight())
						{
							dx = 5 * speedMod;
							dy *= -1;
						}
						else if(hitPaddle())
						{
							if(dx > 0)
							{
								dx = 2 * speedMod;
							}
							else if(dx < 0)
							{
								dx = -2 * speedMod;
							}
							else
							{
								dx = 0;
							}
							dy *= -1;
						}

						for(int i = 0; i < 4; i++)
						{
							int scoreMod = ((3 - i) * 20) +20;
							for(int j = 0; j < 11; j++)
							{
								if(hitBrick(i, j))
								{
									brick[i][j].setDisable(true);
									brick[i][j].setVisible(false);

									//This prevents ball from cutting through
									//when hitting 2 bricks at once
									if(brick[i][j].getLayoutY() + brick[i][j].getHeight() <
										ball.getLayoutY() + ball.getRadius())
									{
										dy = Math.abs(dy);
									}
									else
									{
										dy = Math.abs(dy) * -1;
									}
									numBricks -= 1;
									score += scoreMod;
									lblScore.setText("Score: " + score);
								}
							}
						}

						EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>()
						{
							@Override
							public void handle(MouseEvent e)
							{
								if(!isPlaying && e.getEventType() == MouseEvent.MOUSE_CLICKED && lives > 0)
								{
									dy = -2 * speedMod;
									isPlaying = true;
								}
								else if(e.getEventType() == MouseEvent.MOUSE_MOVED)
								{
									if(e.getX() > bounds.getMaxX() - paddle.getWidth())
									{
										paddle.setLayoutX(bounds.getMaxX() - paddle.getWidth());
									}
									else
									{
										paddle.setLayoutX(e.getX());
									}
								}
							}
						};
						pane.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler);
						pane.addEventFilter(MouseEvent.MOUSE_MOVED, eventHandler);
					}
				}
			)
		);

		timelineAnimation.setCycleCount(Timeline.INDEFINITE);
		timelineAnimation.play();
	}

	private boolean hitRightOrLeftEdge(Bounds bounds)
	{
		return (ball.getLayoutX() <= (bounds.getMinX() + ball.getRadius())) ||
			(ball.getLayoutX() >= (bounds.getMaxX() - ball.getRadius()));
	}

	private boolean hitTop(Bounds bounds)
	{
		return(ball.getLayoutY() <= (bounds.getMinY() + ball.getRadius()));
	}

	private boolean hitBottom(Bounds bounds)
	{
		return(ball.getLayoutY() >= (bounds.getMaxY() - ball.getRadius()));
	}

	private boolean hitPaddle()
	{
		return(ball.getLayoutY() >= (paddle.getLayoutY())) &&
			(ball.getLayoutX() >= (paddle.getLayoutX())) && (ball.getLayoutX() <= (paddle.getLayoutX() + paddle.getWidth()));
	}

	private boolean hitPaddleLeft()
	{
		return(ball.getLayoutY() >= (paddle.getLayoutY())) &&
			(ball.getLayoutX() >= (paddle.getLayoutX())) && (ball.getLayoutX() <= (paddle.getLayoutX() + (paddle.getWidth() / 4)));
	}

	private boolean hitPaddleRight()
	{
		return(ball.getLayoutY() >= (paddle.getLayoutY())) &&
			(ball.getLayoutX() >= (paddle.getLayoutX() + (paddle.getWidth() * .75))) && (ball.getLayoutX() <= (paddle.getLayoutX() + paddle.getWidth()));
	}

	private boolean hitBrick(int i, int j)
	{
		return((ball.getLayoutY() - ball.getRadius()) <= (brick[i][j].getLayoutY() + brick[i][j].getHeight())) &&
			((ball.getLayoutY() + ball.getRadius()) >= (brick[i][j].getLayoutY())) &&
			((ball.getLayoutX() + ball.getRadius()) >= (brick[i][j].getLayoutX())) &&
			((ball.getLayoutX() - ball.getRadius()) <= (brick[i][j].getLayoutX() + brick[i][j].getWidth()) &&
			brick[i][j].isVisible() == true);
	}

	void reloadBricks()
	{
		for(int i = 0; i < 4; i++)
		{
			for(int j = 0; j < 11; j++)
			{
				brick[i][j].setDisable(false);
				brick[i][j].setVisible(true);
				numBricks += 1;
			}
		}
	}

	@Override
	public void start(Stage stage) throws Exception
	{
		Parent root = FXMLLoader.load(getClass().getResource("JarvisJoshBB.fxml"));

		Scene scene = new Scene(root);

		stage.setTitle("Ball Breaker Game");

		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
