package application;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class Controller {
	@FXML 
	private Button enterButton;
	
	@FXML
	private Label outputLabel;
	
	@FXML
	private TextField ladField;
	
	@FXML
	private TextField longField;

	//enter the code for display here
	public void enterButtonListener(){
		if(ladField.getLength() == 0 || longField.getLength() == 0){
			outputLabel.setText("Invalid");
		}
		else{
			outputLabel.setText("Valid");
		}
	}
}
