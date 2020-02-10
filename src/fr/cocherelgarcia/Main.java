package fr.cocherelgarcia;

public class Main {

    public static void main(String[] args) {
        final int vars = 500; // Nombre de variables initiales
        final int varsInc = 500; // Incrémentation du nombre de variables
        final int varsMax = 10000; // Nombre de variables maximales
        final int cont = 50;    // Nombre de contraintes initiales
        final int contInc = 50; // Incrémention du nombre de contraintes
        final int contMax = 500;   // Nombre de contraintes maximales
        /*Float[] z = new Float[]{ 2.0f, 3.0f, 1.0f};
        Float[] a = new Float[]{ 5.0f, 4.0f, 6.0f, 6.0f};
        Float[] b = new Float[]{ 3.0f, 0.0f, 5.0f, 4.0f};
        Float[] c = new Float[]{ 0.0f, 4.0f, 5.0f, 8.0f};
        LinearProgram linearProgram = new LinearProgram(3, 3, a, b, c, z);
        linearProgram.toStandard();
        linearProgram.solve();*/

        // Programme aléatoire
        /*LinearProgram linearProgram = new LinearProgram(3, 3);
        linearProgram.print();
        linearProgram.toStandard();
        linearProgram.print();
        linearProgram.solve();*/

        // Variables
        for(int n = vars; n <= varsMax; n+=varsInc){
            System.out.print("n=" + n);
            // Contraintes
            for(int m = cont; m <= contMax; m+=contInc){
                // Programmes
                long timeBeforeStart = System.currentTimeMillis();
                for(int p = 0; p <= 0; p++){
                    LinearProgram lp = new LinearProgram(n, m);
                    lp.solve();
                }
                long executionTime = System.currentTimeMillis() - timeBeforeStart;
                System.out.print(" " + executionTime + "ms");
            }
            System.out.println();
        }
    }
}
