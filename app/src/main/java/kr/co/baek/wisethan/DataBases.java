package kr.co.baek.wisethan;

import android.provider.BaseColumns;

public final class DataBases {

    public static final class CreateDB implements BaseColumns {
        public static final String HOUR = "hour";
        public static final String MINUTE = "minute";
        public static final String AMPM = "ampm";
        public static final String _TABLENAME0 = "usertable";
        public static final String _CREATE0 = "create table if not exists "+_TABLENAME0+"("
                +_ID+" integer primary key autoincrement, "
                +HOUR+" integer not null , "
                +MINUTE+" integer not null , "
                +AMPM+" text not null );";
    }
}
