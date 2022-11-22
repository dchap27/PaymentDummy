package ng.com.dayma.paymentdummy.utils;

import android.widget.EditText;

import androidx.annotation.NonNull;

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
