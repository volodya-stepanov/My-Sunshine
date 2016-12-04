package com.example.android.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Готовит максимальные/минимальные значения температуры для представления.
     */
//    private String formatHighLows(double high, double low) {
//        boolean isMetric = Utility.isMetric(mContext);
//        String highLowStr = Utility.formatTemperature(mContext, high, isMetric) + "/" + Utility.formatTemperature(mContext, low, isMetric);
//        return highLowStr;
//    }

    /*
        Импортировано изFetchWeatherTask --- но здесь мы переходим прямо из курсора в строку.
        Этот метод берёт строку из курсора и конструирует строку формата "Дата - Погода - High/Low".
        Это строка для отображения в элементе ListView. Этот метод использует метод formatHighLow,
        чтобы получить корректную строку температуры.
     */
//    private String convertCursorRowToUXFormat(Cursor cursor) {
//        // get row indices for our cursor
//        int idx_max_temp = ForecastFragment.COL_WEATHER_MAX_TEMP;
//        int idx_min_temp = ForecastFragment.COL_WEATHER_MIN_TEMP;
//        int idx_date = ForecastFragment.COL_WEATHER_DATE;
//        int idx_short_desc = ForecastFragment.COL_WEATHER_DESC;
//
//
//        String highAndLow = formatHighLows(
//                cursor.getDouble(idx_max_temp),
//                cursor.getDouble(idx_min_temp));
//
//        return Utility.formatDate(cursor.getLong(idx_date)) +
//                " - " + cursor.getString(idx_short_desc) +
//                " - " + highAndLow;
//    }

    /*
        Следующие два метода необходимо переопределять каждый раз при наследовании от CursorAdapter.
        Адаптеры работают с элементами ListView, чтобы наполнять их.
        Они создают дубликаты одного и того же макета и помещают их в ListView.
        Этот метод возвращает макет, который необходимо дублировать
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;

        if (viewType == VIEW_TYPE_TODAY) {
            layoutId = R.layout.list_item_forecast_today;
        }
        else if (viewType == VIEW_TYPE_FUTURE_DAY) {
            layoutId = R.layout.list_item_forecast;
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    /*
        Здесь мы заполняем элементы view содержимым курсора.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Наш view здесь довольно прост - просто text view
        // Мы сохраним функционал пользовательского интерфейса с простой (и медленной!) привязкой.

        ViewHolder viewHolder = (ViewHolder) view.getTag();


        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
        // Use placeholder image for now

        int viewType = getItemViewType(cursor.getPosition());
        int imageResource = -1;

        if (viewType == VIEW_TYPE_TODAY) {
            imageResource = Utility.getArtResourceForWeatherCondition(weatherId);
        }
        else if (viewType == VIEW_TYPE_FUTURE_DAY) {
            imageResource = Utility.getIconResourceForWeatherCondition(weatherId);
        }
                
        viewHolder.iconView.setImageResource(imageResource);

        long dateInMillis = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        String date = Utility.getFriendlyDayString(context, dateInMillis);
        //TextView dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
        viewHolder.dateView.setText(date);

        String description = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        //TextView forecastView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
        viewHolder.descriptionView.setText(description);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        //TextView highView = (TextView) view.findViewById(R.id.list_item_high_textview);
        viewHolder.highTempView.setText(Utility.formatTemperature(context, high, isMetric));

        // TODO Read low temperature from cursor
        double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        //TextView lowView = (TextView) view.findViewById(R.id.list_item_low_textview);
        viewHolder.lowTempView.setText(Utility.formatTemperature(context, low, isMetric));
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }
}
