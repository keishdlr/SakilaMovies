package com.pluralsight.models;

public class Actor {

    //properties that could be part of an actor
    String first_name;
    String last_name;
    int actor_id;

    //constructor to create an instance of the actor
    public Actor(String first_name, String last_name, int actor_id) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.actor_id = actor_id;
    }

    //to string to print out the details of each actor
    @Override
    public  String toString(){
        return  "Actor {" +
                "First_name =" + first_name + '\'' +
                ", Last_name = " + last_name +
                ", ActorID = " + actor_id +
                '}';
    }
}
