package craft.urlshortnerservice.controller;

import craft.urlshortnerservice.model.CustomUrl;
import craft.urlshortnerservice.model.ShortUrlDetails;
import craft.urlshortnerservice.model.LongUrl;
import craft.urlshortnerservice.service.UrlShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
public class UrlShortenerController {

    @Autowired
    private final UrlShortenerService urlShortenerService;

    public UrlShortenerController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @PostMapping("/generate")
    public ResponseEntity generateShortUrl(@RequestBody LongUrl request) {
        try {
            String shortUrl = urlShortenerService.convertToShortUrl(request);
            ShortUrlDetails details = ShortUrlDetails.builder().shortUrl(shortUrl).longUrl(request.getUrl()).build();
            return ResponseEntity.ok().body(getResponseData("Success",details));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(getResponseData("Failed",e.getMessage()));
        }
    }

    @PostMapping("/generate/bulk/")
    public ResponseEntity generateShortUrls(@RequestBody List<LongUrl> longUrlList) {
        try{
            List<ShortUrlDetails> detailsList = longUrlList.stream()
                    .map(url -> ShortUrlDetails.builder().shortUrl(urlShortenerService.convertToShortUrl(url)).longUrl(url.getUrl()).build())
                    .collect(Collectors.toList());
            return ResponseEntity.ok().body(getResponseData("Success",detailsList));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(getResponseData("Failed",e.getMessage()));
        }
    }

    @GetMapping("/short/{shortUrl}")
    public ResponseEntity<String> redirect(@PathVariable String shortUrl) {
        try {
            String longUrl = urlShortenerService.getLongUrl(shortUrl);
            return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT)
                    .location(URI.create(longUrl))
                    .build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/remove/{shortUrl}")
    public ResponseEntity<String> deleteShortUrl(@PathVariable String shortUrl) {
        try {
            urlShortenerService.removeShortUrlFromEntry(shortUrl);
            return ResponseEntity.status(HttpStatus.OK).body("URL removed");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/generate/custom")
    public ResponseEntity<String> getCustomUrl(@RequestBody CustomUrl customUrl){
        try {
            urlShortenerService.createCustomUrl(customUrl);
            return ResponseEntity.status(HttpStatus.OK).body("URL created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    private static Object getResponseData(String status, Object data){
        Map<String,Object> response = new HashMap<>();
        response.put("status", status);
        response.put("data", data);
        return response;
    }

}
