package de.drake.minesweeper.model;

/**
 * Modelliert das gesamte Minesweeper-Spielfeld
 */
public class Spielfeld {
	
	// Instanzattribute
	
	/**
	 * Die Höhe des Minesweepers.
	 */
	private int hoehe;
	
	/**
	 * Die Breite des Minesweepers.
	 */
	private int breite;
	
	/**
	 * Speichert die einzelnen Felder des Minesweepers.
	 */
	private Minefield[][] spielfeld;
	
	/**
	 * Tool zur Berechnung von Minenwahrscheinlichkeiten
	 */
	private ProbabilityCalculator probabilityCalculator;
	
	/**
	 * Speichert die verbleibenden Minen des Minesweepers.
	 * Diese ergibt sich aus der Gesamtzahl der Minen abzüglich den
	 * markierten Minen (auch wenn Minen falsch markiert wurden).
	 */
	private int verbleibendeMinen;
	
	/**
	 * Speichert die Anzahl der Felder, die für einen Sieg noch aufgedeckt
	 * werden müssen. Diese ergibt sich aus der Anzahl der verbleibenden
	 * verdeckten Feldern abzüglich der Gesamtzahl der Minen.
	 */
	private int verbleibendeFelder;
	
	
	// Konstruktoren
	
	/**
	 * Erzeugt ein neues Minesweeper und platziert darin zufällig Minen.
	 * 
	 * @throws RuntimeException
	 * 		Wird geworfen, mehr Minen platziert werden sollen als
	 * 		das Minesweeper Felder hat.
	 * @param hoehe
	 * 		die Höhe des Minesweepers
	 * @param breite
	 * 		die Breite des Minesweepers
	 * @param minenzahl
	 * 		die Gesamtzahl der Minen, die im Minesweeper-Feld verteilt werden sollen
	 */
	public Spielfeld(final int hoehe, final int breite, final int minenzahl)
			throws RuntimeException {
		if (hoehe * breite < minenzahl)
			throw new RuntimeException("Spielfelderzeugung fehlgeschlagen. " +
					"Zu viele Minen!");
		this.hoehe = hoehe;
		this.breite = breite;
		this.verbleibendeMinen = minenzahl;
		this.verbleibendeFelder = this.hoehe * this.breite - minenzahl;
		this.spielfeld = new Minefield[this.hoehe][this.breite];
		for (int zeile = 0; zeile < this.hoehe; zeile++) {
			for (int spalte = 0; spalte < this.breite; spalte++) {
				this.spielfeld[zeile][spalte] = new Minefield();
			}
		}
		this.placeMines(this.hoehe, this.breite, minenzahl);
		this.probabilityCalculator = new ProbabilityCalculator(
				this, this.hoehe, this.breite);
	}
	
	
	// get/set/is-Methoden
	
	/**
	 * Gibt zurück, ob ein bestimmes Feld aufgedeckt ist oder nicht.
	 * 
	 * @param zeile
	 * 		die Zeile des zu überprüfenden Feldes
	 * @param spalte
	 * 		die Spalte des zu überprüfenden Feldes
	 * @return true, wenn das Feld bereits aufgedeckt ist
	 */
	public boolean isUncovered(final int zeile, final int spalte) {
		return this.spielfeld[zeile][spalte].isUncovered;
	}
	
	/**
	 * Markiert ein bestimmtes Feld als aufgedeckt.
	 * 
	 * @param zeile
	 * 		die Zeile des zu überprüfenden Feldes
	 * @param spalte
	 * 		die Spalte des zu überprüfenden Feldes
	 */
	public void setUncovered(final int zeile, final int spalte) {
		if (this.isUncovered(zeile, spalte)) 
			return;
		if (!this.isMine(zeile, spalte)) {
			this.verbleibendeFelder -= 1;
		} else {
			this.verbleibendeMinen -= 1;
		}
		this.probabilityCalculator.notifyOfChangedProbabilities();
		this.spielfeld[zeile][spalte].isUncovered = true;
	}
	
