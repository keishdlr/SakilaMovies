package com.pluralsight;

import com.pluralsight.DAO.ActorDAO;
import com.pluralsight.models.Actor;
import org.apache.commons.dbcp2.BasicDataSource;

import java.util.ArrayList;
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

            ActorDAO actorDAO = new ActorDAO(basicDataSource);

            while(true){

                System.out.println("""
                        What do you want to do?
                            1) Search by actor last name
                            2) Search by actor full name
                            3) Display All actors
                            4) Search Actor by ID
                            0) Exit the app
                        """);

                switch (myScanner.nextInt()){
                    case 1:
                        searchLastName(basicDataSource);
                        break;
                    case 2:
                        searchFullName(basicDataSource);
                        break;
                    case 3:
                        DisplayAllActors(actorDAO);
                        break;
                    case 4:
                        GetByID(actorDAO);
                        break;
                    case 0:
                        System.out.println("See ya!");
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

    //use actor DAO
    public static void DisplayAllActors(ActorDAO actorDAO){
        ArrayList<Actor> actors = actorDAO.getAllActors();
        actors.forEach(System.out::println);
    }

    public static void GetByID(ActorDAO actorDAO){
        ArrayList<Actor> actorsID = actorDAO.getActorbyID(int actor_id);
        actorsID.forEach(System.out::println);
    }

    public static void searchLastName(BasicDataSource basicDataSource){
        //added scanner for user input
        Scanner myScanner = new Scanner(System.in);
        //ask user
        System.out.println("Enter the actor last name: ");
        //user input saved as
        String lastName = myScanner.nextLine();

        //we get to try to run a query and get the results with a prepared statement
        try(
                //get a connection from the pool
                Connection conn = basicDataSource.getConnection();

                //create the prepared statement using the passed in connection
                PreparedStatement preparedStatement = conn.prepareStatement("""
                        SELECT
                            Actor_ID,
                            First_Name,
                            Last_Name,
                            Last_Update
                        FROM
                            Actor
                        WHERE
                            Last_Name = ?
                        ORDER BY
                            Actor_ID
                        """
                )

        ){
            preparedStatement.setString(1, lastName);

            try( ResultSet results = preparedStatement.executeQuery()){
                if (results.next()) {
                    System.out.println("Your matches are:\n");
                        printResults(results);
                } else {
                    System.out.println("No matches found!");
                }
            }
        } catch (SQLException e) {
           System.out.println("ERROR: " + e);
           System.exit(1);
        }
    }

    public static void searchFullName(BasicDataSource basicDataSource) {
        Scanner myScanner = new Scanner(System.in);
        //ask user for first and last name save input
        System.out.print("Enter the actor's first name: ");
        String firstName = myScanner.nextLine();
        System.out.print("Enter the actor's last name: ");
        String lastName = myScanner.nextLine();

        //we get to try to run a query and get the results with a prepared statement
        try (

                //get a connection from the pool
                Connection conn = basicDataSource.getConnection();

                //create the prepared statement using the passed in connection
                PreparedStatement preparedStatement = conn.prepareStatement("""
                        SELECT
                            title
                        FROM
                            film
                        JOIN film_actor ON film.film_id = film_actor.film_id
                        JOIN actor ON film_actor.actor_id = actor.actor_id
                        WHERE
                            actor.First_Name = ? AND actor.Last_Name = ?
                        ORDER BY
                            film.title
                        """
                )
        ) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);

            //get the results from the query
            try (ResultSet results = preparedStatement.executeQuery()) {
                if (results.next()) {
                    printResults(results);
                } else {
                    System.out.println("Nothing found!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Could not get actors");
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
        // do while loop to avoid the "After end result set" exception
        {
            // print the current row first
            do {
                //loop over each column in the row and display the data
                for (int i = 1; i <= columnCount; i++) {
                    //gets the current colum name
                    String columnName = metaData.getColumnName(i);
                    //get the current column value
                    String value = results.getString(i);
                    //print out the column name and column value
                    System.out.println(columnName + ": " + value + " ");
                    System.out.println("---------------------------------------------------------");
                }
                //print an empty line to make the results prettier
                System.out.println();
                //then move to the next row
            } while(results.next());
        }
    }
}