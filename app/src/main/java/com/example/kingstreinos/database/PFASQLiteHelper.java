

package com.example.kingstreinos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import com.example.kingstreinos.models.Exercise;
import com.example.kingstreinos.models.ExerciseSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



public class PFASQLiteHelper extends SQLiteOpenHelper {
    private interface Patch {
         void apply(SQLiteDatabase db);
         void revert(SQLiteDatabase db);
    }

    private static final Patch[] PATCHES = new Patch[] {
            new Patch() {
                public void apply(SQLiteDatabase db) {
                    String EXERCISE_SET_TABLE = "CREATE TABLE " + TABLE_DATA_ES +
                            "(" +
                            KEY_ID_ES + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                            KEY_NAME_ES + " TEXT," +
                            KEY_EXERCISES_ES + " TEXT);";

                    String EXERCISE_TABLE = "CREATE TABLE " + TABLE_DATA_EX +
                            "(" +
                            KEY_ID_EX + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                            KEY_NAME_EX + " TEXT," +
                            KEY_DESCIRPTION_EX + " TEXT," +
                            KEY_IMAGE_EX + " BLOB);";

                    String WORKOUT_SESSION_TABLE = "CREATE TABLE " + TABLE_DATA +
                            "(" +
                            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                            KEY_WORKOUT_TIME + " LONG," +
                            KEY_CALORIES + " INTEGER," +
                            KEY_TIMESTAMP + " INTEGER);";

                    db.execSQL(EXERCISE_SET_TABLE);
                    db.execSQL(EXERCISE_TABLE);
                    db.execSQL(WORKOUT_SESSION_TABLE);
                }
                public void revert(SQLiteDatabase db) {
                    db.execSQL("DROP TABLE " + TABLE_DATA + ";");
                    db.execSQL("DROP TABLE " + TABLE_DATA_EX + ";");
                    db.execSQL("DROP TABLE " + TABLE_DATA_ES + ";");
                }
            }
            , new Patch() {
                public void apply(SQLiteDatabase db) {
                    db.beginTransaction();

                    String RENAME_EXERCISE_TABLE = "ALTER TABLE " + TABLE_DATA_EX + " RENAME TO "+ TABLE_DATA_EX + "_old;";
                    String EXERCISE_TABLE = "CREATE TABLE " + TABLE_DATA_EX +
                            "(" +
                            KEY_ID_EX + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                            KEY_NAME_EX + " TEXT," +
                            KEY_DESCIRPTION_EX + " TEXT," +
                            KEY_IMAGE_EX + " TEXT);";

                    String COPY_EXERCISES = "INSERT INTO "+ TABLE_DATA_EX +
                            "(" + KEY_ID_EX + "," + KEY_NAME_EX + "," + KEY_DESCIRPTION_EX + ")" +
                            " SELECT " + KEY_ID_EX + "," + KEY_NAME_EX + "," + KEY_DESCIRPTION_EX +
                            " FROM " + TABLE_DATA_EX + "_old;";

                    db.execSQL(RENAME_EXERCISE_TABLE);
                    db.execSQL(EXERCISE_TABLE);
                    db.execSQL(COPY_EXERCISES);

                    db.setTransactionSuccessful();
                    db.endTransaction();
                }
                public void revert(SQLiteDatabase db) {
                    db.beginTransaction();

                    String RENAME_EXERCISE_TABLE = "ALTER TABLE " + TABLE_DATA_EX + " RENAME TO "+ TABLE_DATA_EX + "_old;";
                    String EXERCISE_TABLE = "CREATE TABLE " + TABLE_DATA_EX +
                            "(" +
                            KEY_ID_EX + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                            KEY_NAME_EX + " TEXT," +
                            KEY_DESCIRPTION_EX + " TEXT," +
                            KEY_IMAGE_EX + " BLOB);";

                    String COPY_EXERCISES = "INSERT INTO "+ TABLE_DATA_EX +
                            "(" + KEY_ID_EX + "," + KEY_NAME_EX + "," + KEY_DESCIRPTION_EX + ")" +
                            " SELECT " + KEY_ID_EX + "," + KEY_NAME_EX + "," + KEY_DESCIRPTION_EX +
                            " FROM " + TABLE_DATA_EX + "_old;";

                    db.execSQL(RENAME_EXERCISE_TABLE);
                    db.execSQL(EXERCISE_TABLE);
                    db.execSQL(COPY_EXERCISES);

                    db.setTransactionSuccessful();
                    db.endTransaction();
                }
            }
    };

    private static final int DATABASE_VERSION = PATCHES.length;

    public static final String DATABASE_NAME = "PF_TRAINING_DB";

