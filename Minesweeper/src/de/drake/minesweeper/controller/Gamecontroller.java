package de.drake.minesweeper.controller;

import java.util.HashSet;

import de.drake.minesweeper.model.Koordinate;
import de.drake.minesweeper.model.Spielfeld;
import de.drake.minesweeper.view.MinesweeperGUI;

/**
 * Controller, der ein Minesweeperspiel steuert.
 * Verwaltet die Kommunikation zwischen Model und View.
 */
public class Gamecontroller {
	
	// Instanzattribute
	
	/**
	 * Das aktuelle Spielfeld
	 */
	private Spielfeld spielfeld;
	
	/**
	 * Die GUI des Minesweepers
	 */
	private MinesweeperGUI minesweeperGUI;
	
	/**
	 * Die H�he des Minesweepers
	 */
	private int hoehe;
	
	/**
	 * Die Breite des Minesweepers
	 */
	private int breite;
	
	/**
	 * Die Anzahl der Minen im Minesweeper
	 */
	private int minenzahl;
	
	/**
	 * Speichert, ob derzeit die Minenwahrscheinlichkeiten angezeigt werden
	 */
	private boolean mineprobabilitiesAreShown = false;
	
	/**
	 * Speichert, ob die Tools derzeit verwendet werden d�rfen
	 */
	private boolean toolsActive = false;
	
	/**
	 * Verweist w�hrend komplizierteren Berechnungen auf einen Extra-Thread,
	 * in dem die Berechnungen durchgef�hrt werden.
	 */
	private Thread calculatorThread;
	
	
	// Konstruktoren
	
	/**
	 * Startet ein neues Spiel.
	 * 
	 * @param hoehe
	 * 		die H�he des Minesweepers
	 * @param breite
	 * 		die Breite des Minesweepers
	 * @param minenzahl
	 * 		die Gesamtzahl der Minen im Minesweeper
	 * @param maincontroller
	 * 		der Maincontroller, um sp�ter die Parameter �ndern zu k�nnen
	 */
	Gamecontroller(final int hoehe, final int breite, final int minenzahl,
			final Maincontroller maincontroller) {
		this.hoehe = hoehe;
		this.breite = breite;
		this.minenzahl = minenzahl;
		this.minesweeperGUI = new MinesweeperGUI("Minesweeper",
				maincontroller, this, hoehe, breite, minenzahl);
		this.startNewRound();
		minesweeperGUI.setVisible(true);
	}
	
	
	// Instanzmethoden
	
	/**
	 * Startet eine neue Runde mit den selben Einstellungen.
	 * Wird ausgel�st, wenn auf den Smiley geklickt wird.
	 */
	public void startNewRound() {
		if (this.spielfeld != null)
			this.interruptCalculation();
		this.spielfeld = new Spielfeld(hoehe, breite, minenzahl);
		this.minesweeperGUI.reset();
		if (this.mineprobabilitiesAreShown)
			this.removeMineprobabilities();
		this.toolsActive = true;
		if (this.spielfeld.getVerbleibendeFelder() == 0) {
			try {
				this.gameWon();
			} catch (GameOverException e) {
			}
		}
	}
	
	/**
	 * Beendet das Spiel und schlie�t das GUI-Fenster.
	 * Wird verwendet, wenn die Spielparameter ge�ndert wurden.
	 */
	public void endGame() {
		this.interruptCalculation();
		this.minesweeperGUI.dispose();
	}
	
	/**
	 * Verarbeitet einen Rechtsklick auf ein Minenfeld, indem es das entsprechende
	 * Feld als Mine markiert oder die Markierung entfernt.
	 * 
	 * @param zeile
	 * 		die Zeile des angeklickten Minenfeldes
	 * @param spalte
	 * 		die Spalte des angeklickten Minenfeldes
	 */
	public void toggleMineMark(final int zeile, final int spalte) {
		if (this.mineprobabilitiesAreShown)
			this.removeMineprobabilities();
		if (this.spielfeld.isUncovered(zeile, spalte))
			return;
		if (this.spielfeld.isMarkedAsMine(zeile, spalte)) {
			this.unmarkAsMine(zeile, spalte);
		} else {
			this.markAsMine(zeile, spalte);
		}
	}
	
