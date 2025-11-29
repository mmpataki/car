package car.app.service;

import car.app.models.AuthTok;
import car.app.repository.AuthTokRepo;
import car.app.service.api.UnAuthorizedException;
import car.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class AuthTokService {

    @Autowired
    AuthTokRepo AR;

    Random R = new Random();

    public AuthTok getNewAuthTok() throws UnAuthorizedException {
        String usr = SecurityUtil.getCurrentUser();
        if(usr == null || usr.isEmpty()) {
            throw new UnAuthorizedException("login first");
        }
        AuthTok tok = AuthTok.builder().uId(usr).tok(makeNewTok()).build();
        AR.save(tok);
        return tok;
    }

    private String makeNewTok() {
        return R.nextLong() + "" + System.currentTimeMillis() + "-" + R.nextDouble();
    }

    public boolean verify(String usr, String tok) {
        AuthTok result = AR.findById(AuthTok.AuthTokId.builder().uId(usr).tok(tok).build()).orElse(null);
        if(result != null) {
            AR.delete(result);
        }
        return result != null;
    }

}
