package de.drake.minesweeper.view;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * MouseListener für alle Teile der GUI, um gedrückte
 * Maustasten verfolgen zu können
 */
class WindowListener implements MouseListener {
	
	/**
	 * der Handler, der die MouseEvents weiterverarbeitet
	 */
	protected MouseEventHandler mouseEventHandler;
	
	/**
	 * Initialisiert einen Window-Listener zur Überwachung der gedrückten Maustasten.
	 * 
	 * @param mouseEventHandler
	 * 		der Handler, der die MouseEvents weiterverarbeitet
	 */
	WindowListener(final MouseEventHandler mouseEventHandler) {
		this.mouseEventHandler = mouseEventHandler;
	}

	/**
	 * Wird ausgelöst, wenn geklickt wurde.
	 */
	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	/**
	 * Wird ausgelöst, wenn die Maus die Minenfelder verlässt
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	/**
	 * Wird ausgelöst, wenn die Maus die Minenfelder betritt
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	/**
	 * Wird ausgelöst, wenn eine Maustaste gedrückt wird
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
	 * Wird ausgelöst, wenn eine Maustaste losgelassen wird
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