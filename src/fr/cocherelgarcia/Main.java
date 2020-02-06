package fr.cocherelgarcia;

public class Main {

    public static void main(String[] args) {
        LinearProgram linearProgram = new LinearProgram(5, 4);
        linearProgram.generateProgram();
        linearProgram.print();
    }
}
