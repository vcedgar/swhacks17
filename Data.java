public class Data {
	private static double [] [] data = null;
	private static double minLat;
	private static double minLong;
	private static double maxLat;
	private static double maxLong;

	public static double[][] getData() {
		if (data == null) {
			getData(31.2, -115.0, 37.0, -109.0);
		}
		return data;
	}

	public static double[][] getData(double minLat, double minLong, double maxLat, double maxLong) {
		double latitude;
		double longitude;
		double elevation;
		Station.setPersonLat((minLat + maxLat) * .5);
		Station.setPersonLong((minLong + maxLong) * .5);
		Station current = Station.parseCurrent()[0];
		data = new double [(int) ((maxLong - minLong) * 100)];
		for (int i = 0; i < data.length; i ++) {
			data[i] = new double [(int) ((maxLat - minLat) * 100)];
			longitude = (i * .01) + minLong;
			for (int j = 0; j < data[i].length; j ++) {
				latitude = ((data[i].length - j) * .01) + minLat;
				elevation = Station.getElevation(latitude, longitude);
				data[i][j] = Station.accountForAlt(current.getTemp_f(), current.getElevation_m(), elevation);
			}
		}
		return data;
	}

	private static double getMinLat() {
		return minLat;
	}

	private static double getMinLong() {
		return minLong;
	}

	private static double getMaxLat() {
		return maxLat;
	}

	private static double getMaxLong() {
		return maxLong;
	}
}
