package de.drake.minesweeper.controller;
import de.drake.minesweeper.view.MinesweeperGUI;

/**
 * Thread, der zur Berechnung von Minenwahrscheinlichkeiten und ähnlichen
 * Operationen verwendet wird. Der eigene Thread ist notwendig, damit die 
 * Berechnungen bei zu hohem Rechenaufwand manuell unterbrochen werden kann.
 */
class CalculatorThread extends Thread {
	
	/**
	 * Die Aktion, für die der Thread erzeugt wurde
	 */
	private String modus;
	
	/**
	 * Der Gamecontroller, in dem die Aktionen ausgeführt werden sollen
	 */
	private Gamecontroller gamecontroller;
	
	/**
	 * Die GUI, in der das "Stopschild" aufgebaut werden soll
	 */
	private MinesweeperGUI minesweeperGUI;
	
	/**
	 * Erzeugt einen neuen Thread.
	 * @param modus
	 * 		Die Aufgabe, für die der Thread erstellt wurde
	 * @param gamecontroller
	 * 		Der Gamecontroller, in dem die Aktionen ausgeführt werden sollen
	 * @param minesweeperGUI
	 * 		Die GUI, in der das "Stopschild" aufgebaut werden soll
	 */
	CalculatorThread(final String modus, final Gamecontroller gamecontroller,
			final MinesweeperGUI minesweeperGUI) {
		this.modus = modus;
		this.gamecontroller = gamecontroller;
		this.minesweeperGUI = minesweeperGUI;
	}
	
	/**
	 * Startet die Aktion, für die der Thread erzeugt wurde. Diese Methode wird
	 * beim Aufruf von this.start() ausgeführt.
	 */
	public void run(){
		this.minesweeperGUI.showSmileyStop();
		this.minesweeperGUI.showStopMenu(true);
		this.minesweeperGUI.stopMouseListener();
		this.gamecontroller.setToolsActive(false);
		try {
			if (this.modus == "show") {
				this.gamecontroller.executeShowMineprobabilities();
			} else if (this.modus == "loese") {
				this.gamecontroller.executeLoeseDeterministisch();
			} else if (this.modus == "startKI") {
				this.gamecontroller.executeStartKI();
			} else {
				this.gamecontroller.executeActivateKIUntilWin();
			}
			this.minesweeperGUI.showSmileyNormal();
			this.minesweeperGUI.showStopMenu(false);
			this.minesweeperGUI.resumeMouseListener();
			this.gamecontroller.setToolsActive(true);
			this.gamecontroller.resetInterruptionDetected();
		} catch (Exception e) {
			if (e.getMessage().equals("Interrupted")) {
				this.minesweeperGUI.showSmileyNormal();
				this.minesweeperGUI.resumeMouseListener();
				this.gamecontroller.setToolsActive(true);
			}
			this.minesweeperGUI.showStopMenu(false);
			this.gamecontroller.resetInterruptionDetected();
		}
	}
}