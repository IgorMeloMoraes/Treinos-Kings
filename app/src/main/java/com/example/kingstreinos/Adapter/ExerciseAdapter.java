
package com.example.kingstreinos.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kingstreinos.Activity.ExerciseActivity;
import com.example.kingstreinos.R;
import com.example.kingstreinos.database.PFASQLiteHelper;
import com.example.kingstreinos.fragments.ExerciseDialogFragment;


import java.util.ArrayList;
import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.MyViewHolder> {

    private List<Object> exerciseList;
    private ExerciseActivity exerciseActivity;
    private PFASQLiteHelper db = null;
    private Context context;

    public ExerciseAdapter(List<Object> exerciseList, Context ctx) {
        this.exerciseList = exerciseList;
        exerciseActivity = (ExerciseActivity) ctx;
        db = new PFASQLiteHelper(ctx);
        context = ctx;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_exercise, parent, false);

        return new MyViewHolder(itemView, exerciseActivity);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Object ex = exerciseList.get(position);
        holder.name.setText(ex.getClass().getName());
        holder.description.setText(ex.getClass().getName());


        if(!exerciseActivity.getIsInActionMode()){
            holder.checkbox.setVisibility(View.GONE);
        }
        else{
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.checkbox.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }


    public void updateAdapter(ArrayList<Object> list){
        exerciseList = db.getAllExercise();
        for(Object ex : list){
            exerciseList.remove(ex);

        }
        notifyDataSetChanged();
    }




    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView name, description;
        private ImageView exerciseImg;
        private CheckBox checkbox;
        private ExerciseActivity exerciseActivity;
        private CardView cardView;
        public MyViewHolder(View view, ExerciseActivity exerciseActivity) {
            super(view);
            name = (TextView) view.findViewById(R.id.exercise_name);
            description = (TextView) view.findViewById(R.id.exercise_description);
            exerciseImg = (ImageView) view.findViewById(R.id.exercise_img);

            checkbox = (CheckBox) view.findViewById(R.id.check_exercise);
            checkbox.setOnClickListener(this);

            this.exerciseActivity = exerciseActivity;
            cardView = (CardView) itemView.findViewById(R.id.cardView_exercise);
            cardView.setOnLongClickListener(exerciseActivity);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(exerciseActivity.getIsInActionMode())
                exerciseActivity.prepareSelection(v, getAdapterPosition());
            else {
                if (!ExerciseDialogFragment.isOpened()) {
                    ExerciseDialogFragment listDialogFragment = ExerciseDialogFragment.newEditInstance(getAdapterPosition(), exerciseList.get(getAdapterPosition()).getClass().getModifiers());
                    listDialogFragment.show(exerciseActivity.getSupportFragmentManager(), "DialogFragment");
                }
            }
        }

    }
}

