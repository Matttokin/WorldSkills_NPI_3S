package ru.valery.worldskills;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.valery.worldskills.TypeData.UserData;
import ru.valery.worldskills.User.CurrentUserData;

//Экран авторизации

public class LoginActivity extends AppCompatActivity {

    private LinearLayout progressBar;

    private TextInputLayout textMaterial_login;
    private TextInputEditText editMaterial_login;
    private TextInputLayout textMaterial_password;
    private TextInputEditText editMaterial_password;

    private Button button;
    private Button button_server;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        //Инициализация компонентов и установка первичных данных, если это требуется
        initComponents();
    }

    //Инициализация компонентов
    private void initComponents() {
        progressBar = findViewById(R.id.progressLayout);
        progressBar.setVisibility(View.GONE);

        textMaterial_login = findViewById(R.id.textMaterial_login);
        editMaterial_login = findViewById(R.id.editMaterial_login);

        textMaterial_password = findViewById(R.id.textMaterial_password);
        editMaterial_password = findViewById(R.id.editMaterial_password);

        button = findViewById(R.id.button_delivered);
        //Отправляем по нажатию на кнопку запрос авторизации пользователя на сервер
        button.setOnClickListener(v -> sendRequest_log_in());

        button_server = findViewById(R.id.button_server);
        //Открываем активити для настройки сервера
        button_server.setOnClickListener(v -> startActivity(new Intent(this, ServerActivity.class)));
    }

    //Отправляем запрос авторизации пользователя на сервер
    private void sendRequest_log_in() {
        progressBar.setVisibility(View.VISIBLE);
        Applications.getApi().log_in(editMaterial_login.getText().toString(), editMaterial_password.getText().toString()).enqueue(new Callback<UserData>() {

            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {

                if (response.isSuccessful())
                    if (response.body() != null) {
                        Toast.makeText(getApplicationContext(), R.string.succses_login_in_application, Toast.LENGTH_LONG).show();
                        CurrentUserData.setName(response.body().getFIO());
                        CurrentUserData.setToken(response.body().getToken());
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), R.string.user_not_found, Toast.LENGTH_LONG).show();
                    }
                else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), R.string.failure_login, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), R.string.failure, Toast.LENGTH_LONG).show();
            }
        });
    }

}