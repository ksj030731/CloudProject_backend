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
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // 긴 텍스트 저장을 위해 TEXT 타입 지정
    @Column(columnDefinition = "TEXT")
    private String description;

    private Double distance;

    private String duration;

    private String difficulty;

    private String region;

    // 이미지 URL은 길이가 길 수 있으므로 넉넉하게 설정 (기본 255자는 부족할 수 있음)
    @Column(length = 2000)
    private String image;

    private String transportation;

    // 좌표 정보
    private Double latitude;
    private Double longitude;

    @Column(name = "completed_count")
    private Integer completedCount;

    // 편의시설 (값 타입 임베디드)
    @Embedded
    private Facilities facilities;

    // 하이라이트 (별도 테이블로 저장)
    @ElementCollection
    @CollectionTable(name = "course_highlights", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "highlight")
    private List<String> highlights = new ArrayList<>();

    // 구간 정보 (1:N 관계)
    // mappedBy = "course": Section 클래스의 'course' 필드가 주인임
    // CascadeType.ALL: 코스를 저장/삭제하면 구간들도 같이 저장/삭제됨
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    // 연관관계 편의 메서드
    // 코스에 섹션을 추가할 때, 섹션 객체에도 코스 정보를 자동으로 넣어줌 (실수 방지)
    public void addSection(Section section) {
        this.sections.add(section);
        section.setCourse(this);
    }

    @Builder
    public Course(String name, String description, Double distance, String duration,
                  String difficulty, String region, String image, String transportation,
                  Double latitude, Double longitude, Integer completedCount,
                  Facilities facilities, List<String> highlights) {
        this.name = name;
        this.description = description;
        this.distance = distance;
        this.duration = duration;
        this.difficulty = difficulty;
        this.region = region;
        this.image = image;
        this.transportation = transportation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.completedCount = completedCount;
        this.facilities = facilities;
        // null이 들어올 경우를 대비해 안전하게 처리
        if (highlights != null) {
            this.highlights = highlights;
        }
    }
}