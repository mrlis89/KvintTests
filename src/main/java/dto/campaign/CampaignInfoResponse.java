package dto.campaign;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CampaignInfoResponse {
    private int id;
    private String name;
    private String status;
    private String groupId;
    private int calls;
    private int progress;
}
