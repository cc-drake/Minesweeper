package de.drake.minesweeper.model;

import java.util.ArrayList;

/**
 * Repräsentation von zweidimensionalen Koordinaten
 */
public class Koordinate {
	
	/**
	 * Die interne Datenstruktor zur Speicherung der Koordinatenposition
	 */
	ArrayList<Integer> koordinatenliste = new ArrayList<Integer>(2);

	/**
	 * Konstruktor für eine neue Koordinate
	 * 
	 * @param zeile
	 * 		die Zeile der neuen Koordinate
	 * @param spalte
	 * 		die Spalte der neuen Koordinate
	 */
	public Koordinate(final int zeile, final int spalte) {
		koordinatenliste.add(zeile);
		koordinatenliste.add(spalte);
	}
	
	/**
	 * Prüft zwei Koordinaten auf Gleichheit
	 * 
	 * @return true, wenn gleich
	 */
	public boolean equals(Koordinate andereKoordinate) {
		return koordinatenliste.equals(andereKoordinate.koordinatenliste);
	}
	
	/**
	 * Stellt eine Koordinate als String dar
	 * 
	 * @return den erstellten String
	 */
	public String toString() {
		return koordinatenliste.toString();
	}
	
	/**
	 * get-Methode für die Zeile der Koordinate
	 * 
	 * @return die Zeile der Koordinate
	 */
	public int getZeile() {
		return koordinatenliste.get(0);
	}
	
	/**
	 * get-Methode für die Spalte der Koordinate
	 * 
	 * @return die Spalte der Koordinate
	 */
	public int getSpalte() {
		return koordinatenliste.get(1);
	}
}