package application;

import java.net.*;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.List;
import java.awt.image.BufferedImage;
import java.io.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.util.ArrayList;
import java.util.Arrays;
/**
 * Station class containing basic readings from stations
 * @author zachary, Abdul
 *
 */
public class Station {
	private static double personLat;
	private static double personLon;
	private double latitude;
	private double longitude;
	private double temp_f;
	private double elevation_m;
	private String date;
	
	public static void main(String args[]) {
		JFrame frame = new JFrame("JFrame Example");
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());

		panel.add(getImage(33,-110,.1));
		
		frame.add(panel);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	private double newTemp(){
		return this.getTemp_f() + ((getElevation(getPersonLat(), getPersonLon())-this.getElevation_m())/1000)*3.3;
	}
	
	public static Forecast[] getForecasts(Station[] stations){
		Forecast[] forecasts = new Forecast[stations.length];
		for(int i = 0; i < stations.length; i++){
			forecasts[i] = new Forecast(stations[i].getDate(), stations[i].newTemp());
		}
		return forecasts;
	}
	
	public static JLabel getImage(double lat, double lon,double range) {
		return new JLabel(new ImageIcon(makeImage(getData(lat-range,lon-range,lat+range,lon+range))));
	}
	
	private static BufferedImage makeImage(double[][] data) {
		final int size = 500;
		
		
		BufferedImage image = new BufferedImage(size,size,BufferedImage.TYPE_INT_RGB);
		
		
		Graphics2D graphic = image.createGraphics();
		ArrayList<Color> colors = new ArrayList<Color>();
		for (int r=0; r<100; r++) colors.add(new Color(r*255/100,       255,         0));
		for (int g=100; g>0; g--) colors.add(new Color(      255, g*255/100,         0));
		for (int b=0; b<100; b++) colors.add(new Color(      255,         0, b*255/100));
		for (int r=100; r>0; r--) colors.add(new Color(r*255/100,         0,       255));
		for (int g=0; g<100; g++) colors.add(new Color(        0, g*255/100,       255));
		for (int b=100; b>0; b--) colors.add(new Color(        0,       255, b*255/100));
		                          colors.add(new Color(        0,       255,         0));
		
		
		double rowInc = size/data[0].length;
		double colInc = size/data.length;
		
		for (int total = 0; total < data.length*data[0].length; total++) {
			int row = total/data.length;
			int col = total%data.length;
								
			if (data[col][row]==-500) graphic.setColor(Color.GRAY);
			else {
				int colorIndex = 600 - (int)((data[col][row])*(600/120));
				graphic.setColor(colors.get(colorIndex));
			}
			
			graphic.fillRect((int)(row*rowInc),(int)(col*colInc),(int)rowInc,(int)colInc);
		}
		int count  = 0;
		for (int x = 0; x < 120; x+=10) {
			int color = 600 - (int)((x)*(600/(120)));
			
			graphic.setColor(Color.WHITE);
			graphic.fillRect(0, count*20, 30, 20);
			graphic.setColor(colors.get(color));
			graphic.drawString(""+x, 1, count*20);
			count++;
		}
		
		return image;
	}
	
	private static double accountForAlt(double startTemp, double startAlt, double endAlt) {
		return startTemp + ((endAlt-startAlt)/1000)*3.3;
	}
	
	/**
	 * get an updated array of stations from ADDS
	 * @return array of stations
	 */
	public static Station[] parseCurrent() {
		//get the hml file from the website from the nearest station to the person
		String fullXML = getMetars(personLat, personLon);
		
		//parse the station's location data
		Pattern location = Pattern.compile("<Forecast location=.*?lat=(.*?)>", Pattern.DOTALL);
		Matcher locationMatch = location.matcher(fullXML);
		
		//seperate the numbers pattern
		Pattern numbers = Pattern.compile("\"([0-9.]+).*?\"");
		
		//create a list of stations data
		ArrayList<Station> stations = new ArrayList<>();
		
		//lists of strings from parsed numbers
		String[] info = new String[3];
		
		//if a location was actually found
		if (locationMatch.find()) {
			//match numbers
			Matcher numbersMatch = numbers.matcher(locationMatch.group(1));
		
			//fill the array of strings with the found numbers
			int counter = 0;
			while(numbersMatch.find()) {
				info[counter] = numbersMatch.group(1);
				counter++;
			}

			//find all text for a period of forecast
			Pattern period = regexForTag("period");
			Matcher periodMatch = period.matcher(fullXML);
			
			//for each period
			while(periodMatch.find()) {
				//setup a station  object reflecting data
				Station current = new Station();
				current.setLatitude(Double.parseDouble(info[0]));
				current.setLongitude(Double.parseDouble(info[1]));
				current.setElevation_m(Double.parseDouble(info[2]));
				
				Pattern temp = Pattern.compile("<temp .*?>([0-9.]+?)</temp>",Pattern.DOTALL);
				Matcher tempMatcher = temp.matcher(periodMatch.group(1));
				tempMatcher.find();
				current.setTemp_f(Double.parseDouble(tempMatcher.group(1)));
				
				Pattern dateP = regexForTag("valid");
				Matcher dateMatcher = dateP.matcher(periodMatch.group(1));
				dateMatcher.find();
				current.setDate(dateMatcher.group(1));
				
				stations.add(current);
			}
		}
		//return as array of station data
	    return stations.toArray(new Station[stations.size()]);
	}
	
