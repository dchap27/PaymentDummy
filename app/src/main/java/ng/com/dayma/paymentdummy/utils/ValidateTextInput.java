package ng.com.dayma.paymentdummy.utils;

import android.support.annotation.NonNull;
import android.widget.EditText;

public class ValidateTextInput {

    public static boolean validateTextInput(@NonNull EditText... texts) {
        for(EditText editText : texts){
            if(editText.toString().isEmpty()) {
                editText.requestFocus();
                return false;
            }
        }
        return true;
    }
}
