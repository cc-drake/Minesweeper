package de.drake.minesweeper.view;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


/**
 * MouseListener, der den Smiley überwacht
 */
class SmileyListener extends WindowListener implements MouseListener {
	
	/**
	 * Konstruktor für den SmileyListener
	 * 
	 * @param gamecontroller
	 * 		der Controller, der die Events weiterverarbeitet
	 */
	SmileyListener(final MouseEventHandler mouseEventHandler) {
		super(mouseEventHandler);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		super.mouseEventHandler.mouseEnteredSmiley();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		super.mouseEventHandler.mouseExitedSmiley();
	}
}