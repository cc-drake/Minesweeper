package de.drake.minesweeper.view;

import java.awt.*;
import java.awt.event.WindowEvent;
import javax.swing.*;
import de.drake.minesweeper.controller.*;

/**
 * Hauptfenster der Minesweeper-GUI, das die Spieloberfläche beinhaltet
 */
public class MinesweeperGUI extends Frame {

	/**
	 * Die serialVersionUID für NonoGUI
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Das Label, in dem die verbleibende Anzahl von Minen angezeigt wird
	 */
	private Label verbleibendeMinenLabel = new Label("");
	
	/**
	 * Das Label, in dem die verbleibende Anzahl von Feldern angezeigt wird
	 */
	private Label verbleibendeFelderLabel = new Label("");
	
	/**
	 * Array, welches die Felder des Minesweepers als JFrames darstellt
	 */
	private JLabel[][] minefields;
	
	/**
	 * Der Smiley-Button, mit dem ein neues Spiel gestartet werden kann
	 */
	private Smiley smiley;
	
	/**
	 * Die Menüleiste, die über dem Smiley klebt
	 */
	private MinesweeperMenuBar minesweeperMenuBar;
	
	/**
	 * Komponente, die die Events der MinefieldListener verarbeitet
	 */
	private MouseEventHandler mouseEventHandler;
	
	/**
	 * die Gesamtzahl der Minen
	 */
	private int minenzahl;
	
	/**
	 * die Gesamtzahl der Felder
	 */
	private int felderzahl;
	
	// Konstruktoren
	
	/**
	 * Konstruktor zum Erzeugen eines GUI-Fensters
	 * 
	 * @param titel
	 * 		der Titel des GUI-Fensters
	 * @param maincontroller
	 * 		der Controller zum Ändern der Spielparameter
	 * @param gamecontroller
	 * 		der Controller zur Verarbeitung von Spiel-Events
	 * @param hoehe
	 * 		die Höhe des Minesweepers
	 * @param breite
	 * 		die Breite des Minesweepers
	 * @param minenzahl
	 * 		die Gesamtzahl der Minen im Minesweeper
	 */
	public MinesweeperGUI(final String titel,
			final Maincontroller maincontroller,
			final Gamecontroller gamecontroller,
			final int hoehe, final int breite, final int minenzahl) {
		super(titel);
		this.minenzahl = minenzahl;
		this.felderzahl = hoehe * breite;
		this.mouseEventHandler = new MouseEventHandler(
				this, gamecontroller);
		
		this.setSize(Math.max(132,16*breite+6), 16*hoehe + 88);
		this.setLocation(400,300);
		this.setResizable(false);
		this.setLayout(new BorderLayout());
		
		Panel kontrollebene = new Panel();
		kontrollebene.setLayout(new FlowLayout(FlowLayout.CENTER));
		kontrollebene.addMouseListener(new WindowListener(this.mouseEventHandler));
		this.add(kontrollebene, BorderLayout.NORTH);
			
			this.setVerbleibendeMinen(this.minenzahl);
			kontrollebene.add(this.verbleibendeMinenLabel);
			this.verbleibendeMinenLabel.addMouseListener(
					new WindowListener(this.mouseEventHandler));
			this.smiley = new Smiley();
			this.smiley.addMouseListener(new SmileyListener(this.mouseEventHandler));
			kontrollebene.add(this.smiley);
			this.setVerbleibendeFelder(this.felderzahl-this.minenzahl);
			kontrollebene.add(this.verbleibendeFelderLabel);
			this.verbleibendeFelderLabel.addMouseListener(
					new WindowListener(this.mouseEventHandler));
			
		Panel minesweeperPanel = new Panel();
		minesweeperPanel.setLayout(new GridLayout(hoehe, breite));
		this.add(minesweeperPanel, BorderLayout.WEST);
		
			this.minefields = new JLabel[hoehe][breite];
			for (int zeile = 0; zeile < hoehe; zeile++) {
				for (int spalte = 0; spalte < breite; spalte++) {
					this.minefields[zeile][spalte] = new JLabel(
							MinesweeperGUI.imageLoader("covered"));
					this.minefields[zeile][spalte].addMouseListener(
							new MinefieldListener(mouseEventHandler,
									zeile, spalte));
					minesweeperPanel.add(this.minefields[zeile][spalte]);
				}
			}
			
		this.enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		
		this.minesweeperMenuBar = new MinesweeperMenuBar(
				maincontroller, gamecontroller, hoehe, breite, this.minenzahl);
		this.setMenuBar(this.minesweeperMenuBar);
		this.addKeyListener(new MinesweeperKeyListener(gamecontroller));
	}
	
