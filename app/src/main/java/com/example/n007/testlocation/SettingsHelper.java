package com.example.n007.testlocation;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by N007 on 2/8/2560.
 */

public class SettingsHelper {
    private static final String KEY_WSNAMESPACE = "WsNameSpace";
    private static final String KEY_WSURL = "WsUrl";
    private static final String KEY_SURVEYORCODE = "SurveyorCode";

    private static SettingsHelper instance;
    private int evnkey;
    private String latitude;
    private String longitude;
    private String surveyorId;
    private String surveyorName;
    private String speed;
    private String direction;
    private String nSatellites;
    private String tabletVersion;

    public static SettingsHelper getInstance(){
        if(instance == null)
            instance = new SettingsHelper();
        return instance;
    }

    public static String getWsNameSpace(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString(KEY_WSNAMESPACE, "http://www.arunsawad.com/");
    }

//    public static String getWsUrl(Context context) {
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
//        return settings.getString(KEY_WSURL, Constant.WEBSERVICE_URL);
//    }

    public static String getSurveyorCode(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString(KEY_SURVEYORCODE, "");
    }

    public void setEvnkey(int evnkey) {
        this.evnkey = evnkey;
    }

    public int getEvnkey() {
        return evnkey;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setSurveyorId(String surveyorId) {
        this.surveyorId = surveyorId;
    }

    public String getSurveyorId() {
        return surveyorId;
    }

    public void setSurveyorName(String surveyorName) {
        this.surveyorName = surveyorName;
    }

    public String getSurveyorName() {
        return surveyorName;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getSpeed() {
        return speed;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getDirection() {
        return direction;
    }

    public void setnSatellites(String nSatellites) {
        this.nSatellites = nSatellites;
    }

    public String getnSatellites() {
        return nSatellites;
    }

    public void setTabletVersion(String tabletVersion) {
        this.tabletVersion = tabletVersion;
    }

    public String getTabletVersion() {
        return tabletVersion;
    }
}

