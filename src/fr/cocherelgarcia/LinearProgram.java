package fr.cocherelgarcia;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Vector;

public class LinearProgram {

    /**
     * Borne inférieure pour la génération du vecteur profit "c"
     */
    private final static float minC = 0.0f;

    /**
     * Borne supérieure pour la génération du vecteur profit "c"
     */
    private final static float maxC = 500.0f;

    /**
     * Borne inférieure pour la génération de la matrice "A"
     */
    private final static float minA = 0.0f;

    /**
     * Borne supérieure pour la génération de la matrice "A"
     */
    private final static float maxA = 100.0f;

    /**
     * Borne inférieure pour la génération des contraintes "b"
     */
    private final static float minB = 25.0f;

    /**
     * Borne supérieure pour la génération des contraintes "b"
     */
    private final static float maxB = 250.0f;

    /**
     * Nombre de de variables de décision
     */
    private int n;

    /**
     * Nombre de contraintes
     */
    private int m;

    /**
     * Nombre d'itération
     */
    private int iter;

    /**
     * Ligne du pivot
     */
    private int pivotRow;

    /**
     * Colonne du pivot
     */
    private int pivotCol;

    /**
     * Base du programme
     */
    private int[] base;

    /**
     * Matrice du programme
     * La première ligne correspond à la fonction de maximisation
     * Les lignes suivantes sont les contraintes
     */
    private Vector<Vector<Float>> matrix;

    /**
     * Le programme est-il standard ?
     */
    private boolean isStandard;

    /**
     * Debug mode
     */
    private boolean debug = false;

    private enum End{
        NOT_OPTIMAL,
        OPTIMAL,
        NOT_BOUNDED
    }

    /**
     *  Génération d'un programme aléatoire
     * @param n Nombre de variables de décision
     * @param m Nombre de contraintes
     */
    public LinearProgram(int n, int m){
        if(m > n) {
            throw new ExceptionInInitializerError("Le nombre de contrainte ne peux pas être ne peux pas être supérieur au nombre de variables !");
        }
        this.n = n;
        this.m = m;
        this.matrix = new Vector<>();
        generateProgram();
    }

