package car.app.service.api;

import car.app.models.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public abstract class CollabService extends Observable {

    @Autowired
    AuthorizationService AS;

    @Autowired
    UserServiceUtil DS;

    enum EventType {
        NEW_COMMENT
    }

    @PostConstruct
    public void _init() {

    }

    public List<Comment> getComments(String id) throws Exception {
        return _getComments(id);
    }

    public Map<String, Integer> getNumCommentsFor(List<String> ids) throws Exception {
        return _getNumCommentsFor(ids);
    }

    protected abstract Map<String, Integer> _getNumCommentsFor(List<String> ids);

    public Comment comment(String id, String comment) throws Exception {
        AS.checkCommentPermission(id);
        notifyPreEvent(EventType.NEW_COMMENT, EventData.of("docid", id, "comment", comment));
        Comment c = _comment(id, comment);
        notifyPostEvent(EventType.NEW_COMMENT, EventData.of("docid", id, "comment", comment));
        return c;
    }

    // id's made up of three parts
    public void deleteComment(String id, String owner, long time) throws Exception {
        AS.checkCommentDeletePermission(id, owner);
        _deleteComment(id, owner, time);
    }


    protected abstract List<Comment> _getComments(String id) throws Exception;
    protected abstract Comment _comment(String id, String comment) throws Exception;
    protected abstract void _deleteComment(String id, String owner, long time) throws Exception;

}
