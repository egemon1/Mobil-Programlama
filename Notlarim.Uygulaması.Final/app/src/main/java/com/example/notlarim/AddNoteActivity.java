package com.example.notlarim;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddNoteActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextContent;
    private TextView buttonSaveTop;
    private TextView btnBack;
    private AppDatabase db;
    private Note existingNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        // Kenardan kenara görünüm ve sistem çubukları (Status Bar & Navigation Bar) ayarları
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        getWindow().setStatusBarColor(getColor(android.R.color.white));
        getWindow().setNavigationBarColor(getColor(android.R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextContent = findViewById(R.id.editTextContent);
        buttonSaveTop = findViewById(R.id.buttonSaveTop);
        btnBack = findViewById(R.id.btnBack);
        
        db = AppDatabase.getInstance(this);

        if (getIntent().hasExtra("note_to_edit")) {
            existingNote = (Note) getIntent().getSerializableExtra("note_to_edit");
            editTextTitle.setText(existingNote.title);
            editTextContent.setText(existingNote.content);
        }

        buttonSaveTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOrUpdateNote();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveOrUpdateNote() {
        String title = editTextTitle.getText().toString().trim();
        String content = editTextContent.getText().toString().trim();

        if (title.isEmpty() && content.isEmpty()) {
            finish();
            return;
        }

        String currentDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

        if (existingNote != null) {
            existingNote.title = title;
            existingNote.content = content;
            existingNote.date = currentDate;
            db.noteDao().update(existingNote);
        } else {
            Note note = new Note(title, content, currentDate);
            db.noteDao().insert(note);
        }
        
        NoteWidgetProvider.updateAllWidgets(this);
        finish();
    }
}
