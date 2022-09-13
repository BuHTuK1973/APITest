package api;

import io.qameta.allure.junit4.DisplayName;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class OrderGetListTest {

    private OrderAPI orderAPI;
    private List<Object[]> orderList;

    @Before
    public void setup() {
        orderAPI = new OrderAPI();
    }

    @Test
    @DisplayName("Получить список заказов")
    public void getOrderList() {
        orderList = orderAPI.sendGetRequestGetOrderList();
        Assert.assertNotNull(orderList);
    }
}

