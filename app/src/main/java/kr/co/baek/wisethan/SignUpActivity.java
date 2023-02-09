package kr.co.baek.wisethan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {
    private EditText idEditText;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        idEditText = findViewById(R.id.idEditText);
        confirmButton = findViewById(R.id.confirmButton);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(idEditText.getText().toString().isEmpty()){
                    Toast.makeText(SignUpActivity.this, "ID를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else{
                    SharedPreferences sharedPreferences= getSharedPreferences("sign", MODE_PRIVATE);
                    SharedPreferences.Editor editor= sharedPreferences.edit();
                    editor.putString("id", idEditText.getText().toString());
                    editor.apply();
                    Toast.makeText(SignUpActivity.this, idEditText.getText().toString()+"님 환영합니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}