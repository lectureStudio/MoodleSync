package moodle.sync.util;

import javafx.scene.control.TextFormatter;
import java.util.function.UnaryOperator;

public class TextStringFormatter extends TextFormatter<String> {

    public TextStringFormatter(){
        super(new UrlFilter());
    }

    private static class UrlFilter implements UnaryOperator<Change>{

        @Override
        public Change apply(Change change) {
            String newText = change.getControlNewText();
            if(newText.matches("^https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")){
                return change;
            }
            return null;
        }
    }
}
