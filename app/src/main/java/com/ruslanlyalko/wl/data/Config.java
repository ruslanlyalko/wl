package com.ruslanlyalko.wl.data;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface Config {

    String ANDROID = "ANDROID";
    String DB_USERS = "USERS";
    String DB_REPORTS = "REPORTS";
    String DB_PROJECTS = "PROJECTS";
    String DB_HOLIDAYS = "HOLIDAYS";
    String DB_SETTINGS = "SETTINGS";
    String DB_SETTINGS_GENERAL = "general";
    //
    String FIELD_TITLE = "title";
    String FIELD_NAME = "name";
    String FIELD_DATE_TIME = "date/time";
    String FIELD_USER_ID = "userId";
    String FIELD_TOKEN = "token";
    String FIELD_TOKENS = "tokens";
    String FIELD_REMIND_ME_AT = "remindMeAt";
    String FIELD_DEFAULT_WORKING_TIME = "defaultWorkingTime";
    String FIELD_IS_OLD_STYLE_CALENDAR = "isOldStyleCalendar";
    String FIELD_VERSION = "version";
    String FIELD_IS_NIGHT_MODE = "isNightMode";
    //
    String DATABASE_URL = "https://paworkload01.firebaseio.com/";
    String API_KEY = "AIzaSyD-WLO5002WFK0r1nlaBllaRteUr-Xum0U";
    String APP_ID = "paworkload01";
    String APP_NAME = "Workload2";
}
