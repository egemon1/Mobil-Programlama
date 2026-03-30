package com.example.notlarim;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import java.util.List;

public class NoteWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new NoteRemoteViewsFactory(this.getApplicationContext());
    }
}

class NoteRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context;
    private List<Note> notes;

    public NoteRemoteViewsFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onDataSetChanged() {
        notes = AppDatabase.getInstance(context).noteDao().getAllNotes();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (notes == null || position >= notes.size()) return null;

        Note note = notes.get(position);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_item);
        views.setTextViewText(R.id.widgetItemTitle, note.title);
        views.setTextViewText(R.id.widgetItemContent, note.content);

        // Notu düzenleme ekranına göndermek için intent hazırlıyoruz
        Bundle extras = new Bundle();
        extras.putSerializable("note_to_edit", note);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        
        // Tüm container üzerine tıklama veriyoruz
        views.setOnClickFillInIntent(R.id.widgetItemContainer, fillInIntent);

        return views;
    }

    @Override public int getCount() { return notes != null ? notes.size() : 0; }
    @Override public void onCreate() {}
    @Override public void onDestroy() {}
    @Override public RemoteViews getLoadingView() { return null; }
    @Override public int getViewTypeCount() { return 1; }
    @Override public long getItemId(int position) { return position; }
    @Override public boolean hasStableIds() { return true; }
}
