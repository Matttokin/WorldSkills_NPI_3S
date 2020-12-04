package ru.valery.worldskills.TypeData;

//Тип данных для хранения информации о заказе

import java.util.ArrayList;

public class OrderData {
    private String Adres;
    private String Status;
    private ArrayList<NomenclatureData> Nom;

    public String getAdres() {
        return Adres;
    }

    public void setAdres(String adres) {
        this.Adres = adres;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public ArrayList<NomenclatureData> getNom()
    {
        if(Nom == null) Nom = new ArrayList<>();
        return Nom;
    }

    public void setNom(ArrayList<NomenclatureData> nom) {
        this.Nom = nom;
    }
}