package de.drake.minesweeper.model;

/**
 * Eine Tabelle (eher: Matrix), in der m�gliche in der Regel unvollst�ndige
 * Minenverteilungen enthalten sind. Wird vom ProbabilityCalculator verwendet.
 */
class Besetzungstabelle {
	
	/**
	 * Speichert das Minesweeper-Spielfeld.
	 */
	private Spielfeld spielfeld;
	
	/**
	 * Speichert die Anzahl der Minen, die im Rahmen der Minenbesetzung platziert
	 * wurden.
	 */
	private int neueMinen = 0;
	
	/**
	 * Speichert die Anzahl der Felder, die im Rahmen der Minenbesetzung als minenfrei
	 * markiert wurden.
	 */
	private int neueMinenfreieFelder = 0;
	
	/**
	 * Die H�he des Minesweepers.
	 */
	private int hoehe;
	
	/**
	 * Die Breite des Minesweepers.
	 */
	private int breite;
	
	/**
	 * Die m�glichen Besetzungen der Minesweeperfelder
	 */
	private enum Besetzung {Mine, keineMine, unbekannt};
	
	/**
	 * Interne Datenstruktur f�r die Besetzungstabelle
	 */
	private Besetzung[][] besetzungstabelle;
	
	/**
	 * Initialisiert eine Besetzungstabelle. Bereits gefundene Minen oder aufgedeckte
	 * Felder werden bereits ber�cksichtigt.
	 * 
	 * @param spielfeld
	 * 		Das Minesweeper zum Nachschlagen der Modellparameter
	 * @param hoehe
	 * 		Die H�he des Minesweepers
	 * @param breite
	 * 		Die Breite des Minesweepers
	 */
	Besetzungstabelle(final Spielfeld spielfeld, final int hoehe, final int breite) {
		this.spielfeld = spielfeld;
		this.hoehe = hoehe;
		this.breite = breite;
		this.besetzungstabelle = new Besetzung[this.hoehe][this.breite];
		for (int zeile = 0; zeile < this.hoehe; zeile++)
			for (int spalte = 0; spalte < this.breite; spalte++) {
				if (this.spielfeld.isMarkedAsMine(zeile, spalte)) {
					this.besetzungstabelle[zeile][spalte] = Besetzung.Mine;
				} else if (this.spielfeld.isUncovered(zeile, spalte)) {
					this.besetzungstabelle[zeile][spalte] = Besetzung.keineMine;
				} else {
					this.besetzungstabelle[zeile][spalte] = Besetzung.unbekannt;
				}
			}
	}
	
	/**
	 * Pr�ft, ob die Minenbesetzungstabelle noch eine g�ltige L�sung f�r das Minesweeper
	 * zul�sst. Hierbei wird nur die letzte �nderung ber�cksichtigt.
	 * 
	 * @param geaenderteZeile
	 * 		Die Zeile der letzten �nderung
	 * @param geaenderteSpalte
	 * 		Die Spalte der letzten �nderung
	 */
	boolean isStillValid(final int geaenderteZeile, final int geaenderteSpalte) {
		if (this.neueMinen > this.spielfeld.getVerbleibendeMinen())
			return false;
		for (int zeile = Math.max(0,geaenderteZeile-1);
				zeile < Math.min(this.hoehe, geaenderteZeile+2); zeile++)
			for (int spalte = Math.max(0,geaenderteSpalte-1);
					spalte < Math.min(this.breite, geaenderteSpalte+2); spalte++) 
				if (this.spielfeld.isUncovered(zeile, spalte) &&
						(  this.getBenachbarteFelderInTabelle(Besetzung.Mine, zeile, spalte)
						   > this.spielfeld.getBenachbarteMinen(zeile, spalte)
						||
						   this.getBenachbarteFelderInTabelle(Besetzung.Mine, zeile, spalte)
						   + this.getBenachbarteFelderInTabelle(
							   Besetzung.unbekannt, zeile, spalte)
						   < this.spielfeld.getBenachbarteMinen(zeile, spalte)
						)) {
					return false;
				}
		return true;
	}
	
