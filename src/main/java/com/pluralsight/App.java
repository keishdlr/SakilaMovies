package com.pluralsight;

import org.apache.commons.dbcp2.BasicDataSource;

import java.util.Scanner;
import java.sql.*;

public class App {

    public static void main(String[] args) {

        //did we pass in a username and password
        //if not, the application must die
        if(args.length != 2){
            //display a message to the user
            System.out.println("Application needs two args to run: A username and a password for the db");
            //exit the app due to failure because we don't have a username and password from the command line
            System.exit(1);
        }

        //get the username and password from args[]
        String username = args[0];
        String password = args[1];

        //create a scanner to ask the user some questions from our menu
        Scanner myScanner = new Scanner(System.in);


        //get the connection from the datasource
        try (
                //create the basic datasource
                BasicDataSource basicDataSource = new BasicDataSource()
        ){

            //set its configuration
            basicDataSource.setUrl("jdbc:mysql://localhost:3306/northwind");
            basicDataSource.setUsername(username);
            basicDataSource.setPassword(password);


            while(true){

                System.out.println("""
                        What do you want to do?
                            1) Display All Products
                            2) Display All Customers
                            3) Display All Employees
                            4) Display All Suppliers
                            0) Exit the dang app
                        """);

                switch (myScanner.nextInt()){
                    case 1:
                        displayAllProducts(basicDataSource);
                        break;
                    case 2:
                        displayAllCustomers(basicDataSource);
                        break;
                    case 3:
                        displayAllEmployees(basicDataSource);
                        break;
                    case 4:
                        displayAllSuppliers(basicDataSource);
                        break;
                    case 0:
                        System.out.println("Goodbye!");
                        System.exit(0);
                    default:
                        System.out.println("invalid choice");
                }

            }

        }catch(SQLException e){
            System.out.println("Could not connect to DB");
            System.exit(1);
        }

    }

    public static void displayAllProducts(BasicDataSource basicDataSource){

        //we get to try to run a query and get the results with a prepared statement
        try(
                //get a connection from the pool
                Connection connection = basicDataSource.getConnection();

                //create the prepared statement using the passed in connection
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        SELECT
                            ProductName,
                            UnitPrice,
                            UnitsInStock,
                            (UnitPrice * UnitsInStock) AS InventoryValue
                        FROM
                            Products
                        ORDER BY
                            ProductName
                        """
                )




        ){

            try( ResultSet results = preparedStatement.executeQuery()){
                //print the results
                printResults(results);
            }catch(SQLException e){
                System.out.println("stuff hit the fan");
            }



        }catch (SQLException e){
            System.out.println("Could not get all the products");
            System.exit(1);
        }

    }

    public static void displayAllCustomers(BasicDataSource basicDataSource){
        //we get to try to run a query and get the results with a prepared statement
        try(

                //get a connection from the pool
                Connection connection = basicDataSource.getConnection();

                //create the prepared statement using the passed in connection
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        SELECT
                            ContactName,
                            CompanyName,
                            City,
                            Country,
                            Phone
                        FROM
                            Customers
                        ORDER BY
                            Country
                        """
                );

                //get the results from the query
                ResultSet results = preparedStatement.executeQuery()

        ){

            //print the results
            printResults(results);

        }catch (SQLException e){
            System.out.println("Could not get all the customers");
            System.exit(1);
        }
    }

    public static void displayAllEmployees(BasicDataSource basicDataSource){
        //we get to try to run a query and get the results with a prepared statement
        try(

                //get a connection from the pool
                Connection connection = basicDataSource.getConnection();

                //create the prepared statement using the passed in connection
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        SELECT
                            FirstName,
                            LastName,
                            Title,
                            Salary
                        FROM
                            Employees
                        ORDER BY
                            Salary DESC
                        """
                );

                //get the results from the query
                ResultSet results = preparedStatement.executeQuery()

        ){

            //print the results
            printResults(results);

        }catch (SQLException e){
            System.out.println("Could not get all the employees");
            System.exit(1);
        }
    }

    public static void displayAllSuppliers(BasicDataSource basicDataSource){
        //we get to try to run a query and get the results with a prepared statement
        try(
                //get a connection from the pool
                Connection connection = basicDataSource.getConnection();

                //create the prepared statement using the passed in connection
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        SELECT
                           *
                        FROM
                            Suppliers
                        """
                );

                //get the results from the query
                ResultSet results = preparedStatement.executeQuery()

        ){

            //print the results
            printResults(results);

        }catch (SQLException e){
            System.out.println("Could not get all the Suppliers");
            System.exit(1);
        }
    }


    //this method will be used in the displayMethods to actually print the results to the screen
    public static void printResults(ResultSet results) throws SQLException {
        //get the metadata so we have access to the field names
        ResultSetMetaData metaData = results.getMetaData();
        //get the number of rows returned
        int columnCount = metaData.getColumnCount();

        //this is looping over all the results from the DB
        while(results.next()){

            //loop over each column in the row and display the data
            for (int i = 1; i <= columnCount; i++) {
                //gets the current colum name
                String columnName = metaData.getColumnName(i);
                //get the current column value
                String value = results.getString(i);
                //print out the column name and column value
                System.out.println(columnName + ": " + value + " ");
            }

            //print an empty line to make the results prettier
            System.out.println();

        }

    }

}