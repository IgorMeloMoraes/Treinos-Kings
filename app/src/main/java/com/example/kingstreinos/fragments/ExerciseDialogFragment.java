

package com.example.kingstreinos.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.kingstreinos.Activity.ExerciseActivity;
import com.example.kingstreinos.R;
import com.example.kingstreinos.database.PFASQLiteHelper;
import com.example.kingstreinos.models.Exercise;


public class ExerciseDialogFragment extends DialogFragment {

    private static final int SELECT_IMAGE_REQUEST = 1;

    private boolean editDialog = false;
    private static boolean opened;
    private View v;
    private ExerciseActivity ea;
    private int adapterPosition;
    private static Toast toast;
    private PFASQLiteHelper db = null;

    private int exerciseId = -1;
    private Exercise loadedExercise = null;

    private ImageView fragment_img;

    public static ExerciseDialogFragment newEditInstance(int adapterPosition, int exerciseId)
    {
        ExerciseDialogFragment dialogFragment = new ExerciseDialogFragment();
        dialogFragment.editDialog = true;
        dialogFragment.adapterPosition = adapterPosition;
        dialogFragment.exerciseId = exerciseId;
        return dialogFragment;
    }

    public static ExerciseDialogFragment newAddInstance()
    {
        return new ExerciseDialogFragment();
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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        ea = (ExerciseActivity) getActivity();
        db = new PFASQLiteHelper(ea);

        if(exerciseId != -1) {
            loadedExercise = db.getExercise(exerciseId);
        }

        if(loadedExercise == null) {
            loadedExercise = new Exercise(0, "", "", Uri.parse(""));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog);

        builder.setView(R.layout.exercise_dialog);
        builder.setTitle(editDialog ? R.string.edit : R.string.new_exercise);
        builder.setPositiveButton("okay", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                EditText editName = ((AlertDialog)dialogInterface).findViewById(R.id.exercise_name);
                EditText editDescription = ((AlertDialog)dialogInterface).findViewById(R.id.exercise_description);
                String name = editName.getText().toString();
                String description = editDescription.getText().toString();

                loadedExercise.setName(name);
                loadedExercise.setDescription(description);

                if(name.length() == 0 ) {
                    toast = Toast.makeText(ea, getResources().getString(R.string.valid_name), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    if (editDialog) {
                        db.updateExercise(loadedExercise);
                        ea.updateExercise(adapterPosition, loadedExercise);
                    } else {
                        long id = db.addExercise(loadedExercise);
                        loadedExercise.setID((int) id);
                        ea.addExercise(loadedExercise);
                    }
                    ea.setNoExererciseMessage();
                    dismiss();
                }
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), null);

        final AlertDialog dialog = builder.create();
        dialog.show();

        EditText name = dialog.findViewById(R.id.exercise_name);
        EditText description = dialog.findViewById(R.id.exercise_description);
        fragment_img = dialog.findViewById(R.id.exercise_fragment_img);
        fragment_img.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                intent.setType("image/*");

                intent.setFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                        | Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                startActivityForResult(Intent.createChooser(intent, ""), SELECT_IMAGE_REQUEST);

            }
        });

        if(editDialog){
            name.setText(loadedExercise.getName());
            description.setText(loadedExercise.getDescription());
        }

        return dialog;
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {

        if (reqCode == SELECT_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            Activity a = getActivity();

            if(imageUri == null || a == null) {
                Toast.makeText(ea, getResources().getString(R.string.load_image_error), Toast.LENGTH_LONG).show();
                return;
            }

            a.getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

            loadedExercise.setImage(imageUri);

        } else {
            Toast.makeText(ea, getResources().getString(R.string.no_image_picked),Toast.LENGTH_LONG).show();
        }
    }
}
