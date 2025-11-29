package car.app.models;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

@Entity(name = "APP_COMMENTS")
@IdClass(Comment.CID.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Comment {

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    @Setter
    public static class CID implements Serializable {
        String postid;
        String userid;
        Long itime;
    }

    @Id
    String postid;
    @Id
    String userid;
    @Id
    Long itime;

    public String txt;

}
