package pojo;

import lombok.Data;

@Data
public class CourierCredentials {
    private String login;
    private String password;

    public CourierCredentials(CourierPOJO courierPOJO) {
        this.login = courierPOJO.getLogin();
        this.password = courierPOJO.getPassword();
    }

    public CourierCredentials(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public static CourierCredentials from(CourierPOJO courierPOJO) {
        return new CourierCredentials(courierPOJO);
    }

    public static CourierCredentials withoutLogin(CourierPOJO courierPOJO) {
        return new CourierCredentials(courierPOJO.getLogin(), "");
    }

    public static CourierCredentials withoutPassword(CourierPOJO courierPOJO) {
        return new CourierCredentials("", courierPOJO.getPassword());
    }

}
