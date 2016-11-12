/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine.app.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Defines table and column names for the weather database.
 */
public class WeatherContract {

    //------------------------------------------------------------------------------------------
    // Добавлено из гиста
    // "Content authority" - это имя целого контент-провайдера, аналогичное
    // отношению между доменным именем и его веб-сайтом. Удобная строка для использования
    // авторитетного источника контента - имя пакета приложения, что гарантирует уникальность на
    // устройстве.
    public static final String CONTENT_AUTHORITY = "com.example.android.sunshine.app";

    // Используем CONTENT_AUTHORITY, чтобы создать базовую часть для всех URI, которые приложение
    // будет использовать, чтобы контактировать с контент-провайдером.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Возможные пути (добавляемые к базовому контентному URI для возможных URI)
    // Например, content://com.example.android.sunshine.app/weather/ - допустимый путь для
    // просмотра погодных данных. content://com.example.android.sunshine.app/givemeroot/ не сработает,
    // поскольку ContentProvider не имеет информации, что делать с "givemeroot".
    // По крайней мере, будем надеяться. Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_WEATHER = "weather";
    public static final String PATH_LOCATION = "location";
    //------------------------------------------------------------------------------------------

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    /*
        Inner class that defines the table contents of the location table
        Students: This is where you will add the strings.  (Similar to what has been
        done for WeatherEntry)
     */
    public static final class LocationEntry implements BaseColumns {
        //------------------------------------------------------------------------------------------
        // Добавлено из гиста
        // Представляет базовое местоположение таблицы
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        // Курсоры, возвращаемые из контент-провайдера, имеют уникальные типы, основанные на их
        // контенте и базовом пути, использованном для запроса. Андроид использует форму, аналогичную
        // internet media type, или MIME type, чтобы описать тип, возвращаемый URI.
        // Курсоры, которые могут содержать больше одного элемента, содержат в качестве префикса
        // строку CURSOR_DIR_BASE_TYPE, тогда как курсоры, которые возвращают только один элемент,
        // содержат в качестве префикса строку CURSOR_ITEM_BASE_TYPE. Эти типы определены как для
        // запроса местоположения, так и для запроса погоды.
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
            //------------------------------------------------------------------------------------------

            public static final String TABLE_NAME = "location";

            // Строка с настройкой местоположения - это то, что будет передано в openweathermap как запрос местоположения
            public static final String COLUMN_LOCATION_SETTING = "location_setting";

            // Читаемая строка местоположения, предоставляемая API
            public static final String COLUMN_CITY_NAME = "city_name";

            // Чтобы уникально указать местоположение на карте при использовании map-интента,
            // мы храним долготу и широту, как нам вернул их openweathermap API.
            public static final String COLUMN_COORD_LAT = "coord_lat";
            public static final String COLUMN_COORD_LONG = "coord_long";

    }

    /* Inner class that defines the table contents of the weather table */
    public static final class WeatherEntry implements BaseColumns {
        //------------------------------------------------------------------------------------------
        // Добавлено из гиста
        // Представляет базовое местоположение таблицы
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        //------------------------------------------------------------------------------------------

        public static final String TABLE_NAME = "weather";

        // Column with the foreign key into the location table.
        public static final String COLUMN_LOC_KEY = "location_id";
        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_DATE = "date";
        // Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_WEATHER_ID = "weather_id";

        // Short description and long description of the weather, as provided by API.
        // e.g "clear" vs "sky is clear".
        public static final String COLUMN_SHORT_DESC = "short_desc";

        // Min and max temperatures for the day (stored as floats)
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_HUMIDITY = "humidity";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_PRESSURE = "pressure";

        // Windspeed is stored as a float representing windspeed  mph
        public static final String COLUMN_WIND_SPEED = "wind";

        // Degrees are meteorological degrees (e.g, 0 is north, 180 is south).  Stored as floats.
        public static final String COLUMN_DEGREES = "degrees";

        //------------------------------------------------------------------------------------------
        // Добавлено из гиста
        // Функции, которые помогают строить контент-провайдерные запросы.
        // Их удобно иметь, потому что это занимает меньше места в вашем коде, знающем
        // о фактическом кодировании URI, сохраняя эти знания в контракте.
        public static Uri buildWeatherUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /*
            Student: Fill in this buildWeatherLocation function
         */
        public static Uri buildWeatherLocation(String locationSetting) {
            Uri weatherLocationUri = CONTENT_URI.buildUpon()
                    .appendPath(locationSetting)
                    .build();
            return weatherLocationUri;
        }

        // Также мы можем использовать эти функции, чтобы добавлять потенциадьно полезные параметры
        // запроса. В данном случае мы используем параметр запроса для даты начала. Параметры запроса
        // полезны, когда мы имеем фиксированный запрос к базе данных, для которого мы можем захотеть
        // иметь некоторую ограниченную параметризацию. В данном случае это будет параметр для связи
        // между двумя таблицами.
        public static Uri buildWeatherLocationWithStartDate(
                String locationSetting, long startDate) {
            long normalizedDate = normalizeDate(startDate);
            // Строки, такие как locationSetting, могут быть добавлены с помощью функции appendPath
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendQueryParameter(COLUMN_DATE, Long.toString(normalizedDate)).build();
        }

        // Эта функция строит двухчастный URI как с сегментом погоды, так и с сегментом даты.
        public static Uri buildWeatherLocationWithDate(String locationSetting, long date) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendPath(Long.toString(normalizeDate(date))).build();
        }

        // Вспомогательные функции, которые прячут структуру URI от кода
        // для возвращения значений в URI и помещения этих знаний в одно место
        public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static long getDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static long getStartDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(COLUMN_DATE);
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }
        //------------------------------------------------------------------------------------------
    }
}
