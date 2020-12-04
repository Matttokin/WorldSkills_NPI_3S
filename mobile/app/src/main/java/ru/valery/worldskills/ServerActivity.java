package ru.valery.worldskills;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.valery.worldskills.Adapter.CourierAdapter;
import ru.valery.worldskills.TypeData.StatusCourierAndOrderData;
import ru.valery.worldskills.User.CurrentUserData;

//Главный экран

public class ServerActivity extends AppCompatActivity {

    private TextInputEditText editMaterial_server;
    private SharedPreferences mSettings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_activity);
        mSettings = getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE);
        //Инициализация компонентов и установка первичных данных, если это требуется
        initComponents();

    }

    //Инициализация компонентов и установка первичных данных, если это требуется
    private void initComponents(){
        editMaterial_server = findViewById(R.id.editMaterial_server);
        String server = "https://bd508361b787.ngrok.io";
        if (mSettings.contains("server")) {
            server = mSettings.getString("server", "https://bd508361b787.ngrok.io");
        }
        editMaterial_server.setText(server);
        Button button_server_save = findViewById(R.id.button_server_save);
        button_server_save.setOnClickListener(v -> {
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString("server", editMaterial_server.getText().toString());
            editor.apply();
            Applications.setNuewRetro(editMaterial_server.getText().toString());
            finish();
        });
    }



}
