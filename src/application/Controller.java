package application;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class Controller {
	@FXML 
	private Button enterButton;
	
	@FXML
	private Label outputDay;
	
	@FXML
	private Label outputTemp;
	
	@FXML
	private TextField ladField;
	
	@FXML
	private TextField longField;
	
	@FXML
	private SwingNode map;

	//enter the code for display here
	public void enterButtonListener(){
		System.out.println("9999999");
		if(ladField.getLength() == 0 || longField.getLength() == 0){
			outputDay.setText("Invalid");
			outputTemp.setText("Invalid");
			System.out.println("Invalid");
		}
		else{
			DecimalFormat form = new DecimalFormat("#.##");
			double laditude = Double.parseDouble(ladField.getText());
			double longitude = Double.parseDouble(longField.getText());
			Station.setPersonLat(laditude);
			Station.setPersonLon(longitude);
			Forecast[] myFore = Station.getForecasts(Station.parseCurrent());
			String messageDay = "";
			String messageTemp = "";
			for(int i = 0; i < myFore.length;i++){
				messageDay = messageDay + "Day: " + myFore[i].date + "\n";
			}
			for(int i = 0; i < myFore.length;i++){
				messageTemp = messageTemp + "Temperature: " + form.format(myFore[i].temp) + "\n";
			}
			
			map.setContent(Station.getImage(laditude, longitude, 0.1));
			map.prefHeight(200);
			map.prefWidth(200);
			
			outputDay.setText(messageDay);
			System.out.println(messageDay);
			outputTemp.setText(messageTemp);
			System.out.println(messageTemp);
		}
	}
}
