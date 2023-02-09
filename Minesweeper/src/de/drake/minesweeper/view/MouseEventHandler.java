package de.drake.minesweeper.view;

import de.drake.minesweeper.controller.GameOverException;
import de.drake.minesweeper.controller.Gamecontroller;

class MouseEventHandler {

	/**
	 * Die GUI, um den Smiley beim Hovern anzupassen
	 */
	private MinesweeperGUI minesweeperGUI;
	
	/**
	 * Der Controller, der Eingaben verarbeitet
	 */
	private Gamecontroller gamecontroller;
	
	/**
	 * Speichert, ob der EventHandler aktiv auf Eingaben reagieren soll. Im Falle
	 * eines Sieges oder einer Niederlage wird der Handler deaktiviert.
	 */
	private boolean isActive = false;
	
	/**
	 * Speichert die Zeile, in der sich der Cursor befindet
	 */
	private int mousepositionZeile;
	
	/**
	 * Speichert die Spalte, in der sich der Cursor befindet
	 */
	private int mousepositionSpalte;
	
	/**
	 * Speichert, ob die linke Maustaste derzeit gedr�ckt ist
	 */
	private boolean leftMouseIsPressed = false;
	
	/**
	 * Speichert, ob die rechte Maustaste derzeit gedr�ckt ist
	 */
	private boolean rightMouseIsPressed = false;
	
	/**
	 * Speichert, ob die mittlere Maustaste derzeit gedr�ckt ist
	 */
	private boolean middleMouseIsPressed = false;
	
	/**
	 * Speichert, ob sich der Cursor derzeit �ber einem Minefield befindet
	 */
	private boolean mouseIsOverMinefield = false;
	
	/**
	 * Speichert, ob sich der Cursor derzeit �ber dem Smiley befindet
	 */
	private boolean mouseIsOverSmiley = false;

	/**
	 * Speichert, ob der Smiley derzeit als Stopschild angezeigt wird
	 */
	private boolean smileyIsStop = false;
	
	/**
	 * Konstruktor zum Erzeugen eines MouseEventHandlers
	 * 
	 * @param minesweeperGUI
	 * 		Das Hauptfenster der GUI zum Verarbeiten der Smileyeffekte
	 * @param gamecontroller
	 * 		Der Controller, der Eingaben verarbeitet
	 */
	MouseEventHandler(final MinesweeperGUI minesweeperGUI,
			final Gamecontroller gamecontroller) {
		this.minesweeperGUI = minesweeperGUI;
		this.gamecontroller = gamecontroller;
	}
	
	/**
	 * Wird ausgel�st, wenn die linke Maustaste losgelassen wird
	 */
	void leftMouseReleased() {
		final boolean specialClickPreparedBefore = this.specialClickPrepared();
		this.leftMouseIsPressed = false;
		if (this.mouseIsOverSmiley && !specialClickPreparedBefore) {
			this.minesweeperGUI.showSmileyUnhovered();
			if (this.smileyIsStop) {
				this.gamecontroller.interruptCalculation();
			} else {
				this.gamecontroller.startNewRound();
			}
			return;
		}
		if (!this.isActive)
			return;
		if (!this.specialClickPrepared())
			this.minesweeperGUI.showSmileyNormal();
		if (specialClickPreparedBefore && !this.specialClickPrepared()
				&& this.mouseIsOverMinefield)
			this.gamecontroller.removeHoverAround(
					mousepositionZeile, mousepositionSpalte);
		if (!specialClickPreparedBefore && this.mouseIsOverMinefield)
			try {
				this.gamecontroller.uncover(
						this.mousepositionZeile, this.mousepositionSpalte);
			} catch (GameOverException e) {
			}
		if (specialClickPreparedBefore && this.mouseIsOverMinefield)
			this.gamecontroller.specialClick(
					this.mousepositionZeile, this.mousepositionSpalte);
	}
	
