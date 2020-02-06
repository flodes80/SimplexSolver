package fr.cocherelgarcia;

import java.util.Random;

public class Utils {

    /**
     * Renvoi un nombre entre deux bornes (inclusives)
     * @param min Borne inférieure
     * @param max Borne supérieure
     * @return Nombre aléatoire entre min et max
     */
    public static int getRandomBetween(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