	/**
	 * returns a regex pattern for data between tags
	 * @param tag
	 * @return pattern for inside tags
	 */
	private static Pattern regexForTag(String tag) {
		return Pattern.compile("<"+tag+">(.*?)</"+tag+">", Pattern.DOTALL);
	}
	
	private static double getElevation(double lat, double lon) {
		String url = "https://maps.googleapis.com/maps/api/elevation/xml?locations="+lat+","+lon+"&key=AIzaSyCVahlCVNyQ0gyBPJDhMO6oFE9mqWUD4qU";
		String xml = getUrlSource(url);
		
		Pattern elevation = regexForTag("elevation");
		Matcher elevationMatch = elevation.matcher(xml);
		
		elevationMatch.find();
		return Double.parseDouble(elevationMatch.group(1))*3.28084;
	}
	
	/**
	 * create request for api
	 * @param d
	 * @param e
	 * @return XML string
	 */
	private static String getMetars(double d, double e) {
		//api website +lon and lat data
		String url = "http://forecast.weather.gov/MapClick.php?lat="+d+"&lon="+e+"&FcstType=xml";
		return getUrlSource(url);
	}
	/**
	 * connect to a website and retreive info as string
	 * @param url
	 * @return
	 */
	private static String getUrlSource(String url) {
		try {
			URL yahoo = new URL(url);
			URLConnection yc = yahoo.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream(), "UTF-8"));
			String inputLine;
			StringBuilder a = new StringBuilder();
			while ((inputLine = in.readLine()) != null)
				a.append(inputLine);
			in.close();

			return a.toString();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public static double[][] getData(double minLat, double minLon, double maxLat, double maxLon) {
		final int res = 5;
		System.out.println(String.format("Creating a weather map spanning from (%f, %f) to (%f, %f)\nwith a total resolution of %d\n", minLat, minLon, maxLat, maxLon,res*res));

		double[][] data = new double[res][res];
		for (int i = 0; i < res; i++) {
			for(int j = 0; j < res; j++) {
				System.out.println(((i*res+j)/((double)res*res)*100)+"% complete");
				double lat = i*((maxLat-minLat)/res)+minLat;
				double lon = j*((maxLon-minLon)/res)+minLon;
				setPersonLat(lat);
				setPersonLon(lon);
				
				Station[] current = parseCurrent();
				if (current.length>0) {
					Station s = current[0];
					data[i][j] = accountForAlt(s.getTemp_f(),s.getElevation_m(),getElevation(s.getLatitude(),s.getLongitude()));
				}
				else {
					data[i][j]=-500;
				}
			}
		}
		return data;
	}

	//Begin getters
	
	public double getLatitude() {
		return latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public double getTemp_f() {
		return temp_f;
	}
	public double getElevation_m() {
		return elevation_m;
	}
	public String getDate() {
		return date;
	}
	public static double getPersonLat() {
		return personLat;
	}
	public static double getPersonLon() {
		return personLon;
	}
	
	//Begin setters
	
	public void setLatitude(double Latitude) {
		latitude = Latitude;
	}
	public void setLongitude(double Longitude) {
		longitude = Longitude;
	}
	public void setTemp_f(double Temp_f) {
		temp_f = Temp_f;
	}
	public void setElevation_m(double Elevation_m) {
		elevation_m = Elevation_m;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public static void setPersonLat(double lat) {
		personLat = lat;
	}
	public static void setPersonLon(double lon) {
		personLon = lon;
	}
	
	//to String method for easy printing
	public String toString() {
		return "{Latitude: "+this.latitude+", "+
				"Longitute: "+this.longitude+", "+
				"Elevation: "+this.elevation_m+", "+
				"Temp in Fahrenheit: "+this.temp_f+", "+
				"Date: "+date+"}";
	}
}