    private static final String TABLE_DATA = "WORKOUT_SESSION";
    private static final String TABLE_DATA_ES = "EXERCISE_SET";
    private static final String TABLE_DATA_EX = "EXERCISES";

    private static final String KEY_ID = "id";
    private static final String KEY_WORKOUT_TIME = "workoutTime";
    private static final String KEY_CALORIES = "calories";
    private static final String KEY_TIMESTAMP = "time";

    private static final String KEY_ID_ES = "id";
    private static final String KEY_NAME_ES = "name";
    private static final String KEY_EXERCISES_ES = "exercises";

    private static final String KEY_ID_EX = "id";
    private static final String KEY_NAME_EX = "name";
    private static final String KEY_DESCIRPTION_EX = "description";
    private static final String KEY_IMAGE_EX = "image";

    public PFASQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (Patch patch : PATCHES) {
            patch.apply(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = oldVersion; i < newVersion; i++) {
            PATCHES[i].apply(db);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = oldVersion; i > newVersion; i--) {
            PATCHES[i].revert(db);
        }
    }



    public long addExerciseSet(ExerciseSet sampleData) {
        SQLiteDatabase database = this.getWritableDatabase();


        String exerciseList = "";
        try {
            JSONObject json = new JSONObject();
            json.put("uniqueArrays", new JSONArray(sampleData.getExercises()));
            exerciseList = json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ContentValues values = new ContentValues();
        values.put(KEY_NAME_ES, sampleData.getName());
        values.put(KEY_EXERCISES_ES, exerciseList);

        long id = database.insert(TABLE_DATA_ES, null, values);
        database.close();
        return id;
    }


    public long addExercise(Exercise sampleData) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME_EX, sampleData.getName());
        values.put(KEY_DESCIRPTION_EX, sampleData.getDescription());
        values.put(KEY_IMAGE_EX, sampleData.getImage().toString());

