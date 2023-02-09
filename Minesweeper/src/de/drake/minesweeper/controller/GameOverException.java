package de.drake.minesweeper.controller;

/**
 * Fehlerklasse zum Umgang mit Spielende. Wird geworfen, wenn das Spiel
 * gewonnen oder verloren wurde, um dann alle aktiven Operationen abbrechen zu k�nnen.
 */
public class GameOverException extends Exception {
	
	/**
	 * Die serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Konstruktor f�r ein GameOver-Objekt.
	 * @param message
	 * 		Speichert die Fehlerursache. Zul�ssige Werte sind "GameWon" und
	 * 		"GameLost".
	 */
	GameOverException(final String message) {
		super(message);
	}
}