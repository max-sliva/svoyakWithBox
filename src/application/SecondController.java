package application;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class SecondController {
	@FXML TextField scorePlayer1;
	
	public void callF() {
		System.out.println("yes");
		scorePlayer1.setText("Yes");
	}
}
