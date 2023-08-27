package com.apman.utils;


import javafx.util.StringConverter;

public class Converter {

    public static StringConverter<Double> DoubleStringConverter = new StringConverter<>() {
        @Override
        public String toString(Double object) {
            return object.toString();
        }
        @Override
        public Double fromString(String string) {
            return Double.parseDouble(string);
        }
};
}
