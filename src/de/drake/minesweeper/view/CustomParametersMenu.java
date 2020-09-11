package de.drake.minesweeper.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Scanner;

import de.drake.minesweeper.controller.Maincontroller;


/**
 * Fragt die Parameter des gewünschten Minesweeper ab.
 */
public class CustomParametersMenu extends Frame {

	/**
	 * Die serialVersionUID für NonoGUI
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Die TextArea, in dem die Höhe des Minesweeper eingegeben werden soll
	 */
	private TextField hoeheneingabeTextField;
	
	/**
	 * Die TextArea, in dem die Breite des Minesweeper eingegeben werden soll
	 */
	private TextField breiteneingabeTextField;
	
	/**
	 * Die TextArea, in dem die Minenanzahl des Minesweeper eingegeben werden soll
	 */
	private TextField mineneingabeTextField;
	
	
	// Konstruktoren
	
	/**
	 * Erzeugt ein Fenster, in dem die Parameter
	 * des Minesweepers eingegeben werden sollen
	 * 
	 * @param titel
	 * 		der Titel des GUI-Fensters
	 * @param maincontroller
	 * 		der Controller, an den die Eingaben gesendet werden sollen
	 * @param hoehe
	 * 		die voreingestellte Höhe des Minesweepers
	 * @param breite
	 * 		die voreingestellte Breite des Minesweepers
	 * @param minenzahl
	 * 		die voreingestellte Gesamtzahl der Minen im Minesweeper
	 */
	public CustomParametersMenu(final String titel,
			final Maincontroller maincontroller, final int hoehe, final int breite,
			final int minenzahl) {
		super(titel);
		
		this.setSize(400,120);
		this.setLocation(400,300);
		this.setLayout(new BorderLayout());
		this.setResizable(false);
		
		Panel eingabePanel = new Panel();
		eingabePanel.setLayout(new GridLayout(3,2));
		this.add(eingabePanel, BorderLayout.CENTER);
		
			Label hoeheneingabeLabel = new Label("Höhe (1 - 50):");
			this.hoeheneingabeTextField = new TextField();
			eingabePanel.add(hoeheneingabeLabel);
			eingabePanel.add(hoeheneingabeTextField);
			
			Label breiteneingabeLabel = new Label("Breite (1 - 50):");
			this.breiteneingabeTextField = new TextField();
			eingabePanel.add(breiteneingabeLabel);
			eingabePanel.add(breiteneingabeTextField);
			
			Label mineneingabeLabel = new Label("Minenanzahl (0 - Höhe*Breite):");
			this.mineneingabeTextField = new TextField();
			eingabePanel.add(mineneingabeLabel);
			eingabePanel.add(mineneingabeTextField);
			
			this.hoeheneingabeTextField.setText("" + hoehe);
			this.breiteneingabeTextField.setText("" + breite);
			this.mineneingabeTextField.setText("" + minenzahl);
			
		Panel generatePanel = new Panel();
		generatePanel.setLayout(new FlowLayout());
		this.add(generatePanel, BorderLayout.SOUTH);
		
			Button generateButton = new Button("Neues Spiel starten");
			generatePanel.add(generateButton);
		
		this.enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		StartGameActionListener startGameActionListener =
				new StartGameActionListener(maincontroller, this);
		generateButton.addActionListener(startGameActionListener);
		this.hoeheneingabeTextField.addActionListener(startGameActionListener);
		this.breiteneingabeTextField.addActionListener(startGameActionListener);
		this.mineneingabeTextField.addActionListener(startGameActionListener);
	}
	
	/**
	 * Methode, die ausgeführt wird wenn das Fenster geschlossen werden soll
	 */
	protected void processWindowEvent(WindowEvent e) {
		if (e.getID()==WindowEvent.WINDOW_CLOSING) {
			 this.dispose();
			 System.exit(0);
		 }
	}
	
	/**
	 * Liefert die gewünschte Höhe des Minesweepers
	 * 
	 * @return die Eingabe des Benutzers als String
	 */
	String getHoehe() {
		return this.hoeheneingabeTextField.getText();
	}
	
	/**
	 * Liefert die gewünschte Breite des Minesweepers
	 * 
	 * @return die Eingabe des Benutzers als String
	 */
	String getBreite() {
		return this.breiteneingabeTextField.getText();
	}
	
	/**
	 * Liefert die gewünschte Minenzahl des Minesweepers
	 * 
	 * @return die Eingabe des Benutzers als String
	 */
	String getMinenzahl() {
		return this.mineneingabeTextField.getText();
	}
}

/**
 * ActionListener für den Start eines neuen Spiels
 */
class StartGameActionListener implements ActionListener {
	
	/**
	 * Der Controller zum Starten eines neuen Spiels
	 */
	private Maincontroller maincontroller;
	
	/**
	 * Das Eingabefenster für die Werte Höhe, Breite, Minenzahl
	 */
	private CustomParametersMenu customParametersMenu;
	
	/**
	 * Konstruktor zum Erzeugen eines neuen StartGameActionListeners.
	 * 
	 * @param minesweeperController
	 * 		Der Controller zum Starten eines neuen Spiels
	 */
	StartGameActionListener(final Maincontroller maincontroller,
			final CustomParametersMenu customParametersMenu) {
		this.maincontroller = maincontroller;
		this.customParametersMenu = customParametersMenu;
	}

	/**
	 * Versucht, die Spielparameter auszulesen.
	 * Bei gültigen Eingaben wird ein neues Spiel begonnen.
	 * 
	 * @param arg0
	 * 		ActionEvent, welches aus dem Drücken des "neues Spiel starten"
	 * 		oder dem Bestätigen mit Enter resultiert.
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		int hoehe, breite, minenzahl;
		try {
			hoehe = StartGameActionListener.string2int(
					this.customParametersMenu.getHoehe());
			breite = StartGameActionListener.string2int(
					this.customParametersMenu.getBreite());
			minenzahl = StartGameActionListener.string2int(
					this.customParametersMenu.getMinenzahl());
		} catch (Exception f) {
			return;
		}
		if (hoehe < 1 || hoehe > 50 || breite < 1 || breite > 50
				|| minenzahl < 0 || minenzahl > hoehe * breite)
			return;
		this.customParametersMenu.dispose();
		this.maincontroller.startNewGame(hoehe, breite, minenzahl);
	}

	/**
	 * Wandelt einen String in einen int-Wert um.
	 * 
	 * @param inputString
	 * 		der auszulesende String
	 * @return der int-Wert aus dem String
	 * @throws Exception
	 * 		Wird geworfen, wenn der String keinen gültigen int-Wert enthält 
	 */
	private static int string2int(String inputString) throws Exception {
		Scanner scanner = new Scanner(inputString);
		if (scanner.hasNextInt()) {
			int result = scanner.nextInt();
			scanner.close();
			return result;
		}
		scanner.close();
		throw new Exception("Der übergebene String enthält keine gültigen int-Werte.");
	}
}