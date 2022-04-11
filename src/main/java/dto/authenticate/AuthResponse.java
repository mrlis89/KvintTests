package dto.authenticate;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthResponse {
    private String access_token;
    private String refresh_token;
}