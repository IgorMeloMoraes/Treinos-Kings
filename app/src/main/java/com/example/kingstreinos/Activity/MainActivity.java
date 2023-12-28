package com.example.kingstreinos.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.example.kingstreinos.R;
import com.example.kingstreinos.database.PFASQLiteHelper;
import com.example.kingstreinos.models.ExerciseSet;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {


    private boolean isBlockPeriodization = false;

    private Switch blockPeriodizationSwitchButton;

    // Valores do timer
    private static final long startTime = 10; // Starttimer 10 sec
    private long workoutTime = 0;
    private long restTime = 0;
    private int sets = 0;
    private int setsPerRound = 0;
    private boolean blockPeriodizationSwitchState = false;
    private boolean workoutModeSwitchState = false;

    // GUI
    private TextView workoutIntervalText = null;
    private TextView restIntervalText = null;
    private TextView setsText = null;


    private Spinner exerciseSetSpinner;
    private Switch workoutMode;
    private final PFASQLiteHelper db = new PFASQLiteHelper(this);
    ArrayList<Integer> exerciseIds = null;
    ArrayList<Integer> ExerciseIdsForRounds = null;

    private boolean isExerciseMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Defini textos da GUI
        this.workoutIntervalText = this.findViewById(R.id.main_workout_interval_time);
        this.restIntervalText = this.findViewById(R.id.main_rest_interval_time);
        this.setsText = this.findViewById(R.id.main_sets_amount);
        this.workoutMode = findViewById(R.id.workout_mode_switch);
        this.exerciseSetSpinner = findViewById(R.id.exerciseSets);
        this.blockPeriodizationSwitchButton = findViewById(R.id.main_block_periodization_switch);

        this.workoutIntervalText.setText(formatTime(workoutTime));
        this.restIntervalText.setText(formatTime(restTime));
        this.setsText.setText(Integer.toString(sets));
        this.workoutMode.setChecked(workoutModeSwitchState);
        this.blockPeriodizationSwitchButton.setChecked(blockPeriodizationSwitchState);

        if (workoutModeSwitchState) {
            findViewById(R.id.exerciesetsRow).setVisibility(View.VISIBLE);
            isExerciseMode = true;
        }




        //Defini parametros para o botão de switch
        blockPeriodizationSwitchButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isBlockPeriodization = isChecked;
            blockPeriodizationSwitchState = isChecked;
        });

        final List<ExerciseSet> exerciseSetslist = db.getAllExerciseSet(sampleDaaList);

        exerciseIds = new ArrayList<>();
        final List<String> exerciseSetsNames = new ArrayList<>();
        for (Object ex : exerciseSetslist) {
            exerciseSetsNames.add(ex.getClass().getName());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, exerciseSetsNames);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseSetSpinner.setAdapter(dataAdapter);
        exerciseSetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                setsPerRound = Integer.parseInt(exerciseSetslist.get(pos).getClass().getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }


    protected int getNavigationDrawerID() {
        return R.id.nav_main;
    }

    /**
     * Clique em funções para valores de temporizador, bloco AlertDialog de periodização e botão de início de treino
     */
    public void onClick(View view) {
        int id = view.getId();

            if (isExerciseMode) {
                ExerciseIdsForRounds = getExercisesForRounds(exerciseIds, sets);
                setsPerRound = sets * exerciseIds.size();
            } else {
                ExerciseIdsForRounds = exerciseIds;
                setsPerRound = sets;
            }

            if (setsPerRound == 0) {
                if (isExerciseMode && ExerciseIdsForRounds.size() == 0) {
                    Toast.makeText(this, R.string.exercise_set_has_no_exercises, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.exercise_set_has_no_sets, Toast.LENGTH_SHORT).show();
                }
            }

        }


    private String formatTime(long seconds) {
        long min = seconds / 60;
        long sec = seconds % 60;

        return String.format("%02d : %02d", min, sec);
    }

    private ArrayList<Integer> getExercisesForRounds(ArrayList<Integer> exerciseIds, int rounds) {
        ArrayList<Integer> temp = new ArrayList<>();
        for (int i = 0; i < rounds; i++)
            temp.addAll(exerciseIds);
        return temp;
    }
}