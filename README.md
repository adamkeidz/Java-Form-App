# Java Form App

This repository contains a Java application that uses the Swing library to create a GUI form for storing client data. The application allows users to input client details such as Name, IC Number, Gender, Postcode, Town, and specific dates (Commence Date, Expiry Date, Date of Birth).

## Features

- Form fields with restrictions: Certain fields in the form have specific character limits. For example, the Name field has a limit of 30 characters, IC Number field has a limit of 12 characters, and Postcode field has a limit of 5 characters.

- Input validation: The form includes input validation to ensure that the date format entered matches "dd/MM/yyyy". If the input doesn't match this format, an error message is displayed.

- Data extraction from IC: The application extracts the Date of Birth and Gender from the IC Number. If the IC Number doesn't follow the expected format or length, the respective fields are set to null or blank.

- Town auto-population from Postcode: The application reads a CSV file of postcodes and associated towns, storing this data in a HashMap. When a postcode is entered, the corresponding town is auto-populated in the Town field.

- Database connection: The form data is saved into a MySQL database called Insudb, specifically into a table called clients. The application establishes a database connection using JDBC.

## Dependencies

This application requires the following libraries to function correctly:

- Swing library: Used for creating the GUI form.
- JDBC: Required for connecting to the MySQL database.
- OpenCSV: Used for reading CSV files.
- Toedter's JCalendar: Provides a date selection UI.

Please ensure that these dependencies are properly configured and included in your project.

## Configuration

To run the application successfully, you may need to configure the following:

- MySQL database: Ensure you have a MySQL database named Insudb. Update the database connection details (username, password, etc.) in the application code as needed.

- CSV file: Prepare a CSV file with postcodes and associated towns, which will be used for town auto-population. Update the file path or name in the application code if necessary.

## Usage

Describe how to compile and run the application, including any specific steps or commands that need to be followed.

Example:

1. Clone the repository:

   ```shell
   $ git clone https://github.com/adamkeidz/java-stuffs/java-app.git
   
2. Open the project in your preferred Java IDE.

3. Build and compile the project.

4. Run the application.
