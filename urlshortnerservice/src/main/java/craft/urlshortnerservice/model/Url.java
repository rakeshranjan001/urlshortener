package craft.urlshortnerservice.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "url")
public class Url {
    @Id
    private long id;

    @Column(nullable = false)
    private String longUrl = "";

    @Column(nullable = false)
    private Date createdAt;

    private Date expiresAt;
}
