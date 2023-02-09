package de.drake.minesweeper.view;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * MouseListener f�r die einzelnen Minenfelder
 */
class MinefieldListener extends WindowListener implements MouseListener {
	
	/**
	 * die Zeile des Feldes, das der Listener �berwacht
	 */
	private int zeile;
	
	/**
	 * die Spalte des Feldes, das der Listener �berwacht
	 */
	private int spalte;
	
	/**
	 * Initialisiert den Minefield-Listener.
	 * 
	 * @param mouseEventHandler
	 * 		der Handler, der die MouseEvents weiterverarbeitet
	 * @param zeile
	 * 		die Zeile des Feldes, das der Listener �berwacht
	 * @param spalte
	 * 		die Spalte des Feldes, das der Listener �berwacht
	 */
	MinefieldListener(final MouseEventHandler mouseEventHandler,
			final int zeile, final int spalte) {
		super(mouseEventHandler);
		this.zeile = zeile;
		this.spalte = spalte;
	}

	/**
	 * Wird ausgel�st, wenn die Maus �ber das Minenfeld bewegt wird
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
		super.mouseEventHandler.mouseEntered(zeile, spalte);
	}

	/**
	 * Wird ausgel�st, wenn die Maus das Minenfeld verl�sst
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {
		super.mouseEventHandler.mouseExited(zeile, spalte);
	}
}