	/**
	 * Gibt die Minenwahrscheinlichkeit für ein bestimmtes Feld des Minesweepers aus.
	 * 
	 * @param zeile
	 * 		Die Zeile des Feldes, dessen Minenwahrscheinlichkeit berechnet werden soll
	 * @param spalte
	 * 		Die Spalte des Feldes, dessen Minenwahrscheinlichkeit berechnet werden soll
	 * @param isInterrupted
	 * 		Variable, die indiziert, dass die Berechnung abgebrochen werden soll.
	 * 		Soll die Berechnung nicht abbrechbar sein, kann hier einfach
	 * 		false angegeben werden.
	 * @return Die Wahrscheinlichkeit, das das angegebene Feld eine Mine beinhaltet
	 * @throws InterruptedException
	 * 		Wird geworfen, wenn die Berechnung
	 * 		unterbrochen wurde
	 */
	public double getMinenwahrscheinlichkeit(final int zeile, final int spalte)
			throws InterruptedException {
		return(this.probabilityCalculator.getMinenwahrscheinlichkeit(
				zeile, spalte));
	}
	
	/**
	 * Gibt zurück, ob ein bestimmes Feld eine Mine beinhaltet.
	 * 
	 * @param zeile
	 * 		die Zeile des zu überprüfenden Feldes
	 * @param spalte
	 * 		die Spalte des zu überprüfenden Feldes
	 * @return true, wenn das Feld eine Mine beinhaltet
	 */
	public boolean isMine(final int zeile, final int spalte) {
		return this.spielfeld[zeile][spalte].isMine;
	}
	
	/**
	 * Gibt zurück, ob ein bestimmes Feld als Mine markiert ist.
	 * 
	 * @param zeile
	 * 		die Zeile des zu überprüfenden Feldes
	 * @param spalte
	 * 		die Spalte des zu überprüfenden Feldes
	 * @return true, wenn das Feld als Mine markiert ist
	 */
	public boolean isMarkedAsMine(final int zeile, final int spalte) {
		return this.spielfeld[zeile][spalte].isMarkedAsMine;
	}
	
	/**
	 * Markiert ein Feld als Mine bzw.
	 * entfernt eine Minenmarkierung von einem Feld.
	 * 
	 * @param zeile
	 * 		die Zeile des zu überprüfenden Feldes
	 * @param spalte
	 * 		die Spalte des zu überprüfenden Feldes
	 * @param wert
	 * 		true, wenn das Feld als Mine markiert werden soll,
	 * 		false wenn die Markierung entfernt werden soll
	 */
	public void setMarkedAsMine(final int zeile, final int spalte, final boolean wert) {
		if (wert == false)
			this.verbleibendeMinen += 1;
		if (wert == true)
			this.verbleibendeMinen -= 1;
		try {
			if (this.probabilityCalculator.isMineprobabilityUpToDate() &&
					(wert == false || this.getMinenwahrscheinlichkeit(zeile,
							spalte) < 0.9999))
				this.probabilityCalculator.notifyOfChangedProbabilities();
		} catch (InterruptedException e) {
			// Kann nicht vorkommen, da
			// this.probabilityCalculator.isMineprobabilityUpToDate()
			// geprüft wurde und hierbei keine Unterbrechung passieren kann
		}
		this.spielfeld[zeile][spalte].isMarkedAsMine = wert;
	}
	
	/**
	 * Gibt die Anzahl der Minen zurück, die ein bestimmtes Feld umgeben.
	 * 
	 * @param zeile
	 * 		die Zeile des zu bestimmten Feldes
	 * @param spalte
	 * 		die Spalte des zu bestimmten Feldes
	 * @return die gefragte Anzahl der Minen
	 */
	public int getBenachbarteMinen(final int zeile, final int spalte) {
		return this.spielfeld[zeile][spalte].benachbarteMinen;
	}
	
	/**
	 * Gibt die verbleibende Anzahl verdeckter Felder zurück.
	 * 
	 * @return die verbleibende Anzahl Felder
	 */
	public int getVerbleibendeFelder() {
		return this.verbleibendeFelder;
	}
	
