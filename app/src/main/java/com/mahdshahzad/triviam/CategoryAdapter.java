package com.mahdshahzad.triviam;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> categoryList;
    private Resources resources;
    private Context parentContext;
    private int numCategoriesSelected = 0;
    HorizontalScrollView selectedCategories;
    LinearLayout selectedCategoriesLayout;
    TextView numberCategoriesTextView;


    // Constructor
    public CategoryAdapter(List<Category> categoryList, Context context) {
        this.categoryList = categoryList;
        this.resources = context.getResources();
        this.parentContext = context;
        selectedCategories = ((Activity)parentContext).findViewById(R.id.selectedCategoriesScrollView);
        selectedCategoriesLayout = selectedCategories.findViewById(R.id.selectedCategoriesLayout);
        numberCategoriesTextView = ((Activity)parentContext).findViewById(R.id.numberOfSelectedCategoriesTextView);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_category, parent, false);

        return new CategoryViewHolder(itemView);
    }
    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameTextView;
        Button includeButton;
        ImageView categoryImageView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryNameTextView = itemView.findViewById(R.id.categoryNameTextView);
            includeButton = itemView.findViewById(R.id.includeCategoryButton);
            categoryImageView = itemView.findViewById(R.id.categoryImageView);

        }
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);

        holder.categoryNameTextView.setText(category.categoryName);
        holder.categoryNameTextView.setTextColor(resources.getColor(category.textColourId));
        holder.includeButton.setBackgroundColor(resources.getColor(category.cardColourId));
        holder.categoryImageView.setImageResource(category.imageResourceId);
        holder.includeButton.setOnClickListener(view -> {
            // Handle button click to include/exclude category
            if (!category.included) {
                addCategoryToScrollView(category);
                category.included = true;
            }
        });
        
    }

    private void addCategoryToScrollView(Category category) {

        CardView cardView = new CardView(parentContext);

        // Clone the original CardView by inflating it from the layout resource
        View clone = LayoutInflater.from(parentContext).inflate(R.layout.card_view_category, cardView, false);

        TextView categoryNameTextView = clone.findViewById(R.id.categoryNameTextView);
        Button includeButton = clone.findViewById(R.id.includeCategoryButton);
        ImageView categoryImageView = clone.findViewById(R.id.categoryImageView);
        ImageButton removeCategoryButton = clone.findViewById(R.id.categoryRemoveButton);

        categoryNameTextView.setText(category.categoryName);
        categoryNameTextView.setTextColor(resources.getColor(category.textColourId));
        includeButton.setBackgroundColor(resources.getColor(category.cardColourId));
        categoryImageView.setImageResource(category.imageResourceId);
        removeCategoryButton.setVisibility(View.VISIBLE);
        removeCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numCategoriesSelected--;
                numberCategoriesTextView.setText("Categories: " + String.valueOf(numCategoriesSelected));
                if (numCategoriesSelected == 0) {numberCategoriesTextView.setVisibility(View.INVISIBLE);}
                MainActivity.soundPool.play(MainActivity.soundRemoveCategory, 1.0f, 1.0f, 1, 0, 1.0f);
                selectedCategoriesLayout.removeView(cardView);
                category.included = false;
            }
        });


        // Add the clone to the new CardView
        cardView.addView(clone);

        cardView.setBackgroundColor(Color.TRANSPARENT);

        // Add the new CardView to the ScrollView's child layout
        numCategoriesSelected++;
        numberCategoriesTextView.setText("Categories: " + String.valueOf(numCategoriesSelected));
        numberCategoriesTextView.setVisibility(View.VISIBLE);
        MainActivity.soundPool.play(MainActivity.soundSelectCategory, 1.0f, 1.0f, 1, 0, 1.0f);
        selectedCategoriesLayout.addView(cardView,0);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return categoryList.size();
    }

}
