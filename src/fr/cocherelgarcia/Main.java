package fr.cocherelgarcia;

public class Main {

    public static void main(String[] args) {
        /*Float[] z = new Float[]{ 2.0f, 3.0f, 1.0f};
        Float[] a = new Float[]{ 5.0f, 4.0f, 6.0f, 6.0f};
        Float[] b = new Float[]{ 3.0f, 0.0f, 5.0f, 4.0f};
        Float[] c = new Float[]{ 0.0f, 4.0f, 5.0f, 8.0f};
        LinearProgram linearProgram = new LinearProgram(3, 3, a, b, c, z);
        linearProgram.toStandard();
        linearProgram.solve();*/

        // Programme aléatoire
        LinearProgram linearProgram = new LinearProgram(3, 3);
        linearProgram.print();
        linearProgram.toStandard();
        linearProgram.print();
        linearProgram.solve();
    }
}
