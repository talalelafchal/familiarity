/*
	Course: Programming Fundamentals COIT11222 2014 Term2
	Purpose: Assignment two -- Motorcity Car Insurance GUI application
	Programmer: Bernard Li
	File: CarInsuranceGUI.java
	Date: 14 August 2014
*/

/*
	Enter your header comment here
*/


import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;


public class CarInsuranceGUI extends JFrame
{
	///////////////////////////////////////////////////////////
	// declare your data structures and class variables here //
	///////////////////////////////////////////////////////////


	// GUI components
	JLabel registrationLabel;		// label for registration field
	JTextField registrationField;	// field to enter the cars's registration
	JLabel ageLabel;				// label for selecting the car's age from drop-down combo box
	JComboBox<String> ageCombo;		// drop-down combo box for selecting the car's age
	JCheckBox accidentCheckBox;		// check box for selecting if car has been in an accident or not
	JButton enterButton;			// button for entering car's detail
	JButton displayAllButton;		// button to display all cars entered so far
	JButton sortButton;				// button to sort the car records by name
	JButton searchButton;			// button to search for a car using it's registration
	JButton exitButton;				// button to exit the program
	JTextArea textArea;				// text area for displaying the data
	JScrollPane scrollPane;			// scoll pane for text area scrolling

    ArrayList<CarItem> carList = new ArrayList<CarItem>();

	// Constructor
	public CarInsuranceGUI()
	{
		super("Motorcity Car Insurance");	 			// invoke JFrame constructor
		setLayout(new FlowLayout());					// set the layout to flow layout

		registrationLabel = new JLabel("Registration");	// create registration label
		add(registrationLabel);							// add the label to the JFrame
		registrationField = new JTextField(15);			// create registration field
		add(registrationField);							// add the registration field to the JFrame

		ageLabel = new JLabel("Age");					// create age label
		add(ageLabel);									// add the name label
		ageCombo = new JComboBox<String>();				// create the age combo box
		for (int i = 0; i <= 30; i++)					// populate the age combo box with numbers 0-30
		{
			ageCombo.addItem(i + "");					// add number as a string
		}
		add(ageCombo);									// add the age combo

		accidentCheckBox = new JCheckBox("Accident?");	// create accident checkbox
		add(accidentCheckBox);							// add accident checkbox

		enterButton = new JButton("Enter");				// create enter button
		add(enterButton);								// add enter button

		displayAllButton = new JButton("Display All");	// create display all button
		add(displayAllButton);							// add display all button

		sortButton = new JButton("Sort");				// create sort button
		add(sortButton);								// add sort button

		searchButton = new JButton("Search");			// create search button
		add(searchButton);								// add search button

		exitButton = new JButton("Exit");				// create exit button
		add(exitButton);								// add exit button

		textArea = new JTextArea(16, 52);				// create text area
		// set text area to a monospaced font so the columns can be aligned using a format string
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		textArea.setEditable(false);					// set the text area to be read only
		scrollPane = new JScrollPane(textArea);			// put the text area into the scroll pane
		add(scrollPane);								// add the scroll pane


		// add the ActionListener objects to all of the buttons and the name field
		ButtonHandler buttonEvent = new ButtonHandler();	// create event handler object

		enterButton.addActionListener(buttonEvent);
		registrationField.addActionListener(buttonEvent);
		displayAllButton.addActionListener(buttonEvent);
		sortButton.addActionListener(buttonEvent);
		searchButton.addActionListener(buttonEvent);
		exitButton.addActionListener(buttonEvent);

		// when the user pushes the system close (X top right corner)
		addWindowListener( // override window closing method
			new WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{
					exit();	// Attempt to exit application
				}
			}
		);

