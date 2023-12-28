
package com.example.kingstreinos.models;

import java.util.ArrayList;


public class ExerciseSet {
    private String NAME;
    private ArrayList<Integer> EXERCISE_IDS;
    private int ID;

    public ExerciseSet() {
    }

    public ExerciseSet(int id, String name, ArrayList<Integer> exercise_ids) {

        this.ID = id;
        this.NAME = name;
        this.EXERCISE_IDS = exercise_ids;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return NAME;
    }

    public void setName(String name) { this.NAME = name; }

    public ArrayList<Integer> getExercises() {
        return EXERCISE_IDS;
    }

    public void setExercises(ArrayList<Integer> exercise_ids) { this.EXERCISE_IDS = exercise_ids; }

    public int getNumber() { return EXERCISE_IDS.size(); }

}
