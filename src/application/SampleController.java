package application;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.controlsfx.dialog.FontSelectorDialog;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

public class SampleController implements Initializable {
	@FXML
	Button newGame;
	@FXML
	Button connBox;
	@FXML
	BorderPane mainPane;
	@FXML
	GridPane titlesVbox;
	File selectedFile;
	ArrayList<Label> titles = null;
	ArrayList<Label> titles2 = null;
	Label[][] questions = null;
	String[][] answers = null;
	Button[][] numbers = null, numbers2 = null;
	@FXML
	ToolBar toolBar;
	@FXML
	GridPane gridTable;
	@FXML
	Label labelQuest;
	@FXML
	Label labelAnswer;
	@FXML
	ImageView imageView;
	@FXML
	StackPane stackPane;
	@FXML
	MediaView mediaView;
	@FXML
	CheckBox statCheck;
	@FXML
	CheckBox secondWindow;
	@FXML
	ToolBar statBar;
	@FXML
	SplitPane splitPane;
	@FXML
	Label questLabel; // на панели статистики
	@FXML
	Label player1;
	@FXML
	Label player2;
	@FXML
	Label player3;
	@FXML
	Label player4;
	@FXML
	Spinner<Integer> spinner1;
	@FXML
	Spinner<Integer> spinner2;
	@FXML
	Spinner<Integer> spinner3;
	@FXML
	Spinner<Integer> spinner4;
	ArrayList<Spinner> spinners;

	Stage stage;
	MediaPlayer musicPlayer;
	int row = 0, col = 0;
	int numOfQuestPass = 0;
	int num = 0;
	int labelClicked = 0;
	int rightClicked = 0;
	int rightClickedMusic = 0;
	String[] players = new String[4];
	TextField score1, score2, score3, score4;
	Label pl1, pl2, pl3, pl4;
	Label topicLabel2ndWindow;
	GridPane titlesGrid2;
	SplitPane tempSplit;
	GridPane numbersGrid;
	Label labelQuest2;
	MediaView mediaView2;
	ImageView imageView2;
	Font font = new Font("Arial Black", 25);
	BufferedWriter bufWriter = null;
	FileWriter fWriter = null;
	int nPlayersInt = 0;
	private Enumeration portList;
	String choosenPort;
	private JFrame dialog;
	private static final int TIME_OUT = 1000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 1000000;
	private Enumeration portEnum; // номер порта
	private CommPortIdentifier portId = null; // id порта
	static SerialPort serialPort; // объект для работы с полученным СОМ-портом
	private InputStream input; // поток для получения данных с СОМ-порта
	private static OutputStream output; // // поток для отправки данных в СОМ-порт
	int available = -1;
	boolean flags[] = new boolean[4];
	private Integer[] spinData = {0,0,0,0};
	private ArrayList<TextField> scores; //тексты с очками на втором окне
	private ArrayList<Label> players2;	//имена игроков на втором окне
	private ArrayList<Label> players1;
	