        long id = database.insert(TABLE_DATA_EX, null, values);
        database.close();
        return id;
    }





    public void addExerciseSetWithID(ExerciseSet sampleData) {
        SQLiteDatabase database = this.getWritableDatabase();

        String exerciseList = "";
        try {
            JSONObject json = new JSONObject();
            json.put("uniqueArrays", new JSONArray(sampleData.getExercises()));
            exerciseList = json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ContentValues values = new ContentValues();
        values.put(KEY_ID_ES, sampleData.getID());
        values.put(KEY_NAME_ES, sampleData.getName());
        values.put(KEY_EXERCISES_ES, exerciseList);

        database.insert(TABLE_DATA_ES, null, values);

        database.close();
    }


    public void addExerciseWithID(Exercise sampleData) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID_EX, sampleData.getID());
        values.put(KEY_NAME_EX, sampleData.getName());
        values.put(KEY_DESCIRPTION_EX, sampleData.getDescription());
        values.put(KEY_IMAGE_EX, sampleData.getImage().toString());

        database.insert(TABLE_DATA_EX, null, values);

        database.close();
    }



    public ExerciseSet getExerciseSet(int id) {
        SQLiteDatabase database = this.getWritableDatabase();

        Log.d("DATABASE", Integer.toString(id));

        Cursor cursor = database.query(TABLE_DATA_ES, new String[]{KEY_ID_ES,
                        KEY_NAME_ES, KEY_EXERCISES_ES}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        ExerciseSet data = new ExerciseSet();

        if( cursor != null && cursor.moveToFirst() ){
            data.setID(Integer.parseInt(cursor.getString(0)));
            data.setName(cursor.getString(1));

            ArrayList<Integer> exerciseList = new ArrayList<Integer>();
            try {
                JSONObject json = new JSONObject(cursor.getString(2));
                JSONArray jArray = json.optJSONArray("uniqueArrays");
                for (int i = 0; i < jArray.length(); i++) {
                    exerciseList.add(Integer.parseInt(jArray.getString(i)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            data.setExercises(exerciseList);

            Log.d("DATABASE", "Read " + cursor.getString(1) + " from  ES DB");

            cursor.close();
        }
        return data;
    }

    public Exercise getExercise(int id) {
        SQLiteDatabase database = this.getWritableDatabase();

        Log.d("DATABASE", Integer.toString(id));

        Cursor cursor = database.query(TABLE_DATA_EX, new String[]{KEY_ID_EX,
                        KEY_NAME_EX, KEY_DESCIRPTION_EX, KEY_IMAGE_EX}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        Exercise data = new Exercise(0, null, null, null);

        if( cursor != null && cursor.moveToFirst() ){
            data.setID(Integer.parseInt(cursor.getString(0)));
            data.setName(cursor.getString(1));
            data.setDescription(cursor.getString(2));


            String uriString = cursor.getString(3);
            if(uriString == null) {
                uriString = "";
            }

            data.setImage(Uri.parse(uriString));

            Log.d("DATABASE", "Read " + cursor.getString(1) + " from  EX DB");

            cursor.close();
        }

        return data;

    }



        String selectQuery = "SELECT  * FROM " + TABLE_DATA;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);





    public List<ExerciseSet> getAllExerciseSet(List<ExerciseSet> sampleDaaList) {
            List<ExerciseSet> sampleDataList = new ArrayList<ExerciseSet>();

        String selectQuery = "SELECT  * FROM " + TABLE_DATA_ES;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        Object sampleData = null;

        if (cursor.moveToFirst()) {
            do {
                sampleData = new ExerciseSet();
                ((ExerciseSet) sampleData).setID(Integer.parseInt(cursor.getString(0)));
                ((ExerciseSet) sampleData).setName(cursor.getString(1));

                ArrayList<Integer> exerciseList = new ArrayList<Integer>();
                try {
                    JSONObject json = new JSONObject(cursor.getString(2));
                    JSONArray jArray = json.optJSONArray("uniqueArrays");
                    for (int i = 0; i < jArray.length(); i++) {
                        exerciseList.add(Integer.parseInt(jArray.getString(i)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ((ExerciseSet) sampleData).setExercises(exerciseList);

                sampleDataList.add((ExerciseSet) sampleData);
                Log.d("DATABASE", "Read " + cursor.getString(1) + " from  ES DB");

            } while (cursor.moveToNext());
        }

        return sampleDaaList;
    }


    public List<Object> getAllExercise() {
        return getAllExercise(getWritableDatabase());
    }

    private static List<Object> getAllExercise(SQLiteDatabase database) {
        List<Object> sampleDataList = new ArrayList<Object>();

        String selectQuery = "SELECT  * FROM " + TABLE_DATA_EX;

        Cursor cursor = database.rawQuery(selectQuery, null);

        Object sampleData = null;

        if (cursor.moveToFirst()) {
            do {

                sampleData = new Exercise(0, null, null, null);
                ((Exercise) sampleData).setID(Integer.parseInt(cursor.getString(0)));
                ((Exercise) sampleData).setName(cursor.getString(1));
                ((Exercise) sampleData).setDescription(cursor.getString(2));

                String uriString = cursor.getString(3);
                if(uriString == null) {
                    uriString = "";
                }

                ((Exercise) sampleData).setImage(Uri.parse(uriString));

                sampleDataList.add(sampleData);
                Log.d("DATABASE", "Read " + cursor.getString(1) + " from  ES DB");

            } while (cursor.moveToNext());
        }

        return sampleDataList;
    }


    public int updateExerciseSet(ExerciseSet exerciseSet) throws JSONException {
        SQLiteDatabase database = this.getWritableDatabase();

        JSONObject json = new JSONObject();
        json.put("uniqueArrays", new JSONArray(exerciseSet.getExercises()));
        String exerciseList = json.toString();

        //To adjust this class for your own data, please add your values here.
        ContentValues values = new ContentValues();
        values.put(KEY_NAME_ES, exerciseSet.getName());
        values.put(KEY_EXERCISES_ES, exerciseList);

        return database.update(TABLE_DATA_ES, values, KEY_ID_ES + " = ?",
                new String[] { String.valueOf(exerciseSet.getID()) });
    }


    public int updateExercise(Exercise exercise){
        SQLiteDatabase database = this.getWritableDatabase();

        //To adjust this class for your own data, please add your values here.
        ContentValues values = new ContentValues();
        values.put(KEY_NAME_EX, exercise.getName());
        values.put(KEY_DESCIRPTION_EX, exercise.getDescription());
        values.put(KEY_IMAGE_EX, exercise.getImage().toString());

        return database.update(TABLE_DATA_EX, values, KEY_ID_EX + " = ?",
                new String[] { String.valueOf(exercise.getID()) });
    }


    public void deleteExerciseSet(ExerciseSet sampleData) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_DATA_ES, KEY_ID_ES + " = ?",
                new String[] { Integer.toString(sampleData.getID()) });
        //always close the DB after deletion of single entries
        database.close();
    }


    public void deleteExercise(Exercise sampleData) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_DATA_EX, KEY_ID_EX + " = ?",
                new String[] { Integer.toString(sampleData.getID()) });
        //always close the DB after deletion of single entries
        database.close();
    }




    public void deleteAllExerciseSet() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("delete from "+ TABLE_DATA_ES);
    }


    public void deleteAllExercise() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("delete from "+ TABLE_DATA_EX);
    }

}
