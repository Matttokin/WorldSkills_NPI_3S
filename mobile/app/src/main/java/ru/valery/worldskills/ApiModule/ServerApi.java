package ru.valery.worldskills.ApiModule;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.valery.worldskills.TypeData.StatusCourierAndOrderData;
import ru.valery.worldskills.TypeData.UserData;


public interface ServerApi {
    //Метод для авторизации
    @POST(ApiValue.LOG_IN)
    Call<UserData> log_in(@Query("login") String login,
                          @Query("password") String password);

    //Метод для получения статуса курьера и его заказа
    @POST(ApiValue.GET_STATUS_AND_ORDER)
    Call<StatusCourierAndOrderData> getStatusCourierAndOrder(@Query("token") String token);

    //Метод установки статуса заказку "Доставлено"
    @POST(ApiValue.FINISH_ORDER)
    Call<String> setFinishOrder(@Query("token") String token);

    //Метод установки статуса заказку "Получен курьером"
    @POST(ApiValue.RECEIVED_ORDER)
    Call<String> setReceivedOrder(@Query("token") String token);

}
