package gp.parcer.gp_parcer;

import java.util.Random;

public class Generator {

    public static final int REF_CODE_LEN = 6;
    private static Random random = new Random();

    public static boolean doRandom() {
        int length = random.nextInt(100);
        if (length < 20){
            return true;
        }

        return false;
    }

    public static String generate() {
        int length = random.nextInt(REF_CODE_LEN);
        final String characters = " abcdefghijklmnopqrstuvwxyzABCDEFGHIJLMNOPQRSTUVWXYZ1234567890";
        StringBuilder result = new StringBuilder();
        while (length > 0) {
            Random rand = new Random();
            result.append(characters.charAt(rand.nextInt(characters.length())));
            length--;
        }
        return result.toString().toUpperCase();
    }
}
