package com.example.notlarim;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class NoteWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            
            // Liste için servis bağlantısı
            Intent intent = new Intent(context, NoteWidgetService.class);
            views.setRemoteAdapter(R.id.widgetListView, intent);

            // Liste öğelerine tıklandığında (Düzenleme modu)
            Intent editIntent = new Intent(context, AddNoteActivity.class);
            PendingIntent editPendingIntent = PendingIntent.getActivity(context, 0, editIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
            views.setPendingIntentTemplate(R.id.widgetListView, editPendingIntent);

            // Sağ üstteki + butonuna tıklandığında (Yeni not modu)
            Intent addIntent = new Intent(context, AddNoteActivity.class);
            // Yeni not olduğunu anlaması için extra göndermiyoruz, böylece temiz sayfa açılır
            PendingIntent addPendingIntent = PendingIntent.getActivity(context, 1, addIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widgetBtnAdd, addPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    public static void updateAllWidgets(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(context, NoteWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.widgetListView);
    }
}
