package de.igloffstein.maik.arevelation.helpers;

import android.app.Activity;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.github.marmaladesky.R;

import java.util.Random;

/**
 * Created by maik on 16.01.18.
 */

public class PasswordGenerationHelper {

    private static String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static String numbers = "0123456789";
    private static String specialCharacters = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    public static String getRandomPassword(Activity activity) {
        int defaultSize = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(activity).getString("preference_password_size", "6"));
        boolean useCharacters = PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("preference_enable_characters", true);
        boolean useNumbers = PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("preference_enable_numbers", true);
        boolean userSpecialChars = PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("preference_enable_special_characters", true);

        return getRandomPassword(activity, defaultSize, useCharacters, useNumbers, userSpecialChars);
    }

    public static String getRandomPassword(Activity activity, int passwordSize, boolean useCharacters, boolean useNumbers, boolean useSpecialChars) {
        String availableCharacters = shuffle(
                new StringBuilder()
                        .append(useCharacters ? characters : "")
                        .append(useNumbers ? numbers : "")
                        .append(useSpecialChars ? specialCharacters : "").toString()
        );


        Random random = new Random();
        StringBuilder password = new StringBuilder();

        if (availableCharacters.length() > 0) {
            for (int idx = 0; idx < passwordSize; idx++) {
                password.append(availableCharacters.charAt(Math.abs(random.nextInt()) % availableCharacters.length()));
            }
        } else {
            Toast.makeText(activity, R.string.generated_empty_password_label, Toast.LENGTH_LONG).show();
        }

        return password.toString();
    }

    private static String shuffle(String text) {
        if (text.length() <= 1)
            return text;

        int split = text.length() / 2;

        String temp1 = shuffle(text.substring(0, split));
        String temp2 = shuffle(text.substring(split));

        if (Math.random() > 0.5)
            return temp1 + temp2;
        else
            return temp2 + temp1;
    }
}