	/**
	 * Wird ausgel�st, wenn die rechte Maustaste losgelassen wird
	 */
	void rightMouseReleased() {
		final boolean specialClickPreparedBefore = this.specialClickPrepared();
		this.rightMouseIsPressed = false;
		if (this.mouseIsOverSmiley && this.leftMouseIsPressed
				&& !this.middleMouseIsPressed)
			this.minesweeperGUI.showSmileyHovered();
		if (!this.isActive)
			return;
		if (specialClickPreparedBefore && !this.specialClickPrepared()
				&& this.mouseIsOverMinefield) {
			this.gamecontroller.removeHoverAround(mousepositionZeile, mousepositionSpalte);
			this.gamecontroller.hover(mousepositionZeile, mousepositionSpalte);
		}
		if (specialClickPreparedBefore && this.mouseIsOverMinefield)
			this.gamecontroller.specialClick(
					this.mousepositionZeile, this.mousepositionSpalte);
	}
	
	/**
	 * Wird ausgel�st, wenn die mittlere Maustaste losgelassen wird
	 */
	void middleMouseReleased() {
		this.middleMouseIsPressed = false;
		if (this.mouseIsOverSmiley && this.leftMouseIsPressed
				&& !this.rightMouseIsPressed)
			this.minesweeperGUI.showSmileyHovered();
		if (!this.isActive)
			return;
		if (!this.leftMouseIsPressed)
			this.minesweeperGUI.showSmileyNormal();
		if (!this.specialClickPrepared() && this.mouseIsOverMinefield)
			this.gamecontroller.removeHoverAround(mousepositionZeile, mousepositionSpalte);
		if (!this.specialClickPrepared() && this.leftMouseIsPressed
				 && this.mouseIsOverMinefield)
			this.gamecontroller.hover(mousepositionZeile, mousepositionSpalte);
		if (this.mouseIsOverMinefield)
			this.gamecontroller.specialClick(
					this.mousepositionZeile, this.mousepositionSpalte);
	}
	
	/**
	 * Wird ausgel�st, wenn die linke Maustaste gedr�ckt wird
	 */
	void leftMousePressed() {
		final boolean specialClickPreparedBefore = this.specialClickPrepared();
		this.leftMouseIsPressed = true;
		if (this.mouseIsOverSmiley && !this.specialClickPrepared())
			this.minesweeperGUI.showSmileyHovered();
		if (!this.isActive)
			return;
		if (!specialClickPreparedBefore)
			this.minesweeperGUI.showSmileyO();
		if (!this.specialClickPrepared() && this.mouseIsOverMinefield)
			this.gamecontroller.hover(mousepositionZeile, mousepositionSpalte);
		if (!specialClickPreparedBefore && this.specialClickPrepared()
				&& this.mouseIsOverMinefield)
			this.gamecontroller.hoverAround(mousepositionZeile, mousepositionSpalte);
	}
	
	/**
	 * Wird ausgel�st, wenn die rechte Maustaste gedr�ckt wird
	 */
	void rightMousePressed() {
		final boolean specialClickPreparedBefore = this.specialClickPrepared();
		this.rightMouseIsPressed = true;
		if (this.mouseIsOverSmiley && this.leftMouseIsPressed
				&& !this.middleMouseIsPressed)
			this.minesweeperGUI.showSmileyUnhovered();
		if (!this.isActive)
			return;
		if (!specialClickPreparedBefore && this.specialClickPrepared()
				&& this.mouseIsOverMinefield)
			this.gamecontroller.hoverAround(mousepositionZeile, mousepositionSpalte);
		if (!this.specialClickPrepared() && this.mouseIsOverMinefield) {
			this.gamecontroller.toggleMineMark(mousepositionZeile, mousepositionSpalte);
		}
	}
	
