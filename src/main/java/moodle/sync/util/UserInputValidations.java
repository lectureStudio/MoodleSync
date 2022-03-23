package moodle.sync.util;

import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public class UserInputValidations {

    public static UnaryOperator<TextFormatter.Change> numberValidationFormatter = change -> {
        if(change.getControlNewText().matches("\\d+")){
            return change; //if change is a number
        } else {
            change.setText(""); //else make no change
            /*change.setRange(    //don't remove any selected text either.
                    change.getRangeStart(),
                    change.getRangeStart()
            )*/;
            return change;
        }
    };

    private static UnaryOperator<TextFormatter.Change> urlValidationFormatter = change -> {
        if(change.getControlNewText().matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")){
            return change; //if change is a number
        } else {
            //change.setText(""); //else make no change
            /*change.setRange(    //don't remove any selected text either.
                    change.getRangeStart(),
                    change.getRangeStart()
            )*/;System.out.println("Keine geeignete Angabe" + change.getControlNewText());
            return change;
        }
    };

    public static TextFormatter<Integer> numberTextFormatter() {
        return new TextFormatter<Integer>(numberValidationFormatter);
    }

    public static TextFormatter<String> urlTextFormatter() {
        return new TextFormatter(urlValidationFormatter);
    }

}
