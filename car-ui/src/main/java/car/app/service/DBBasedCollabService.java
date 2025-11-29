package car.app.service;

import car.app.models.Comment;
import car.app.repository.CommentsRepo;
import car.app.service.api.CollabService;
import car.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DBBasedCollabService extends CollabService {

    @Autowired
    CommentsRepo CR;

    @Override
    protected Map<String, Integer> _getNumCommentsFor(List<String> ids) {
        return CR.countWithPostidIn(ids).stream().collect(Collectors.toMap(x -> (String)((Object[])x)[0], x -> ((Long)((Object[])x)[1]).intValue()));
    }

    @Override
    protected List<Comment> _getComments(String id) throws Exception {
        return CR.findAll(Example.of(Comment.builder().postid(id).build()));
    }

    @Override
    protected Comment _comment(String id, String comment) throws Exception {
        Comment c = Comment.builder().postid(id).userid(SecurityUtil.getCurrentUser()).itime(System.currentTimeMillis()).txt(comment).build();
        CR.save(c);
        return c;
    }

    @Override
    protected void _deleteComment(String id, String owner, long time) throws Exception {
        CR.deleteById(Comment.CID.builder().postid(id).userid(owner).itime(time).build());
    }

}
