package de.drake.minesweeper.model;

/**
 * Stellt Methoden zur Berechnung der Minenwahrscheinlichkeit zur Verfügung.
 * Wir modellieren das Minesweeper wie folgt: Die Felder X_ij sind hypergeometrische
 * Bernoulli-Variablen mit sum X_ij = minenzahl (1=Bombe, 0=keine Bombe).
 * Bei einem teilweise gelösten Minesweeper haben wir Informationen A={X_11+X_12=1,...}
 * und B={X_11=0,X_13=1,...}. Bedingt man nach B, so bilden die verbleibenden Felder
 * wieder ein Minesweeper-Modell mit verbleibendeMinen Minen und
 * (verbleibendeFelder + verbleibendeMinen) Feldern. Das zugehörige Wahrscheinlichkeitsmaß
 * für dieses Modell bezeichnen wir als P_B.
 */
class ProbabilityCalculator {
	
	/**
	 * Speichert das Minesweeper-Spielfeld.
	 */
	private Spielfeld spielfeld;
	
	/**
	 * Die Höhe des Minesweepers.
	 */
	private int hoehe;
	
	/**
	 * Die Breite des Minesweepers.
	 */
	private int breite;
	
	/**
	 * Indiziert, ob die Berechnungen aufgrund der hohen Rechenzeit
	 * abgebrochen werden soll.
	 * Ist diese Variable true, wird zum nächstmöglichen Zeitpunkt eine
	 * InterruptedException geworfen.
	 */
	private boolean interruptionDetected = false;
	
	/**
	 * Speichert, ob seit der letzten Wahrscheinlichkeitsberechnung Anderungen
	 * am Modell vorgenommen wurden. Wird benötigt, da bei einer Änderung 
	 * am Modell sämtliche Wahrscheinlichkeiten neu berechnet werden müssen.
	 */
	private boolean probabilitiesAreUpToDate = false;
	
	/**
	 * Speichert die Wahrscheinlichkeit aller Felder, eine Mine zu beinhalten.
	 * Muss bei Änderungen am Minesweeper komplett neu berechnet werden.
	 */
	private double[][] mineprobability;
	
	/**
	 * Enthält das Zwischenergebnis P_B(A).
	 */
	private double modellwahrscheinlichkeit;
	
	/**
	 * Initialisiert den ProbabilityCalculator.
	 * 
	 * @param spielfeld
	 * 		Das Minesweeper zum Nachschlagen der Modellparameter
	 * @param hoehe
	 * 		Die Höhe des Minesweepers
	 * @param breite
	 * 		Die Breite des Minesweepers
	 */
	ProbabilityCalculator(final Spielfeld spielfeld, final int hoehe,
			final int breite) {
		this.spielfeld = spielfeld;
		this.hoehe = hoehe;
		this.breite = breite;
	}
	
	/**
	 * Gibt zurück, ob sich aktuelle Minenwahrscheinlichkeiten im Speicher befinden.
	 * Hiervon hängt es ab, ob beim Aufruf von getMinenwahrscheinlichkeit() die
	 * Wahrscheinlichkeiten neu berechnet werden müssen oder nicht.
	 * @return true, wenn die Wahrscheinlichkeiten noch aktuell sind
	 */
	boolean isMineprobabilityUpToDate() {
		return this.probabilitiesAreUpToDate;
	}
	
	/**
	 * Gibt die Minenwahrscheinlichkeit für ein bestimmtes Feld des Minesweepers aus.
	 * Sind keine aktuellen Werte im Speicher, so wird die Minenwahrscheinlichkeit
	 * für jedes Feld neu berechnet.
	 * Die Minenwahrscheinlichkeit für ein Feld ij berechnet sich hierbei nach
	 * der folgenden Formel:
	 * P_B(X_ij=1|A) = P_B(A|X_ij=1) * P_B(X_ij=1) / P_B(A).
	 * Während die Berechnung von P_B(X_ij=1) einfach ist
	 * (= verbleibendeMinen / (verbleibendeFelder + verbleibendeMinen), benötigen wir für
	 * die Berechnung von P_B(A) und P_B(A|X_ij=1) einen Backtracking-Algorithmus,
	 * welcher alle möglichen Besetzungen der in A betroffenen Felder ermittelt.
	 * 
	 * @param zeile
	 * 		Die Zeile des Feldes, dessen Minenwahrscheinlichkeit berechnet werden soll
	 * @param spalte
	 * 		Die Spalte des Feldes, dessen Minenwahrscheinlichkeit berechnet werden soll
	 * @return Die Wahrscheinlichkeit, das das angegebene Feld eine Mine beinhaltet
	 * @throws InterruptedException 
	 * 		Wird geworfen, wenn die Berechnung der Minenwahrscheinlichkeit
	 * 		unterbrochen wurde
	 */
	double getMinenwahrscheinlichkeit(final int zeile, final int spalte) throws
	InterruptedException {
		if (this.spielfeld.isMarkedAsMine(zeile, spalte))
			return 1.;
		if (this.spielfeld.isUncovered(zeile, spalte))
			return 0.;
		if (this.probabilitiesAreUpToDate)
			return this.mineprobability[zeile][spalte];
		Besetzungstabelle besetzungstabelle = new Besetzungstabelle(
				this.spielfeld, this.hoehe, this.breite);
		this.mineprobability = new double[this.hoehe][this.breite];
		this.modellwahrscheinlichkeit = 0;
		this.berechneMinenwahrscheinlichkeitBacktrack(besetzungstabelle);
		for (int zeilenindex = 0; zeilenindex < this.hoehe; zeilenindex++)
			for (int spaltenindex = 0; spaltenindex < this.breite; spaltenindex++) {
				if (this.interruptionDetected) 
					throw new InterruptedException("Interrupted");
				// Hier wird jetzt P_B(X_ij=1|A) = P_B(A|X_ij=1) * P_B(X_ij=1) / P_B(A)
				// gerechnet:
				this.mineprobability[zeilenindex][spaltenindex] *=
						(double) this.spielfeld.getVerbleibendeMinen()
						/ (this.spielfeld.getVerbleibendeFelder()
						   + this.spielfeld.getVerbleibendeMinen())
						/ this.modellwahrscheinlichkeit;
			}
		this.probabilitiesAreUpToDate = true;
		return this.mineprobability[zeile][spalte];
	}

