package ng.com.dayma.paymentdummy;

import android.text.InputFilter;
import android.text.Spanned;

public class AlphaNumericInputFilter implements InputFilter {

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

        // Keep only alphabetic characters
        StringBuilder builder = new StringBuilder();
        for(int i = start; i < end; i++){
            char c = source.charAt(i);
            if(Character.isLetter(c)){
                builder.append(c);
            }
        }
        // if all characters are valid return null, otherwise return filtered characters
        boolean allCharactersValid = (builder.length() == end - start);

        return allCharactersValid ? null : builder.toString();
    }
}
