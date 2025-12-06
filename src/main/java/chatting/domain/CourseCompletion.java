package chatting.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "course_id" }) // 유저당 코스별 기록은 하나만 (업데이트 방식)
})
public class CourseCompletion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    private String completionTime; // "02:45:30" (String으로 단순 저장하거나 Time 타입 사용)

    private LocalDate date; // 완주 날짜

    private Integer completionCount; // 완주 횟수

    // ✨ [추가] 해당 코스에서 걸은 총 거리 (누적)
    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    @Builder.Default
    private Double courseTotalDistance = 0.0;

    // ✨ [추가] 완주 정보 업데이트 메서드
    public void updateCompletion() {
        this.completionCount++;
        this.date = LocalDate.now();
    }

    public void addDistance(double distance) {
        if (this.courseTotalDistance == null) {
            this.courseTotalDistance = 0.0;
        }
        this.courseTotalDistance += distance;
    }
}