	/**
	 * Berechnet rekursiv P_B(A|X_ij=1) sowie die Modellwahrscheinlichkeit P_B(A).
	 * Die Werte für P_B(A|X_ij=1) werden in this.mineprobability gespeichert, 
	 * die für P_B(A) in this.modellwahrscheinlichkeit. Vor Auführung dieser Methode
	 * müssen this.mineprobability und this.modellwahrscheinlichkeit auf 0 gesetzt werden!
	 * Zunächst werden in dieser Methode mögliche Besetzungen der in A betroffenen Felder
	 * gesucht. Dann wird für jede gefundene Besetzung this.werteBesetzungAus gestartet.
	 * 
	 * @param besetzungstabelle
	 * 		Die zu Beginn noch unvollständige Besetzungstabelle der in A beteiligten
	 * 		Felder, die nach und nach gefüllt wird
	 * @throws InterruptedException 
	 * 		Wird geworfen, wenn die Berechnung der
	 * 		Minenwahrscheinlichkeit unterbrochen wurde
	 */
	private void berechneMinenwahrscheinlichkeitBacktrack(
			final Besetzungstabelle besetzungstabelle) throws InterruptedException {
		Koordinate aktuellerIndex;
		if (this.interruptionDetected)
			throw new InterruptedException("Interrupted");
		try {
			aktuellerIndex = besetzungstabelle.getNextUnknownIndexInA();
		} catch (RuntimeException e) {
			this.werteBesetzungAus(besetzungstabelle);
			return;
		}
		besetzungstabelle.setMine(aktuellerIndex);
		if (besetzungstabelle.isStillValid(
				aktuellerIndex.getZeile(),aktuellerIndex.getSpalte()))
			this.berechneMinenwahrscheinlichkeitBacktrack(besetzungstabelle);
		besetzungstabelle.setKeineMine(aktuellerIndex);
		if (besetzungstabelle.isStillValid(
				aktuellerIndex.getZeile(),aktuellerIndex.getSpalte()))
			this.berechneMinenwahrscheinlichkeitBacktrack(besetzungstabelle);
		besetzungstabelle.setUnbekannt(aktuellerIndex);
	}
	
