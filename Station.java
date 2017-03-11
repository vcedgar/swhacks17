import java.net.*;
import java.io.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		//example
		
		//set person's location to pinal mountain near globe
		Station.setPersonLat(33.3);
		Station.setPersonLon(-110.8);
	
		//get list of forecasts	
		Station[] current = parseCurrent();
		
		//print list of forecasts
		System.out.println(Arrays.toString(current));
	}
	
	
	/**
	 * get an updated array of stations from ADDS
	 * @return array of stations
	 */
	private static Station[] parseCurrent() {
		//get the hml file from the website from the nearest station to the person
		String fullXML = getMetars(personLat, personLon);
		
		//parse the station's location data
		Pattern location = Pattern.compile("<Forecast location=(.*?)>", Pattern.DOTALL);
		Matcher locationMatch = location.matcher(fullXML);
		
		//seperate the numbers pattern
		Pattern numbers = Pattern.compile("\"([0-9.]+).*?\"");
		
		//create a list of stations data
		ArrayList<Station> stations = new ArrayList<>();
		
		//lists of strings from parsed numbers
		String[] info = new String[4];
		
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
				current.setLatitude(Double.parseDouble(info[1]));
				current.setLongitude(Double.parseDouble(info[2]));
				current.setElevation_m(Double.parseDouble(info[3]));
				
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
