package car.app.repository;

import car.app.models.AuthTok;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthTokRepo extends CrudRepository<AuthTok, AuthTok.AuthTokId> {

}
