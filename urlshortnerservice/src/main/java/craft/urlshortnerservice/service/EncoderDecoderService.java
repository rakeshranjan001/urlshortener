package craft.urlshortnerservice.service;

import org.springframework.stereotype.Service;


@Service
public class EncoderDecoderService {
    private static final String allowedString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private char[] allowedCharacters = allowedString.toCharArray();
    private static int base = 62;
    private static int maxLen = UrlConstants.maxLen;
    public String encode(long index){
        StringBuilder encodedString = new StringBuilder();
        if(index == 0) {
            return String.valueOf(allowedCharacters[0]);
        }

        while(index > 0){
            encodedString.insert(0,allowedCharacters[(int) (index % base)]);
            index = index / base;
        }

        return encodedString.toString();
    }

    public long decode(String input){
        char[] characters = input.toCharArray();
        int length = characters.length;

        long decoded = 0;

        long counter = 1;
        for (int i = 0; i < length; i++) {
            decoded += allowedString.indexOf(characters[i]) * Math.pow(base, length - counter);
            counter++;
        }
        return decoded;
    }

    public boolean validateEncodedString(String input) throws IllegalArgumentException{
        int length = input.length();

        if (length > maxLen)
            throw new IllegalArgumentException("Length should not be greater than "+maxLen);

        if(!input.matches("["+allowedString+"]+"))
            throw new IllegalArgumentException("Url should contain only valid characters : "+allowedString);
        return true;
    }
}
