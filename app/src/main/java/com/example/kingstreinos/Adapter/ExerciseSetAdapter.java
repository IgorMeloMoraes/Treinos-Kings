

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

import com.example.kingstreinos.Activity.ExerciseSetActivity;
import com.example.kingstreinos.R;
import com.example.kingstreinos.database.PFASQLiteHelper;
import com.example.kingstreinos.fragments.ExerciseSetDialogFragment;

import java.util.ArrayList;
import java.util.List;


public class ExerciseSetAdapter extends RecyclerView.Adapter<ExerciseSetAdapter.MyViewHolder> {

    private List<Object> exerciseSetsList;
    private ExerciseSetActivity exerciseSetActivity;
    private PFASQLiteHelper db = null;
    private Context context;

    public ExerciseSetAdapter(List<Object> exerciseSetsList, Context ctx) {
        this.exerciseSetsList = exerciseSetsList;
        exerciseSetActivity = (ExerciseSetActivity) ctx;
        db = new PFASQLiteHelper(ctx);
        context = ctx;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_exerciseset, parent, false);

        return new MyViewHolder(itemView, exerciseSetActivity);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Object es = exerciseSetsList.get(position);

        if(!exerciseSetActivity.getIsInActionMode()){
            holder.checkbox.setVisibility(View.GONE);
        }
        else{
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.checkbox.setChecked(false);
        }
    }



    @Override
    public int getItemCount() {
        return exerciseSetsList.size();
    }


    public void updateAdapter(ArrayList<Object> list){
        for(Object es : list){
            exerciseSetsList.remove(es);
        }
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView name, number;
        private ImageView[] imageViews;
        private ImageView exercise1, exercise2, exercise3, exercise4, exercise5, exercise6;
        private CheckBox checkbox;
        private ExerciseSetActivity exerciseSetActivity;
        private CardView cardView;
        public MyViewHolder(View view, ExerciseSetActivity exerciseSetActivity) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            exercise1 = (ImageView) view.findViewById(R.id.exercise_img_1);
            exercise2 = (ImageView) view.findViewById(R.id.exercise_img_2);
            exercise3 = (ImageView) view.findViewById(R.id.exercise_img_3);
            exercise4 = (ImageView) view.findViewById(R.id.exercise_img_4);
            exercise5 = (ImageView) view.findViewById(R.id.exercise_img_5);
            exercise6 = (ImageView) view.findViewById(R.id.exercise_img_6);

            imageViews = new ImageView[]{exercise1, exercise2, exercise3, exercise4, exercise5, exercise6};

            number = (TextView) view.findViewById(R.id.number);
            checkbox = (CheckBox) view.findViewById(R.id.check_list_item);
            checkbox.setOnClickListener(this);

            this.exerciseSetActivity = exerciseSetActivity;
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            cardView.setOnLongClickListener(exerciseSetActivity);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(exerciseSetActivity.getIsInActionMode())
                exerciseSetActivity.prepareSelection(v, getAdapterPosition());
            else {
                if (!ExerciseSetDialogFragment.isOpened()) {

                }
            }
        }

    }
}

