package kr.co.baek.wisethan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button chatButton, notificationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatButton = findViewById(R.id.chatButton);
        notificationButton = findViewById(R.id.notificationButton);

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences= getSharedPreferences("sign", MODE_PRIVATE);
                String id = sharedPreferences.getString("id","");
                Intent intent;
                if(id.equals("")){
                    intent = new Intent(MainActivity.this, SignUpActivity.class);
                    finish();
                } else{
                    intent = new Intent(MainActivity.this, ChatActivity.class);
                }
                startActivity(intent);
            }
        });

        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences= getSharedPreferences("sign", MODE_PRIVATE);
                String id = sharedPreferences.getString("id","");
                Intent intent;
                if(id.equals("")){
                    intent = new Intent(MainActivity.this, SignUpActivity.class);
                    finish();
                } else{
                    intent = new Intent(MainActivity.this, NotificationActivity.class);
                }
                startActivity(intent);
            }
        });
    }
}