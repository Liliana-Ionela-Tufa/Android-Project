package com.example.licenta2024;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;

public class CreateRoute extends AppCompatActivity {

    Button datePickerButton, yesButton, noButton;
    TextView selectedDateText;
    ImageButton goBack;
    String selectedDate;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_route);
        datePickerButton = findViewById(R.id.datePickerButton);
        selectedDateText = findViewById(R.id.selectedDateText);
        goBack = findViewById(R.id.goBack);
        yesButton = findViewById(R.id.yes);
        noButton = findViewById(R.id.no);

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateRoute.this, ChooseAttractions.class);
                intent.putExtra("date", selectedDate);
                startActivity(intent);
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateRoute.this, OptionsMenu.class);
                intent.putExtra("date", selectedDate);
                startActivity(intent);
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateRoute.this, UserProfile.class);
                startActivity(intent);
            }
        });


        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateRoute.this,
                        (vw, year, month, dayOfMonth) -> {
                            selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                            selectedDateText.setText(selectedDate);
                            // Optionally, filter or refresh your tourist attractions based on the date selected
                        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();

            }
        });
    }
}