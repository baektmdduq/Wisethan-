package kr.co.baek.wisethan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "알림 해제 결과창", Toast.LENGTH_SHORT).show();
    }
}
