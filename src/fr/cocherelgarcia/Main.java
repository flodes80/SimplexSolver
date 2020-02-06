package fr.cocherelgarcia;

public class Main {

    public static void main(String[] args) {
        LinearProgram linearProgram = new LinearProgram(3, 3);
        linearProgram.generateProgram();
        linearProgram.print();
        linearProgram.toStandard();
        linearProgram.print();
        linearProgram.solve();
    }
}
