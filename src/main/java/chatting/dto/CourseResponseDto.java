package chatting.dto;

import chatting.domain.Course;
import chatting.domain.Facilities;
import chatting.domain.Section;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class CourseResponseDto {
    private Long id;
    private String name;
    private String description;
    private Double distance;
    private String duration;
    private String difficulty;
    private String region;
    private String image;
    private String transportation;
    private Double latitude;
    private Double longitude;
    private Integer completedCount;
    private Facilities facilities;
    private List<String> highlights;
    private List<SectionDto> sections; // 섹션 정보도 DTO로 변환해야 함

    // Entity -> DTO 변환 생성자
    public CourseResponseDto(Course course) {
        this.id = course.getId();
        this.name = course.getName();
        this.description = course.getDescription();
        this.distance = course.getDistance();
        this.duration = course.getDuration();
        this.difficulty = course.getDifficulty();
        this.region = course.getRegion();
        this.image = course.getImage();
        this.transportation = course.getTransportation();
        this.latitude = course.getLatitude();
        this.longitude = course.getLongitude();
        this.completedCount = course.getCompletedCount();
        this.facilities = course.getFacilities();
        this.highlights = course.getHighlights();

        // 섹션 리스트를 SectionDto 리스트로 변환 (중요!)
        this.sections = course.getSections().stream()
                .map(SectionDto::new)
                .collect(Collectors.toList());
    }

    // 내부 클래스로 SectionDto 정의 (간단하게)
    @Getter
    @NoArgsConstructor
    public static class SectionDto {
        private String sectionCode;
        private String name;
        private Double distance;
        private String duration;
        private String difficulty;
        private String startPoint;
        private String endPoint;
        private List<String> checkpoints;

        public SectionDto(Section section) {
            this.sectionCode = section.getSectionCode();
            this.name = section.getName();
            this.distance = section.getDistance();
            this.duration = section.getDuration();
            this.difficulty = section.getDifficulty();
            this.startPoint = section.getStartPoint();
            this.endPoint = section.getEndPoint();
            this.checkpoints = section.getCheckpoints();
        }
    }
}