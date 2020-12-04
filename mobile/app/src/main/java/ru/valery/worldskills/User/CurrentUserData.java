package ru.valery.worldskills.User;

//Тип данных для хранения текущей информации о пользователе

public class CurrentUserData {
    private static String token;
    private static String name;

    public static String getToken() {
        return token;
    }

    public static void setToken(String token1) {
        token = token1;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name1) {
        name = name1;
    }
}

