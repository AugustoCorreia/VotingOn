package br.com.augusto.votin_on.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ResultResponse {
    private String guideTitle;
    private Long guideId;
    private Long yesVotes;
    private Long noVotes;
}
