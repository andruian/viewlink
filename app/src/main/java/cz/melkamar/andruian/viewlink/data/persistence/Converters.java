package cz.melkamar.andruian.viewlink.data.persistence;

import android.arch.persistence.room.TypeConverter;

import cz.melkamar.andruian.viewlink.model.datadef.PropertyPath;

public class Converters {
    // TODO use  to-from JSON conversion ?

    /**
     * Convert a string into a {@link PropertyPath} instance.
     *
     * The string-serialized format of a property path is the individual properties separated with a magic string "/@%/".
     *
     * @param string A serialized {@link PropertyPath} object.
     * @return A deserialized {@link PropertyPath} object.
     */
    @TypeConverter
    public static PropertyPath fromString(String string) {
        return new PropertyPath(string.split("/@%/"));
    }

    /**
     * Convert a {@link PropertyPath} instance into a string.
     *
     * The string-serialized format of a property path is the individual properties separated with a magic string "/@%/".
     *
     * @param propertyPath The {@link PropertyPath} object to serialize.
     * @return The string-serialized representation of the {@link PropertyPath} object.
     */
    @TypeConverter
    public static String toString(PropertyPath propertyPath) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < propertyPath.getPathElements().length; i++) {
            builder.append(propertyPath.getPathElements()[i]);
            if (i < propertyPath.getPathElements().length - 1) builder.append("/@%/");
        }
        return builder.toString();
    }

    /**
     * Convert a string-serialized array into the elements of the array.
     *
     * The string-serialized format of a string array is the individual elements separated with a magic string "/@%/".
     *
     * @param string The string to deserialize.
     * @return The deserialized array of strings.
     */
    @TypeConverter
    public static String[] strToStrArr(String string) {
        return string.split("/@%/");
    }

    /**
     * Convert an array of strings into its serialized form.
     *
     * The string-serialized format of a string array is the individual elements separated with a magic string "/@%/".
     *
     * @param stringArray The array of strings to serialize.
     * @return The serialized string.
     */
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