	/**
	 * Markiert ein bestimmtes Feld als Mine.
	 * 
	 * @param zeile
	 * 		die Zeile des zu markierenden Feldes
	 * @param spalte
	 * 		die Spalte des zu markierenden Feldes
	 */
	private void markAsMine(final int zeile, final int spalte) {
		if (this.mineprobabilitiesAreShown)
			this.removeMineprobabilities();
		this.spielfeld.setMarkedAsMine(zeile, spalte, true);
		this.minesweeperGUI.setVerbleibendeMinen(this.spielfeld.getVerbleibendeMinen());
		this.minesweeperGUI.showMark(zeile, spalte);
	}
	
	/**
	 * Entfernt die Minenmarkierung eines bestimmten Feldes.
	 * 
	 * @param zeile
	 * 		die Zeile des zu bearbeitenden Feldes
	 * @param spalte
	 * 		die Spalte des zu bearbeitenden Feldes
	 */
	private void unmarkAsMine(final int zeile, final int spalte) {
		this.spielfeld.setMarkedAsMine(zeile, spalte, false);
		this.minesweeperGUI.setVerbleibendeMinen(this.spielfeld.getVerbleibendeMinen());
		this.minesweeperGUI.showBlank(zeile, spalte);
	}
	
	/**
	 * Deckt ein Feld des Minesweepers auf.
	 * 
	 * @param zeile
	 * 		die Zeile des zu aufzudeckenden Feldes
	 * @param spalte
	 * 		die Spalte des zu aufzudeckenden Feldes
	 * @throws GameOverException Wird geworfen, wenn das Aufdecken eines Feldes
	 * zum Spielende f�hrte.
	 */
	public void uncover(final int zeile, final int spalte) throws GameOverException {
		if (this.mineprobabilitiesAreShown)
			this.removeMineprobabilities();
		if (this.spielfeld.isUncovered(zeile, spalte) 
				|| this.spielfeld.isMarkedAsMine(zeile, spalte))
			return;
		this.spielfeld.setUncovered(zeile, spalte);
		if (this.spielfeld.isMine(zeile, spalte)) {
			this.gameLost(zeile, spalte);
			return;
		}
		this.minesweeperGUI.setVerbleibendeFelder(this.spielfeld.getVerbleibendeFelder());
		this.minesweeperGUI.showValue(zeile, spalte,
				this.spielfeld.getBenachbarteMinen(zeile, spalte));
		// Wenn ein leeres Feld aufgedeckt wird
		if (this.spielfeld.getBenachbarteMinen(zeile, spalte) == 0) {
			this.uncoverMinelessArea(zeile, spalte);
		}
		if (this.spielfeld.getVerbleibendeFelder() == 0) {
			this.gameWon();
		}
	}
	
	/**
	 * Dr�ckt ein verdecktes Feld ein, wenn die linke Maustaste gedr�ckt gehalten wird.
	 * 
	 * @param zeile
	 * 		die Zeile des zu hovernden Feldes
	 * @param spalte
	 * 		die Spalte des zu hovernden Feldes
	 */
	public void hover(final int zeile, final int spalte) {
		if (this.mineprobabilitiesAreShown)
			this.removeMineprobabilities();
		if (this.spielfeld.isUncovered(zeile, spalte)
				|| this.spielfeld.isMarkedAsMine(zeile, spalte))
			return;
		this.minesweeperGUI.showValue(zeile, spalte, 0);
	}

	/**
	 * Entfernt s�mtliche Minenwahrscheinlichkeiten von den Feldern im GUI-Fenster.
	 */
	private void removeMineprobabilities() {
		for (int zeile = 0; zeile < this.hoehe; zeile++)
			for (int spalte = 0; spalte < this.breite; spalte++) {
				if (this.spielfeld.isMarkedAsMine(zeile, spalte)
						|| this.spielfeld.isUncovered(zeile, spalte))
					continue;
				this.minesweeperGUI.removeNumber(zeile, spalte);
				this.minesweeperGUI.showBlank(zeile, spalte);
			}
		this.mineprobabilitiesAreShown = false;
	}

