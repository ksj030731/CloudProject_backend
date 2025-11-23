package chatting.dto;

import chatting.domain.Badge;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BadgeDto {
    private Long id;
    private String name;
    private String description;
    private String icon;
    private String condition; // DB 컬럼은 conditions일 수 있으나 프론트는 condition을 원함
    private String rarity;

    public BadgeDto(Badge entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.icon = entity.getIcon();
        this.condition = entity.getConditions();
        this.rarity = entity.getRarity();
    }
}