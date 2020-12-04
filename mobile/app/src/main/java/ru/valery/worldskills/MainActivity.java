package ru.valery.worldskills;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.valery.worldskills.Adapter.CourierAdapter;
import ru.valery.worldskills.TypeData.StatusCourierAndOrderData;
import ru.valery.worldskills.User.CurrentUserData;

//Главный экран

public class MainActivity extends AppCompatActivity {

    private TextView textView_name;
    private TextView textView_staus;
    private CardView cardView_order;
    private RecyclerView recycletView_orders;
    private TextView textView_order_address;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button button_delivered;
    private Button button_received;
    private Button button_scan;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //Инициализация компонентов и установка первичных данных, если это требуется
        initComponents();

        //Запрашиваем у сервера данные о статусе и заказе курьера
        sendRequest_get_status_and_order_courier();
    }

    //Инициализация компонентов и установка первичных данных, если это требуется
    private void initComponents(){
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        //При свайпе посылаем запрос на получение статуса курьера и его заказа
        swipeRefreshLayout.setOnRefreshListener(() -> {
            sendRequest_get_status_and_order_courier();
        });

        textView_name = findViewById(R.id.textView_name);
        textView_name.setText(getString(R.string.сourier) + CurrentUserData.getName());

        textView_staus = findViewById(R.id.textView_staus);
        textView_staus.setVisibility(View.GONE);

        cardView_order = findViewById(R.id.cardView_order);
        cardView_order.setVisibility(View.INVISIBLE);

        recycletView_orders = findViewById(R.id.recycletView_orders);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycletView_orders.setLayoutManager(layoutManager);

        textView_order_address = findViewById(R.id.textView_order_address);

        button_delivered = findViewById(R.id.button_delivered);
        //Отправляем по нажатию на кнопку запрос на сервер, который устанавливает для заказа, закрепленного за курьером, статус "Доставлен"
        button_delivered.setOnClickListener(v -> sendRequest_set_status_delivered_order());

        button_received = findViewById(R.id.button_received);
        //Отправляем по нажатию на кнопку запрос на сервер, который устанавливает для заказа, закрепленного за курьером, статус "Получен курьером"
        button_received.setOnClickListener(v -> sendRequest_set_status_received_order());

        button_scan = findViewById(R.id.button_scan);
        //Открываем активити для скана
        button_scan.setOnClickListener(v -> startActivity(new Intent(this, CameraActivity.class)));
    }

    //Отправляем запрос на сервер, который устанавливает для заказа, закрепленного за курьером, статус "Доставлен"
    private void sendRequest_set_status_delivered_order(){
        swipeRefreshLayout.setRefreshing(true);

        Applications.getApi().setFinishOrder(CurrentUserData.getToken()).enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) {
                    if (!response.body().equals("")) {
                        Toast.makeText(getApplicationContext(), R.string.succses, Toast.LENGTH_LONG).show();
                        cardView_order.setVisibility(View.INVISIBLE);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.failure_server, Toast.LENGTH_LONG).show();
                }

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getApplicationContext(), R.string.failure, Toast.LENGTH_LONG).show();
            }
        });
    }

    //Отправляем запрос на сервер, который устанавливает статус заказа, закрепленного за курьером,  "Получен курьером"
    private void sendRequest_set_status_received_order(){
        swipeRefreshLayout.setRefreshing(true);
        Applications.getApi().setReceivedOrder(CurrentUserData.getToken()).enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) {
                    if (!response.body().equals("")) {
                        Toast.makeText(getApplicationContext(), R.string.succses, Toast.LENGTH_LONG).show();
                        button_delivered.setVisibility(View.VISIBLE);
                        button_received.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.failure_server, Toast.LENGTH_LONG).show();
                }

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getApplicationContext(), R.string.failure, Toast.LENGTH_LONG).show();
            }
        });
    }

    //Запрашиваем у сервера данные о статусе и заказе курьера
    private void sendRequest_get_status_and_order_courier(){
        swipeRefreshLayout.setRefreshing(true);
        Applications.getApi().getStatusCourierAndOrder(CurrentUserData.getToken()).enqueue(new Callback<StatusCourierAndOrderData>() {

            @Override
            public void onResponse(Call<StatusCourierAndOrderData> call, Response<StatusCourierAndOrderData> response) {

                if (response.isSuccessful()) {
                    textView_staus.setText(getString(R.string.сourier_status) + ": " + response.body().getStatus());
                    textView_staus.setVisibility(View.VISIBLE);
                    if(response.body().getOrder() != null)
                    {
                        if(response.body().getOrder().getStatus().equals("Ожидает курьера")){
                            button_delivered.setVisibility(View.GONE);
                            button_received.setVisibility(View.VISIBLE);
                        }
                        else{
                            button_delivered.setVisibility(View.VISIBLE);
                            button_received.setVisibility(View.GONE);
                        }

                        textView_order_address.setText(getString(R.string.address) + response.body().getOrder().getAdres());
                        CourierAdapter adapters = new CourierAdapter(getApplicationContext(), response.body().getOrder());
                        recycletView_orders.setAdapter(adapters);
                        cardView_order.setVisibility(View.VISIBLE);
                    }
                    else {
                        cardView_order.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.failure_server, Toast.LENGTH_LONG).show();
                }

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<StatusCourierAndOrderData> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getApplicationContext(), R.string.failure, Toast.LENGTH_LONG).show();
            }
        });
    }

}
