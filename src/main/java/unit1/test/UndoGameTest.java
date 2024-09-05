package unit1.test;

import unit1.UndoGame;

class UndoGameTest {

    public static void main(String[] args) {
        UndoGame nim = new UndoGame(30);
        nim.play(System.out, System.in);
    }
}