	/**
	 * Wertet eine mögliche Besetzung von A aus, um die Wahrscheinlichkeiten
	 * P_B(A|X_ij=1) und P_B(A) zu ermitteln. Diese ergeben sich aus den Formeln
	 * P_B(A) = sum_{mögliche Besetzungen der in A beteiligten Felder} P_B(Besetzung)
	 * P_B(A|X_ij=1)=sum_{mögl. Bes. der in A bet. Felder mit X_ij=1} P_B(Besetzung|X_ij=1)
	 * Bezeichnen wir die einzelnen in A beteiligten Felder als A(k), so lassen sich
	 * die einzelnen Besetzungswahrscheinlichkeiten alle mehr oder weniger
	 * nach der Formel
	 * P_B(Besetzung) = P(A(1)=1) * P(A(2)=1|A(1)=1) * ... * P(A(m+1)=0|A(1)=...=A(m)=1)*...
	 * berechnen. Bedingt man zusätzlich nach X_ij=1, so muss man noch prüfen, ob
	 * das X_ij eines der A(k) ist, da sich dann die Wahrscheinlichkeiten verschieben.
	 * 
	 * @param besetzungstabelle
	 * 		Eine Besetzungstabelle, in der alle in A beteiligten Felder belegt wurden
	 * 		und die ausgewertet werden soll
	 */
	 private void werteBesetzungAus(Besetzungstabelle besetzungstabelle) {
		// P_B(Besetzung)
		double besetzungsWS = 1;
		// P_B(Besetzung|X_ij=1), X_ij = A(k) für ein k
		double besetzungsWSBedingtNachFeldInA = 1;
		// P_B(Besetzung|X_ij=1), X_ij != A(k) für alle k
		double besetzungsWSBedingtNachFeldAusserhalbVonA = 1;
		// Zunächst die Terme der Form P_B(x(1)=1) * P_B(x(2) = 1 | x(1) = 1)...
		for (int neueMine = 0; neueMine < besetzungstabelle.getNeueMinen();
				neueMine++) {
			besetzungsWS *=
					(double) (this.spielfeld.getVerbleibendeMinen() - neueMine)
					/ (this.spielfeld.getVerbleibendeFelder()
					   + this.spielfeld.getVerbleibendeMinen() - neueMine);
			besetzungsWSBedingtNachFeldAusserhalbVonA *=
					(double) (this.spielfeld.getVerbleibendeMinen() - neueMine - 1)
					/ (this.spielfeld.getVerbleibendeFelder()
					   + this.spielfeld.getVerbleibendeMinen() - neueMine - 1);
		}
		for (int neueMine = 0; neueMine < besetzungstabelle.getNeueMinen() - 1;
				neueMine++) {
			besetzungsWSBedingtNachFeldInA *=
					(double) (this.spielfeld.getVerbleibendeMinen() - neueMine - 1)
					/ (this.spielfeld.getVerbleibendeFelder()
					   + this.spielfeld.getVerbleibendeMinen() - neueMine - 1);
		}
		// Nun die Terme der Form P_B(x(m)=0 | x(1) = ... = x(m-1) = 1) * 
		// P_B(x(m+1)=0 | x(1) = ... = x(m-1) = 1, x(m) = 0)...
		for (int neueNichtmine = 0; neueNichtmine
				< besetzungstabelle.getNeueMinenfreieFelder();	neueNichtmine++) {
			besetzungsWS *=
					(double) (this.spielfeld.getVerbleibendeFelder() - neueNichtmine)
					/ (this.spielfeld.getVerbleibendeFelder()
					   + this.spielfeld.getVerbleibendeMinen()
					   - besetzungstabelle.getNeueMinen() - neueNichtmine);
			besetzungsWSBedingtNachFeldInA *=
					(double) (this.spielfeld.getVerbleibendeFelder() - neueNichtmine)
					/ (this.spielfeld.getVerbleibendeFelder()
					   + this.spielfeld.getVerbleibendeMinen()
					   - besetzungstabelle.getNeueMinen() - neueNichtmine);
			besetzungsWSBedingtNachFeldAusserhalbVonA *=
					(double) (this.spielfeld.getVerbleibendeFelder() - neueNichtmine)
					/ (this.spielfeld.getVerbleibendeFelder()
					   + this.spielfeld.getVerbleibendeMinen()
					   - besetzungstabelle.getNeueMinen() - neueNichtmine - 1);
		}
		// Und am Schluss die BesetzungsWahrscheinlichkeiten aufsummieren:
		this.modellwahrscheinlichkeit += besetzungsWS;
		for (int zeile = 0; zeile < this.hoehe; zeile++)
			for (int spalte = 0; spalte < this.breite; spalte++) {
				if (this.spielfeld.isUncovered(zeile, spalte)
						|| this.spielfeld.isMarkedAsMine(zeile, spalte)
						|| (!besetzungstabelle.isMine(zeile, spalte)
							&& this.spielfeld.hasUncoveredNeighbor(zeile, spalte)))
					continue;
				if (this.spielfeld.hasUncoveredNeighbor(zeile, spalte)) {
					this.mineprobability[zeile][spalte] +=
						besetzungsWSBedingtNachFeldInA;
				} else {
					this.mineprobability[zeile][spalte] +=
						besetzungsWSBedingtNachFeldAusserhalbVonA;
				}
			}
	}
	
	/**
	 * Benachrichtigt den ProbabilityCalculator darüber, dass am Modell Änderungen
	 * vorgenommen wurden, so dass eventuell vorhandene Zwischenergebnisse
	 * verworfen werden.
	 */
	void notifyOfChangedProbabilities() {
		this.probabilitiesAreUpToDate = false;
	}
	
	/**
	 * Teilt dem ProbabilityCalculator mit, ob die nächsten 
	 * Wahrscheinlichkeitsberechnungen unterbrochen werden sollen oder nicht.
	 * @param wert
	 * 		bei "true" werden die Berechnungen unterbrochen, bei "false" nicht.
	 */
	void setInterruptionDetected(final boolean wert) {
		if (wert == false && this.interruptionDetected == true)
			this.notifyOfChangedProbabilities();
		this.interruptionDetected = wert;
	}

	/**
	 * Gibt zurück, ob derzeit eine Berechnung läuft, die unterbrochen werden soll
	 * @return "true", wenn die Berechnung unterbrochen werden soll
	 */
	boolean isInterrupted() {
		return this.interruptionDetected;
	}
}