	/**
	 * Stellt den Ursprungszustand eines gehoverten Feldes wieder her.
	 * 
	 * @param zeile
	 * 		die Zeile des zur�ckzusetzenden Feldes
	 * @param spalte
	 * 		die Spalte des zu zur�ckzusetzenden Feldes
	 */
	public void removeHover(final int zeile, final int spalte) {
		if (this.mineprobabilitiesAreShown)
			this.removeMineprobabilities();
		if (this.spielfeld.isUncovered(zeile, spalte)
				|| this.spielfeld.isMarkedAsMine(zeile, spalte))
			return;
		this.minesweeperGUI.showBlank(zeile, spalte);
	}
	
	/**
	 * Dr�ckt ein Gebiet um ein Feld ein, wenn die mittlere oder beide Maustasten
	 * gleichzeitig gedr�ckt werden.
	 * 
	 * @param zeile
	 * 		die Zeile des Zentrums des zu hovernden Gebiets
	 * @param spalte
	 * 		die Spalte des Zentrums des zu hovernden Gebiets
	 */
	public void hoverAround(final int zeile, final int spalte) {
		if (this.mineprobabilitiesAreShown)
			this.removeMineprobabilities();
		for (int nachbarZeile = Math.max(0,zeile-1);
				nachbarZeile < Math.min(zeile+2,this.hoehe); nachbarZeile++)
			for (int nachbarSpalte = Math.max(0,spalte-1);
					nachbarSpalte < Math.min(spalte+2, this.breite); nachbarSpalte++)
				if (!this.spielfeld.isUncovered(nachbarZeile, nachbarSpalte)
						&& !this.spielfeld.isMarkedAsMine(nachbarZeile, nachbarSpalte)) {
					this.minesweeperGUI.showValue(nachbarZeile, nachbarSpalte, 0);
				}
	}
	
	/**
	 * Stellt den Ursprungszustand eines gehoverten Gebietes wieder her.
	 * 
	 * @param zeile
	 * 		die Zeile des Zentrums des zur�ckzusetzenden Gebietes
	 * @param spalte
	 * 		die Spalte des Zentrums des zur�ckzusetzenden Gebietes
	 */
	public void removeHoverAround(final int zeile, final int spalte) {
		if (this.mineprobabilitiesAreShown)
			this.removeMineprobabilities();
		for (int nachbarZeile = Math.max(0,zeile-1);
				nachbarZeile < Math.min(zeile+2,this.hoehe); nachbarZeile++)
			for (int nachbarSpalte = Math.max(0,spalte-1);
					nachbarSpalte < Math.min(spalte+2, this.breite); nachbarSpalte++)
				if (!this.spielfeld.isUncovered(nachbarZeile, nachbarSpalte)
						&& !this.spielfeld.isMarkedAsMine(nachbarZeile, nachbarSpalte)) {
					this.minesweeperGUI.showBlank(nachbarZeile, nachbarSpalte);
				}
	}
	
	/**
	 * Deckt alle Felder eines minenlosen Gebietes auf.
	 * 
	 * @param zeile
	 * 		die Zeile, in der ein Nullfeld aufgedeckt wurde
	 * @param spalte
	 * 		die Spalte, in der ein Nullfeld aufgedeckt wurde
	 */
	private void uncoverMinelessArea(final int zeile, final int spalte) {
		HashSet<Koordinate> zuBearbeitendeNullfelder = new HashSet<Koordinate>(1);
		zuBearbeitendeNullfelder.add(new Koordinate(zeile, spalte));
		while (zuBearbeitendeNullfelder.size() > 0) {
			HashSet<Koordinate> neuGefundeneNullfelder =
				new HashSet<Koordinate>();
			for (Koordinate koordinate : zuBearbeitendeNullfelder) {
				this.bearbeiteNullfeld(koordinate,
						neuGefundeneNullfelder);
			}
			zuBearbeitendeNullfelder = neuGefundeneNullfelder;
		}
		this.minesweeperGUI.setVerbleibendeFelder(this.spielfeld.getVerbleibendeFelder());
	}

