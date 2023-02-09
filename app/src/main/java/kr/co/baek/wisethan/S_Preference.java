package kr.co.baek.wisethan;

import android.content.Context;
import android.content.SharedPreferences;

public class S_Preference {
    //TODO == [사용 설명] ==
    /** [전체 key 확인]
     S_Preference.getTotalKey(getApplication()); //저장된 특정 데이터 불러온다
     */

    /** [String 저장]
     S_Preference.setString(getApplication(), "Name", "Data_kwon"); //특정 데이터 저장한다
     S_Preference.getString(getApplication(), "Name"); //저장된 특정 데이터 불러온다
     */

    /** [Int 저장]
     S_Preference.setInt(getApplication(), "Age", 28); //특정 데이터 저장한다
     S_Preference.getInt(getApplication(), "Age"); //저장된 특정 데이터 불러온다
     */

    /** [Boolean 저장]
     S_Preference.setBoolean(getApplication(), "Sex", true); //특정 데이터 저장한다
     S_Preference.getBoolean(getApplication(), "Sex"); //저장된 특정 데이터 불러온다
     */

    /** [특정 key 삭제]
     S_Preference.removeKey(getApplication(), "Name"); //특정 데이터 삭제한다
     */

    /** [전체 key 삭제]
     S_Preference.clear(getApplication()); //전체 데이터 삭제한다
     */

    //TODO == [전역 변수] ==
    public static final String PREFERENCES_NAME = "rebuild_preference";
    private static final String DEFAULT_VALUE_STRING = "";
    private static final boolean DEFAULT_VALUE_BOOLEAN = false;
    private static final int DEFAULT_VALUE_INT = -1;

    //TODO == [객체 생성] ==
    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    //TODO == [전체 key 값 저장] ==
    public static void setTotalKey(Context context, String key, String value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
        editor.apply();
    }

    //TODO == [String 값 저장] ==
    public static void setString(Context context, String key, String value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
        editor.apply();
        //TODO == [전체 데이터에 키 저장] ==
        String data = "";
        data = getTotalKey(context);
        if(data.contains("["+key+"]") == false){
            data = data + "["+ key + "]";
            setTotalKey(context, "TotalKey", data);
        }
    }

    //TODO == [boolean 값 저장] ==
    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
        editor.apply();
        //TODO == [전체 데이터에 키 저장] ==
        String data = "";
        data = getTotalKey(context);
        if(data.contains("["+key+"]") == false){
            data = data + "["+ key + "]";
            setTotalKey(context, "TotalKey", data);
        }
    }

    //TODO == [int 값 저장] ==
    public static void setInt(Context context, String key, int value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
        editor.apply();
        //TODO == [전체 데이터에 키 저장] ==
        String data = "";
        data = getTotalKey(context);
        if(data.contains("["+key+"]") == false){
            data = data + "["+ key + "]";
            setTotalKey(context, "TotalKey", data);
        }
    }

    //TODO == [전체 Key 값 호출] ==
    public static String getTotalKey(Context context) {
        SharedPreferences prefs = getPreferences(context);
        String value = prefs.getString("TotalKey", DEFAULT_VALUE_STRING);
        return value;
    }

    //TODO == [String 값 호출] ==
    public static String getString(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        String value = prefs.getString(key, DEFAULT_VALUE_STRING);
        return value;
    }

    //TODO == [boolean 값 호출] ==
    public static boolean getBoolean(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        boolean value = prefs.getBoolean(key, DEFAULT_VALUE_BOOLEAN);
        return value;
    }

    //TODO == [int 값 호출] ==
    public static int getInt(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        int value = prefs.getInt(key, DEFAULT_VALUE_INT);
        return value;
    }

    //TODO == [전체 key 값에서 특정 값 삭제] ==
    public static void removeTotalKey(Context context, String key) {
        if(getTotalKey(context).contains("["+key+"]") == true){
            String data = getTotalKey(context);
            data = data.replace("["+ key + "]","");
            setTotalKey(context, "TotalKey", data);
        }
    }

    //TODO == [특정 key 삭제] ==
    public static void removeKey(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.remove(key);
        edit.commit();
        //TODO == [전체 데이터에서 키값 삭제] ==
        if(getTotalKey(context).contains("["+key+"]") == true){
            removeTotalKey(context,key);
        }
    }

    //TODO == [전체 key 삭제] ==
    public static void clear(Context context) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
        //TODO == [전체 데이터 삭제] ==
        String data = "";
        data = getTotalKey(context);
        if(data.length() > 0 && data.equals("") == false){
            setTotalKey(context, "TotalKey", "");
        }
    }

}
