package ru.valery.worldskills.ApiModule;

public class ApiValue {
    //Путь к Api
    public final static String URL_API = "https://885e950c470c.ngrok.io" + "/api/";

    //Путь к методу для авторизации
    public final static String LOG_IN = "getToken";

    //Путь к методу для получения статуса курьера и его заказа
    public final static String GET_STATUS_AND_ORDER = "getStatusAndOrder";

    //Путь к методу установки статуса заказку "Доставлено"
    public final static String FINISH_ORDER = "finishOrder";

    //Путь к методу установки статуса заказку "Получен курьером"
    public final static String RECEIVED_ORDER = "ChangeStatusOrder";


}