	/**
	 * Methode, die ausgeführt wird wenn das Fenster geschlossen werden soll
	 */
	protected void processWindowEvent(WindowEvent e) {
		if (e.getID()==WindowEvent.WINDOW_CLOSING) {
			 this.dispose();
			 System.exit(0);
		 }
	}
	
	/**
	 * Methode, die die GUI für eine neue Runde zurücksetzt.
	 * Hierbei werden alle Felder verdeckt, der Smiley zurückgesetzt,
	 * die verbliebenen Minen/Felder aktualisiert und die MouseListener aktivert.
	 */
	public void reset() {
		this.showSmileyNormal();
		this.setVerbleibendeMinen(this.minenzahl);
		this.setVerbleibendeFelder(this.felderzahl-this.minenzahl);
		for (int zeile = 0; zeile < this.minefields.length; zeile ++)
			for (int spalte = 0; spalte < this.minefields[0].length; spalte++)
				this.showBlank(zeile, spalte);
		this.mouseEventHandler.setActive(true);
	}
	
	/**
	 * Methode, die es der KI ermöglicht, ihr Glück noch einmal zu versuchen.
	 * Hierbei werden alle Felder verdeckt, der Smiley auf das Stopschild
	 * zurückgesetzt und die verbliebenen Minen/Felder aktualisiert.
	 */
	public void resetForKI() {
		this.showSmileyStop();
		this.setVerbleibendeMinen(this.minenzahl);
		this.setVerbleibendeFelder(this.felderzahl-this.minenzahl);
		for (int zeile = 0; zeile < this.minefields.length; zeile ++)
			for (int spalte = 0; spalte < this.minefields[0].length; spalte++)
				this.showBlank(zeile, spalte);
	}
	
	/**
	 * Deaktivert die MouseListener außer dem Smiley. Wird ausgeführt, wenn das Spiel
	 * gewonnen oder verloren wurde oder Berechnungen stattfinden.
	 */
	public void stopMouseListener() {
		this.mouseEventHandler.setActive(false);
	}
	
	/**
	 * Aktiviert die MouseListener wieder. Wird ausgeführt, wenn Berechnungen
	 * abgeschlossen wurden, ohne dass das Spiel beendet wurde.
	 */
	public void resumeMouseListener() {
		this.mouseEventHandler.setActive(true);
	}
	
	/**
	 * Methode, die dem Smiley sein normales Aussehen gibt
	 */
	public void showSmileyNormal() {
		this.smiley.showSmileyNormal();
		this.mouseEventHandler.setSmileyIsStop(false);
	}
	
	/**
	 * Methode, die den Smiley tötet
	 */
	public void showSmileyDead() {
		this.smiley.showSmileyDead();
		this.mouseEventHandler.setSmileyIsStop(false);
	}
	
	/**
	 * Methode, die den Smiley "O" sagen lässt
	 */
	void showSmileyO() {
		this.smiley.showSmileyO();
	}
	
	/**
	 * Methode, die dem Smiley eine Sonnenbrille verpasst
	 */
	public void showSmileyCool() {
		this.smiley.showSmileyCool();
		this.mouseEventHandler.setSmileyIsStop(false);
	}
	
	/**
	 * Methode, die dem Smiley einen Hover-Effekt verpasst
	 */
	void showSmileyHovered() {
		this.smiley.showSmileyHovered();
	}
	
	/**
	 * Methode, die einen Hover-Effekt des Smileys entfernt
	 */
	void showSmileyUnhovered() {
		this.smiley.showSmileyUnhovered();
	}
	
	/**
	 * Methode, die den Smiley in ein Stopschild verwandelt und 
	 */
	public void showSmileyStop() {
		this.smiley.showSmileyStop();
		this.mouseEventHandler.setSmileyIsStop(true);
	}
	
	/**
	 * Legt fest, ob der Menüpunkt "Operation abbrechen" abgezeigt werden soll.
	 * @param wert
	 * 		bei "true" wird der Menüpunkt angezeigt, bei "false" nicht.
	 */
	public void showStopMenu(final boolean wert) {
		this.minesweeperMenuBar.showStopOption(wert);
	}
	
	/**
	 * Methode, die ein Minesweeperfeld visuell als Mine markiert
	 * 
	 * @param zeile
	 * 		die Zeile, in der sich das Minesweeperfeld befindet
	 * @param spalte
	 * 		die Spalte, in der sich das Minesweeperfeld befindet
	 */
	public void showMark(final int zeile, final int spalte) {
		this.minefields[zeile][spalte].setIcon(MinesweeperGUI.imageLoader("mineMark"));
	}
	
