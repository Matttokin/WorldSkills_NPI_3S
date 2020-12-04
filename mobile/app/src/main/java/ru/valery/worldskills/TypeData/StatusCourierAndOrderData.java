package ru.valery.worldskills.TypeData;

//Тип данных для хранения статуса курьера и данных о заказе

public class StatusCourierAndOrderData {
    private String Status;
    private OrderData Order;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        this.Status = status;
    }

    public OrderData getOrder() {
        return Order;
    }

    public void setOrder(OrderData order) {
        this.Order = order;
    }
}
