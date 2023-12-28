
package com.example.kingstreinos.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.kingstreinos.Activity.ExerciseActivity;
import com.example.kingstreinos.Activity.ExerciseSetActivity;
import com.example.kingstreinos.R;
import com.example.kingstreinos.database.PFASQLiteHelper;
import com.example.kingstreinos.models.Exercise;
import com.example.kingstreinos.models.ExerciseSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ExerciseSetDialogFragment extends DialogFragment {

    private static boolean editDialog;
    private static boolean opened;
    private View v;
    private ExerciseSetActivity ea;
    private static int adapterPosition;
    private static int adapterId;

    private ArrayList<Exercise> exercises;
    private Toast toast;



    public static ExerciseSetDialogFragment newAddInstance()
    {
        editDialog = false;
        ExerciseSetDialogFragment dialogFragment = getListDialogFragment();
        return dialogFragment;
    }

    private static ExerciseSetDialogFragment getListDialogFragment()
    {
        ExerciseSetDialogFragment dialogFragment = new ExerciseSetDialogFragment();
        return dialogFragment;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        opened = true;
    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        super.onDismiss(dialog);
        opened = false;
    }

    public static boolean isOpened()
    {
        return opened;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        ea = (ExerciseSetActivity) getActivity();
        PFASQLiteHelper db = new PFASQLiteHelper(ea);
        exercises = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.exerciseset_dialog, null);



        builder.setView(v);
        builder.setTitle(getResources().getString(R.string.new_exercise_set));

        FloatingActionButton addExerciseFab = (FloatingActionButton) v.findViewById(R.id.fab_add_exercise);

        addExerciseFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent exercisePickerIntent = new Intent(getActivity(), ExerciseActivity.class);
                exercisePickerIntent.putExtra("pickerMode", true);
                startActivityForResult(exercisePickerIntent, 1);
            }
        });

        List<ExerciseSet> exerciseSetsList = new ArrayList<>();
        if(exerciseSetsList.size() > 0)
            adapterId = exerciseSetsList.get(adapterPosition).getID();
        else{
            adapterId = 0;
        }

        if(editDialog){

            builder.setTitle(getResources().getString(R.string.edit));

            EditText etext = (EditText) v.findViewById(R.id.list_name);

            etext.setText(db.getExerciseSet(adapterId).getName());
            for(Integer ex: db.getExerciseSet(adapterId).getExercises()){
                exercises.add(db.getExercise(ex));
            }
        }

        setNoExererciseMessage();

        builder.setPositiveButton("okay", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {

            }
        });

        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                EditText text = (EditText) v.findViewById(R.id.list_name);
                String name = text.getText().toString();

                if( name.length() == 0 ) {
                    toast = Toast.makeText(ea, getResources().getString(R.string.valid_name), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else if(exercises.size() == 0){
                    toast = Toast.makeText(ea, getResources().getString(R.string.choose_one_exercise), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else {
                    if (editDialog) {
                        try {
                            ea.updateExerciseSet(adapterPosition, adapterId, name, exercisesToIds(exercises));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ea.addExerciseSet(name, exercisesToIds(exercises));
                    }
                    ea.setNoExererciseSetsMessage();
                    dialog.dismiss();
                }
            }
        });

        return dialog;
    }

    public void setNoExererciseMessage(){

    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            ArrayList<Exercise> tmp =  (ArrayList<Exercise>) data.getSerializableExtra("result");
            exercises.addAll(tmp);

            setNoExererciseMessage();
        }

    }

    private ArrayList<Integer> exercisesToIds (ArrayList<Exercise> exercises){
        ArrayList<Integer> ids = new ArrayList<>();
        for(Exercise ex : exercises){
            ids.add(ex.getID());
        }
        return ids;
    }
}
