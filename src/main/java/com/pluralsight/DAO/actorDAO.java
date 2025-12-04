package com.pluralsight.DAO;

import com.pluralsight.models.actor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Array;

public class actorDAO {
    //DAO to connect to the DB and get connections from the pool
    private DataSource ds;

    //constructor with ds passed it
    public actorDAO(DataSource ds){

    }
}