	/**
	 * Gibt die verbleibende Anzahl unmarkierter Minen zurück.
	 * 
	 * @return die verbleibende Anzahl Minen
	 */
	public int getVerbleibendeMinen() {
		return this.verbleibendeMinen;
	}
	
	
	// Weitere Instanzmethoden
	
	/**
	 * Prüft, ob ein Feld des Minesweepers neben einer aufgedeckten Zahl liegt
	 * 
	 * @param zeile
	 * 		die Zeile des Feldes
	 * @param spalte
	 * 		die Spalte des Feldes
	 * @return true, wenn ein aufgedecktes Nachbarfeld existiert
	 */
	public boolean hasUncoveredNeighbor(final int zeile, final int spalte) {
		for (int nachbarzeile = Math.max(0,zeile-1);
				nachbarzeile < Math.min(this.hoehe, zeile+2); nachbarzeile++)
			for (int nachbarspalte = Math.max(0,spalte-1);
					nachbarspalte < Math.min(this.breite, spalte+2); nachbarspalte++)
				if (this.isUncovered(nachbarzeile, nachbarspalte))
					return true;
		return false;
	}
	
	/**
	 * Plaziert zufällig die Minen des Minesweepers.
	 */
	private void placeMines(final int hoehe, final int breite, final int minenzahl) {
		int zuPlazierendeMinen = minenzahl;
		while (zuPlazierendeMinen != 0) {
			final int zufaelligeZeile = (int) (Math.random() * hoehe);
			final int zufaelligeSpalte = (int) (Math.random() * breite);
			if (this.spielfeld[zufaelligeZeile][zufaelligeSpalte].isMine)
				continue;
			this.spielfeld[zufaelligeZeile][zufaelligeSpalte].isMine = true;
			zuPlazierendeMinen -= 1;
			if (zufaelligeZeile > 0)
				this.spielfeld[zufaelligeZeile-1][zufaelligeSpalte].benachbarteMinen += 1;
			if (zufaelligeZeile > 0 && zufaelligeSpalte > 0)
				this.spielfeld[zufaelligeZeile-1][zufaelligeSpalte-1].benachbarteMinen += 1;
			if (zufaelligeZeile > 0 && zufaelligeSpalte < breite-1)
				this.spielfeld[zufaelligeZeile-1][zufaelligeSpalte+1].benachbarteMinen += 1;
			if (zufaelligeSpalte > 0)
				this.spielfeld[zufaelligeZeile][zufaelligeSpalte-1].benachbarteMinen += 1;
			if (zufaelligeSpalte < breite-1)
				this.spielfeld[zufaelligeZeile][zufaelligeSpalte+1].benachbarteMinen += 1;
			if (zufaelligeZeile < hoehe-1)
				this.spielfeld[zufaelligeZeile+1][zufaelligeSpalte].benachbarteMinen += 1;
			if (zufaelligeZeile < hoehe-1 && zufaelligeSpalte > 0)
				this.spielfeld[zufaelligeZeile+1][zufaelligeSpalte-1].benachbarteMinen += 1;
			if (zufaelligeZeile < hoehe-1 && zufaelligeSpalte < breite-1)
				this.spielfeld[zufaelligeZeile+1][zufaelligeSpalte+1].benachbarteMinen += 1;
		}
	}

	/**
	 * Teilt dem ProbabilityCalculator mit, ob die nächsten 
	 * Wahrscheinlichkeitsberechnungen unterbrochen werden sollen oder nicht.
	 * @param wert
	 * 		bei "true" werden die Berechnungen unterbrochen, bei "false" nicht.
	 */
	public void setInterruptionDetected(final boolean wert) {
		this.probabilityCalculator.setInterruptionDetected(wert);
	}
	
	/**
	 * Gibt zurück, ob derzeit eine Berechnung läuft, die unterbrochen werden soll
	 * @return "true", wenn die Berechnung unterbrochen werden soll
	 */
	public boolean calculationIsInterrupted() {
		return this.probabilityCalculator.isInterrupted();
	}
}