	/**
	 * Deckt die Nachbarfelder eines Nullfeldes auf und registriert diese
	 * gegebenenfalls zur sp�teren Weiterverarbeitung.
	 * 
	 * @param koordinate
	 * 		die Position des Nullfeldes
	 * @param neuGefundeneNullfelder
	 * 		hier werden die neu entdeckten Nullfelder aus der Nachbarschaft abgespeichert
	 */
	private void bearbeiteNullfeld(final Koordinate koordinate,
			HashSet<Koordinate> neuGefundeneNullfelder) {
		final int zeile = koordinate.getZeile();
		final int spalte = koordinate.getSpalte();
		// Gehe alle Nachbarfelder durch...
		for (int nachbarZeile = Math.max(0,zeile-1);
				nachbarZeile < Math.min(zeile+2,this.hoehe); nachbarZeile++)
		for (int nachbarSpalte = Math.max(0,spalte-1);
				nachbarSpalte < Math.min(spalte+2, this.breite); nachbarSpalte++) {
			// Wenn das Nachbarfeld bereits aufgedeckt ist oder als Mine markiert ist, ignoriere es
			if (this.spielfeld.isUncovered(nachbarZeile, nachbarSpalte)
					|| this.spielfeld.isMarkedAsMine(nachbarZeile, nachbarSpalte))
				continue;
			// das Nachbarfeld aufdecken...
			this.spielfeld.setUncovered(nachbarZeile, nachbarSpalte);
			this.minesweeperGUI.showValue(nachbarZeile, nachbarSpalte,
					this.spielfeld.getBenachbarteMinen(nachbarZeile, nachbarSpalte));
			// Wenn es ein Nullfeld ist, registrieren 
			if (this.spielfeld.getBenachbarteMinen(nachbarZeile, nachbarSpalte) == 0) {
				neuGefundeneNullfelder.add(new Koordinate(nachbarZeile, nachbarSpalte));
			}
		}
	}

	/**
	 * Wird ausgef�hrt, wenn das Spiel gewonnen wurde
	 * @throws GameOverException
	 * 		Wird geworfen, um beispielsweise mit der KI auf den Sieg
	 * 		reagieren zu k�nnen
	 */
	private void gameWon() throws GameOverException {
		this.minesweeperGUI.stopMouseListener();
		this.toolsActive = false;
		this.minesweeperGUI.showSmileyCool();
		this.markAllMines();
		throw new GameOverException("GameWon");
	}
	
	/**
	 * Wird ausgef�hrt, wenn das Spiel verloren wurde
	 * @param zeile
	 * 		die Zeile, in der die Mine aufgedeckt wurde
	 * @param spalte
	 * 		die Spalte, in der die Mine aufgedeckt wurde
	 * @throws GameOverException
 	 * 		Wird geworfen, um beispielsweise mit der KI auf die Niederlage
	 * 		reagieren zu k�nnen
	 */
	private void gameLost(final int zeile, final int spalte) throws GameOverException {
		this.minesweeperGUI.stopMouseListener();
		this.toolsActive = false;
		this.minesweeperGUI.showMineRed(zeile, spalte);
		this.minesweeperGUI.showSmileyDead();
		this.showAllMines();
		throw new GameOverException("GameLost");
	}
	
	/**
	 * Methode, die im Falle einer Niederlage die Positionen aller Minen anzeigt
	 */
	private void showAllMines() {
		for (int zeile = 0; zeile < this.hoehe; zeile++)
			for (int spalte = 0; spalte < this.breite; spalte++) {
				if (this.spielfeld.isUncovered(zeile, spalte))
					continue;
				if (this.spielfeld.isMine(zeile, spalte)
						&& !this.spielfeld.isMarkedAsMine(zeile, spalte))
					this.minesweeperGUI.showMine(zeile, spalte);
				if (!this.spielfeld.isMine(zeile, spalte)
						&& this.spielfeld.isMarkedAsMine(zeile, spalte))
					this.minesweeperGUI.showMineCrossed(zeile, spalte);
			}
	}
	
	/**
	 * Methode, die im Falle eines Sieges alle Minen mit einem F�hnchen versieht
	 */
	private void markAllMines() {
		for (int zeile = 0; zeile < this.hoehe; zeile++)
			for (int spalte = 0; spalte < this.breite; spalte++)
				if (this.spielfeld.isMine(zeile, spalte)
						&& !this.spielfeld.isMarkedAsMine(zeile, spalte))
					this.markAsMine(zeile, spalte);
	}

