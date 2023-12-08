package com.universal.core.library.utils;

public class LocationHelper {
    public static double distance(double lat1, double lon1,
                                  double lat2, double lon2) {

        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers is 6371. Use 3956 for miles
        double r = 6371;

        // calculate the result
        var result = (c * r) * 1000;
        return result <= 0.00001 ? 0 : result;
    }

}
