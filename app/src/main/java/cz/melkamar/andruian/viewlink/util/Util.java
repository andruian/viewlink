package cz.melkamar.andruian.viewlink.util;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Util {
    public static String readRawTextFile(Context ctx, int resId) throws IOException {
        InputStream inputStream = ctx.getResources().openRawResource(resId);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        StringBuilder text = new StringBuilder();

        while ((line = buffreader.readLine()) != null) {
            text.append(line);
            text.append('\n');
        }
        return text.toString();
    }

    /**
     * Convert the radius of a circle defined in WGS84 into kilometers. This is approximate.
     *
     * @param lat    The latitude of the center of a circle in WGS84.
     * @param lng    The longitude of the center of a circle in WGS84.
     * @param radius The radius of the circle in WGS84.
     * @return The approximate radius of the circle in kilometers.
     */
    public static float convertRadiusToKilometers(double lat, double lng, double radius) {
        float[] result = new float[1];
        Location.distanceBetween(lat, lng, lat + radius, lng, result);
        return result[0] / 1000;
    }

    public static int colorFromHue(float hue) {
        return Color.HSVToColor(new float[]{hue, 1, 1});
    }
}
