package ru.valery.worldskills.TypeData;

//Тип данных для хранения номенклатуры заказа

public class NomenclatureData {
    private String Name;
    private Integer CountInOrder;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public Integer getCount() {
        return CountInOrder;
    }

    public void setCount(Integer count) {
        this.CountInOrder = count;
    }
}