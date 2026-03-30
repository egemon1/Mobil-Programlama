package com.example.notlarim;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private AppDatabase db;
    private FloatingActionButton fabAdd;
    private TextView textViewCount;
    private EditText editTextSearch;
    private List<Note> allNotes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Kenardan kenara (Edge-to-Edge) görünüm ve sistem çubuğu ayarları
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        getWindow().setStatusBarColor(getColor(R.color.bg_gray));
        getWindow().setNavigationBarColor(getColor(R.color.bg_gray));
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        recyclerView = findViewById(R.id.recyclerViewNotes);
        fabAdd = findViewById(R.id.fabAdd);
        textViewCount = findViewById(R.id.textViewCount);
        editTextSearch = findViewById(R.id.editTextSearch);
        
        db = AppDatabase.getInstance(this);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnNoteClickListener(new NoteAdapter.OnNoteClickListener() {
            @Override
            public void onNoteClick(Note note) {
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                intent.putExtra("note_to_edit", note);
                startActivity(intent);
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterNotes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Note noteToDelete = adapter.getNoteAt(position);
                db.noteDao().delete(noteToDelete);
                loadNotes();
                NoteWidgetProvider.updateAllWidgets(MainActivity.this);
                Toast.makeText(MainActivity.this, "Not silindi", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                startActivity(intent);
            }
        });
        
        loadNotes();
    }

    private void filterNotes(String query) {
        List<Note> filteredList = new ArrayList<>();
        for (Note note : allNotes) {
            if (note.title.toLowerCase().contains(query.toLowerCase()) || 
                note.content.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(note);
            }
        }
        adapter.setNotes(filteredList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }

    private void loadNotes() {
        allNotes = db.noteDao().getAllNotes();
        adapter.setNotes(allNotes);
        
        if (textViewCount != null) {
            textViewCount.setText(allNotes.size() + " Not");
        }
    }
}
