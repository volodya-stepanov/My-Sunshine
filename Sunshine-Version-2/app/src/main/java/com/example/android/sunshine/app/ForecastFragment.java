package com.example.android.sunshine.app;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Володя on 13.08.2016.
 */
public class ForecastFragment extends Fragment {

    public ForecastFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            new FetchWeatherTask().execute("94043");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String[] forecastArray = {
                "Today - Sunny - 26 / 15",
                "Tomorrow - Cloudy - 28 / 18",
                "Sat - Sunny - 30 / 20",
                "Sun - Foggy - 31 / 16",
                "Mon - Sunny - 25 / 15",
                "Tue - Cloudy - 27 / 16",
                "Wed - Sunny - 27 / 16"
        };

        List<String> weekForecast = new ArrayList<String>(Arrays.asList(forecastArray));

        // context Текущий контекст
        ArrayAdapter<String> mForecastAdapter = new ArrayAdapter<String>(getActivity(),
                // resource ID ресурса layout-файла, содержащего макет для использования при создании экземпляров View
                R.layout.list_item_forecast,
                // textViewResourceId ID ресурса TextView, который должен быть заполнен, в ресурсе layout-файла
                R.id.list_item_forecast_textview,
                // objects Объекты для отображения в ListView
                weekForecast);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);   // TODO: Разобраться, в чём дело!
        listView.setAdapter(mForecastAdapter);

        new FetchWeatherTask().execute("94043");
//        new FetchWeatherTask().execute();

        return rootView;
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, Void>
    {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected Void doInBackground(String... params) {

            // Если нет почтового индекса, нечего искать. Проверка размера массива параметров
            if (params.length == 0) {
                return null;
            }

            // Эти две строки должны быть объявлены за пределами try/catch
            // чтобы они могли быть закрыты в блоке finally
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Будет содержать необработанный JSON-ответ как строку
            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            int numDays = 7;

            try{
                // Создаём URL для запроса OpenWeatherMap
                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";

                // TODO: Забыла добавить APPKEY
                // Метод parse создаёт Uri, которое парсит данную закодированную Uri-строку
                // Метод buildUpon создает новый builder, копируя атрибуты из этого Uri
                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, params[0])
                .appendQueryParameter(FORMAT_PARAM, format)
                .appendQueryParameter(UNITS_PARAM, units)
                .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                .build();

                // Создаём URL на основе URI
                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Url built with builder: " + url);

                // Создаём запрос к OpenWeatherMap и открываем соединение
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Читаем входной поток в строку
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null){
                    // Нечего делать
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Так как это JSON, добавление новой строки не обязательно (это не повлияет на парсинг)
                    // Но это делает отладку *намного* проще, если вы распечатаете заполненный
                    // буфер для отладки
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0){
                    // Хранилище было пустое. Нет смысла в парсинге
                    return null;
                }
                forecastJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Forecast JSON string: " + forecastJsonStr);

                double maxTemperature = getMaxTemperatureForDay(forecastJsonStr, 0);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
                // Если код не получил успешно данные о погоде, нет смысла пытаться парсить его.
                return null;
            } finally {
                if (urlConnection!=null){
                    urlConnection.disconnect();
                }
                if (reader != null){
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("ForecastFragment", "Error closing stream, e");
                    }
                }
            }
            return null;
        }

        private double getMaxTemperatureForDay(String forecastJsonStr, int dayIndex)
        throws JSONException {
            JSONObject jsonObject = new JSONObject(forecastJsonStr);

            return -1;
        }
    }
}
