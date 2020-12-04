package ru.valery.worldskills.TypeData;

//Тип данных для приема информации статусе курьера и данных о заказе

public class CourierData {
    private String Name;
    private String Status;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        this.Status = status;
    }
}
