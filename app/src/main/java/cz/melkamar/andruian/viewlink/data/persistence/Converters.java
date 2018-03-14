package cz.melkamar.andruian.viewlink.data.persistence;

import android.arch.persistence.room.TypeConverter;

import cz.melkamar.andruian.viewlink.model.PropertyPath;

public class Converters {
    // TODO use  to-from JSON conversion ?

    @TypeConverter
    public static PropertyPath fromString(String string) {
        return new PropertyPath(string.split("/@%/"));
    }

    @TypeConverter
    public static String toString(PropertyPath propertyPath) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < propertyPath.getPathElements().length; i++) {
            builder.append(propertyPath.getPathElements()[i]);
            if (i < propertyPath.getPathElements().length - 1) builder.append("/@%/");
        }
        return builder.toString();
    }

    @TypeConverter
    public static String[] strToStrArr(String string) {
        return string.split("/@%/");
    }

    @TypeConverter
    public static String strArrToStr(String[] stringArray) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < stringArray.length; i++) {
            builder.append(stringArray[i]);
            if (i < stringArray.length - 1) builder.append("/@%/");
        }
        return builder.toString();
    }
}
