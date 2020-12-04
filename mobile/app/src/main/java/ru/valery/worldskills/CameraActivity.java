package ru.valery.worldskills;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.datamatrix.DataMatrixReader;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;
import com.google.zxing.oned.CodaBarWriter;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

import ru.valery.worldskills.TypeData.DataQrCode;


public class CameraActivity extends AppCompatActivity {

    private static final int CAMERA_CODE = 1;

    private ImageView imageView_photo;
    private ImageView imageView_barcode;
    private TextView textView_date_recive_order;
    private Button button_yes;
    private Button button_no;
    private Button button_confirm_recive;
    private Button button_chanel_added_p;
    private CardView cardView_photo;
    private CardView cardView_refresh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);

        initComponents();

        //Запускаем активити для снятия фото
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_CODE);
    }

    //Инициализация компонентов
    private void initComponents() {
        imageView_photo = findViewById(R.id.imageView_photo);
        imageView_barcode = findViewById(R.id.imageView_barcode);

        textView_date_recive_order = findViewById(R.id.textView_date_recive_order);

        cardView_photo = findViewById(R.id.cardView_photo);
        cardView_photo.setVisibility(View.GONE);

        cardView_refresh = findViewById(R.id.cardView_refresh);
        cardView_refresh.setVisibility(View.GONE);

        button_yes = findViewById(R.id.button_yes);
        button_yes.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_CODE);
        });


        button_confirm_recive = findViewById(R.id.button_confirm_recive);
        button_confirm_recive.setOnClickListener(view -> {

        });

        button_chanel_added_p = findViewById(R.id.button_chanel_added_p);
        button_chanel_added_p.setOnClickListener(view -> {
            finish();
        });


        button_no = findViewById(R.id.button_no);
        button_no.setOnClickListener(view -> finish());

    }

    //Декодирование QR кода
    public Boolean readQRImage(Bitmap bMap) {
        String contents = "null";

        int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];

        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new QRCodeReader();
        try {
            Result result = reader.decode(bitmap);
            contents = result.getText();
            String date = setDateText(contents);
            generateQRCode_general(date, imageView_barcode);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //Устанавливаем данные даты
    private String setDateText(String json){
        json = json.substring(1,json.length()-1).replace("\\\"","\"");;
        String date = "Нет данных";
        try {
            JsonObject jobjQrData = new Gson().fromJson(json, JsonObject.class);
            String result = jobjQrData.get("shipping_date").toString();
            textView_date_recive_order.setText("Дата получения: " + result);
            date = result;
        }
        catch (Exception e){
            textView_date_recive_order.setText("Дата получения: Не обнаружено");
        }

        return date;

    }

    //Устанавливаем штрихкод
    private void generateQRCode_general(String data, ImageView img)throws WriterException {
        com.google.zxing.Writer writer = new Code128Writer();
        String finaldata = Uri.encode(data, "utf-8");

        BitMatrix bm = writer.encode(finaldata, BarcodeFormat.CODE_128,150, 150);
        Bitmap ImageBitmap = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < 150; i++) {//width
            for (int j = 0; j < 150; j++) {//height
                ImageBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK: Color.WHITE);
            }
        }

        if (ImageBitmap != null) {
            imageView_barcode.setImageBitmap(ImageBitmap);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CODE & resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageView_photo.setImageBitmap(bitmap);
            if(readQRImage(bitmap)){
                cardView_photo.setVisibility(View.VISIBLE);
                cardView_refresh.setVisibility(View.GONE);
            }
            else {
                cardView_photo.setVisibility(View.GONE);
                cardView_refresh.setVisibility(View.VISIBLE);
            }
        }
        else {
            cardView_photo.setVisibility(View.GONE);
            cardView_refresh.setVisibility(View.VISIBLE);
        }

    }
}
