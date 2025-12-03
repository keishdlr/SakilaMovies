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
            basicDataSource.setUrl("jdbc:mysql://localhost:3306/Sakila");
            basicDataSource.setUsername(username);
            basicDataSource.setPassword(password);


            while(true){

                System.out.println("""
                        What do you want to do?
                            1) Display Actors
                            2) Display Films
                            0) Exit the dang app
                        """);

                switch (myScanner.nextInt()){
                    case 1:
                        displayActors(basicDataSource);
                        break;
                    case 2:
                        displayFilms(basicDataSource);
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

    public static void displayActors(BasicDataSource basicDataSource){
        //added scanner for user input
        Scanner myScanner = new Scanner(System.in);
        //ask user
        System.out.println("Enter an actor last name: ");
        //user input saved as
        String lastName = myScanner.nextLine();

        //we get to try to run a query and get the results with a prepared statement
        try(
                //get a connection from the pool
                Connection connection = basicDataSource.getConnection();

                //create the prepared statement using the passed in connection
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        SELECT
                            ActorID,
                            FirstName,
                            LastName,
                            LastUpdate
                        FROM
                            Actor
                        WHERE
                            LastName = ?
                        ORDER BY
                            ActorID
                        """
                )

        ){

            preparedStatement.setString(1, lastName);

            try( ResultSet results = preparedStatement.executeQuery()){
                if (results.next()) {
                    System.out.println("Your matches are:\n");
                    do {
                        int id = results.getInt("ActorID");
                        String first = results.getString("FirstName");
                        String last = results.getString("LastName");
                        Timestamp updated = results.getTimestamp("LastUpdate");

                        System.out.printf("%d: %s %s (Last update: %s)%n",
                                id, first, last, updated);
                    } while (results.next());
                } else {
                    System.out.println("No matches!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Could not get actors");
            System.exit(1);
        }
    }

    public static void displayFilms(BasicDataSource basicDataSource) {
        Scanner myScanner = new Scanner(System.in);
        //ask user for first and last name save input
        System.out.print("Enter the actor's first name: ");
        String firstName = myScanner.nextLine();
        System.out.print("Enter the actor's last name: ");
        String lastName = myScanner.nextLine();

        //we get to try to run a query and get the results with a prepared statement
        try (

                //get a connection from the pool
                Connection connection = basicDataSource.getConnection();

                //create the prepared statement using the passed in connection
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        SELECT
                            title
                        FROM
                            film
                        JOIN film_actor ON film.film_id = film-actor.film_id
                        JOIN actor ON film-actor.actor_id = actor.actor_id
                        WHERE
                            actor.FirstName = ? AND actor.LastName = ?
                        ORDER BY
                            film.title
                        """
                );


        ) {
            //get the results from the query
            try (ResultSet results = preparedStatement.executeQuery()) {
                if (results.next()) {
                    System.out.println("Your matches are:\n");
                    do {
                        System.out.printf("%d: %s %s%n",
                                results.getInt("actor_id"),
                                results.getString("first_name"),
                                results.getString("last_name"));
                    } while (results.next());
                } else {
                    System.out.println("No matches!");
                }
            } catch (SQLException e) {
                System.out.println("Could not get all the customers");
                System.exit(1);
            }
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