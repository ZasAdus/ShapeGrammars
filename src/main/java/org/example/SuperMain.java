package org.example;

public class SuperMain{
    public static void main(String[] args){           // Jar ma problemy z mainem rozszerzającym Application, dlatego main wywowyłany jest przez inną klasę
        Main.main(args);
    }
}