		////////////////////////////////////////////////////////////////////////
		// contruct your data structures and set up your class variables here //
		////////////////////////////////////////////////////////////////////////


	} // end constructor


	// method used to enter that data for a car
	private void enterData()
	{
		// TO BE COMPLETED BY STUDENTS

		registrationField.requestFocus();


        CarItem car = new CarItem();

        if (registrationField.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Please enter a car's registration",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else if (carList.size() <= 20) {
            car.setRegistration(registrationField.getText());
            car.setAge(ageCombo.getSelectedIndex());
            car.setAccident(accidentCheckBox.isSelected());

            if (car.getAge() > 5) {
                car.setFee(350);
            } else {
                car.setFee(200);
            }

            if (!car.isAccident()){
                double fee = car.getFee();
                car.setFee(  fee*0.75);
            }

            carList.add(car);
            registrationField.setText("");
            ageCombo.setSelectedIndex(0);
            accidentCheckBox.setSelected(false);

            String title = "Registration\tAge\tAccident\tFee\n";

            String separator = "---------------------------------------------\n";

            String currentCar = String.format(
                    "%1$s\t\t%2$d\t%3$s\t\t$%4$.2f\n", car.getRegistration(),
                    car.getAge(), car.isAccident() ? "Yes" : "No",
                    car.getFee());

            System.out.println(title + currentCar);

            textArea.setText(title + separator + currentCar);
        } else {
            JOptionPane.showMessageDialog(null,
                    "You have reached the limit of cars to enter", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
	}// enterData


	// method used to display the cars entered into the program
	private void displayAll()
	{
		// TO BE COMPLETED BY STUDENTS
        String seperator = "---------------------------------------------\n";
        double totalFee = 0;
        displayContent(carList);


        for (CarItem car : carList) {
            totalFee += car.getFee();
        }
        textArea.append(seperator);
        textArea.append("\t\t\t\tTotal fees:$" + totalFee );
	}// displayAll


	// method used to sort the car arrays based on registration
	private void sort()
	{
		// TO BE COMPLETED BY STUDENTS
        registrationField.requestFocus();

        ArrayList<CarItem> sortedList = (ArrayList<CarItem>) carList.clone();

        for (int i = 0; i < sortedList.size(); i++) {

            for (int j = i + 1; j < sortedList.size(); j++) {

                if (sortedList.get(i).getRegistration()
                        .compareTo(sortedList.get(j).getRegistration()) > 0) {

                    CarItem temp = sortedList.get(i);
                    sortedList.set(i, sortedList.get(j));
                    sortedList.set(j, temp);
                }
            }
        }
        for (CarItem car : sortedList)
            System.out.println(car.getRegistration());
        carList = sortedList;

        displayAll();
	}// sort


	// method used to search for a car by registration
	private void search()
	{
		// TO BE COMPLETED BY STUDENTS
        String searchName = JOptionPane.showInputDialog(null,
                "Enter the registration of car to search", "Input",
                JOptionPane.QUESTION_MESSAGE);

        ArrayList<CarItem> result = new ArrayList<CarItem>();

        for (CarItem car : carList) {
            if (car.getRegistration().indexOf(searchName) >= 0) {
                result.add(car);
            }
        }

        if (result.size() > 0) {
            displayContent(result);
        } else {
            textArea.setText("");
            JOptionPane.showMessageDialog(null, searchName + " car not found",
                    "Message", JOptionPane.INFORMATION_MESSAGE);
        }
	}// search


    private void displayContent(List<CarItem> carItemList){
        String title =  "Registration\tAge\tAccident\tFee\n";

        String seperator = "---------------------------------------------\n";
        textArea.setText(title + seperator);

        for (CarItem car : carItemList) {
            String currentCar = String.format(
                    "%1$s\t\t%2$d\t%3$s\t\t$%4$.2f\n", car.getRegistration(),
                    car.getAge(), car.isAccident() ? "Yes" : "No",
                    car.getFee());
            textArea.append(currentCar);

        }
    }

	// method used to exit the program
	// a confirmation dialog should be shown to the user before exiting
	private void exit()
	{
		// TO BE COMPLETED BY STUDENTS
		System.exit(0);

	}// exit


	public static void main(String [] args)
	{ // main method to create an instance of the class
		CarInsuranceGUI carInsurance = new CarInsuranceGUI();			// create instance of class

		carInsurance.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);	// let the code close the program
		carInsurance.setSize(440, 390);										// dimensions of the JFrame
		carInsurance.setVisible(true);										// make the application visible
	} // main


	// private class used as an action listener for the buttons
	private class ButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == enterButton || e.getSource() == registrationField)	// enter button or return hit in registration field
				enterData();
			else if (e.getSource() == displayAllButton)						// display all button hit
				displayAll();
			else if (e.getSource() == sortButton)							// sort button hit
				sort();
			else if (e.getSource() == searchButton)							// search button hit
				search();
			else if (e.getSource() == exitButton)							// exit button hit
				exit();
		}
	}// end ButtonHandler


    public class CarItem{
        private String registration;
        private int age;
        private boolean isAccident;
        private double fee;

        public double getFee() {
            return fee;
        }

        public void setFee(double fee) {
            this.fee = fee;
        }

        public boolean isAccident() {
            return isAccident;
        }

        public void setAccident(boolean isAccident) {
            this.isAccident = isAccident;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getRegistration() {
            return registration;
        }

        public void setRegistration(String registration) {
            this.registration = registration;
        }



    }
}// end class

