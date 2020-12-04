package ru.valery.worldskills.TypeData;

//Тип данных для приема информации о пользователе после авторизации

public class UserData {
    private String FIO;
    private String Token;

    public String getFIO() {
        return FIO;
    }

    public void setFIO(String FIO) {
        this.FIO = FIO;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }
}