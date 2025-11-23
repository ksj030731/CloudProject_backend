package chatting.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Badge {
    @Id
    // 뱃지 ID는 MockData의 ID(1, 2, 13, 14...)를 그대로 쓰기 위해 Auto Increment를 끕니다.
    // 필요하면 @GeneratedValue 쓰셔도 됩니다.
    private Long id;

    private String name;
    private String description;
    private String icon; // "🥾", "🏆" 같은 이모지
    private String conditions; // "condition"은 예약어일 수 있어서 s 붙임
    private String rarity; // "common", "rare", "legendary"
}