	/**
	 * F�hrt die spezielle Klick-Aktion durch. Hierbei werden alle nicht-markierten
	 * Nachbarfelder aufgedeckt, wenn bereits alle umgebenden Minen gefunden wurden.
	 * 
	 * @param zeile
	 * 		die Zeile des Feldes, f�r das die Aktion ausgef�hrt werden soll
	 * @param spalte
	 * 		die Spalte des Feldes, f�r das die Aktion ausgef�hrt werden soll
	 */
	public void specialClick(final int zeile, final int spalte) {
		if (this.mineprobabilitiesAreShown)
			this.removeMineprobabilities();
		if (!this.spielfeld.isUncovered(zeile, spalte))
			return;
		// Markierte Minen im Umkreis z�hlen
		int anzahlMarkierterMinen = 0;
		for (int nachbarZeile = Math.max(0,zeile-1);
				nachbarZeile < Math.min(zeile+2,this.hoehe); nachbarZeile++)
			for (int nachbarSpalte = Math.max(0,spalte-1);
					nachbarSpalte < Math.min(spalte+2, this.breite); nachbarSpalte++) {
				if (this.spielfeld.isMarkedAsMine(nachbarZeile, nachbarSpalte))
					anzahlMarkierterMinen++;
				}
		// Mit der tats�chlichen Anzahl benachbarter Minen vergleichen
		if (anzahlMarkierterMinen == this.spielfeld.getBenachbarteMinen(zeile, spalte))
			this.uncoverNeighbourhood(zeile, spalte);
	}
	
	/**
	 * Deckt alle Felder in einem Umkreis von einem Feld auf.
	 * Als Minen markierte Felder werden verdeckt gelassen.
	 * 
	 * @param zeile
	 * 		die Zeile des Feldes, um das herum alles aufgedeckt werden soll
	 * @param spalte
	 * 		die Spalte des Feldes, um das herum alles aufgedeckt werden soll
	 */
	private void uncoverNeighbourhood(final int zeile, final int spalte) {
		for (int nachbarZeile = Math.max(0,zeile-1);
				nachbarZeile < Math.min(zeile+2,this.hoehe); nachbarZeile++)
			for (int nachbarSpalte = Math.max(0,spalte-1);
					nachbarSpalte < Math.min(spalte+2, this.breite); nachbarSpalte++)
				try {
					this.uncover(nachbarZeile,nachbarSpalte);
				} catch (GameOverException e) {
					return;
				}
	}
	
	/**
	 * Zeichnet s�mtliche Minenwahrscheinlichkeiten in das GUI-Fenster ein.
	 * Werden diese bereits angezeigt, so werden sie wieder ausgeblendet.
	 * Diese Methode erstellt einen eigenen Thread, der die Berechnungen vornimmt.
	 */
	public void showMineprobabilities() {
		if (!this.toolsActive)
			return;
		if (this.mineprobabilitiesAreShown) {
			this.removeMineprobabilities();
			return;
		}
		this.calculatorThread = new CalculatorThread("show", this, 
			this.minesweeperGUI);
		this.calculatorThread.start();
	}
	
	/**
	 * Interne Methode, die s�mtliche Minenwahrscheinlichkeiten in das GUI-Fenster
	 * einzeichnen soll. Darf nur aus einem eigenen Thread heraus aufgerufen werden!
	 * 
	 * @throws InterruptedException
	 * 		wird geworfen, wenn die Wahrscheinlichkeitsberechnungen
	 * 		unterbrochen wurden
	 */
	void executeShowMineprobabilities()
			throws InterruptedException {
		for (int zeile = 0; zeile < this.hoehe; zeile++)
			for (int spalte = 0; spalte < this.breite; spalte++) {
				if (this.spielfeld.isMarkedAsMine(zeile, spalte)
						|| this.spielfeld.isUncovered(zeile, spalte))
					continue;
				double wahrscheinlichkeit = 
					this.spielfeld.getMinenwahrscheinlichkeit(zeile, spalte);
				String nummer;
				if (wahrscheinlichkeit >= 0.995) {
					nummer = "x";
				} else {
					nummer = "" + (int) Math.round(100*wahrscheinlichkeit);
				}
				this.minesweeperGUI.showNumber(zeile, spalte, nummer);
			}
		this.mineprobabilitiesAreShown = true;
	}
	
