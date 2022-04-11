package dto.authenticate;
import lombok.*;

@Data
@AllArgsConstructor
public class AuthRequest {
    private String login;
    private String password;
}