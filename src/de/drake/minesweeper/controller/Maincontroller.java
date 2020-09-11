package de.drake.minesweeper.controller;

import de.drake.minesweeper.view.CustomParametersMenu;

	/**
	 * Controller zum Starten von Minesweeper-Spielen. 
	 * Verwaltet das Fenster zum Erstellen eines Minesweeperspiels
	 * nach benutzerdefinierten Parametern.
	 * @author Drake
	 */
public class Maincontroller {

	/**
	 * Das aktuell aktive Minesweeper-Spiel
	 */
	private Gamecontroller gamecontroller = null;
	
	/**
	 * Konstruktor, der die Parameterkonfiguration aufruft
	 */
	private Maincontroller() {
		this.startNewGame(20,20,134);
	}
	
	/**
	 * Startet ein neues Spiel.
	 * 
	 * @param hoehe
	 * 		Die Höhe des Minesweepers
	 * @param breite
	 * 		Die Breite des Minesweepers
	 * @param minenzahl
	 * 		Die Anzahl der Minen im Minesweeper
	 */
	public void startNewGame(final int hoehe, final int breite, final int minenzahl) {
		if (this.gamecontroller != null)
			this.gamecontroller.endGame();
		this.gamecontroller = new Gamecontroller(hoehe, breite, minenzahl, this);
	}
	
	/**
	 * Öffnet ein Fenster zur Eingabe von benutzerdefinierten
	 * Minesweeper-Einstellungen.
	 * 
	 * @param hoehe
	 * 		die voreingestellte Höhe des Minesweepers
	 * @param breite
	 * 		die voreingestellte Breite des Minesweepers
	 * @param minenzahl
	 * 		die voreingestellte Gesamtzahl der Minen im Minesweeper
	 */
	public void selectCustomParameters(final int hoehe, final int breite,
			final int minenzahl) {
		if (this.gamecontroller != null)
			this.gamecontroller.endGame();
		CustomParametersMenu customParametersMenu = new CustomParametersMenu(
				"Benutzerdefiniert", this, hoehe, breite, minenzahl);
		customParametersMenu.setVisible(true);
	}

	/**
	 * Startet das Minesweeper-Programm.
	 * 
	 * @param args
	 * 		wird ignoriert
	 */
	public static void main(String[] args) {
		new Maincontroller();
	}
}