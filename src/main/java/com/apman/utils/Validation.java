package com.apman.utils;

import java.util.function.UnaryOperator;

import javafx.scene.control.TextFormatter;

public class Validation {
    public static UnaryOperator<TextFormatter.Change> doubleFilter = change -> {
        System.out.println("Change: " + change.getText());
        if(change.getText().matches("\\d*\\.?\\d*")){
            System.out.println("Matched");
            return change; //if change is a number
        } else {
            change.setText(""); //else make no change
            change.setRange(    //don't remove any selected text either.
                change.getRangeStart(),
                change.getRangeStart()
            );
            return change;
        }
    };

    public static UnaryOperator<TextFormatter.Change> emailFilter = change -> {
        System.out.println("Change: " + change.getText());
        if(change.getText().matches("(?:[a-z0-9!#$%&'+/=?^_{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_{|}~-]+)|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])\")@(?:(?:a-z0-9?.)+a-z0-9?|[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-][a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])")){
            System.out.println("Matched");
            return change; //if change is a number
        } else {
            change.setText(""); //else make no change
            change.setRange(    //don't remove any selected text either.
                    change.getRangeStart(),
                    change.getRangeStart()
            );
            return change;
        }
    };
    

    public static String validEmailRegex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    public static String validRateRegex = "\\d+\\.?\\d*?";
}
