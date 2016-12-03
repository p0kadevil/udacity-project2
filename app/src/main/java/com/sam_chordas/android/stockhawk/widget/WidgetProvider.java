package com.sam_chordas.android.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

/**
 * Created by cebert on 03.12.16.
 */

public class WidgetProvider extends AppWidgetProvider {

    public static String TAG = WidgetProvider.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int widgetId : appWidgetIds)
        {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_listview_layout);

            Intent intent = new Intent(context, MyStocksActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget, pendingIntent);
            setRemoteAdapter(context, remoteViews);

            Intent clickIntent = new Intent(context, MyStocksActivity.class);
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntent)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    private void setRemoteAdapter(Context context, final RemoteViews views) {

        try
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                views.setRemoteAdapter(R.id.widget_list,
                        new Intent(context, WidgetRemoteViewsService.class));
            }
            else
            {
                views.setRemoteAdapter(0, R.id.widget_list,
                        new Intent(context, WidgetRemoteViewsService.class));
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }
}