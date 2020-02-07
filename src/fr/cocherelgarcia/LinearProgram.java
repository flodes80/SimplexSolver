package fr.cocherelgarcia;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
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
     * Ligne du pivot
     */
    private int pivotRow;

    /**
     * Colonne du pivot
     */
    private int pivotCol;

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
        if(m > n)
            throw new ExceptionInInitializerError("Le nombre de contrainte ne peux pas être ne peux pas être supérieur au nombre de variables !");
        this.n = n;
        this.m = m;
        matrix = new Vector<>();
    }

    public LinearProgram(int n, int m, Float[]... lines){
        this(n, m);
        int i = 0;
        for(Float[] line : lines){
            matrix.add(new Vector<>());
            matrix.get(i).addAll(Arrays.asList(line));
            i++;
        }
    }

    /**
     * Génération du programme canonique aléatoire en fonction du nombre de variables décision
     * ainsi que du nombre de contraintes
     */
    public void generateProgram(){
        // Utilisation "<=" car première ligne correspond à la fonction de maximisation donc une ligne en plus dans la matrice
        // en plus des contraintes
        for(int i = 0; i <= m; i++ ){
            // Ajout d'une contrainte
            matrix.add(new Vector<>());
            for(int j = 0; j < n; j++){
                // Ajout de coefficients aléatoires aux variables de décisions
                matrix.get(i).add((float) Utils.getRandomBetween(minRand, maxRand));

                // Si on est en fin de ligne on ajoute la contrainte d'égalité / inégalité
                if(j == n-1 && i != matrix.size()-1)
                    matrix.get(i).add((float) Utils.getRandomBetween(0, maxRand));
            }
        }
    }

    /**
     * Convertit le programme canonique en programme standard
     */
    public void toStandard(){
        // Doublement du nombre de variables
        n = n + m;
        for(int i = 0; i <= m; i++){
            float equality = matrix.get(i).lastElement(); // Sauvegarde du dernier élément qui est l'égalité pour pouvoir ajouter les variables d'écart entre deux
            if(i != matrix.size()-1)
                matrix.get(i).remove(equality);

            for(int j = n/2; j < n-1; j++){
                // Si on est à la première ligne (fonction maximisation) pas d'ajout de variable d'écart, seulement pour les contraintes
                if(j == (n/2) + i && i != matrix.size()-1)
                    matrix.get(i).add(1.0f); // Ajout variable d'écart
                else
                    matrix.get(i).add(0.0f); // Sinon ajout d'une variable avec "0"
            }
            if(i != matrix.size()-1)
                matrix.get(i).add(equality);// On remet en place l'égalité si on est sur une contrainte
        }
    }

    /**
     * Fonction de recherche du prochain pivot
     */
    private boolean findPivot(){
        float bestValue = 0.0f;
        pivotCol = -1;
        pivotRow = -1;

        // Recherche de la colonne de la fonction Z avec la plus grande valeur
        for(int i = 0; i < n; i++){
            if(matrix.lastElement().get(i) > bestValue){
                bestValue = matrix.lastElement().get(i);
                pivotCol = i;
            }
        }
        // Arrêt recherche, aucun pivotage possible
        if(pivotCol == -1)
            return true;

        bestValue = Float.MAX_VALUE;
        // Recherche du plus petit ratio
        for(int i = 0; i < m; i++){
            try{
                float ratio = matrix.get(i).lastElement() / matrix.get(i).get(pivotCol);
                if(ratio > 0 && bestValue > ratio){
                    bestValue = ratio;
                    pivotRow = i;
                }
            }catch (ArithmeticException e){
                continue;
            }
        }

        // Arrêt recherche, aucun pivotage possible
        return pivotRow == -1;
    }

    /**
     * Création du nouveau tableau
     */
    private void buildNewMatrix(){
        Vector<Vector<Float>> tmpMatrix = Utils.getMatrixCopy(matrix);
        float pivot = tmpMatrix.get(pivotRow).get(pivotCol);

        // Remplissage de la colonne du pivot par 0 et 1 à la place du pivot
        for(int i = 0; i <= m; i++){
            if(i != pivotRow)
                tmpMatrix.get(i).set(pivotCol, 0.0f);
            else
                tmpMatrix.get(i).set(pivotCol, 1.0f);
        }

        // Division de la ligne du pivot par le pivot
        for(int i = 0; i < n; i++){
            float newValue = tmpMatrix.get(pivotRow).get(i) / pivot;
            tmpMatrix.get(pivotRow).set(i, newValue);
        }

        for(int i = 0; i <= m; i++){

            // Si ligne du pivot on passe à la suivante
            if(i == pivotRow)
                continue;

            for(int j = 0; j <= n; j++){

                // Si colonne du pivot on passe à la suivante ou "b" de ligne Z
                if(j == pivotCol || (j == n && i == matrix.size()-1))
                    continue;
                else{
                    float newValue = tmpMatrix.get(i).get(j) - (matrix.get(pivotRow).get(j) * matrix.get(i).get(pivotCol));
                    tmpMatrix.get(i).set(j, newValue);
                }

            }
        }
        matrix = tmpMatrix;
    }

    /**
     * Résolution du programme
     */
    public void solve(){
        boolean optimal = false;

        while(!optimal){
            // Etape 1: recherche du pivot
            optimal = findPivot();

            // Etape 2 : Construction nouveau tableau
            if(!optimal)
                buildNewMatrix();
        }
        System.out.println("Solution: ");
        print();
    }

    /**
     * Affiche le programme
     */
    public void print(){
        NumberFormat nf = new DecimalFormat("##.##");
        String format = "%-15s";
        System.out.println("--------------------------------------------------------");
        for(int i = 0; i <= m; i++){
            // Affichage ligne de maximisation
            if(i == matrix.size()-1)
                System.out.print("Max z=");

            for(int j = 0; j < n; j++){
                // Obtention valeur courante
                float value = matrix.get(i).get(j);

                // Si valeur = 0 on affiche pas
                if(value != 0.0) {
                    System.out.printf(format, nf.format(value) + "x" + (j+1) + " ");
                }
                else
                    System.out.printf(format, " ");

                // Si dernière colonne on affiche le "="
                if(j == n-1 && i != matrix.size()-1){
                    System.out.printf(format, "= " + nf.format(matrix.get(i).get(j+1)));
                }
            }
            System.out.println();
        }
        System.out.println("--------------------------------------------------------");
    }


}