	/**
	 * Wird ausgel�st, wenn die mittlere Maustaste gedr�ckt wird
	 */
	void middleMousePressed() {
		final boolean specialClickPreparedBefore = this.specialClickPrepared();
		this.middleMouseIsPressed = true;
		if (this.mouseIsOverSmiley && this.leftMouseIsPressed
				&& !this.rightMouseIsPressed)
			this.minesweeperGUI.showSmileyUnhovered();
		if (!this.isActive)
			return;
		if (!specialClickPreparedBefore)
			this.minesweeperGUI.showSmileyO();
		if (!specialClickPreparedBefore && this.mouseIsOverMinefield)
			this.gamecontroller.hoverAround(mousepositionZeile, mousepositionSpalte);
	}
	
	/**
	 * Wird ausgel�st, wenn die Maus �ber ein bestimmtes Minenfeld bewegt wird
	 * 
	 * @param zeile
	 * 		die Zeile, �ber die der Mauszeiger bewegt wird
	 * @param spalte
	 * 		die Spalte, �ber die der Mauszeiger bewegt wird
	 */
	void mouseEntered(final int zeile, final int spalte) {
		this.mouseIsOverMinefield = true;
		this.mousepositionZeile = zeile;
		this.mousepositionSpalte = spalte;
		if (!this.isActive)
			return;
		if (this.specialClickPrepared())
			this.gamecontroller.hoverAround(zeile, spalte);
		if (!this.specialClickPrepared() && this.leftMouseIsPressed)
			this.gamecontroller.hover(zeile, spalte);
	}
	
	/**
	 * Wird ausgel�st, wenn die Maus �ber dem Smiley bewegt wird
	 */
	void mouseEnteredSmiley() {
		this.mouseIsOverSmiley = true;
		if (this.leftMouseIsPressed && !this.specialClickPrepared())
			this.minesweeperGUI.showSmileyHovered();
	}
	
	/**
	 * Wird ausgel�st, wenn die Maus von einem bestimmten Minenfeld wegbewegt wird
	 * 
	 * @param zeile
	 * 		die Zeile des Feldes, das der Mauszeiger verl�sst
	 * @param spalte
	 * 		die Spalte des Feldes, das der Mauszeiger verl�sst
	 */
	void mouseExited(final int zeile, final int spalte) {
		if (this.mousepositionZeile == zeile && this.mousepositionSpalte == spalte) {
			this.mouseIsOverMinefield = false;
		}
		if (!this.isActive)
			return;
		if (this.specialClickPrepared())
			this.gamecontroller.removeHoverAround(zeile, spalte);
		if (!this.specialClickPrepared() && this.leftMouseIsPressed)
			this.gamecontroller.removeHover(zeile, spalte);
	}
	
	/**
	 * Wird ausgel�st, wenn die Maus den Smiley verl�sst
	 */
	void mouseExitedSmiley() {
		this.mouseIsOverSmiley = false;
		if (this.leftMouseIsPressed && !this.specialClickPrepared())
			this.minesweeperGUI.showSmileyUnhovered();
	}
	
	/**
	 * Gibt aus, ob derzeit die Voraussetzungen f�r einen "Special Click" getroffen sind
	 * 
	 * @return true, wenn Special Click vorbereitet
	 */
	private boolean specialClickPrepared() {
		return ((this.leftMouseIsPressed && this.rightMouseIsPressed)
				|| this.middleMouseIsPressed);
	}
	
	/**
	 * Aktiviert oder deaktivert den EventHandler.
	 * 
	 * @param value
	 * 		true, wenn der EventHandler aktiviert werden soll, false, wenn er deaktiviert
	 * 		werden soll. 
	 */
	void setActive(final boolean value) {
		this.isActive = value;
		if (!this.isActive && this.specialClickPrepared()) {
			this.gamecontroller.removeHoverAround(
					mousepositionZeile, mousepositionSpalte);
		} else if (!this.isActive && this.leftMouseIsPressed)
			this.gamecontroller.removeHover(
					mousepositionZeile, mousepositionSpalte);
	}

	public void setSmileyIsStop(final boolean smileyIsStop) {
		this.smileyIsStop = smileyIsStop;
	}
}