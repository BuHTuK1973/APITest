package pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourierPOJO {
    private String login;
    private String password;
    private String firstName;

    public static CourierPOJO getRandom() {
        String login = RandomStringUtils.randomAlphanumeric(8);
        String password = RandomStringUtils.randomAlphanumeric(8);
        String firstName = RandomStringUtils.randomAlphanumeric(8);
        return new CourierPOJO(login, password, firstName);
    }
}
