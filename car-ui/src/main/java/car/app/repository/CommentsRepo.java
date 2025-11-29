package car.app.repository;

import car.app.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentsRepo extends JpaRepository<Comment, Comment.CID> {

    @Query("select c.postid, count(*) from APP_COMMENTS c where c.postid in :ids group by postid")
    List<?> countWithPostidIn(@Param("ids") List<String> ids);
}