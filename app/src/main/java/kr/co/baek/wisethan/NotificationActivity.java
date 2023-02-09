package kr.co.baek.wisethan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class NotificationActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private NotificationManager manager;
    private NotificationChannel channel;
    private NotificationCompat.Builder builder;

    private static final String CHANNEL_ID = "one-channel";
    private static final String CHANNEL_NAME = "My channel one";
    private static final String CHANNEL_DESCRIPTION = "My channel one Descripion";

    private CustomAdapter adapter;
    private  ArrayList<String> items;
    private static ArrayList<String> arrayIndex =  new ArrayList<String>();
    private static ArrayList<String> arrayData = new ArrayList<String>();
    private DbOpenHelper mDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Notification");

        manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.enableLights(true);
            channel.setVibrationPattern(new long[]{100, 200, 300});
            manager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        }else{
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        }

        ListView listView = findViewById(R.id.db_list_view);
        items = new ArrayList<>();
        adapter = new CustomAdapter(this, 0, items);
        listView.setAdapter(adapter);

        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();

        showDatabase();
    }

    public void showDatabase(){
        Cursor iCursor = mDbOpenHelper.sortByTimeColumn();
        Log.d("showDatabase", "DB Size: " + iCursor.getCount());
        arrayData.clear();
        boolean first = false;
        while(iCursor.moveToNext()){
            @SuppressLint("Range") String tempIndex = iCursor.getString(iCursor.getColumnIndex("_id"));
            @SuppressLint("Range") String tempHour = iCursor.getString(iCursor.getColumnIndex("hour"));
            @SuppressLint("Range") String tempMinute = iCursor.getString(iCursor.getColumnIndex("minute"));
            @SuppressLint("Range") String tempAmPm = iCursor.getString(iCursor.getColumnIndex("ampm"));

            int hour = Integer.parseInt(tempHour);
            int minute = Integer.parseInt(tempMinute);
            StringBuilder sb = new StringBuilder();

            if(hour < 10){
                sb.append("0");
            }
            sb.append(hour).append(":");

            if(minute<10){
                sb.append("0");
            }
            sb.append(minute).append(" ").append(tempAmPm);
            String Result = sb.toString();
            arrayData.add(Result);
            arrayIndex.add(tempIndex);

            if(!first){
                first = true;

                builder.setSmallIcon(android.R.drawable.ic_notification_clear_all)
                        .setContentTitle(Result)
                        .setContentText("View")
                        .setDefaults(NotificationCompat.FLAG_NO_CLEAR)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setOngoing(true)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setAutoCancel(false);

                Intent intent = new Intent(NotificationActivity.this, MainActivity.class);
                PendingIntent pendingIntent = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    pendingIntent = PendingIntent.getActivity(NotificationActivity.this, 10, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_IMMUTABLE);
                }else{
                    pendingIntent = PendingIntent.getActivity(NotificationActivity.this, 10, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                }


                builder.setContentIntent(pendingIntent);
                manager.notify(1000, builder.build());
            }
        }

        adapter.clear();
        adapter.addAll(arrayData);
        adapter.notifyDataSetChanged();

        mDbOpenHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.add_notification){
            TimePickerDialog timePickerDialog = new TimePickerDialog(NotificationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                    int nHour = hour;
                    int nMinute = minute;
                    String amPm;

                    if(hour == 12){
                        amPm = "PM";
                    } else if(hour > 12){
                        amPm = "PM";
                        nHour -= 12;
                    } else{
                        amPm = "AM";
                    }

                    StringBuilder sb = new StringBuilder();
                    if(nHour<10){
                        sb.append("0");
                    }
                    sb.append(nHour).append(":");

                    if(nMinute<10){
                        sb.append("0");
                    }
                    sb.append(nMinute).append(" ").append(amPm);

                    mDbOpenHelper.open();
                    mDbOpenHelper.insertColumn(String.valueOf(nHour), String.valueOf(nMinute), amPm);
                    //showDatabase();
                    builder.setSmallIcon(android.R.drawable.ic_notification_clear_all)
                            .setContentTitle(sb.toString())
                            .setContentText("View")
                            .setDefaults(NotificationCompat.FLAG_NO_CLEAR)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setOngoing(true)
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                            .setAutoCancel(false);

                    Intent intent = new Intent(NotificationActivity.this, MainActivity.class);
            /*        PendingIntent pendingIntent = PendingIntent.getActivity(NotificationActivity.this, 10, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);*/

                    PendingIntent pendingIntent = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        pendingIntent = PendingIntent.getActivity(NotificationActivity.this, 10, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_IMMUTABLE);
                    }else{
                        pendingIntent = PendingIntent.getActivity(NotificationActivity.this, 10, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                    }

                    builder.setContentIntent(pendingIntent);

                    PendingIntent addActionIntent;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        addActionIntent = PendingIntent.getBroadcast(NotificationActivity.this, 20,
                                new Intent(NotificationActivity.this, MyReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_IMMUTABLE);

                    }else {
                        addActionIntent = PendingIntent.getBroadcast(NotificationActivity.this, 20,
                                new Intent(NotificationActivity.this, MyReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
                    }

                    builder.clearActions();
                    builder.addAction(new NotificationCompat.Action.Builder(android.R.drawable.ic_menu_share,"알람 해제", addActionIntent).build());

                    manager.notify(1000, builder.build());
                    showDatabase();
                }
            }, 0, 0, false);
            timePickerDialog.show();
        }
        else if(item.getItemId() == android.R.id.home){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class CustomAdapter extends ArrayAdapter<String> {
        private ArrayList<String> items;

        public CustomAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
            super(context, textViewResourceId, objects);
            this.items = objects;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.notify_item, null);
            }

            TextView textView = (TextView)v.findViewById(R.id.textView);
            textView.setText(items.get(position));

            final String text = items.get(position);

            return v;
        }
    }
}