package de.drake.minesweeper.view;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * MouseListener für die einzelnen Minenfelder
 */
class MinefieldListener extends WindowListener implements MouseListener {
	
	/**
	 * die Zeile des Feldes, das der Listener überwacht
	 */
	private int zeile;
	
	/**
	 * die Spalte des Feldes, das der Listener überwacht
	 */
	private int spalte;
	
	/**
	 * Initialisiert den Minefield-Listener.
	 * 
	 * @param mouseEventHandler
	 * 		der Handler, der die MouseEvents weiterverarbeitet
	 * @param zeile
	 * 		die Zeile des Feldes, das der Listener überwacht
	 * @param spalte
	 * 		die Spalte des Feldes, das der Listener überwacht
	 */
	MinefieldListener(final MouseEventHandler mouseEventHandler,
			final int zeile, final int spalte) {
		super(mouseEventHandler);
		this.zeile = zeile;
		this.spalte = spalte;
	}

	/**
	 * Wird ausgelöst, wenn die Maus über das Minenfeld bewegt wird
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
		super.mouseEventHandler.mouseEntered(zeile, spalte);
	}

	/**
	 * Wird ausgelöst, wenn die Maus das Minenfeld verlässt
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {
		super.mouseEventHandler.mouseExited(zeile, spalte);
	}
}