	/**
	 * Methode, die eine zweistellige Zahl in ein Minesweeperfeld einträgt. Wird zur
	 * Anzeige von Minenwahrscheinlichkeiten verwendet.
	 * 
	 * @param zeile
	 * 		die Zeile, in der sich das Minesweeperfeld befindet
	 * @param spalte
	 * 		die Spalte, in der sich das Minesweeperfeld befindet
	 * @param nummer
	 * 		die Nummer, die eingetragen werden soll
	 */
	public void showNumber(final int zeile, final int spalte, final String nummer) {
		this.minefields[zeile][spalte].setIcon(null);
		this.minefields[zeile][spalte].setText(nummer);
	}
	
	/**
	 * Methode, die eine zweistellige Zahl aus einem Minesweeperfeld entfernt
	 * 
	 * @param zeile
	 * 		die Zeile, in der sich die Zahl befindet
	 * @param spalte
	 * 		die Spalte, in der sich die Zahl befindet
	 */
	public void removeNumber(final int zeile, final int spalte) {
		this.minefields[zeile][spalte].setText(null);
	}
	
	/**
	 * Methode, die ein Minenfeld als verdecktes Feld anzeigt
	 * 
	 * @param zeile
	 * 		die Zeile, in der sich das Minesweeperfeld befindet
	 * @param spalte
	 * 		die Spalte, in der sich das Minesweeperfeld befindet
	 */
	public void showBlank(final int zeile, final int spalte) {
		this.minefields[zeile][spalte].setIcon(MinesweeperGUI.imageLoader("covered"));
	}
	
	/**
	 * Methode, die in ein Minesweeperfeld die Zahl der benachbarten Minen einträgt
	 * 
	 * @param zeile
	 * 		die Zeile, in der sich das Minesweeperfeld befindet
	 * @param spalte
	 * 		die Spalte, in der sich das Minesweeperfeld befindet
	 * @param benachbarteMinen
	 * 		die Zahl, die in das Feld eingetragen werden soll
	 */
	public void showValue(final int zeile, final int spalte, final int benachbarteMinen) {
		this.minefields[zeile][spalte].setIcon(
				MinesweeperGUI.imageLoader("" + benachbarteMinen));
	}
	
	/**
	 * Methode, die in ein Minesweeperfeld eine Mine einträgt
	 * 
	 * @param zeile
	 * 		die Zeile, in der sich das Minesweeperfeld befindet
	 * @param spalte
	 * 		die Spalte, in der sich das Minesweeperfeld befindet
	 */
	public void showMine(final int zeile, final int spalte) {
		this.minefields[zeile][spalte].setIcon(
				MinesweeperGUI.imageLoader("mine"));
	}
	
	/**
	 * Methode, die in ein Minesweeperfeld eine durchgekreuzte Mine einträgt
	 * 
	 * @param zeile
	 * 		die Zeile, in der sich das Minesweeperfeld befindet
	 * @param spalte
	 * 		die Spalte, in der sich das Minesweeperfeld befindet
	 */
	public void showMineCrossed(final int zeile, final int spalte) {
		this.minefields[zeile][spalte].setIcon(
				MinesweeperGUI.imageLoader("mineCrossed"));
	}
	
	/**
	 * Methode, die eine rote Mine in ein Minesweeperfeld einträgt
	 * 
	 * @param zeile
	 * 		die Zeile, in der die Mine aufgedeckt wurde
	 * @param spalte
	 * 		die Spalte, in der die Mine aufgedeckt wurde
	 */
	public void showMineRed(final int zeile, final int spalte) {
		this.minefields[zeile][spalte].setIcon(
				MinesweeperGUI.imageLoader("mineRed"));
	}
	
	/**
	 * Methode, die die Anzahl der verbleibenden Minen aktualisiert
	 * 
	 * @param verbleibendeMinen
	 * 		die aktuelle Anzahl der verbleibenden Minen
	 */
	public void setVerbleibendeMinen(final int verbleibendeMinen) {
		this.verbleibendeMinenLabel.setText("" + verbleibendeMinen
				+ "/" + this.minenzahl);
	}
	
	/**
	 * Methode, die die Anzahl der verbleibenden Felder aktualisiert
	 * 
	 * @param verbleibendeMinen
	 * 		die aktuelle Anzahl der verbleibenden Felder
	 */
	public void setVerbleibendeFelder(final int verbleibendeFelder) {
		this.verbleibendeFelderLabel.setText("" + verbleibendeFelder
				+ "/" + (this.felderzahl - this.minenzahl));
	}
	
	/**
	 * Lädt ein ImageIcon aus dem Unterordner "images"
	 * 
	 * @param name
	 * 		der Name des ImageIcons
	 * @return das geladene ImageIcon
	 */
	static ImageIcon imageLoader(String name) {
		return new ImageIcon(
				ClassLoader.getSystemClassLoader().getResource(
				"de/drake/minesweeper/view/images/" + name + ".PNG"));
	}
}