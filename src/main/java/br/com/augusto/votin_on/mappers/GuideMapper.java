package br.com.augusto.votin_on.mappers;

import br.com.augusto.votin_on.dtos.GuideCreateRequest;
import br.com.augusto.votin_on.dtos.GuideCreateResponse;
import br.com.augusto.votin_on.entity.Guide;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GuideMapper {
    public static Guide toEntity(GuideCreateRequest request){
        return Guide.builder()
        		.title(request.getTitle())
                .shared(false)
        		.build();
    }

    public static GuideCreateResponse toCreateResponse(Guide entity){
        return GuideCreateResponse.builder()
        		.id(entity.getId())
        		.title(entity.getTitle())
        		.build();
    }
}
