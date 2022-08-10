package craft.urlshortnerservice.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShortUrlDetails {
    private String shortUrl;
    private String longUrl;
}
