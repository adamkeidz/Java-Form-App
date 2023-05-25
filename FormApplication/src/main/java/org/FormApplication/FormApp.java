package org.FormApplication;

import javax.swing.*;

import com.opencsv.exceptions.CsvValidationException;
import com.toedter.calendar.JDateChooser; // for date choosing UI
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.io.*;

import com.opencsv.CSVReader;

import java.util.HashMap;
import java.util.Map;


public class FormApp extends JFrame {
    Calendar cld = Calendar.getInstance(); // it will return the time and date of timezone of the system
    JDateChooser dcCommence = new JDateChooser(cld.getTime());
    JDateChooser dcExpiry = new JDateChooser();
    JDateChooser dcDOB = new JDateChooser();
    private JPanel panel1;
    private JTextField textName;
    private JComboBox cbGender;
    private JTextField textPostcode;
    private JTextField textIC;
    private JTextField textTown;
    private JButton saveButton;
    private JLabel labelCommence;
    private JLabel labelName;
    private JLabel labelGender;
    private JLabel labelPostcode;
    private JLabel labelExpiry;
    private JLabel labelIC;
    private JLabel labelDOB;
    private JLabel labelTown;
    private JPanel jpCal0;
    private JPanel jpCal1;
    private JPanel jpCal2;
    private final Map<String, String> postcodeToTown = new HashMap<>(); // key and value, key is postcode and town is value

    private String name;
    private String icNumber;
    private String gender;
    private String postcode;
    private String town;
    private Date commenceDate;
    private Date expiryDate;
    private Date dob;


    public FormApp(String title) {

        super(title);
        //panel1.setBackground(Color.);
        // in plain-document, user can give any input because unrestricted
        textName.setDocument(new javax.swing.text.PlainDocument());
        ((javax.swing.text.AbstractDocument) textName.getDocument()).setDocumentFilter(new NameFilter());

        // Set maximum length of textName to 30
        textName.setDocument(new JTextFieldLimit(30));

        // Set maximum length of textIC to 12
        textIC.setDocument(new JTextFieldLimit(12));

        // Set maximum length of textPostcode to 5
        textPostcode.setDocument(new JTextFieldLimit(5));

        // add input verifier to textCommence
        dcCommence.setInputVerifier(new DateVerifier());
        // add input verifier to textExpiry
        dcExpiry.setInputVerifier(new DateVerifier());
        // add input verifier to textDOB
        dcDOB.setInputVerifier(new DateVerifier());

        cbGender.setSelectedIndex(0);
        cbGender.setEnabled(true);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(panel1);

        //calendar
        dcCommence.setDateFormatString("dd/MM/yyyy");
        dcExpiry.setDateFormatString("dd/MM/yyyy");
        dcDOB.setDateFormatString("dd/MM/yyyy");
        jpCal0.add(dcCommence);
        jpCal1.add(dcExpiry);

        textIC.getDocument().addDocumentListener(new DocumentListener() {
            // Document listener for textIC field
            // When the text is inserted, removed or changed in the textIC field,
            // it triggers the respective DocumentListener method
            @Override
            public void insertUpdate(DocumentEvent e) {
                // when text is inserted
                setDOBFromIC();
                setGenderFromIC();
            }

            @Override
            //when text is removed
            public void removeUpdate(DocumentEvent e) {
                setDOBFromIC();
                setGenderFromIC();
            }

            @Override
            //when text is changed
            public void changedUpdate(DocumentEvent e) {
                setDOBFromIC();
                setGenderFromIC();
            }
        });



        // Create a CSVReader to read from the mypostcodes.csv resource file.
        // FormApp.class.getResourceAsStream("/mypostcodes.csv") opens an InputStream for the file.
        // We wrap this InputStream in an InputStreamReader, which is then passed to the CSVReader constructor.
        try (CSVReader reader = new CSVReader(new InputStreamReader(FormApp.class.getResourceAsStream("/mypostcodes.csv")))) {
            String[] nextLine; // A variable to hold each line read from the CSV file.

            // Read from the CSV file line by line.
            // reader.readNext() returns the next line from the CSV file as a String array, or null if there are no more lines.
            // We assign the return value to nextLine and continue the loop as long as nextLine is not null.
            while ((nextLine = reader.readNext()) != null) {

                // Extract the postcode and town values from the current line.
                // nextLine[0] is the first field in the line (the postcode), and nextLine[2] is the third field (the town).
                String postcode = nextLine[0];
                String town = nextLine[2];

                // Add the postcode and town to the postcodeToTown map.
                postcodeToTown.put(postcode, town);
            }

            // Catch any CsvValidationException or IOException that may be thrown during the above process.
            // CsvValidationException is thrown by reader.readNext() if a line cannot be parsed.
            // IOException is thrown by reader.readNext() and the CSVReader constructor if there's a problem reading from the file.
        } catch (CsvValidationException | IOException e) {
            // Print the stack trace of the exception to the console. This helps with debugging, as it shows where the exception occurred.
            e.printStackTrace();
        }



        //Document listener for textTown
        textPostcode.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                // if new character is inserted into textfield, it will call the method
                setTownFromPostcode();
            }

