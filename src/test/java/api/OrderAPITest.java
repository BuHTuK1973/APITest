package api;

import pojo.OrderPOJO;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class OrderAPITest {
    private int actualTrack;

    private OrderAPI orderAPI;

    private final String[] colorsList;

    public OrderAPITest(String[] colorsList) {
        this.colorsList = colorsList;
    }

    @Parameterized.Parameters(name = "Тестовые данные: Предпочитаемые цвета")
    public static Collection<String[][]> colorsData() {
        return Arrays.asList(new String[][][]{
                {{"BlACK", "GREY"}},
                {{"BlACK"}},
                {{"GREY"}},
                {{}}
        });
    }

    @Test
    @DisplayName("Проверка создания заказа с разным количеством предпочитаемых цветов") // имя теста
    public void createOrderSuccessRegardlessOfColor() {
        orderAPI = new OrderAPI();
        OrderPOJO order = OrderPOJO.getDefault();
        order.setColor(Arrays.asList(colorsList));
        Response response = orderAPI.sendPostRequestCreateOrder(order);
        actualTrack = orderAPI.responseCreatedOrderStatus201(response);
        Assert.assertNotEquals("Track заказа должен был быть больше 0, что-то пошло не так", 0, actualTrack);
    }

}
