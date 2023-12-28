
package com.example.kingstreinos.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kingstreinos.Adapter.ExerciseSetAdapter;
import com.example.kingstreinos.R;
import com.example.kingstreinos.database.PFASQLiteHelper;
import com.example.kingstreinos.fragments.ExerciseSetDialogFragment;
import com.example.kingstreinos.models.ExerciseSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;



public class ExerciseSetActivity extends BaseActivity implements View.OnLongClickListener{

    private List<Object> exerciseSetsList = new ArrayList<Object>();
    private RecyclerView recyclerView;
    private ExerciseSetAdapter mAdapter;
    private FloatingActionButton newListFab;
    private FloatingActionButton deleteFab;
    private LinearLayout noListsLayout;
    private boolean is_in_action_mode = false;
    private ArrayList<Object> selection_list = new ArrayList<Object>();
    private PFASQLiteHelper db = new PFASQLiteHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_exercisesets);

        exerciseSetsList = db.getAllExerciseSet(sampleDaaList);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        mAdapter = new ExerciseSetAdapter(exerciseSetsList, ExerciseSetActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        newListFab = (FloatingActionButton) findViewById(R.id.fab_new_list);
        deleteFab = (FloatingActionButton) findViewById(R.id.fab_delete_item);
        noListsLayout = (LinearLayout) findViewById(R.id.no_lists_layout);

        ((View)deleteFab).setVisibility(View.GONE);
        noListsLayout.setVisibility(View.VISIBLE);
        setNoExererciseSetsMessage();

        newListFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ( !ExerciseSetDialogFragment.isOpened() )
                {
                    ExerciseSetDialogFragment listDialogFragment = ExerciseSetDialogFragment.newAddInstance();
                    listDialogFragment.show(getSupportFragmentManager(), "DialogFragment");
                }
                }
        });

        deleteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ExerciseSetActivity.this, 0);
                builder.setMessage(R.string.dialog_exercise_set_confirm_delete_message)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for(Object es : selection_list){
                                    db.deleteExerciseSet(es);
                                }
                                mAdapter.updateAdapter(selection_list);
                                clearActionMode();
                                setNoExererciseSetsMessage();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null).create().show();
            }
        });
    }


    public void addExerciseSet(String name, ArrayList<Integer> exercises){
        int lastId = (int) db.addExerciseSet(new ExerciseSet(0, name, exercises));
        exerciseSetsList.add(new ExerciseSet(lastId, name, exercises));
        ArrayList<Object> empty = new ArrayList<Object>();
        mAdapter.updateAdapter(empty);
        recyclerView.getLayoutManager().scrollToPosition(exerciseSetsList.size()-1);
    }

    public void updateExerciseSet(int position, int id, String name, ArrayList<Integer> exercises) throws JSONException {
        ExerciseSet temp = new ExerciseSet(id, name, exercises);
        db.updateExerciseSet(temp);
        ArrayList<Object> empty = new ArrayList<Object>();
        mAdapter.updateAdapter(empty);
        mAdapter.notifyItemChanged(position);
    }


    public void setNoExererciseSetsMessage(){
        if(mAdapter.getItemCount() == 0){
            findViewById(R.id.no_lists_layout).setVisibility(View.VISIBLE);
        }
        else{
            findViewById(R.id.no_lists_layout).setVisibility(View.GONE);
        }
    }


    public void clearActionMode(){
        is_in_action_mode = false;
        selection_list.clear();
        ((View)newListFab).setVisibility(View.VISIBLE);
        ((View)deleteFab).setVisibility(View.GONE);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        this.getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void onBackPressed(){
        if(is_in_action_mode){
            clearActionMode();
            mAdapter.notifyDataSetChanged();
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        is_in_action_mode = true;
        mAdapter.notifyDataSetChanged();
        ((View)newListFab).setVisibility(View.GONE);
        ((View)deleteFab).setVisibility(View.VISIBLE);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));
        this.getWindow().setStatusBarColor(Color.LTGRAY);
        return true;
    }

    public void prepareSelection(View view, int position){
        if(view instanceof CheckBox) {
            if (((CheckBox) view).isChecked()) {
                selection_list.add(exerciseSetsList.get(position));
            } else {
                selection_list.remove(exerciseSetsList.get(position));
            }
        }
    }

    protected int getNavigationDrawerID() {
        return R.id.nav_exercisesets;
    }

    public boolean getIsInActionMode() {return is_in_action_mode;}

}
