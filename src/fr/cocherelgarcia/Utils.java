package fr.cocherelgarcia;

import java.util.Random;
import java.util.Vector;

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

    /**
     * Retourne une copie profonde du vecteur de vecteur de float passé en argument
     * @param argMatrix Matrice à copier
     * @return une copie de la matrice argMatrix
     */
    public static Vector<Vector<Float>> getMatrixCopy(Vector<Vector<Float>> argMatrix){
        Vector<Vector<Float>> returnMatrix = new Vector<>();
        for(int i = 0; i < argMatrix.size(); i++) {
            returnMatrix.add(new Vector<>());
            for (int j = 0; j < argMatrix.get(i).size(); j++)
                returnMatrix.get(i).add(argMatrix.get(i).get(j));
        }
        return returnMatrix;
    }
}
