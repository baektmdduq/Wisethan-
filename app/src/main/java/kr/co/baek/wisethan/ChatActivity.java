package kr.co.baek.wisethan;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    private EditText chatText;
    private Button sendButton, selectImageButton, galleryButton, cameraButton;
    private String id, sImage;
    private DatabaseReference myRef;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<ChatData> chatList;
    private LinearLayout linearContainer, buttonLayout;
    private boolean imageButtonClick = false;
    private ActivityResultLauncher<Intent> resultLauncher, activityResultPicture;
    private Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        SharedPreferences sharedPreferences= getSharedPreferences("sign", MODE_PRIVATE);
        id = sharedPreferences.getString("id","");

        chatText = findViewById(R.id.chatText);
        sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = chatText.getText().toString();
                if (msg != null) {
                    ChatData chat = new ChatData();
                    chat.setName(id);
                    chat.setMsg(msg);

                    Date today = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm a", Locale.US);
                    chat.setTime(format.format(today));

                    if (sImage != null) {
                        chat.setImageResId(sImage);
                    }
                    myRef.push().setValue(chat);

                    chatText.setText("");
                }
                sImage = null;
            }
        });

        //????????????????????? ????????? ??????
        recyclerView = findViewById(R.id.recyclerView);
        //    recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);

        chatList = new ArrayList<>();
        adapter = new ChatAdapter(chatList, id);
        recyclerView.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("message");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatData chat = snapshot.getValue(ChatData.class);
                ((ChatAdapter) adapter).addChat(chat);
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        buttonLayout = findViewById(R.id.buttonLayout);
        linearContainer = findViewById(R.id.linearContainer);
        selectImageButton = findViewById(R.id.selectImageButton);
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!imageButtonClick) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                            (ViewGroup.LayoutParams.MATCH_PARENT, ConvertDPtoPX(ChatActivity.this, 100));
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    buttonLayout.setLayoutParams(params);
                    imageButtonClick = true;
                } else {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                            (ViewGroup.LayoutParams.MATCH_PARENT, ConvertDPtoPX(ChatActivity.this, 50));
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    buttonLayout.setLayoutParams(params);
                    imageButtonClick = false;
                }
            }
        });

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.get_image, linearContainer, true);

        galleryButton = view.findViewById(R.id.galleryButton);
        galleryButton.setOnClickListener(view1 -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            intent.setAction(Intent.ACTION_PICK);
            resultLauncher.launch(intent);
        });

        checkSelfPermission();
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == RESULT_OK) {
                            Intent intent = result.getData();
                            Uri uri = intent.getData();
                            if (result.getData() != null) {
                                uri = result.getData().getData();
                            }
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                                reSizeBitMap(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        cameraButton = view.findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goCamera();
            }
        });

        activityResultPicture = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try {
                            if (image_uri != null) {
                                try {
                                    //TODO [????????? ???????????? ????????? ?????? ??????]
                                    InputStream is = ChatActivity.this.getContentResolver().openInputStream(image_uri);
                                    int size = is.read();

                                    //TODO [MediaStore ??? ?????? JPEG ????????? ?????? ??????]
                                    //    saveFile(getNowTime24(), imageView);

                                    if (result.getResultCode() == RESULT_OK) {
                                        try {
                                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(ChatActivity.this.getContentResolver(), image_uri);
                                            reSizeBitMap(bitmap);
                                            bitmap.recycle();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    //????????? ?????? ????????? ????????? ???????????? ??????
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //????????? ?????? ?????? ??????
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            int length = permissions.length;
            for (int i = 0; i < length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MainActivity", "?????? ?????? : " + permissions[i]);
                }
            }
        }
    }

    public void checkSelfPermission() {
        String temp = "";

        //?????? ?????? ?????? ??????
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.READ_EXTERNAL_STORAGE + " ";
        }
        //?????? ?????? ?????? ??????
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.WRITE_EXTERNAL_STORAGE + " ";
        }
        if (TextUtils.isEmpty(temp) == false) {
            // ?????? ??????
            ActivityCompat.requestPermissions(this, temp.trim().split(" "),1);
        }
    }

    public static int ConvertDPtoPX(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    // ???????????? ????????? ????????? ???????????????
    public static String byteArrayToBinaryString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; ++i) {
            sb.append(byteToBinaryString(b[i]));
        }
        return sb.toString();
    }

    // ???????????? ???????????? ???????????????
    public static String byteToBinaryString(byte n) {
        StringBuilder sb = new StringBuilder("00000000");
        for (int bit = 0; bit < 8; bit++) {
            if (((n >> bit) & 1) > 0) {
                sb.setCharAt(7 - bit, '1');
            }
        }
        return sb.toString();
    }

    public void goCamera(){
        try {
            ContentValues values = new ContentValues(); //TODO [????????? Picture ????????? ????????? ????????????]
            values.put(MediaStore.Images.Media.TITLE, getNowTime24()); //TODO [?????? ??????]
            values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera"); //TODO [??????]

            image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
            //    startActivity(cameraIntent); //TODO [?????? ????????? ??????]
            activityResultPicture.launch(cameraIntent);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //TODO [?????? [6] : ?????? ?????? ?????? ?????? ????????? ??????]
    public static String getNowTime24() {
        long time = System.currentTimeMillis();
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyyMMddkkmmss", Locale.US);
        String str = dayTime.format(new Date(time));
        return "CM"+str; //TODO [CM??? ????????? ??????]
    }

    public void reSizeBitMap(Bitmap bitmap){
        //TODO [????????? ???????????? ??????]
        int width = 1300; // [???????????? ??????]
        int height = 1300; //[???????????? ??????]
        float bmpWidth = bitmap.getWidth();
        float bmpHeight = bitmap.getHeight();

        if (bmpWidth > width) {
            // [????????? ???????????? ??? ????????? ??????]
            float mWidth = bmpWidth / 100;
            float scale = width/ mWidth;
            bmpWidth *= (scale / 100);
            bmpHeight *= (scale / 100);
        } else if (bmpHeight > height) {
            // [????????? ???????????? ??? ????????? ??????]
            float mHeight = bmpHeight / 100;
            float scale = height/ mHeight;
            bmpWidth *= (scale / 100);
            bmpHeight *= (scale / 100);
        }

        //TODO [??????????????? ?????? ??????, ?????? ??????]
        String fileName = getNowTime24();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName+".JPG");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.IS_PENDING, 1);
        }
        ContentResolver contentResolver = getContentResolver();
        Uri item = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //TODO [?????? ?????? ??????]
        ParcelFileDescriptor pdf = null;
        try {
            pdf = contentResolver.openFileDescriptor(item, "w", null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (pdf == null) {
            Log.d("","\n"+"[A_Camera > saveFile() ????????? : MediaStore ?????? ?????? ??????]");
            Log.d("","\n"+"[?????? : "+String.valueOf("ParcelFileDescriptor ?????? null")+"]");
        }

        //TODO [???????????? ??? ????????? ???????????? ?????????]
        Matrix matrix = new Matrix();
        matrix.setRotate(90);

        Bitmap resizedBmp = Bitmap.createScaledBitmap(bitmap, (int) bmpWidth, (int) bmpHeight, true); //TODO [????????? ?????? ??????]
        //     Bitmap resizedBmp = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, true); //TODO [????????? ????????? ??????]

        Bitmap rotateBmp = Bitmap.createBitmap(resizedBmp, 0, 0, resizedBmp.getWidth(), resizedBmp.getHeight(),
                matrix, false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // ??????

        rotateBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos); //resizedBmp
        byte[] imageInByte = baos.toByteArray();

        //TODO [???????????? ???????????? ??????]
        FileOutputStream outputStream = new FileOutputStream(pdf.getFileDescriptor());
        try {
            outputStream.write(imageInByte);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //TODO [?????? ?????? ??????]
        //[????????? : ????????? ?????? ??????]
        S_Preference.setString(getApplication(), "saveCameraScopeContent", String.valueOf(item));

        //[?????? : ????????? ?????? ??????]
        Cursor c = getContentResolver().query(Uri.parse(String.valueOf(item)), null,null,null,null);
        c.moveToNext();
        String absolutePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA)+1);
        S_Preference.setString(getApplication(), "saveCameraScopeAbsolute", absolutePath);

        Log.d("","\n"+"[A_Camera > saveFile() ????????? : MediaStore ?????? ?????? ??????]");
        Log.d("","\n"+"[????????? ?????? ?????? : "+S_Preference.getString(getApplication(), "saveCameraScopeContent")+"]");
        Log.d("","\n"+"[?????? ?????? ?????? : "+S_Preference.getString(getApplication(), "saveCameraScopeAbsolute")+"]");

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        sImage = byteArrayToBinaryString(imageInByte);
        sendButton.performClick();

        resizedBmp.recycle();
        rotateBmp.recycle();
    }
}