package icu.cyclone.alex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.function.Predicate;

/**
 * @author Aleksey Babanin
 * @since 2020/11/11
 */
public class ConsoleUtils {
    private static final String INVALID_VALUE = "Invalid value";

    public static String requestString(String message, Predicate<String> validator) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(message);
        String input = reader.readLine();

        while (validator != null && !validator.test(input)) {
            System.out.println(INVALID_VALUE);
            System.out.println(message);
            input = reader.readLine();
        }

        return input;
    }
}