	@FXML
	void handleConnBox(ActionEvent event) {
		int n = 0;
		connBox.setDisable(true);
//		dialog = new JFrame(); // создание диалогового окна для выбора СОМ-порта и задание ему размера
//		dialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		dialog.setBounds(100, 100, 100, 100);
		ArrayList<String> comList = new ArrayList(); // список СОМ-портов
		portList = CommPortIdentifier.getPortIdentifiers(); // получение СОМ-портов

		// Process the list.
		while (portList.hasMoreElements()) { // пока есть СОМ-порт
			CommPortIdentifier cpi = (CommPortIdentifier) portList.nextElement(); // считываение следующего СОМ-порта из
																					// списка
			System.out.print("Port " + cpi.getName() + " ");
			if (cpi.getPortType() == CommPortIdentifier.PORT_SERIAL) { // если тип порта PORT_SERIAL
				System.out.println("is a Serial Port: " + cpi); // вывод в консоль СОМ-порта
				comList.add(cpi.getName()); // добавление СОМ-порта в список
				n++; // количество обработанных СОМ-портов
			} else if (cpi.getPortType() == CommPortIdentifier.PORT_PARALLEL) { // иначе если тип порта PORT_PARALLEL
				System.out.println("is a Parallel Port: " + cpi);
				System.out.println(cpi.getName());
			} else {
				System.out.println("is an Unknown Port: " + cpi);
			}
		}
		Object[] ports = new Object[n]; // создание массива объектов
		ports = comList.toArray(); // добавление подходящих СОМ-портов из списка в массив
		
		List<String> choices = new ArrayList<>();
		for (int i = 0; i < ports.length; i++) {
			choices.add(ports[i].toString());
			
		}
		
		ChoiceDialog<Object> dialogNPorts = new ChoiceDialog<>(choices);
		dialogNPorts.setTitle("Подключение к коробке");
//		dialogNPorts.setHeaderText("Установите кол-во игроков");
//		dialogNPorts.se
		dialogNPorts.setContentText("Выберите COM-порт из списка:");
		Optional<Object> resultN = dialogNPorts.showAndWait();
		// The Java 8 way to get the response value (with lambda expression).

		resultN.ifPresent(chPort -> {
			choosenPort = chPort.toString().substring(1, chPort.toString().toString().length()-1);
			System.out.println("Your choice: " + choosenPort);
		});
		resultN.ifPresent(chPort -> {
			choosenPort = chPort.toString().substring(1, chPort.toString().toString().length()-1);
			System.out.println("Your choice: " + choosenPort);
		});
		dialogNPorts.close();
		
//		choosenPort = (String) JOptionPane.showInputDialog( // создание и показ диалогового окна с выбором СОМ-порта
//				dialog, "Пожалуйста выберите устройство для игры\n" + "Из предложенного списка:", "Выбор устройства",
//				JOptionPane.YES_NO_OPTION, null, ports, "ports");
		if (choosenPort != null && choosenPort.contains("COM1") == false) {

			// и берем идентификатор выбранного порта
			Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
			while (portEnum.hasMoreElements()) { // перебираем все порты
				CommPortIdentifier cpi = (CommPortIdentifier) portEnum.nextElement();
				// если находим выбранный, то берем идентификатор
				if (cpi.getName().equals(choosenPort))
					portId = cpi;
			}
			System.out.println("Chosen port: " + portId.getName());

			try { // тут работа с установкой параметров порта и связи с ним потока
					// open serial port, and use class name for the appName.
				serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);

				// set port parameters
				serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);

				// open the streams
				input = serialPort.getInputStream();
				output = serialPort.getOutputStream(); // связываем поток с выбранным ком-портом
				// add event listeners
				serialPort.addEventListener(new SerialPortEventListener() { // это для приема данных из ардуино
																			
					@Override
					public void serialEvent(SerialPortEvent oEvent) {
						if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
							try {
								BufferedReader inputReader = new BufferedReader(new InputStreamReader(input));

								// byte chunk[] = new byte[available];
								// input.read(chunk, 0, available);
								String inputLine = inputReader.readLine();
								System.out.println(inputLine);
								if (available == -1) // если параметр равен -1, то можно обрабатывать нажатие
														// физической кнопки игрока
								{
									System.out.println("Pressed "+(available-2) +"button");
									available = Integer.parseInt(inputLine); // парс полученного пина кнопки
									for (Spinner<Integer> sp : spinners) {
										if (spinners.indexOf(sp) != available-2) {
											sp.setDisable(true);//для блокировки спиннеров игроков, кроме нажавшего кнопку
										}
									}
									players2.get(available-2).setStyle("-fx-border-color: green; -fx-border-width:5;");
									players1.get(available-2).setStyle("-fx-border-color: green; -fx-border-width:5;");
									// arrPlusButton.get(available - 2).setEnabled(true); // вычитаем 2, так как
									// 0-ой и
									// 1-ый пин заняты
									// bluetooth-модулем
									// arrMinusButton.get(available - 2).setEnabled(true); // и активируем кнопки
									// плюс
									// и минус с соответствующим
									// индексом
								}

								// Displayed results are codepage dependent
								// System.out.print(new String(chunk));
							} catch (Exception e) {
								System.err.println(e.toString());
							}
						}
					}
				});
				serialPort.notifyOnDataAvailable(true);
			} catch (Exception e) {
				System.err.println(e.toString());
				e.printStackTrace();
			}
		}
	}

	@FXML
	private void handleScoreChanged(MouseEvent event) throws IOException {
		Spinner<Integer> tempSpinner1 = (Spinner<Integer>) event.getSource();
		tempSpinner1.valueProperty().addListener(new ChangeListener<Integer>() { //для раскраски последнего правильно ответившего игрока 
            @Override //не работает первый нажатый раз 
            public void changed(ObservableValue<? extends Integer> observable,//
            		Integer oldValue, Integer newValue) {
            	System.out.println("old = "+oldValue+" new = "+newValue); 
            		if (oldValue<newValue) {
            			tempSpinner1.setStyle("-fx-border-color: brown; -fx-border-width:5;");
            			for (TextField sc: scores) {
            				sc.setStyle("-fx-border-width:0;");
            			}
            			scores.get(spinners.indexOf(tempSpinner1)).setStyle("-fx-border-color: brown; -fx-border-width:5;");
            			for (Spinner sp: spinners) {
	            			if (sp!=tempSpinner1) sp.setStyle("-fx-border-width:0;");
            			}
            		} else {
//            			for (Spinner sp: spinners) {
//            				sp.setDisable(false);
//            			}
            			System.out.println("Other value");
            		}
            }
        });
		
		for (Spinner sp: spinners) { //для разблокировки спиннеров игроков
			sp.setDisable(false);
		}
		for (Label pl2: players2) {
			pl2.setStyle("-fx-border-width:0;");
		}
		for (Label pl1: players1) {
			pl1.setStyle("-fx-border-width:0;");
		}
//		players1.get(available-2).setStyle("-fx-border-width:0;");
		if (spinData [spinners.indexOf(tempSpinner1)] < tempSpinner1.getValue()) {
			for (Spinner sp: spinners) {
    			if (sp!=tempSpinner1) sp.setStyle("-fx-border-width:0;");
			}
			for (TextField sc: scores) {
				sc.setStyle("-fx-border-width:0;");
			}
//			for (Label pl2: players2) {
//				pl2.setStyle("-fx-border-width:0;");
//			}
//			for (Label pl1: players1) {
//				pl1.setStyle("-fx-border-width:0;");
//			}
			tempSpinner1.setStyle("-fx-border-color: brown; -fx-border-width:5;");
			scores.get(spinners.indexOf(tempSpinner1)).setStyle("-fx-border-color: brown; -fx-border-width:5;");
			players1.get(spinners.indexOf(tempSpinner1)).setStyle("-fx-border-color: brown; -fx-border-width:5;");
			players2.get(spinners.indexOf(tempSpinner1)).setStyle("-fx-border-color: brown; -fx-border-width:5;");
			spinData[spinners.indexOf(tempSpinner1)] = tempSpinner1.getValue();
		}

		
		if (dialog!=null) dialog.dispose();
//		tempSpinner1.setStyle("-fx-border-color: brown; -fx-border-width:5;");
//		tempSpinner1.getV
		if (output != null) output.write('0');
		available = -1;
		int spinNumber = Integer.parseInt(tempSpinner1.getId().substring(7));
//		scores.get(spinNumber-1).setStyle("-fx-border-color: brown; -fx-border-width:5;");
		switch (spinNumber) {
		case 1:
			score1.setText(tempSpinner1.getValue().toString());
			bufWriter.write("!!" + player1.getText() + "changed score " + tempSpinner1.getValue() + "\n");
			break;
		case 2:
			score2.setText(tempSpinner1.getValue().toString());
			bufWriter.write("!!" + player2.getText() + "changed score " + tempSpinner1.getValue() + "\n");
			break;
		case 3:
			score3.setText(tempSpinner1.getValue().toString());
			bufWriter.write("!!" + player3.getText() + "changed score " + tempSpinner1.getValue() + "\n");
			break;
		case 4:
			score4.setText(tempSpinner1.getValue().toString());
			bufWriter.write("!!" + player4.getText() + "changed score " + tempSpinner1.getValue() + "\n");
			break;
		}
		System.out.println("spin# " + spinNumber);
	}

	@FXML
	private void secondWindowCheck(ActionEvent event) {
		if (secondWindow.isSelected())
			stage.show();
		else
			stage.hide();
	}

	@FXML
	private void handleFontPlus(ActionEvent event) {
		Font tempFont = labelQuest.getFont();
		double fontSize = tempFont.getSize();
		tempFont = new Font(labelQuest.getFont().getFamily(), fontSize + 1);
		labelQuest.setFont(tempFont);
		labelQuest2.setFont(tempFont);
	}

	@FXML
	private void handleFontMinus(ActionEvent event) {
		Font tempFont = labelQuest.getFont();
		double fontSize = tempFont.getSize();
		tempFont = new Font(labelQuest.getFont().getFamily(), fontSize - 1);
		labelQuest.setFont(tempFont);
		labelQuest2.setFont(tempFont);
	}

	@FXML
	private void handleClick(MouseEvent event) {
		Node tempNode = (Node) event.getSource();
		if (event.getButton() == MouseButton.PRIMARY) {
			System.out.println("clickedLeft");
			if (labelClicked == 0) {
				labelClicked++;
				labelAnswer.setText(answers[row][col]);
			} else if (labelClicked == 1) {
				labelClicked = 0;
				tempNode.setVisible(false);
				tempNode.setDisable(true);
				labelQuest2.setVisible(false);
				mediaView2.setVisible(false);
				imageView2.setVisible(false);
				// labelQuest.setVisible(false);
				gridTable.setVisible(true);
				numbersGrid.setVisible(true);
				labelAnswer.setText("Ответ :");
				if (tempNode instanceof MediaView) {
					// System.out.println("MediaClick!");
					MediaView thisMedia = (MediaView) tempNode;
					thisMedia.getMediaPlayer().stop();
					mediaView2.getMediaPlayer().stop();
				}
				questLabel.setText("");
				if (musicPlayer != null)
					musicPlayer.stop();
				titlesVbox.getChildren().get(row).setStyle("-fx-border-color: transparent;");
				titlesGrid2.getChildren().get(row).setStyle("-fx-border-color: transparent;");
				numOfQuestPass++;
				if (numOfQuestPass == (num * 5))
					showWinMessage();
			}
		} else if (event.getButton() == MouseButton.SECONDARY) { // для mp3
			if (tempNode instanceof ImageView && musicPlayer != null) {
				System.out.println("MusikClick!");
				if (rightClickedMusic == 0 || rightClickedMusic % 2 == 0) {
					musicPlayer.pause();
				} else {
					musicPlayer.play();
				}
				rightClickedMusic++;
			}
		}
	}

	@FXML
	private void mediaRightClick(ContextMenuEvent event) {
		System.out.println("RightClick");
		MediaView tempNode = (MediaView) event.getSource();
		if (rightClicked == 0 || rightClicked % 2 == 0) {
			tempNode.getMediaPlayer().pause();
			mediaView2.getMediaPlayer().pause();
		} else {
			tempNode.getMediaPlayer().play();
			mediaView2.getMediaPlayer().play();
		}
		rightClicked++;
	}

	@FXML
	private void statSwitch(ActionEvent event) {
		statBar.setVisible(statCheck.isSelected());
	}

	@FXML
	private void showContextMenu(ContextMenuEvent event) {
		MenuItem item1 = new MenuItem("Шрифт...");
		item1.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				System.out.println("Шрифт");
				if (!titles.isEmpty()) {
					FontSelectorDialog fonts = new FontSelectorDialog(titles.get(0).getFont());
					Font temFont = fonts.showAndWait().get();
					for (Label label : titles) {
						label.setFont(temFont);
					}
					for (Label label : titles2) {
						label.setFont(temFont);
					}
				}
			}
		});

		final ContextMenu contextMenu = new ContextMenu(item1);
		contextMenu.setMaxSize(50, 50);
		contextMenu.show(mainPane.getScene().getWindow(), event.getScreenX(), event.getScreenY());
	}

	private void setSpinnersStep(int step) {
		int value = (spinner1.getValue() != null) ? (spinner1.getValue()) : (0);
		SpinnerValueFactory<Integer> valueFactory1 = new SpinnerValueFactory.IntegerSpinnerValueFactory(-10000, 10000,
				value, step);
		spinner1.setValueFactory(valueFactory1);
		value = (spinner2.getValue() != null) ? (spinner2.getValue()) : (0);
		SpinnerValueFactory<Integer> valueFactory2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(-10000, 10000,
				value, step);
		spinner2.setValueFactory(valueFactory2);
		value = (spinner3.getValue() != null) ? (spinner3.getValue()) : (0);
		SpinnerValueFactory<Integer> valueFactory3 = new SpinnerValueFactory.IntegerSpinnerValueFactory(-10000, 10000,
				value, step);
		spinner3.setValueFactory(valueFactory3);
		value = (spinner4.getValue() != null) ? (spinner4.getValue()) : (0);
		SpinnerValueFactory<Integer> valueFactory4 = new SpinnerValueFactory.IntegerSpinnerValueFactory(-10000, 10000,
				value, step);
		spinner4.setValueFactory(valueFactory4);
	}

	@FXML
	private void videoSizeClick(ActionEvent event) {
		Button tempBut = (Button) event.getSource();
		switch (tempBut.getText()) {
		case "+":
			System.out.println("Video +");
			mediaView2.setScaleX(mediaView2.getScaleX() + 0.2);
			mediaView2.setScaleY(mediaView2.getScaleY() + 0.2);
			imageView2.setScaleX(imageView2.getScaleX() + 0.2);
			imageView2.setScaleY(imageView2.getScaleY() + 0.2);
			break;
		case "-":
			System.out.println("Video -");
			mediaView2.setScaleX(mediaView2.getScaleX() - 0.2);
			mediaView2.setScaleY(mediaView2.getScaleY() - 0.2);
			imageView2.setScaleX(imageView2.getScaleX() - 0.2);
			imageView2.setScaleY(imageView2.getScaleY() - 0.2);
			break;
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		spinners = new ArrayList<>();
		spinners.add(spinner1);
		spinners.add(spinner2);
		spinners.add(spinner3);
		spinners.add(spinner4);

		players1 = new ArrayList<>();
		players1.add(player1);
		players1.add(player2);
		players1.add(player3);
		players1.add(player4);
		mediaView.setFitWidth(800);
		mediaView.setFitHeight(600);
		setSpinnersStep(0);
		
//		spinner1.getValueFactory().setValue(10);
//		spinner1.getValueFactory().setValue(0);
		statBar.setVisible(false);
		System.out.println("111");
		titles = new ArrayList<>();
		labelQuest.setFont(font);
		newGame.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
				Date date = new Date();
				String dateString = dateFormat.format(date).toString() + ".txt";
				System.out.println(dateString);
				File logFile = new File(dateString);
				try {
					fWriter = new FileWriter(logFile);
					bufWriter = new BufferedWriter(fWriter);
					bufWriter.write("New game!!\n");
				} catch (IOException e2) {
					e2.printStackTrace();
				}

				BorderPane root = null;
				stage = new Stage();
				try {
					root = FXMLLoader.load(getClass().getResource("secondWindow.fxml"));
					// создаем окно, в него помещаем содержимое root
					stage.setScene(new Scene(root));
					// и задаем для него главное окно – окно, в котором находится mainPane
					stage.initOwner(mainPane.getScene().getWindow()); // mainPane - BorderPane
					stage.show();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				stage.setOnCloseRequest(e -> {
					secondWindow.setSelected(false);
				});
				mainPane.getScene().getWindow().setOnCloseRequest(e -> {
					// try {
					System.out.println("Closing");
					if (dialog!=null) dialog.dispose();
					try {
						if (output!=null) output.close();
						if (input!=null) input.close();
						if (serialPort!=null) serialPort.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					showWinMessage();
					// bufWriter.close();
					// fWriter.close();
					// } catch (IOException e1) {
					// e1.printStackTrace();
					// }

				});
				stage.hide();
				secondWindow.setDisable(false);
				getNodesFromSecondWindow(root);

				List<Integer> choices = new ArrayList<>();
				choices.add(1);
				choices.add(2);
				choices.add(3);
				choices.add(4);

				ChoiceDialog<Integer> dialogNPlayes = new ChoiceDialog<>(3, choices);
				dialogNPlayes.setTitle("Кол-во игроков");
				dialogNPlayes.setHeaderText("Установите кол-во игроков");
				dialogNPlayes.setContentText("Выберите кол-во из списка:");
				Optional<Integer> resultN = dialogNPlayes.showAndWait();
				// The Java 8 way to get the response value (with lambda expression).

				resultN.ifPresent(nPlayers -> {
					nPlayersInt = nPlayers;
					System.out.println("Your choice: " + nPlayers);
				});

				TextInputDialog dialog = new TextInputDialog("Игрок");
				// dialog.setContentText("Please enter your name:");
				// Traditional way to get the response value.
				Optional<String> result = null;
				for (int i = 0; i < nPlayersInt; i++) {
					dialog.setTitle("Игрок" + (i + 1));
					dialog.setHeaderText("Введите имя игрока " + (i + 1));
					result = dialog.showAndWait();
					if (result.isPresent()) {
						players[i] = result.get();
						// System.out.println(""+playerId+" player = " + s);
					}
				}
				player1.setText(players[0]);
				if (nPlayersInt > 1)
					player2.setText(players[1]);
				else {
					player2.setVisible(false);
					spinner2.setVisible(false);
				}
				if (nPlayersInt > 2)
					player3.setText(players[2]);
				else {
					player3.setVisible(false);
					spinner3.setVisible(false);
				}
				if (nPlayersInt > 3)
					player4.setText(players[3]);
				else {
					player4.setVisible(false);
					spinner4.setVisible(false);
				}
				pl1.setText(players[0]);
				if (nPlayersInt > 1)
					pl2.setText(players[1]);
				else {
					pl2.setVisible(false);
					score2.setVisible(false);
				}
				if (nPlayersInt > 2)
					pl3.setText(players[2]);
				else {
					pl3.setVisible(false);
					score3.setVisible(false);
				}
				if (nPlayersInt > 3)
					pl4.setText(players[3]);
				else {
					pl4.setVisible(false);
					score4.setVisible(false);
				}
				// System.out.println("1 player = " + player1.getText());
				// System.out.println("2 player = " + player2.getText());
				// System.out.println("3 player = " + player3.getText());

				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Открыть файл с темами");
				fileChooser.setInitialDirectory(new File(Paths.get(".").toAbsolutePath().normalize().toString()));
				fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"),
						new ExtensionFilter("All Files", "*.*"));
				Window mainStage = mainPane.getScene().getWindow();
				selectedFile = fileChooser.showOpenDialog(mainStage);

				System.out.println(selectedFile);
				FileReader fReader = null;
				BufferedReader bufReader = null;
				titles2 = new ArrayList<>();
				try {
					fReader = new FileReader(selectedFile);
					bufReader = new BufferedReader(fReader);
					String s;
					while ((s = bufReader.readLine()) != null) { // считываем темы и подсчитываем их кол-во
						// System.out.println(s);
						if (s.contains("Тема ") && !s.contains("Тема :")) {
							Label newLabel = new Label(s.substring(s.lastIndexOf(":") + 2));
							// Font font=new Font("Arial Black", 20);
							// newLabel.setStyle(
							// "-fx-border-style: solid inside;" +
							// "-fx-border-width: 2;" +
							// "-fx-border-radius: 5;" +
							// "-fx-border-color: blue;");

							newLabel.setFont(font);
							newLabel.setWrapText(true);
							newLabel.setMaxWidth(Double.MAX_VALUE);
							VBox.setVgrow(newLabel, Priority.ALWAYS);
							// System.out.println(newLabel.getText());
							titles.add(newLabel);

							Label secNewLabel = new Label(newLabel.getText());
							// secNewLabel.setStyle(
							// "-fx-border-style: solid inside;" +
							// "-fx-border-width: 2;" +
							// "-fx-border-radius: 5;" +
							// "-fx-border-color: blue;");

							secNewLabel.setFont(font);
							secNewLabel.setWrapText(true);
							secNewLabel.setMaxWidth(Double.MAX_VALUE);
							VBox.setVgrow(secNewLabel, Priority.ALWAYS);
							titles2.add(secNewLabel);
							num++;
						}
					}
					System.out.println("num=" + num);
					if (num == 0) {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("Проблема с файлом!");
						alert.setHeaderText("Возможно, файл не в кодировке ANSI. Переведите файл в кодировку ANSI.");
						alert.setContentText("Приложение закроется");
						Stage curStage = (Stage) mainPane.getScene().getWindow();
						curStage.close();
						alert.showAndWait();
						// Platform.exit();
					}
					numbers = new Button[num][5];
					numbers2 = new Button[num][5];
					for (int i = 0; i < numbers.length; i++) {
						for (int j = 0; j < 5; j++) {
							numbers[i][j] = new Button("" + (j + 1) * 10);
							numbers2[i][j] = new Button("" + (j + 1) * 10);
							// System.out.print(" "+numbers[i][j].getText());
						}
						// System.out.println();
					}
					questions = new Label[num][5];
					answers = new String[num][5];
					int i = 0;
					int j = 0;
					String quest = "";
					fReader = new FileReader(selectedFile);
					bufReader = new BufferedReader(fReader);
					s = "";
					while ((s = bufReader.readLine()) != null) { // считываем вопросы и ответы
						if (s.contains("Тема :"))
							break;
						if (s.contains("Тема "))
							continue;
						j = 0;
						while (!s.contains("Тема ")) {
							quest = s;
							if (s.contains("Тема :"))
								break;
							while ((s = bufReader.readLine()) != null && !s.contains("Ответ: ")) {
								if (!s.contains("Тема "))
									quest = quest + s;
							}
							questions[i][j] = new Label(quest);
							System.out.println("Вопрос за " + (j + 1) * 10 + ": " + questions[i][j].getText());
							answers[i][j] = new String(s);
							System.out.println("Ответ за " + (j + 1) * 10 + ": " + answers[i][j]);
							j++;
							s = "";
							if (j == 5)
								break;
						}
						i++;
						if (i == num)
							break;
					}
					bufReader.close();
					fReader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println(titles);
				for (int i = 0; i < num; i++) {
					RowConstraints row = new RowConstraints();
					row.setVgrow(Priority.ALWAYS);
					// splitPane.setHg
					titlesVbox.getRowConstraints().add(row);
					titlesGrid2.getRowConstraints().add(row);
					titles.get(i).setMaxHeight(titlesVbox.getHeight() / num);
					titles2.get(i).setMaxHeight(titlesGrid2.getHeight() / num);
					titlesVbox.add(titles.get(i), 0, i);
					titlesGrid2.add(titles2.get(i), 0, i);
				}
				// titlesVbox.getChildren().addAll(titles);
				// titlesVbox.setStyle(
				// "-fx-border-style: solid inside;" +
				// "-fx-border-width: 2;" +
				// "-fx-border-radius: 5;" +
				// "-fx-border-color: blue;");
				// titlesGrid2.setStyle(
				// "-fx-border-style: solid inside;" +
				// "-fx-border-width: 2;" +
				// "-fx-border-radius: 5;" +
				// "-fx-border-color: blue;");
				for (int i = 0; i < num; i++) {
					RowConstraints row = new RowConstraints();
					row.setVgrow(Priority.ALWAYS);
					gridTable.getRowConstraints().add(row);
					numbersGrid.getRowConstraints().add(row);
				}
				statCheck.setDisable(false);
				System.out.println("rows=" + gridTable.getRowConstraints().size());
				for (int i = 0; i < numbers.length; i++) {
					for (int j = 0; j < 5; j++) {
						gridTable.add(numbers[i][j], j, i);
						numbersGrid.add(numbers2[i][j], j, i);
						GridPane.setVgrow(numbers[i][j], Priority.ALWAYS);
						GridPane.setHgrow(numbers[i][j], Priority.ALWAYS);
						numbers[i][j].setMaxWidth(Double.MAX_VALUE);
						numbers[i][j].setMaxHeight(Double.MAX_VALUE);

						GridPane.setVgrow(numbers2[i][j], Priority.ALWAYS);
						GridPane.setHgrow(numbers2[i][j], Priority.ALWAYS);
						numbers2[i][j].setMaxWidth(Double.MAX_VALUE);
						numbers2[i][j].setMaxHeight(Double.MAX_VALUE);

						numbers[i][j].setOnAction(e -> {
							Button temp = (Button) e.getSource();
							row = gridTable.getRowIndex(temp);
							col = gridTable.getColumnIndex(temp);
							// System.out.println(temp.getText()+" row="+row+" col="+col);
							temp.setDisable(true);
							temp.setStyle("-fx-text-fill: lavender ;");
							showQuestion(row, col);
							numbers2[row][col].setStyle("-fx-text-fill: lavender ;");
							setSpinnersStep((col + 1) * 10);
							titlesVbox.getChildren().get(row)
									.setStyle("-fx-border-color: red;" + "-fx-border-width: 5;");
							titlesGrid2.getChildren().get(row)
									.setStyle("-fx-border-color: red;" + "-fx-border-width: 5;");
							// gridTable.getRowIndex(temp);
						});
						numbers[i][j].setFont(font);
						numbers2[i][j].setFont(font);
					}
				}
			}
		});
	}

	protected void showWinMessage() {
		try {
			bufWriter.write("!!" + player1.getText() + "final score " + spinner1.getValue() + "\n");
			if (nPlayersInt > 1)
				bufWriter.write("!!" + player2.getText() + "final score " + spinner2.getValue() + "\n");
			if (nPlayersInt > 2)
				bufWriter.write("!!" + player3.getText() + "final score " + spinner3.getValue() + "\n");
			if (nPlayersInt > 3)
				bufWriter.write("!!" + player4.getText() + "final score " + spinner4.getValue() + "\n");
			bufWriter.close();
			fWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Игра окончена!");
		alert.setHeaderText(player1.getText() + " получает " + spinner1.getValue() + "\n"
				+ ((nPlayersInt > 1) ? (player2.getText() + " получает " + spinner2.getValue() + "\n") : "")
				+ ((nPlayersInt > 2) ? (player3.getText() + " получает " + spinner3.getValue() + "\n") : "")
				+ ((nPlayersInt > 3) ? (player4.getText() + " получает " + spinner4.getValue() + "\n") : ""));
		alert.setContentText("Приложение закроется");
		alert.setY(stage.getY() + stage.getHeight() / 2);
		alert.setX(stage.getX() + stage.getWidth() / 2);
		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add(getClass().getResource("myDialogs.css").toExternalForm());
		dialogPane.getStyleClass().add("myDialog");
		alert.showAndWait();
		Stage curStage = (Stage) mainPane.getScene().getWindow();
		try {
			if (input!=null) input.close();
			if (output!=null) output.close();
			if (serialPort!=null) serialPort.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		curStage.close();
	}

	protected void getNodesFromSecondWindow(BorderPane root) {
		System.out.println(root.getChildren());
		ToolBar secStatBar = (ToolBar) root.getChildren().get(1);
		HBox tempBox = (HBox) secStatBar.getChildrenUnmodifiable().get(0);
		System.out.println(tempBox.getChildren());
		pl1 = (Label) tempBox.getChildren().get(0);
		score1 = (TextField) tempBox.getChildren().get(1);
		pl2 = (Label) tempBox.getChildren().get(2);
		score2 = (TextField) tempBox.getChildren().get(3);
		pl3 = (Label) tempBox.getChildren().get(4);
		score3 = (TextField) tempBox.getChildren().get(5);
		pl4 = (Label) tempBox.getChildren().get(6);
		score4 = (TextField) tempBox.getChildren().get(7);
		topicLabel2ndWindow = (Label) tempBox.getChildren().get(8);
		tempSplit = (SplitPane) root.getChildren().get(0);
		titlesGrid2 = (GridPane) tempSplit.getItems().get(0);
		StackPane tempStack = (StackPane) tempSplit.getItems().get(1);
		labelQuest2 = (Label) tempStack.getChildren().get(0);
		labelQuest2.setFont(font);
		numbersGrid = (GridPane) tempStack.getChildren().get(1);
		mediaView2 = (MediaView) tempStack.getChildren().get(2);
		imageView2 = (ImageView) tempStack.getChildren().get(3);
		System.out.println(tempStack.getChildren());
		scores = new ArrayList<>();
		scores.add(score1);
		scores.add(score2);
		scores.add(score3);
		scores.add(score4);
		players2= new ArrayList<Label>();
		players2.add(pl1);
		players2.add(pl2);
		players2.add(pl3);
		players2.add(pl4);

	}

	private void showQuestion(int i, int j) {
		System.out.println(" row=" + i + " col=" + j);
		System.out.println(questions[i][j].getText());
		questLabel.setText(titles.get(row).getText() + " за " + (col + 1) * 10);
		topicLabel2ndWindow.setText(titles.get(row).getText() + " за " + (col + 1) * 10);
		gridTable.setVisible(false);
		numbersGrid.setVisible(false);
		String questString = questions[i][j].getText();
		if (questString.contains("picture")) {
			File file = new File(questString.substring(questString.lastIndexOf(" ") + 1));
			Image image1 = new Image(file.toURI().toString());
			Image image2 = new Image(file.toURI().toString());
			imageView.setImage(image1);
			imageView.setVisible(true);
			imageView.setDisable(false);
			imageView.setPreserveRatio(true);
			// imageView.setFitHeight(stackPane.getHeight());
			// imageView.fitHeightProperty().bind(mainPane.getScene().getWindow().heightProperty());
			scaleImage(imageView, stackPane);
			imageView.maxHeight(2000);

			imageView2.setImage(image2);
			imageView2.setVisible(true);
			imageView2.setDisable(false);
			imageView2.setPreserveRatio(true);
			StackPane tempPane = (StackPane) imageView2.getParent();
			scaleImage(imageView2, tempPane);

			System.out.println("imageHeight=" + imageView2.getBoundsInParent().getHeight() + "  paneHeight="
					+ tempPane.getHeight());
			// imageView2.setFitHeight(tempPane.getHeight());
			// imageView2.fitHeightProperty().bind(mainPane.getScene().getWindow().heightProperty());
			imageView2.maxHeight(2000);
		} else if (questString.contains("video")) {
			rightClicked = 0;
			File file = new File(questString.substring(questString.lastIndexOf(" ") + 1));
			Media media = new Media(file.toURI().toString());

			// Create the player and set to play automatically.
			// MediaPlayer mediaPlayer = new MediaPlayer(media);
			// mediaPlayer.
			// mediaPlayer.setAutoPlay(true);
			// Create the view and add it to the Scene.
			mediaView.setMediaPlayer(new MediaPlayer(media));
			mediaView.setVisible(true);
			mediaView.setDisable(false);
			// mediaView.fitWidthProperty().bind(stackPane.widthProperty());
			mediaView.getMediaPlayer().play();
			scaleVideo(mediaView, stackPane);

			mediaView2.setMediaPlayer(new MediaPlayer(media));
			mediaView2.setVisible(true);
			mediaView2.setDisable(false);
			// mediaView.fitHeightProperty().bind(stackPane.widthProperty());
			// mediaView2.fitWidthProperty().bind(tempSplit.widthProperty());
			mediaView2.getMediaPlayer().play();
			mediaView2.getMediaPlayer().setMute(true);
			StackPane tempPane = (StackPane) mediaView2.getParent();
			scaleVideo(mediaView2, tempPane);

			System.out.println(stackPane.getWidth());
			// mediaView.setFitWidth(stackPane.getWidth());
			// mediaView.setPreserveRatio(true);
		} else if (questString.contains("music")) {
			File file = new File("musik.png");
			Image image1 = new Image(file.toURI().toString());
			imageView.setImage(image1);
			imageView.setVisible(true);
			imageView.setDisable(false);
			imageView.setPreserveRatio(true);
			// imageView.setFitHeight(stackPane.getHeight());
			imageView.fitHeightProperty().bind(mainPane.getScene().getWindow().heightProperty());
			imageView.maxHeight(2000);

			imageView2.setImage(image1);
			imageView2.setVisible(true);
			imageView2.setDisable(false);
			imageView2.setPreserveRatio(true);
			StackPane tempPane = (StackPane) imageView2.getParent();
			scaleImage(imageView2, tempPane);
			imageView2.maxHeight(2000);

			rightClickedMusic = 0;
			File fileMusic = new File(questString.substring(questString.lastIndexOf(" ") + 1));
			Media media = new Media(fileMusic.toURI().toString());
			// Create the player and set to play automatically.
			musicPlayer = new MediaPlayer(media);
			musicPlayer.setAutoPlay(true);
		} else {
			labelQuest.setText(questions[i][j].getText());
			labelQuest.setVisible(true);
			labelQuest.setDisable(false);

			labelQuest2.setText(questions[i][j].getText());
			labelQuest2.setVisible(true);
			labelQuest2.setDisable(false);
		}
	}

	private void scaleImage(ImageView imageView22, StackPane tempPane) {
		if (imageView22.getImage().getHeight() > imageView22.getImage().getWidth()) {
			while (imageView22.getBoundsInParent().getHeight() > (tempPane.getHeight() - 50)) {
				imageView22.setScaleX(imageView22.getScaleX() - 0.1);
				imageView22.setScaleY(imageView22.getScaleY() - 0.1);
			}
			while (imageView22.getBoundsInParent().getHeight() < (tempPane.getHeight() - 50)) {
				imageView22.setScaleX(imageView22.getScaleX() + 0.1);
				imageView22.setScaleY(imageView22.getScaleY() + 0.1);
			}
		} else {
			while (imageView22.getBoundsInParent().getWidth() > (tempPane.getWidth() - 50)) {
				imageView22.setScaleX(imageView22.getScaleX() - 0.1);
				imageView22.setScaleY(imageView22.getScaleY() - 0.1);
			}
			while (imageView22.getBoundsInParent().getWidth() < (tempPane.getWidth() - 50)) {
				imageView22.setScaleX(imageView22.getScaleX() + 0.1);
				imageView22.setScaleY(imageView22.getScaleY() + 0.1);
			}
		}
	}

	private void scaleVideo(MediaView mediaView22, StackPane tempPane) {
		while (mediaView22.getBoundsInParent().getWidth() > (tempPane.getWidth() - 60)) {
			mediaView22.setScaleX(mediaView22.getScaleX() - 0.1);
			mediaView22.setScaleY(mediaView22.getScaleY() - 0.1);
		}
		while (mediaView22.getBoundsInParent().getWidth() < (tempPane.getWidth() - 60)) {
			mediaView22.setScaleX(mediaView22.getScaleX() + 0.1);
			mediaView22.setScaleY(mediaView22.getScaleY() + 0.1);
		}

	}

}
