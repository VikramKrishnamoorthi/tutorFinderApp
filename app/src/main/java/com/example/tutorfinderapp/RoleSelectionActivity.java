package com.example.tutorfinderapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RoleSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_tutor_selection);

        Button tutorButton = findViewById(R.id.tutorButton);
        Button studentButton = findViewById(R.id.studentButton);

        tutorButton.setOnClickListener(v ->
                Toast.makeText(RoleSelectionActivity.this, "Tutor selected", Toast.LENGTH_SHORT).show()
        );

        studentButton.setOnClickListener(v ->
                Toast.makeText(RoleSelectionActivity.this, "Student selected", Toast.LENGTH_SHORT).show()
        );
    }
}
