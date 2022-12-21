package moodle.sync.core.util;

public final class VerifyDataService {

    public static boolean validateString(String string){
        if(string == null || string.isEmpty() || string.isBlank()) {
            return false;
        } else {
            return true;
        }
    }
}
