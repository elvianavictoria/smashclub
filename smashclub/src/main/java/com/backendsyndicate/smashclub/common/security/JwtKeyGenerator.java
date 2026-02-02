// Untuk generate secret key yang aman
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import javax.crypto.SecretKey;

public class JwtKeyGenerator {
    public static void main(String[] args) {
        // Generate random 256-bit (32-byte) secret key
        SecretKey key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);

        // Convert to Base64 string
        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());

        System.out.println("Generated JWT Secret Key:");
        System.out.println("jwt.secret-key: " + base64Key);
        System.out.println("\nCopy this to your application.yml file");
    }
}