import java.net.*;
import java.util.*;
import java.io.*;

public class Coordinates {
	public static void main(String []args) {
	}

	public static String getMetars(int minLat, int maxLat, int minLong, int maxLong) {
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
}
