package com.sam_chordas.android.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;

/**
 * Created by cebert on 03.12.16.
 */

public class WidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new RemoteViewsFactory() {

            private Cursor data = null;
            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {

                if (data != null)
                    data.close();

                final long identityToken = Binder.clearCallingIdentity();

                data = getContentResolver().query(
                        QuoteProvider.Quotes.CONTENT_URI,
                        new String[] {
                                QuoteColumns._ID,
                                QuoteColumns.SYMBOL,
                                QuoteColumns.BIDPRICE,
                                QuoteColumns.PERCENT_CHANGE,
                                QuoteColumns.CHANGE,
                                QuoteColumns.ISUP
                        },
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},
                        null);

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {

            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {

                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position))
                    return null;

                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_listview_item);

                views.setTextViewText(R.id.stock_symbol, data.getString(data.getColumnIndex
                        (getResources().getString(R.string.symbol))));

                views.setInt(R.id.change,
                        "setBackgroundResource",
                        data.getInt(data.getColumnIndex(QuoteColumns.ISUP)) == 1 ?
                                R.drawable.percent_change_pill_green : R.drawable.percent_change_pill_red);

                views.setTextViewText(R.id.change,
                        data.getString(data.getColumnIndex(Utils.showPercent ?
                                QuoteColumns.PERCENT_CHANGE : QuoteColumns.CHANGE)));

                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra(getResources().getString(R.string.symbol),
                        data.getString(data.getColumnIndex(QuoteColumns.SYMBOL)));
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return null;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {

                if (data != null && data.moveToPosition(position))
                {
                    final int QUOTES_ID_COL = 0;
                    return data.getLong(QUOTES_ID_COL);
                }

                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}