package fr.cocherelgarcia;

public class Main {

    public static void main(String[] args) {
        Float[] z = new Float[]{ 3.0f, 1.0f, 2.0f};
        Float[] a = new Float[]{ 1.0f, 0.0f, 4.0f, 8.0f};
        Float[] b = new Float[]{ -2.0f, 1.0f, -5.0f, 10.0f};
        LinearProgram linearProgram = new LinearProgram(3, 2, a, b, z);
        System.out.println("Version canonique:");
        linearProgram.print();
        linearProgram.toStandard();
        System.out.println("Version standard:");
        linearProgram.print();
        linearProgram.solve();

        // Programme aléatoire
        /*LinearProgram linearProgram = new LinearProgram(3, 3);
        linearProgram.generateProgram();
        linearProgram.print();
        linearProgram.toStandard();
        linearProgram.print();
        linearProgram.solve();*/
    }
}
