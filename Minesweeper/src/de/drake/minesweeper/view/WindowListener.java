package de.drake.minesweeper.view;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * MouseListener f�r alle Teile der GUI, um gedr�ckte
 * Maustasten verfolgen zu k�nnen
 */
class WindowListener implements MouseListener {
	
	/**
	 * der Handler, der die MouseEvents weiterverarbeitet
	 */
	protected MouseEventHandler mouseEventHandler;
	
	/**
	 * Initialisiert einen Window-Listener zur �berwachung der gedr�ckten Maustasten.
	 * 
	 * @param mouseEventHandler
	 * 		der Handler, der die MouseEvents weiterverarbeitet
	 */
	WindowListener(final MouseEventHandler mouseEventHandler) {
		this.mouseEventHandler = mouseEventHandler;
	}

	/**
	 * Wird ausgel�st, wenn geklickt wurde.
	 */
	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	/**
	 * Wird ausgel�st, wenn die Maus die Minenfelder verl�sst
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	/**
	 * Wird ausgel�st, wenn die Maus die Minenfelder betritt
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	/**
	 * Wird ausgel�st, wenn eine Maustaste gedr�ckt wird
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {
		if (arg0.getButton() == MouseEvent.BUTTON1) {
			this.mouseEventHandler.leftMousePressed();
		}
		if (arg0.getButton() == MouseEvent.BUTTON2) {
			this.mouseEventHandler.middleMousePressed();
		}
		if (arg0.getButton() == MouseEvent.BUTTON3) {
			this.mouseEventHandler.rightMousePressed();
		}
	}

	/**
	 * Wird ausgel�st, wenn eine Maustaste losgelassen wird
	 */
	@Override
	public void mouseReleased(MouseEvent arg0) {
		if (arg0.getButton() == MouseEvent.BUTTON1) {
			this.mouseEventHandler.leftMouseReleased();
		}
		if (arg0.getButton() == MouseEvent.BUTTON2) {
			this.mouseEventHandler.middleMouseReleased();
		} 
		if (arg0.getButton() == MouseEvent.BUTTON3) {
			this.mouseEventHandler.rightMouseReleased();
		}
	}
}