	/**
	 * Z�hlt zu einem Feld die benachbarten Felder eines bestimmten Typs.
	 * 
	 * @param besetzung
	 * 		Der Typ der zu z�hlenden Felder (normalerweise Mine oder unbekannt)
	 * @param zeile
	 * 		Die Zeile des Feldes, deren Nachbarfelder gefragt sind
	 * @param spalte
	 * 		Die Spalte des Feldes, deren Nachbarfelder gefragt sind
	 * @return Die Nachbarfelder des bestimmten Typs
	 */
	private int getBenachbarteFelderInTabelle(final Besetzung besetzung,
			final int zeile, final int spalte) {
		int benachbarteFelder = 0;
		for (int nachbarzeile = Math.max(0,zeile-1);
				nachbarzeile < Math.min(this.hoehe, zeile+2); nachbarzeile++)
			for (int nachbarspalte = Math.max(0,spalte-1);
					nachbarspalte < Math.min(this.breite, spalte+2); nachbarspalte++)
				if (this.besetzungstabelle[nachbarzeile][nachbarspalte] == besetzung)
					benachbarteFelder++;
		if (this.besetzungstabelle[zeile][spalte] == besetzung)
			benachbarteFelder--;
		return benachbarteFelder;
	}
	
	/**
	 * Liefert die erste in A verwendete Koordinate, wo die Besetzung noch unbekannt ist.
	 * 
	 * @return Die Koordinaten des Feldes
	 * @throws RuntimeException
	 * 		wird geworfen, wenn alle in A betroffenen Felder bereits belegt wurden
	 * 		und daher kein weiterer Index gefunden werden kann.
	 */
	Koordinate getNextUnknownIndexInA() throws RuntimeException {
		for (int zeile = 0; zeile < this.hoehe; zeile++)
			for (int spalte = 0; spalte < this.breite; spalte++)
				if (this.besetzungstabelle[zeile][spalte] == Besetzung.unbekannt
						&& this.spielfeld.hasUncoveredNeighbor(zeile, spalte))
					return new Koordinate(zeile, spalte);
		throw new RuntimeException("Kein weiterer Index mehr gefunden.");
	}

	/**
	 * Belegt ein unbekanntes Feld mit einer Mine.
	 * 
	 * @param feld
	 * 		Die Koordinaten des zu belegenden Feldes
	 */
	void setMine(Koordinate feld) {
		this.besetzungstabelle[feld.getZeile()][feld.getSpalte()] = Besetzung.Mine;
		this.neueMinen += 1;
	}
	
	/**
	 * Markiert ein Minenfeld als minenfrei
	 * 
	 * @param feld
	 * 		Die Koordinaten des zu belegenden Feldes
	 */
	void setKeineMine(Koordinate feld) {
		this.besetzungstabelle[feld.getZeile()][feld.getSpalte()] = Besetzung.keineMine;
		this.neueMinen -= 1;
		this.neueMinenfreieFelder += 1;
	}
	
	/**
	 * Markiert ein minenfreies Feld als unbekannt.
	 * 
	 * @param feld
	 * 		Die Koordinaten des zu belegenden Feldes
	 */
	void setUnbekannt(Koordinate feld) {
		this.besetzungstabelle[feld.getZeile()][feld.getSpalte()] = Besetzung.unbekannt;
		this.neueMinenfreieFelder -= 1;
	}
	
	/**
	 * Gibt die Anzahl der Minen aus, die im Rahmen der Minenbesetzung neu platziert
	 * wurden.
	 * 
	 * @return die Anzahl der entsprechenden Minen
	 */
	int getNeueMinen() {
		return this.neueMinen;
	}
	
	/**
	 * Gibt die Anzahl der Felder aus, die im Rahmen der Minenbesetzung neu als minenfrei
	 * markiert wurden.
	 * 
	 * @return die Anzahl der entsprechenden Felder
	 */
	int getNeueMinenfreieFelder() {
		return this.neueMinenfreieFelder;
	}
	
	/**
	 * Gibt aus, ob ein bestimmtes Feld der Besetzungstabelle als Mine markiert ist.
	 * 
	 * @return true, wenn dort eine Mine markiert wurde
	 */
	boolean isMine(final int zeile, final int spalte) {
		return (this.besetzungstabelle[zeile][spalte] == Besetzung.Mine);
	}
}