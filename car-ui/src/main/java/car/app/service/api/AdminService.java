package car.app.service.api;

import car.app.CarAppConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    UserServiceUtil DS;

    @Autowired
    CarAppConfiguration conf;

    public void backup() throws Exception {

    }

    public void restore() throws Exception {

    }
}
