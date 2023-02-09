package de.drake.minesweeper.view;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import de.drake.minesweeper.controller.Gamecontroller;

/**
 * Der Keylistener für Tastatureingaben während des Spiels (z.B. F2 für neues Spiel).
 */
class MinesweeperKeyListener implements KeyListener {
	
	/**
	 * Speichert den Gamecontroller zur Verarbeitung der KeyEvents.
	 */
	private Gamecontroller gamecontroller;
	
	/**
	 * Legt einen neuen MinesweeperKeyListener an.
	 * 
	 * @param gamecontroller
	 * 		der Gamecontroller zur Verarbeitung der KeyEvents
	 */
	MinesweeperKeyListener(final Gamecontroller gamecontroller) {
		this.gamecontroller = gamecontroller;
	}

	/**
	 * Wird ausgelöst, wenn eine Taste gedrückt wurde
	 * 
	 * @param arg0
	 * 		das KeyEvent, was durch die gedrückte Taste ausgelöst wurde
	 */
	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_F2) {
			this.gamecontroller.startNewRound();
			return;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_F5) {
			this.gamecontroller.showMineprobabilities();
			return;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_F6) {
			this.gamecontroller.loeseDeterministisch();
			return;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_F7) {
			this.gamecontroller.startKI();
			return;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_F8) {
			this.gamecontroller.activateKIUntilWin();
			return;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
			this.gamecontroller.interruptCalculation();
			return;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}
}