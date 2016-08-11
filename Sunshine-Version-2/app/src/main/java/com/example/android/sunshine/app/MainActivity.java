package com.example.android.sunshine.app;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.android.sunshine.app/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.android.sunshine.app/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
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

            /**
             *
             * @param context Текущий контекст
             * @param resource ID ресурса layout-файла, содержащего макет для использования при создании экземпляров View
             * @param textViewResourceId ID ресурса TextView, который должен быть заполнен, в ресурсе layout-файла
             * @param objects Объекты для отображения в ListView
             */
            //TODO: Переделать комментарии
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

            // Эти две строки должны быть объявлены за пределами try/catch
            // чтобы они могли быть закрыты в блоке finally
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Будет содержать необработанный JSON-ответ как строку
            String forecastJsonStr = null;

            try{
                // Создаём URL для запроса OpenWeatherMap
                String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7";
                String apiKey = "&APPID=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;
                URL url = new URL(baseUrl.concat(apiKey));

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
            } catch (IOException e) {
                Log.e("PlaceHolderFragment", "Error", e);
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
                        Log.e("PlaceholderFragment", "Error closing stream, e");
                    }
                }
            }

            return rootView;
        }
    }
}