            public void removeUpdate(DocumentEvent e) {
                // When a character is removed, call setTownFromPostcode method
                // and if the text field is empty, clear the textTown field
                setTownFromPostcode();
                if (textPostcode.getText().isEmpty()) {
                    textTown.setText("");
                }
            }

            public void changedUpdate(DocumentEvent e) {
                // Not needed for plain text fields
            }
        });

        jpCal2.add(dcDOB);

        // Add save button ActionListener
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveData();
            }
        });

        // Connect to database
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Set up the database connection
            String url = "jdbc:mysql://127.0.0.1/FormApp";
            String username = "newuser";
            String password = "mymy$ql123";
            Connection conn = DriverManager.getConnection(url, username, password);

            // Use the connection to execute SQL statements...

            // Close the connection when done
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        this.pack();
    }


    public static void main(String[] args) {
        JFrame frame = new FormApp("Form Application");
        frame.setVisible(true);
    }


    private void setDOBFromIC() {
        String ic = textIC.getText().trim();
        // Check if the IC value is 12 digits and contains only digits
        if (ic.length() == 12 && ic.matches("\\d+")) {
            // Extract year, month, and day from the IC value
            int year = Integer.parseInt(ic.substring(0, 2));
            int month = Integer.parseInt(ic.substring(2, 4));
            int day = Integer.parseInt(ic.substring(4, 6));

            // Check if the month and day values are valid
            if (month >= 1 && month <= 12 && day >= 1 && day <= 31) {
                Calendar cal = Calendar.getInstance(); // Get the current calendar instance
                int currYear = cal.get(Calendar.YEAR) % 100; // Get the current year

                // Calculate the year of birth based on the IC value
                if (year > currYear) {
                    year += 1900;
                } else {
                    year += 2000;
                }

                cal.set(year, month - 1, day);
                Date dob = cal.getTime();
                dcDOB.setDate(dob);
            } else {
                // Show error message if the month and day values are invalid
                JOptionPane.showMessageDialog(this, "Invalid date of birth");
            }
        } else {
            // Set the date of birth field to null if the IC value is not valid
            dcDOB.setDate(null);
        }
    }

    private void setGenderFromIC() {
        String ic = textIC.getText().trim();
        // Check if the IC value is 12 digits and contains only digits
        if (ic.length() == 12 && ic.matches("\\d+")) {
            // Extract last two digits
            int year = Integer.parseInt(ic.substring(11, 12));
            System.out.println(year);
            if(year %2 == 1){
                cbGender.setSelectedIndex(1);
            }else{
                cbGender.setSelectedIndex(2);
            }

        } else {
            // Set into blank
            cbGender.setSelectedIndex(0);
        }
    }

    private void saveData() {
        try {
            // Get the values entered by the user
            String name = textName.getText();
            String icNumber = textIC.getText();
            String gender = cbGender.getSelectedItem().toString();
            String postcode = textPostcode.getText();
            String town = textTown.getText();

            // Get the date values
            Date commenceDate = dcCommence.getDate();
            Date expiryDate = dcExpiry.getDate();
            Date dob = dcDOB.getDate();

            // Check if the required fields are not empty
            if (name.isEmpty() || icNumber.isEmpty() || gender.isEmpty() || postcode.isEmpty() || town.isEmpty() || commenceDate == null || expiryDate == null || dob == null) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Convert the date values to SQL date format
            java.sql.Date sqlCommenceDate = new java.sql.Date(commenceDate.getTime());
            java.sql.Date sqlExpiryDate = new java.sql.Date(expiryDate.getTime());
            java.sql.Date sqlDOB = new java.sql.Date(dob.getTime());

            // Open a connection to the database
            Connection conn = null;
            try {
                conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/FormApp", "newuser", "mymy$ql123");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            // Prepare a statement for the INSERT query
            PreparedStatement statement = conn.prepareStatement("INSERT INTO clients (name, ic_number, gender, postcode, town, commence_date, expiry_date, dob) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

            // Set the parameters of the statement
            statement.setString(1, name);
            statement.setString(2, icNumber);
            statement.setString(3, gender);
            statement.setString(4, postcode);
            statement.setString(5, town);
            statement.setDate(6, sqlCommenceDate);
            statement.setDate(7, sqlExpiryDate);
            statement.setDate(8, sqlDOB);

            // Execute the query
            statement.executeUpdate();

            // Close the connection and statement
            statement.close();
            conn.close(); // close everytime done updating the database to avoid resource leak

            // Display a success message
            JOptionPane.showMessageDialog(this, "Data saved successfully!");
        } catch (SQLException ex) {
            // Display an error message if the save operation failed
            JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }




    public class JTextFieldLimit extends PlainDocument {
        private int limit;

        public JTextFieldLimit(int limit) {
            super();
            this.limit = limit;
        }

        @Override
        public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
            if (str == null) {
                return;
            }
            // If the length of the text and the string to be inserted is less than or equal to the limit, insert the string
            if ((getLength() + str.length()) <= limit) {
                super.insertString(offset, str, attr);
            }
        }
    }

    // custom input verifier for date fields
    private class DateVerifier extends InputVerifier {
        public boolean verify(JComponent input) {
            // cast the input component to a JTextField
            JTextField textField = (JTextField) input;
            String text = textField.getText().trim(); // get the text from the text field and remove any leading/trailing white spaces
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateFormat.setLenient(false); // when false = very strict which can only use the pattern given
            try {
                Date date = dateFormat.parse(text); // attempt to parse the input text into a Date object using the date format object
                return true;
            } catch (ParseException e) {
                // if an exception is caught, display an error message and return false
                JOptionPane.showMessageDialog(null, "Invalid date format (dd/MM/yyyy)");
                return false;
            }
        }
    }

    private void setTownFromPostcode() {
        // Get the value of the postcode field
        String postcode = textPostcode.getText();
        // Look up the corresponding town for the postcode in the postcodeToTown map
        String town = postcodeToTown.get(postcode);

        // If the postcode field is not empty and there is a town corresponding to the postcode in the map,
        // set the text of the town field to the corresponding town
        if (!postcode.isEmpty() && postcodeToTown.containsKey(postcode)) {
            textTown.setText(postcodeToTown.get(postcode));
        } else {
            // Otherwise, clear the text of the town field
            textTown.setText("");
        }
    }

    public class NameFilter extends DocumentFilter {
        @Override
        public void insertString(DocumentFilter.FilterBypass fb, int offset, // method to check for the new character typed in
                                 String string, AttributeSet attr)           // if a-z or A-Z then it will be inserted into the field
                throws BadLocationException {
            if (string.matches("^[a-zA-Z ]*$")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(DocumentFilter.FilterBypass fb, int offset, int length,
                            String string, AttributeSet attr)   // method to check when user replaces an existing
                throws BadLocationException {                   // character in the text field with new characters
            if (string.matches("^[a-zA-Z ]*$")) {
                super.replace(fb, offset, length, string, attr);
            }
        }
    }

}