	/**
	 * L�st ein Minesweeper so weit wie m�glich, ohne zu raten.
	 * Hierbei kann man nur auf Minen sto�en, wenn Minen zu Unrecht
	 * falsch markiert wurden.
	 * Diese Methode erstellt einen eigenen Thread, der die Berechnungen vornimmt.
	 */
	public void loeseDeterministisch() {
		if (!this.toolsActive)
			return;
		this.calculatorThread = new CalculatorThread("loese", this, 
				this.minesweeperGUI);
		this.calculatorThread.start();
	}
	
	/**
	 * Interne Methode, die das Minesweeper so weit wie m�glich l�st,
	 * ohne zu raten. Darf nur aus einem eigenen Thread heraus aufgerufen werden!
	 * 
	 * @throws Exception
	 * 		wird geworfen, wenn die Wahrscheinlichkeitsberechnungen
	 * 		unterbrochen wurden (InterruptedException) oder das Spiel
	 * 		gewonnen/verloren wurde (GameOverException). Der genaue Fehlertyp kann
	 * 		�ber exception.getMessage() erfragt werden.
	 */
	void executeLoeseDeterministisch() throws Exception {
		boolean arbeitVorhanden = true;
		while (arbeitVorhanden) {
			arbeitVorhanden = false;
			double[][] mineprobabilities = new double[this.hoehe][this.breite];
			for (int zeile = 0; zeile < this.hoehe; zeile++)
				for (int spalte = 0; spalte < this.breite; spalte++) {
					mineprobabilities[zeile][spalte] =
						this.spielfeld.getMinenwahrscheinlichkeit(zeile, spalte);
				}
			for (int zeile = 0; zeile < this.hoehe; zeile++)
				for (int spalte = 0; spalte < this.breite; spalte++) {
					if (!this.spielfeld.isUncovered(zeile, spalte) && 
							mineprobabilities[zeile][spalte] < 0.0001) {
						this.uncover(zeile, spalte);
						arbeitVorhanden = true;
					}
					if (!this.spielfeld.isMarkedAsMine(zeile, spalte) && 
							mineprobabilities[zeile][spalte] > 0.9999)
						this.markAsMine(zeile, spalte);
				}
		}
	}
	
	/**
	 * L�sst die KI das Spiel zu Ende spielen.
	 * Diese l�st zun�chst alles Deterministische und versucht dann,
	 * m�glichst geschickt zu raten, bis das Spiel beendet ist.
	 * Diese Methode erstellt einen eigenen Thread, der die Berechnungen vornimmt.
	 */
	public void startKI() {
		if (!this.toolsActive)
			return;
		this.calculatorThread = new CalculatorThread("startKI", this, 
				this.minesweeperGUI);
		this.calculatorThread.start();
	}
	
	/**
	 * Interne Methode, die die KI das Spiel zu Ende spielen l�sst. 
	 * Darf nur aus einem eigenen Thread heraus aufgerufen werden!
	 * 
	 * @throws Exception
	 * 		wird geworfen, wenn die Wahrscheinlichkeitsberechnungen
	 * 		unterbrochen wurden (InterruptedException) oder das Spiel
	 * 		gewonnen/verloren wurde (GameOverException). Der genaue Fehlertyp kann
	 * 		�ber exception.getMessage() erfragt werden.
	 */
	void executeStartKI() throws Exception {
		// Wenn ein neues Spiel gestartet wurde, 2 mittlere Felder aufdecken
		if (this.spielfeld.getVerbleibendeFelder() ==
				this.hoehe * this.breite - minenzahl) {
			this.uncover(this.hoehe/2, this.breite/2);
			if (this.hoehe > 1)
				this.uncover(this.hoehe/2 - 1, this.breite/2);
		}
		while (true) {
			this.executeLoeseDeterministisch();
			this.deckeOptimalesFeldAuf();
		}
	}
	
	/**
	 * L�sst die KI solange spielen, bis das Spiel gewonnen wurde.
	 * Diese Methode erstellt einen eigenen Thread, der die Berechnungen vornimmt.
	 */
	public void activateKIUntilWin() {
		if (!this.toolsActive)
			return;
		this.calculatorThread = new CalculatorThread("startKIUntilWin", this, 
				this.minesweeperGUI);
		this.calculatorThread.start();
	}
	
