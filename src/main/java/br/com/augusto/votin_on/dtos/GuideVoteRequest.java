package br.com.augusto.votin_on.dtos;

import br.com.augusto.votin_on.enuns.VoteEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GuideVoteRequest {
  @NotBlank
  @CPF
  private String cpf;

  @NotNull
  private Long guideId;

  @NotNull
  private VoteEnum vote;
}
