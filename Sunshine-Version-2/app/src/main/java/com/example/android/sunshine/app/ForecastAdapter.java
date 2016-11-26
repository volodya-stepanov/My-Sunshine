package com.example.android.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Готовит максимальные/минимальные значения температуры для представления.
     */
    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(mContext);
        String highLowStr = Utility.formatTemperature(high, isMetric) + "/" + Utility.formatTemperature(low, isMetric);
        return highLowStr;
    }

    /*
        Импортировано изFetchWeatherTask --- но здесь мы переходим прямо из курсора в строку.
        Этот метод берёт строку из курсора и конструирует строку формата "Дата - Погода - High/Low".
        Это строка для отображения в элементе ListView. Этот метод использует метод formatHighLow,
        чтобы получить корректную строку температуры.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {
        // get row indices for our cursor
        int idx_max_temp = ForecastFragment.COL_WEATHER_MAX_TEMP;
        int idx_min_temp = ForecastFragment.COL_WEATHER_MIN_TEMP;
        int idx_date = ForecastFragment.COL_WEATHER_DATE;
        int idx_short_desc = ForecastFragment.COL_WEATHER_DESC;


        String highAndLow = formatHighLows(
                cursor.getDouble(idx_max_temp),
                cursor.getDouble(idx_min_temp));

        return Utility.formatDate(cursor.getLong(idx_date)) +
                " - " + cursor.getString(idx_short_desc) +
                " - " + highAndLow;
    }

    /*
        Следующие два метода необходимо переопределять каждый раз при наследовании от CursorAdapter.
        Адаптеры работают с элементами ListView, чтобы наполнять их.
        Они создают дубликаты одного и того же макета и помещают их в ListView.
        Этот метод возвращает макет, который необходимо дублировать
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_forecast, parent, false);

        return view;
    }

    /*
        Здесь мы заполняем элементы view содержимым курсора.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Наш view здесь довольно прост - просто text view
        // Мы сохраним функционал пользовательского интерфейса с простой (и медленной!) привязкой.

//        TextView tv = (TextView)view;
//        tv.setText(convertCursorRowToUXFormat(cursor));
    }
}