	/**
	 * Interne Methode, die die KI solange spielen l�sst, bis das Spiel gewonnen
	 * wurde. 
	 * Darf nur aus einem eigenen Thread heraus aufgerufen werden!
	 * 
	 * @throws Exception
	 * 		wird geworfen, wenn die Wahrscheinlichkeitsberechnungen
	 * 		unterbrochen wurden (InterruptedException) oder das Spiel
	 * 		gewonnen/verloren wurde (GameOverException). Der genaue Fehlertyp kann
	 * 		�ber exception.getMessage() erfragt werden.
	 */
	void executeActivateKIUntilWin() throws Exception {
		try {
			// Wenn ein neues Spiel gestartet wurde, 2 mittlere Felder aufdecken
			if (this.spielfeld.getVerbleibendeFelder() ==
					this.hoehe * this.breite - minenzahl) {
				this.uncover(this.hoehe/2, this.breite/2);
				if (this.hoehe > 1)
					this.uncover(this.hoehe/2 - 1, this.breite/2);
			}
			while (true) {
				this.executeLoeseDeterministisch();
				this.deckeOptimalesFeldAuf();
			}
		} catch (Exception e) {
			if (e.getMessage().equals("GameLost")) {
				if (this.spielfeld.calculationIsInterrupted()) {
					this.startNewRound();
					throw new InterruptedException("Interrupted");
				}
				this.spielfeld = new Spielfeld(hoehe, breite, minenzahl);
				this.minesweeperGUI.resetForKI();
				this.executeActivateKIUntilWin();
			} else {
				throw e;
			}
		}
	}
	
	/**
	 * Deckt das Feld mit der geringsten Minenwahrscheinlichkeit auf,
	 * das neben einem aufgedeckten Feld liegt. Gibt es kein solches Feld,
	 * wird das n�chstbeste verdeckte Feld aufgedeckt.
	 * 
	 * @throws Exception
	 * 		wird geworfen, wenn die Wahrscheinlichkeitsberechnungen
	 * 		unterbrochen wurden (InterruptedException) oder das Spiel
	 * 		gewonnen/verloren wurde (GameOverException). Der genaue Fehlertyp kann
	 * 		�ber exception.getMessage() erfragt werden.
	 */
	private void deckeOptimalesFeldAuf() throws Exception {
		Integer optimaleZeile = null;
		Integer optimaleSpalte = null;
		for (int zeile = 0; zeile < this.hoehe; zeile++)
			for (int spalte = 0; spalte < this.breite; spalte++) {
				if (this.spielfeld.isUncovered(zeile, spalte) ||
						this.spielfeld.isMarkedAsMine(zeile, spalte))
					continue;
				if (optimaleZeile == null) {
					optimaleZeile = zeile;
					optimaleSpalte = spalte;
					continue;
				}
				if (!this.spielfeld.hasUncoveredNeighbor(zeile, spalte))
					continue;
				if (this.spielfeld.getMinenwahrscheinlichkeit(zeile, spalte) < 
						this.spielfeld.getMinenwahrscheinlichkeit(
						optimaleZeile, optimaleSpalte) ||
						!this.spielfeld.hasUncoveredNeighbor(
						optimaleZeile, optimaleSpalte)) {
					optimaleZeile = zeile;
					optimaleSpalte = spalte;
				}
			}
		if (optimaleZeile != null) {
			this.uncover(optimaleZeile, optimaleSpalte);
		}
	}
	
	/**
	 * Unterbricht alle Wahrscheinlichkeitsberechnungen, sofern derzeit welche in
	 * einem eigenen Thread durchgef�hrt werden. Sorgt indirekt daf�r, dass in dem
	 * entsprechenden Thread eine InterruptedException geworfen wird.
	 */
	public void interruptCalculation() {
		if (this.toolsActive)
			return;
		this.spielfeld.setInterruptionDetected(true);
	}
	
	/**
	 * Entfernt den Berechnungs-Unterbrecher, damit wieder neue Berechnungen 
	 * m�glich sind.
	 */
	void resetInterruptionDetected() {
		this.spielfeld.setInterruptionDetected(false);
	}
	
	/**
	 * Legt fest, ob die Wahrscheinlichkeitstools noch aktiviert werden d�rfen oder
	 * nicht.
	 * @param wert
	 * 		bei "true" werden die Tools wieder aktiv geschaltet, bei "false"
	 * 		werden sie abgeschaltet.
	 */
	void setToolsActive(final boolean wert) {
		this.toolsActive = wert;
	}
}