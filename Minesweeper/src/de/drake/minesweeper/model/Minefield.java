package de.drake.minesweeper.model;

/**
 * Repräsentiert ein Feld eines Minesweepers.
 */
class Minefield {
	
	/**
	 * Indiziert, ob das Feld bereits aufgedeckt wurde
	 */
	boolean isUncovered = false;
	
	/**
	 * Indiziert, ob auf dem Feld eine Mine liegt
	 */
	boolean isMine = false;
	
	/**
	 * Indiziert, ob ein Feld als Mine markiert wurde
	 */
	boolean isMarkedAsMine = false;
	
	/**
	 * Beinhaltet die Anzahl der Minen in den umgebenden Feldern
	 */
	int benachbarteMinen = 0;
}