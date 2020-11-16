package com.bitspilani.inventorytrackerjava.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bitspilani.inventorytrackerjava.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    List<String> titles;
    List<String> content;

    public Adapter(List<String> titles, List<String> content){
        this.titles = titles;
        this.content = content;
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        holder.noteTitle.setText(titles.get(position));
        holder.noteContent.setText(content.get(position));
        holder.mCardView.setCardBackgroundColor(holder.view.getResources().getColor(getRandomColour()));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Item is clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getRandomColour() {
        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.color.blue);
        colorCode.add(R.color.yellow);
        colorCode.add(R.color.skyblue);
        colorCode.add(R.color.lightPurple);
        colorCode.add(R.color.lightGreen);
        colorCode.add(R.color.gray);
        colorCode.add(R.color.pink);
        colorCode.add(R.color.red);
        colorCode.add(R.color.greenlight);
        colorCode.add(R.color.notgreen);

        Random randomColor = new Random();
        int number = randomColor.nextInt(colorCode.size());
        return colorCode.get(number);
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView noteTitle,noteContent;
        CardView mCardView;
        View view;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            noteContent = itemView.findViewById(R.id.content);
            noteTitle = itemView.findViewById(R.id.titles);
            mCardView = itemView.findViewById(R.id.noteCard);
            view = itemView;
        }
    }
}
