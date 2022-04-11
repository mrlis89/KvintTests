package dto.refresh;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshRequest {
    private final String refresh_token;
}