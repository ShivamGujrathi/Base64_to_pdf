package com.example.base64_to_pdf;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private Button btnSelect, decode;
    private TextView textView;
    private  int REQ_PDF = 100;
    private  String encodedPDF;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textview);
        btnSelect = findViewById(R.id.select_pdf);
        decode = findViewById(R.id.decode);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("application/pdf");
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(chooseFile, REQ_PDF);
            }
        });
        decode.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View view) {
                try {
                    ContentResolver resolver=getContentResolver();
                    ContentValues contentValues=new ContentValues();
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,"New pdf"+".pdf");
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"application/pdf");
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_DOWNLOADS+File.separator+"TestFolder1");
                    Uri pdfuri=resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI,contentValues);
                    FileOutputStream os = (FileOutputStream) resolver.openOutputStream(Objects.requireNonNull(pdfuri));
                    byte[] pdfAsBytes = Base64.decode(encodedPDF, 0);
                    os.write(pdfAsBytes);
                    os.flush();
                    os.close();
                    textView.setText("PDF Decoded And Saved");
                } catch (IOException e) {
                    Log.d("tag", "File.toByteArray() error");
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PDF && resultCode == RESULT_OK && data != null) {
            Uri path = data.getData();
            try {
                InputStream inputStream = MainActivity.this.getContentResolver().openInputStream(path);
                byte[] pdfInBytes = new byte[inputStream.available()];
                inputStream.read(pdfInBytes);
                encodedPDF = Base64.encodeToString(pdfInBytes, Base64.DEFAULT);
                Toast.makeText(this, "PDF Encoded", Toast.LENGTH_SHORT).show();
                textView.setText("PDF Encoded");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}