    /**
     * Génération d'un programme à partir du nombre de varaibles, du nombre de contraintes et leur définition
     * La fonction de maximisation doit être déclarée en dernière
     * @param n Nombre de variables de décision
     * @param m Nombre de contraintes
     * @param lines Définitions des contraintes avec leurs variables
     */
    public LinearProgram(int n, int m, Float[]... lines){
        if(m > n)
            throw new ExceptionInInitializerError("Le nombre de contrainte ne peux pas être ne peux pas être supérieur au nombre de variables !");
        this.n = n;
        this.m = m;
        this.matrix = new Vector<>();
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
    private void generateProgram(){
        for(int i = 0; i <= m; i++ ){
            // Ajout d'une contrainte
            matrix.add(new Vector<>());
            for(int j = 0; j < n; j++){
                if(i == m)
                    matrix.get(i).add(Utils.getRandomBetween(minC, maxC));  // Ajout de coefficients aléatoires à la fonction de maximisation
                else
                    matrix.get(i).add(Utils.getRandomBetween(minA, maxA)); // Ajout de coefficients aléatoires aux variables de décisions

                // Si on est en fin de ligne on ajoute la contrainte d'égalité / inégalité
                if(j == n-1 && i != m )
                    matrix.get(i).add(Utils.getRandomBetween(minB, maxB));
            }
        }
    }

    /**
     * Convertit le programme canonique en programme standard
     */
    private void toStandard(){
        // Doublement du nombre de variables
        n = n + m;
        // Création et initialisation de la base
        base = new int[m];
        for(int i = 0; i <= m; i++){
            float equality = matrix.get(i).lastElement(); // Sauvegarde du dernier élément qui est l'égalité pour pouvoir ajouter les variables d'écart entre deux
            // Si ligne de maximisation (dernière ligne), pas de suppression d'égalité
            if(i != matrix.size()-1)
                matrix.get(i).remove(matrix.get(i).lastIndexOf(equality));

            for(int j = n-m; j < n; j++){
                // Si on est à la (fonction maximisation) pas d'ajout de variable d'écart, seulement pour les contraintes
                if(j == n-m + i && i != matrix.size()-1)
                    matrix.get(i).add(1.0f); // Ajout variable d'écart
                else
                    matrix.get(i).add(0.0f); // Sinon ajout d'une variable avec "0"
            }
            if(i != matrix.size()-1){
                matrix.get(i).add(equality);// On remet en place l'égalité si on est sur une contrainte
                base[i] = matrix.get(i).lastIndexOf(1.0f);
            }
        }
        isStandard = true;
    }

    /**
     * Fonction de recherche du prochain pivot
     * Retourne si la solution n'est pas bornée, si elle est optimale ou non
     */
    private End findPivot(){
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
        if(pivotCol == -1 && iter > 0)
            return End.OPTIMAL;
        else if(pivotCol == -1 && iter == 0)
            return End.NOT_BOUNDED;
        else
            pivotCol = matrix.lastElement().indexOf(bestValue);

        bestValue = Float.MAX_VALUE;
        // Recherche du plus petit ratio
        for(int i = 0; i < m; i++){
            try{
                float ratio = matrix.get(i).lastElement() / matrix.get(i).get(pivotCol);
                if(ratio > 0 && bestValue > ratio){
                    bestValue = ratio;
                    pivotRow = matrix.indexOf(matrix.get(i));
                }
            }catch (ArithmeticException e){
                continue;
            }
        }

        // Si pivot trouvé on continue
        if(pivotRow != -1){
            base[pivotRow] = pivotCol;  // Mise à jour de la base
            if(debug)
                System.out.println("Nouveau pivot (" + pivotRow + "," + pivotCol +") = " + matrix.get(pivotRow).get(pivotCol));
            return End.NOT_OPTIMAL;
        }

        for(int i = 0; i < n; i++){
            if(matrix.lastElement().get(i) > 0)
                return End.NOT_BOUNDED;
        }
        return End.OPTIMAL;
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
        for(int i = 0; i <= n; i++){
            // On ne touche pas à la colonne du pivot
            if(i != pivotCol) {
                float newValue = tmpMatrix.get(pivotRow).get(i) / pivot;
                tmpMatrix.get(pivotRow).set(i, newValue);
            }
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
                    float newValue = tmpMatrix.get(i).get(j) - (matrix.get(pivotRow).get(j) * matrix.get(i).get(pivotCol)) / matrix.get(pivotRow).get(pivotCol);
                    tmpMatrix.get(i).set(j, newValue);
                }

            }
        }
        matrix = tmpMatrix;
    }

    /**
     * Résolution du programme
     */
    public boolean solve(){
        End end = End.NOT_OPTIMAL;
        iter = 0;
        if(!isStandard)
            toStandard();
        while(end != End.NOT_BOUNDED && end != End.OPTIMAL){
            if(debug)
                print();

            // Etape 1: recherche du pivot
            end = findPivot();

            // Etape 2 : Construction nouveau tableau
            if(end == End.NOT_OPTIMAL){
                buildNewMatrix();
                iter++;
            }
        }
        if(debug)
            printSolution(end);
        return end == End.OPTIMAL;
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
            else if(isStandard)
                System.out.print("x"+(base[i] + 1)+"     ");

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


    /**
     * Affiche la solution finale
     */
    public void printSolution(End end){
        if(end == End.NOT_BOUNDED){
            System.out.println("Programme impossible ou non borné.");
        }
        else{
            System.out.println("Solution:");
            for(int i = 0; i < n; i++){
                float value = 0.0f;
                for(int j = 0; j < base.length; j++){
                    if(base[j] == i)
                        value = matrix.get(j).lastElement();
                }
                System.out.println("x" + (i+1) + " = " + value);
            }
        }

    }

}
