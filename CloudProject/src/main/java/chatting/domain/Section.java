package chatting.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "sections")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✨ 중요: 어떤 코스에 속한 구간인지 연결 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    // Mock Data의 "id": "1-1" 같은 값을 저장하고 싶다면 별도 필드로 관리
    @Column(name = "section_code")
    private String sectionCode; // "1-1", "1-2" 등

    @Column(nullable = false)
    private String name; // "1-1구간"

    private Double distance; // 11.5

    private String duration; // "4시간"

    private String difficulty; // "하"

    @Column(name = "start_point")
    private String startPoint; // "임랑해수욕장"

    @Column(name = "end_point")
    private String endPoint; // "기장군청"

    // ✨ Checkpoints (구간 내 체크포인트 리스트)
    // 문자열 배열 ["칠암항", "수산과학연구소"] -> 별도 테이블로 자동 관리
    @ElementCollection
    @CollectionTable(
            name = "section_checkpoints",
            joinColumns = @JoinColumn(name = "section_id")
    )
    @Column(name = "checkpoint_name")
    private List<String> checkpoints = new ArrayList<>();

    @Builder
    public Section(Course course, String sectionCode, String name, Double distance,
                   String duration, String difficulty, String startPoint,
                   String endPoint, List<String> checkpoints) {
        this.course = course;
        this.sectionCode = sectionCode;
        this.name = name;
        this.distance = distance;
        this.duration = duration;
        this.difficulty = difficulty;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.checkpoints = checkpoints;
    }
}