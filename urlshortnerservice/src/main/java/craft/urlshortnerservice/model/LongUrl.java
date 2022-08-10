package craft.urlshortnerservice.model;

import lombok.Data;

import java.util.Date;

@Data
public class LongUrl {
    private String url;
    private Date expiresAt;
}
