package de.drake.minesweeper.controller;

/**
 * Fehlerklasse zum Umgang mit Spielende. Wird geworfen, wenn das Spiel
 * gewonnen oder verloren wurde, um dann alle aktiven Operationen abbrechen zu können.
 */
public class GameOverException extends Exception {
	
	/**
	 * Die serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Konstruktor für ein GameOver-Objekt.
	 * @param message
	 * 		Speichert die Fehlerursache. Zulässige Werte sind "GameWon" und
	 * 		"GameLost".
	 */
	GameOverException(final String message) {
		super(message);
	}
}