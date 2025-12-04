package com.pluralsight.DAO;

import com.pluralsight.models.Actor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ActorDAO {
    //DAO to connect to the DB and get connections from the pool
    private final DataSource ds;

    //constructor with ds passed it
    public ActorDAO(DataSource ds) {
        this.ds = ds;
    }

    //this method gets all products from the db
    public ArrayList<Actor> getAllActors() {

        //create an empty array list
        // "ArrayList" made of "actors" called "actorArrayList
            ArrayList<Actor> actorList = new ArrayList<>();

        //run query to get the results using a prepared statement
        try (
                //get a connection from the pool
                Connection connection = this.ds.getConnection();

                //prepared statement with the passed in connection we created above
                PreparedStatement query = connection.prepareStatement("""
                        SELECT
                            actor_id,
                            first_name,
                            last_name
                        FROM
                            actor
                        ORDER BY
                            actor_id
                        """
                )
        ) {
            //get the result set go through it and create java objects to add to the list
            try (ResultSet results = query.executeQuery()) {
                while (results.next()) {
                    //create the object from the results
                    Actor newActor = new Actor(
                            results.getString("first_name"),
                            results.getString("last_name"),
                            results.getInt("actor_id")
                    );
                    // add the created object above to the list
                    actorList.add(newActor);
                }
            } catch (SQLException e) {
                System.out.println("ERROR: " + e);
            }

        } catch (SQLException e) {
            System.out.println("Could not get all actors");
            System.exit(1);
        }
        //return the list
        return actorList;
    }

    //this method
    public Actor getActorbyID(int actor_id) {
        Actor actor = null;

        //run Query and get results with prepared statement
        try (
                //get a connection again
                Connection connection = this.ds.getConnection();

                //create another prepared statement
                PreparedStatement query = connection.prepareStatement("""
                        SELECT
                            actor_id,
                            first_name,
                            last_name
                        FROM
                            actor
                        WHERE
                            actor_id = ?
                        ORDER BY
                            actor_id
                        """
                )
        ){
            query.setInt(1, actor_id);

            //get the results
            try (ResultSet results = query.executeQuery()) {
                while(results.next()) {
                    //create the new object from the results
                    actor = new Actor(
                            results.getString("first_name"),
                            results.getString("last_name"),
                            results.getInt("actor_id")
                    );
                }
            } catch (SQLException e) {
                System.out.println("ERROR:" + e);
            }
        }catch(SQLException e){
            System.out.println("Could not get the actors");
        }
        //return the actor
        return actor;
    }

}