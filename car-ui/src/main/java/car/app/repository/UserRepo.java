package car.app.repository;

import car.app.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends CrudRepository<User, String> {

    public List<UserNameAndFullName> findByUserNameContainingIgnoreCaseOrFullNameContainingIgnoreCase(String userName, String fullName);

    interface UserNameAndFullName {
        String getUserName();
        String getFullName();
    }

}
