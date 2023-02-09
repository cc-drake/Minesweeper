package de.drake.minesweeper.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import de.drake.minesweeper.controller.Gamecontroller;
import de.drake.minesweeper.controller.Maincontroller;

/**
 * Die Menüleiste des Minesweepers samt Inhalt und ActionListener.
 */
class MinesweeperMenuBar extends MenuBar implements ActionListener {
	
	/**
	 * Die SerialVersionUID für die MinesweeperMenuBar
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Der Menüpunkt "Abbrechen"
	 */
	private Menu stopMenu;
	
	/**
	 * Controller zum Verarbeiten der Eingaben
	 */
	private Gamecontroller gamecontroller;
	
	/**
	 * Controller zum Verarbeiten der Eingaben
	 */
	private Maincontroller maincontroller;
	
	/**
	 * Die Höhe des Minesweepers
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
	 * Erzeugt eine Menüleiste für die MinesweeperGUI.
	 * 
	 * @param gamecontroller
	 * 		der Gamecontroller, um die Tools starten zu können
	 * @param maincontroller
	 * 		der Maincontroller, um ein neues Spiel starten zu können
	 * @param hoehe
	 * 		die Höhe des Minesweepers
	 * @param breite
	 * 		die Breite des Minesweepers
	 * @param minenzahl
	 * 		die Gesamtzahl der Minen im Minesweeper
	 */
	MinesweeperMenuBar(final Maincontroller maincontroller,
			final Gamecontroller gamecontroller,
			final int hoehe, final int breite, final int minenzahl) {
		super();
		this.maincontroller = maincontroller;
		this.gamecontroller = gamecontroller;
		this.hoehe = hoehe;
		this.breite = breite;
		this.minenzahl = minenzahl;
		
		Menu spielMenu = new Menu("Spiel");
		this.add(spielMenu);
		
		MenuItem itemNeu = new MenuItem("Neu (F2)");
		itemNeu.setActionCommand("Neu");
		itemNeu.addActionListener(this);
		spielMenu.add(itemNeu);
		
		spielMenu.addSeparator();
		
		MenuItem itemAnfaenger = new MenuItem("Anfänger");
		itemAnfaenger.setActionCommand("Anfänger");
		itemAnfaenger.addActionListener(this);
		spielMenu.add(itemAnfaenger);
		
		MenuItem itemFortgeschrittene = new MenuItem("Fortgeschrittene");
		itemFortgeschrittene.setActionCommand("Fortgeschrittene");
		itemFortgeschrittene.addActionListener(this);
		spielMenu.add(itemFortgeschrittene);
		
		MenuItem itemProfis = new MenuItem("Profis");
		itemProfis.setActionCommand("Profis");
		itemProfis.addActionListener(this);
		spielMenu.add(itemProfis);
		
		MenuItem itemBenutzerdefiniert = new MenuItem("Benutzerdefiniert");
		itemBenutzerdefiniert.setActionCommand("Benutzerdefiniert");
		itemBenutzerdefiniert.addActionListener(this);
		spielMenu.add(itemBenutzerdefiniert);
		
		spielMenu.addSeparator();
		
		MenuItem itemBeenden = new MenuItem("Beenden");
		itemBeenden.setActionCommand("Beenden");
		itemBeenden.addActionListener(this);
		spielMenu.add(itemBeenden);
		
		
		Menu toolMenu = new Menu("Tools");
		this.add(toolMenu);
		
		MenuItem itemWS = new MenuItem("Minenwahrscheinlichkeiten anzeigen (F5)");
		itemWS.setActionCommand("WS");
		itemWS.addActionListener(this);
		toolMenu.add(itemWS);
		
		MenuItem itemLoese = new MenuItem("Löse, ohne zu raten (F6)");
		itemLoese.setActionCommand("Löse");
		itemLoese.addActionListener(this);
		toolMenu.add(itemLoese);
		
		MenuItem itemKI = new MenuItem("KI aktivieren (F7)");
		itemKI.setActionCommand("KI");
		itemKI.addActionListener(this);
		toolMenu.add(itemKI);
		
		MenuItem itemKIBisSieg = new MenuItem("KI bis zum Sieg spielen lassen (F8)");
		itemKIBisSieg.setActionCommand("KIBisSieg");
		itemKIBisSieg.addActionListener(this);
		toolMenu.add(itemKIBisSieg);
		
		this.stopMenu = new Menu("Stop");
		MenuItem itemStop = new MenuItem("Operation abbrechen (ESC)");
		itemStop.setActionCommand("Stop");
		itemStop.addActionListener(this);
		this.stopMenu.add(itemStop);
	}
	
	/**
	 * Legt fest, ob der Menüpunkt "Stop" abgezeigt werden soll.
	 * @param wert
	 * 		bei "true" wird der Menüpunkt angezeigt, bei "false" nicht.
	 */
	void showStopOption(final boolean wert) {
		if (wert == true) {
			this.add(this.stopMenu);
		} else {
			this.remove(this.stopMenu);
		}
	}

	/**
	 * Verarbeitet die Aktionen des Minesweeper-Menüs.
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getActionCommand() == "Neu") {
			this.gamecontroller.startNewRound();
			return;
		}
		if (arg0.getActionCommand() == "Anfänger") {
			this.maincontroller.startNewGame(9, 9, 10);
			return;
		}
		if (arg0.getActionCommand() == "Fortgeschrittene") {
			this.maincontroller.startNewGame(16, 16, 40);
			return;
		}
		if (arg0.getActionCommand() == "Profis") {
			this.maincontroller.startNewGame(16, 30, 99);
			return;
		}
		if (arg0.getActionCommand() == "Benutzerdefiniert") {
			this.maincontroller.selectCustomParameters(this.hoehe,
					this.breite, this.minenzahl);
			return;
		}
		if (arg0.getActionCommand() == "Beenden") {
			this.gamecontroller.endGame();
			return;
		}
		if (arg0.getActionCommand() == "WS") {
			this.gamecontroller.showMineprobabilities();
			return;
		}
		if (arg0.getActionCommand() == "Löse") {
			this.gamecontroller.loeseDeterministisch();
			return;
		}
		if (arg0.getActionCommand() == "KI") {
			this.gamecontroller.startKI();
			return;
		}
		if (arg0.getActionCommand() == "KIBisSieg") {
			this.gamecontroller.activateKIUntilWin();
			return;
		}
		if (arg0.getActionCommand() == "Stop") {
			this.gamecontroller.interruptCalculation();
			return;
		}
	}
}
