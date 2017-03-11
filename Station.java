import java.net.*;
import java.io.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.ArrayList;

public class Station {
	private double latitude;
	private double longitude;
	private double temp_c;
	private double elevation_m;
	
	public static void main(String args[]) {
		for (Station s : parseCurrent()) {
			System.out.println(s);
		}
	}
	
	public static Station[] parseCurrent() {
		String fullXML = getMetars(25,-100,65,-100);
		
	    Pattern metar = regexForTag("METAR");
	    Matcher fullXMLMatcher = metar.matcher(fullXML);
	    
	    ArrayList<Station> stations = new ArrayList<>();
	    
	    while (fullXMLMatcher.find()) {
	    	Station currentStation = new Station();
	    	
	    	String stationXML = fullXMLMatcher.group(1);
	    	String[] dataTags = new String[]{"latitude", "longitude", "temp_c", "elevation_m"};
	    	Pattern[] dataPatterns = new Pattern[dataTags.length];
	    	for (int i = 0; i < dataTags.length; i++) {
	    		dataPatterns[i] = regexForTag(dataTags[i]);
	    	}
	       
	    	for (int i = 0; i < dataTags.length; i++) {
	    		Matcher dataMatcher = dataPatterns[i].matcher(stationXML);
	    		while(dataMatcher.find()) {
	    			switch(i) {
	    			//doesn't verify int
	    			case 0: currentStation.setLatitude(Double.parseDouble(dataMatcher.group(1)));
	    			case 1: currentStation.setLongitude(Double.parseDouble(dataMatcher.group(1)));
	    			case 2: currentStation.setTemp_c(Double.parseDouble(dataMatcher.group(1)));
	    			case 3: currentStation.setElevation_m(Double.parseDouble(dataMatcher.group(1)));
	    			}
	    		}
	    	}
	    	stations.add(currentStation);
	       
	    }
	    
	   return stations.toArray(new Station[stations.size()]);
	}
	
	public static Pattern regexForTag(String tag) {
		return Pattern.compile("<"+tag+">(.*?)</"+tag+">", Pattern.DOTALL);
	}
	
	public static String getMetars(int minLat, int minLong, int maxLat, int maxLong) {
		String url = "https://aviationweather.gov/adds/dataserver_current/httpparam?dataSource=metars&requestType=retrieve&format=xml&minLat=" + minLat + "&minLon=" + minLong + "&maxLat=" + maxLat + "&maxLon=" + maxLong + "&hoursBeforeNow=3";
		return getUrlSource(url);
	}

	public static String getUrlSource(String url) {
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
	
	public double getLatitude() {
		return latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public double getTemp_c() {
		return temp_c;
	}
	public double getElevation_m() {
		return elevation_m;
	}
	public void setLatitude(double Latitude) {
		latitude = Latitude;
	}
	public void setLongitude(double Longitude) {
		longitude = Longitude;
	}
	public void setTemp_c(double Temp_c) {
		temp_c = Temp_c;
	}
	public void setElevation_m(double Elevation_m) {
		elevation_m = Elevation_m;
	}
	
	public String toString() {
		return "Latitude: "+this.latitude+"\n"+
				"Longitute: "+this.longitude+"\n"+
				"Elevation: "+this.elevation_m+"\n"+
				"Temp in Celcius: "+this.temp_c;
	}
	private static double getDistance(double la1, double lo1, double la2, double lo2)
	{
		double distance = Math.acos(Math.sin(deg2rad(la1)) * Math.sin(deg2rad(la2)) + 
				Math.cos(deg2rad(la1)) * Math.cos(deg2rad(la2)) * Math.cos(deg2rad(lo2 - lo1)));
		return (rad2deg(distance) * 60.0 * 1.1515);
	}
	private static double deg2rad(double deg)
	{
		return (deg * Math.PI/180.0);
	}
	private static double rad2deg(double rad)
	{
		return (rad * 180.0 / Math.PI);
	}
	private static double getTempInFahrenheit(double temp)
	{
		temp = ((temp * 9 / 5.0) + 32);
		return temp;
	}
}

