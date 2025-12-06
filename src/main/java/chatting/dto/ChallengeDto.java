package chatting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeDto {
    private Long id;
    private String title;
    private String description;
    private double target; // 목표치 (km일 수도 있어서 double)
    private double current; // 현재 수치
    private String reward;
    private String category; // 'distance' | 'courses' | 'reviews' | 'social'
    private boolean completed;
}
