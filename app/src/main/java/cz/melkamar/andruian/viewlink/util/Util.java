package cz.melkamar.andruian.viewlink.util;

import android.content.Context;
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

    public static float convertRadiusToKilometers(double lat, double lng, double radius) {
        float[] result = new float[1];
        Location.distanceBetween(lat, lng, lat + radius, lng, result);
        return result[0] / 1000;
    }
}
