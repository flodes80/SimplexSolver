package fr.cocherelgarcia;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Vector;

public class LinearProgram {

    /**
     * Borne inférieure pour la génération d'une variable de décision dans les contraintes
     */
    private final static int minRand = -20;

    /**
     * Borne supérieure pour la génération d'une variable de décision dans les contraintes
     */
    private final static int maxRand = 20;

    /**
     * Nombre de de variables de décision
     */
    private int n;

    /**
     * Nombre de contraintes
     */
    private int m;

    /**
     * Matrice du programme
     * La première ligne correspond à la fonction de maximisation
     * Les lignes suivantes sont les contraintes
     */
    private Vector<Vector<Float>> matrix;

    /**
     *
     * @param n Nombre de variables de décision
     * @param m Nombre de contraintes
     */
    public LinearProgram(int n, int m){
        this.n = n;
        this.m = m;
        matrix = new Vector<Vector<Float>>();
    }

    /**
     * Génération du programme canonique aléatoire en fonction du nombre de variables décision
     * ainsi que du nombre de contraintes
     */
    public void generateProgram(){
        for(int i = 0; i <= n; i++ ){
            // Ajout d'une contrainte
            matrix.add(new Vector<Float>());
            for(int j = 0; j <= m; j++){
                // Ajout d'une variable décision
                matrix.get(i).add(Float.valueOf(Utils.getRandomBetween(minRand, maxRand)));

                if(j == m && i > 0)
                    matrix.get(i).add(Float.valueOf(Utils.getRandomBetween(minRand, maxRand)));
            }
        }
    }

    /**
     * Affiche le programme
     */
    public void print(){
        NumberFormat nf = new DecimalFormat("##.##");
        String format = "%-15s";
        System.out.println("--------------------------------------------------------");
        for(int i = 0; i <= n; i++){
            // Affichage ligne de maximisation
            if(i == 0)
                System.out.print("Max z=");

            for(int j = 0; j <= m; j++){
                // Obtention valeur courante
                float value = matrix.get(i).get(j);

                // Si valeur = 0 on affiche pas
                if(value != 0.0) {
                    // Si valeur > 0 et ce n'est pas la première valeur on affiche le "+"
                    if(value > 0 && j > 0)
                        System.out.print("+");
                    System.out.printf(format, nf.format(value) + "x" + (j+1) + " ");
                }
                else
                    System.out.printf(format, " ");

                // Si dernière colonne on affiche le "="
                if(j == m && i > 0){
                    System.out.printf(format, "= " + nf.format(matrix.get(i).get(j+1)));
                }
            }
            System.out.println("");
        }
        System.out.println("--------------------------------------------------------");
    }
}
