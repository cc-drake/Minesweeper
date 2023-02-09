package de.drake.minesweeper.view;

import java.awt.Dimension;

import javax.swing.JLabel;

/**
 * Repräsentiert den Smiley-Button und speichert den aktuellen Zustand des Buttons.
 */
class Smiley extends JLabel {

	/**
	 * Die serialVersionUID für den Smiley
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Die möglichen Anzeigen des Smileys
	 */
	private enum Smileystate {Cool, Dead, Normal, O, Stop};
	
	/**
	 * Das aktuelle Aussehen des Smileys. Wird gespeichert, um bei Hovereffekten
	 * zum richtigen Smiley zurückzukehren.
	 */
	private Smileystate smileystate;
	
	/**
	 * Speichert, ob der Smiley derzeit gehovered ist.
	 */
	private boolean smileyIsHovered = false;
	
	/**
	 * Konstruktor für einen Smiley.
	 */
	Smiley() {
		super();
		this.setPreferredSize(new Dimension(26,26));
	}
	
	/**
	 * Methode, die dem Smiley sein normales Aussehen gibt
	 */
	void showSmileyNormal() {
		this.smileystate = Smileystate.Normal;
		if (!this.smileyIsHovered) {
			this.setIcon(MinesweeperGUI.imageLoader("smileyNormal"));
		} else {
			this.setIcon(MinesweeperGUI.imageLoader("smileyO"));
		}
	}
	
	/**
	 * Methode, die den Smiley tötet
	 */
	void showSmileyDead() {
		this.smileystate = Smileystate.Dead;
		if (!this.smileyIsHovered)
			this.setIcon(MinesweeperGUI.imageLoader("smileyDead"));
	}
	
	/**
	 * Methode, die den Smiley "O" sagen lässt
	 */
	void showSmileyO() {
		this.smileystate = Smileystate.O;
		if (!this.smileyIsHovered)
			this.setIcon(MinesweeperGUI.imageLoader("smileyO"));
	}
	
	/**
	 * Methode, die dem Smiley eine Sonnenbrille verpasst
	 */
	void showSmileyCool() {
		this.smileystate = Smileystate.Cool;
		if (!this.smileyIsHovered)
			this.setIcon(MinesweeperGUI.imageLoader("smileyCool"));
	}
	
	/**
	 * Methode, die dem Smiley einen Hover-Effekt verpasst
	 */
	void showSmileyHovered() {
		this.smileyIsHovered = true;
		if (!this.smileystate.equals(Smileystate.Stop)) {
			this.setIcon(MinesweeperGUI.imageLoader("smileyHovered"));
		} else {
			this.setIcon(MinesweeperGUI.imageLoader("smileyStopHovered"));
		}
	}
	
	/**
	 * Methode, die einen Hover-Effekt des Smileys entfernt
	 */
	void showSmileyUnhovered() {
		this.smileyIsHovered = false;
		this.setIcon(MinesweeperGUI.imageLoader("smiley" + this.smileystate));
	}

	public void showSmileyStop() {
		this.smileystate = Smileystate.Stop;
		this.setIcon(MinesweeperGUI.imageLoader("smileyStop"));
	}
}