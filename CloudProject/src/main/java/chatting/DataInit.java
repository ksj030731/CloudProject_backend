package chatting;

import chatting.domain.Course;
import chatting.domain.Facilities;
import chatting.domain.Section;
import chatting.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInit implements CommandLineRunner {

    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // DB에 데이터가 이미 있으면 실행하지 않음 (중복 방지)
        if (courseRepository.count() > 0) {
            System.out.println("이미 데이터가 존재하여 초기화를 건너뜁니다.");
            return;
        }

        System.out.println("초기 데이터 저장을 시작합니다...");

        // ==========================================
        // 1코스
        // ==========================================
        Course course1 = Course.builder()
                .name("1코스")
                .description("기장 갈맷길로 임랑해수욕장에서 송정해수욕장까지 이어지는 해안 코스입니다. 수산과학연구소, 일광해수욕장, 해동용궁사 등을 지나며 부산의 동쪽 바다를 감상할 수 있습니다.")
                .distance(27.5)
                .duration("9시간")
                .difficulty("중")
                .region("기장군")
                .image("https://images.unsplash.com/photo-1703768516086-45eb97f36ce7?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxrb3JlYW4lMjBiZWFjaCUyMGNvYXN0bGluZXxlbnwxfHx8fDE3NTkwMjE4ODJ8MA&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral")
                .transportation("버스 181번, 185번 이용 가능")
                .latitude(35.2447)
                .longitude(129.2224)
                .completedCount(1247)
                .facilities(new Facilities(true, true, true, true))
                .highlights(Arrays.asList("일광해변 해돋이", "해동용궁사", "기장 해안절경"))
                .build();

        course1.addSection(Section.builder()
                .sectionCode("1-1")
                .name("1-1구간")
                .distance(11.5)
                .duration("4시간")
                .difficulty("하")
                .startPoint("임랑해수욕장")
                .endPoint("기장군청")
                .checkpoints(Arrays.asList("칠암항", "수산과학연구소", "일광해수욕장"))
                .build());

        course1.addSection(Section.builder()
                .sectionCode("1-2")
                .name("1-2구간")
                .distance(16.0)
                .duration("5시간")
                .difficulty("중")
                .startPoint("기장군청")
                .endPoint("송정해수욕장")
                .checkpoints(Arrays.asList("월전마을", "대변항", "오랑대", "해동용궁사"))
                .build());

        courseRepository.save(course1);


        // ==========================================
        // 2코스
        // ==========================================
        Course course2 = Course.builder()
                .name("2코스")
                .description("송정해수욕장에서 오륙도까지 이어지는 해운대와 수영구의 대표적인 해안 코스입니다. 해운대해수욕장, 동백섬, 광안리해수욕장을 거쳐 이기대까지 부산의 명소를 연결합니다.")
                .distance(23.4)
                .duration("8시간")
                .difficulty("중")
                .region("해운대구/수영구")
                .image("https://images.unsplash.com/photo-1647767444107-8f383924382d?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxoYWV1bmRhZSUyMGJlYWNoJTIwYnVzYW58ZW58MXx8fHwxNzU5MDIxOTYxfDA&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral")
                .transportation("지하철 2호선 해운대역, 금련산역, 버스 20번, 83번 이용 가능")
                .latitude(35.1595)
                .longitude(129.1603)
                .completedCount(891)
                .facilities(new Facilities(true, true, true, true))
                .highlights(Arrays.asList("해운대 해변", "동백섬 누리마루", "광안대교 야경", "이기대 절벽"))
                .build();

        course2.addSection(Section.builder()
                .sectionCode("2-1")
                .name("2-1구간")
                .distance(12.0)
                .duration("4시간")
                .difficulty("하")
                .startPoint("송정해수욕장")
                .endPoint("민락교(수영구방면)")
                .checkpoints(Arrays.asList("청사포", "해운대해수욕장", "동백섬(누리마루)"))
                .build());

        course2.addSection(Section.builder()
                .sectionCode("2-2")
                .name("2-2구간")
                .distance(11.4)
                .duration("4시간")
                .difficulty("중")
                .startPoint("민락교")
                .endPoint("오륙도 진입데크")
                .checkpoints(Arrays.asList("광안리해수욕장", "동생말", "이기대 어울마당"))
                .build());

        courseRepository.save(course2);


        // ==========================================
        // 3코스
        // ==========================================
        Course course3 = Course.builder()
                .name("3코스")
                .description("오륙도에서 태종대까지 이어지는 부산의 중심부를 가로지르는 긴 코스입니다. UN기념공원, 용두산공원, 영도를 거쳐 태종대에 이르는 부산의 역사와 문화를 체험할 수 있습니다.")
                .distance(42.0)
                .duration("14시간")
                .difficulty("중")
                .region("남구/중구/영도구")
                .image("https://images.unsplash.com/photo-1591366152219-48d643eb3aac?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxidXNhbiUyMHRvd2VyJTIwY2l0eSUyMHZpZXd8ZW58MXx8fHwxNzU5MDIxOTYxfDA&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral")
                .transportation("지하철 1호선 남포역, 버스 8번, 30번, 88번 이용 가능")
                .latitude(35.0512)
                .longitude(129.0867)
                .completedCount(387)
                .facilities(new Facilities(true, true, true, true))
                .highlights(Arrays.asList("UN기념공원", "용두산공원 부산타워", "태종대 절벽", "영도 해안절경"))
                .build();

        course3.addSection(Section.builder()
                .sectionCode("3-1")
                .name("3-1구간")
                .distance(12.5)
                .duration("4시간")
                .difficulty("중")
                .startPoint("오륙도 진입데크")
                .endPoint("부산진시장")
                .checkpoints(Arrays.asList("신선대", "UN기념공원", "우암소막마을"))
                .build());

        course3.addSection(Section.builder()
                .sectionCode("3-2")
                .name("3-2구간")
                .distance(14.6)
                .duration("5시간")
                .difficulty("중")
                .startPoint("부산진시장")
                .endPoint("절영해안산책로관리센터")
                .checkpoints(Arrays.asList("증산공원", "초량성당", "용두산공원"))
                .build());

        course3.addSection(Section.builder()
                .sectionCode("3-3")
                .name("3-3구간")
                .distance(14.9)
                .duration("5시간")
                .difficulty("중")
                .startPoint("절영해안산책로관리센터")
                .endPoint("아미르공원")
                .checkpoints(Arrays.asList("75광장", "영도해녀문화전시관", "태종대"))
                .build());

        courseRepository.save(course3);


        // ==========================================
        // 4코스
        // ==========================================
        Course course4 = Course.builder()
                .name("4코스")
                .description("절영해안산책로에서 낙동강하굿둑까지 서부산의 해안을 따라 이어지는 코스입니다. 송도해수욕장, 감천항, 몰운대, 다대포해수욕장을 거쳐 낙동강에 이르는 긴 여정입니다.")
                .distance(36.7)
                .duration("12시간")
                .difficulty("중")
                .region("영도구/서구/사하구/강서구")
                .image("https://images.unsplash.com/photo-1754195451576-9c034bbf4ab2?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxzb25nZG8lMjBiZWFjaCUyMGJ1c2FufGVufDF8fHx8MTc1OTAyMTk2Mnww&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral")
                .transportation("지하철 1호선 토성역, 다대포해수욕장역, 버스 2번, 7번 이용 가능")
                .latitude(35.0977)
                .longitude(129.0104)
                .completedCount(542)
                .facilities(new Facilities(true, true, true, true))
                .highlights(Arrays.asList("송도 스카이워크", "감천문화마을", "몰운대 일몰", "다대포 낙조분수"))
                .build();

        course4.addSection(Section.builder()
                .sectionCode("4-1")
                .name("4-1구간")
                .distance(12.9)
                .duration("4시간")
                .difficulty("중")
                .startPoint("절영해안산책로관리센터")
                .endPoint("감천항")
                .checkpoints(Arrays.asList("송도해수욕장", "암남공원"))
                .build());

        course4.addSection(Section.builder()
                .sectionCode("4-2")
                .name("4-2구간")
                .distance(13.0)
                .duration("4시간")
                .difficulty("중")
                .startPoint("감천항")
                .endPoint("몰운대")
                .checkpoints(Arrays.asList("두송반도전망대"))
                .build());

        course4.addSection(Section.builder()
                .sectionCode("4-3")
                .name("4-3구간")
                .distance(10.8)
                .duration("4시간")
                .difficulty("중")
                .startPoint("몰운대")
                .endPoint("낙동강하굿둑")
                .checkpoints(Arrays.asList("다대포해수욕장", "응봉봉수대 입구"))
                .build());

        courseRepository.save(course4);


        // ==========================================
        // 5코스
        // ==========================================
        Course course5 = Course.builder()
                .name("5코스")
                .description("낙동강하굿둑에서 시작해 신항과 가덕도를 연결하는 서남단 해안 코스입니다. 명지오션시티, 신호항, 부산신항을 거쳐 가덕도의 아름다운 자연을 체험할 수 있습니다.")
                .distance(44.2)
                .duration("15시간")
                .difficulty("상")
                .region("강서구")
                .image("https://images.unsplash.com/photo-1730825963012-579d146bd11a?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxnYWRlb2tkbyUyMGlzbGFuZCUyMGtvcmVhfGVufDF8fHx8MTc1OTAyMTk2Mnww&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral")
                .transportation("버스 김해 123번, 1008번 이용 가능")
                .latitude(35.0154)
                .longitude(128.9021)
                .completedCount(76)
                .facilities(new Facilities(false, false, true, true))
                .highlights(Arrays.asList("명지 해수욕장", "신항 전망", "가덕도 자연경관", "연대봉 등반"))
                .build();

        course5.addSection(Section.builder()
                .sectionCode("5-1")
                .name("5-1구간")
                .distance(14.1)
                .duration("5시간")
                .difficulty("중")
                .startPoint("낙동강하굿둑")
                .endPoint("신호항")
                .checkpoints(Arrays.asList("명지오션시티", "신호대교"))
                .build());

        course5.addSection(Section.builder()
                .sectionCode("5-2")
                .name("5-2구간")
                .distance(15.0)
                .duration("5시간")
                .difficulty("상")
                .startPoint("신호항")
                .endPoint("지양곡")
                .checkpoints(Arrays.asList("부산신항", "천가교", "연대봉"))
                .build());

        course5.addSection(Section.builder()
                .sectionCode("5-3")
                .name("5-3구간")
                .distance(15.1)
                .duration("5시간")
                .difficulty("상")
                .startPoint("지양곡")
                .endPoint("천가교")
                .checkpoints(Arrays.asList("대항어촌체험마을", "어음포", "동선방조제", "정거생태마을"))
                .build());

        courseRepository.save(course5);


        // ==========================================
        // 6코스
        // ==========================================
        Course course6 = Course.builder()
                .name("6코스")
                .description("낙동강하굿둑에서 북쪽으로 이어지는 강서구와 북구의 내륙 산악 코스입니다. 삼락생태공원, 금정산성을 거쳐 부산의 북부 산악지대를 탐험할 수 있습니다.")
                .distance(44.9)
                .duration("13시간")
                .difficulty("상")
                .region("강서구/북구/금정구")
                .image("https://images.unsplash.com/photo-1617286243498-dff5d6fac156?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxrb3JlYW4lMjByaXZlciUyMHBhcmt8ZW58MXx8fHwxNzU5MDIxOTY2fDA&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral")
                .transportation("지하철 3호선 구포역, 화명역, 버스 90번 이용 가능")
                .latitude(35.2364)
                .longitude(129.0319)
                .completedCount(198)
                .facilities(new Facilities(true, true, true, true))
                .highlights(Arrays.asList("삼락생태공원", "운수사", "금정산성", "화명수목원"))
                .build();

        course6.addSection(Section.builder()
                .sectionCode("6-1")
                .name("6-1구간")
                .distance(13.3)
                .duration("4시간")
                .difficulty("하")
                .startPoint("낙동강하굿둑")
                .endPoint("도시철도 구포역")
                .checkpoints(Arrays.asList("삼락생태공원", "삼락IC"))
                .build());

        course6.addSection(Section.builder()
                .sectionCode("6-2")
                .name("6-2구간")
                .distance(12.8)
                .duration("4시간")
                .difficulty("상")
                .startPoint("도시철도 구포역")
                .endPoint("주례정")
                .checkpoints(Arrays.asList("구포무장애숲길", "운수사"))
                .build());

        course6.addSection(Section.builder()
                .sectionCode("6-3")
                .name("6-3구간")
                .distance(8.1)
                .duration("2시간")
                .difficulty("중")
                .startPoint("주례정")
                .endPoint("어린이대공원")
                .checkpoints(Arrays.asList("선암사"))
                .build());

        course6.addSection(Section.builder()
                .sectionCode("6-4")
                .name("6-4구간")
                .distance(11.3)
                .duration("3시간")
                .difficulty("중")
                .startPoint("도시철도 구포역")
                .endPoint("금정산성 동문")
                .checkpoints(Arrays.asList("화명생태공원", "화명운동장", "화명수목원", "금정산성 서문"))
                .build());

        courseRepository.save(course6);


        // ==========================================
        // 7코스
        // ==========================================
        Course course7 = Course.builder()
                .name("7코스")
                .description("어린이대공원에서 금정산을 거쳐 노포동까지 이어지는 금정구의 대표적인 산악 코스입니다. 금정산성과 범어사를 지나며 부산의 진산을 체험할 수 있습니다.")
                .distance(22.0)
                .duration("7시간")
                .difficulty("중")
                .region("동래구/금정구")
                .image("https://images.unsplash.com/photo-1662527984434-8a3d93dfac33?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxrb3JlYW4lMjBtb3VudGFpbiUyMHRlbXBsZXxlbnwxfHx8fDE3NTkwMjE5NjV8MA&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral")
                .transportation("지하철 1호선 범어사역, 노포역, 버스 90번 이용 가능")
                .latitude(35.2364)
                .longitude(129.0319)
                .completedCount(324)
                .facilities(new Facilities(true, false, true, true))
                .highlights(Arrays.asList("금정산성", "범어사 고찰", "부산 도심 전망", "산악 트레킹"))
                .build();

        course7.addSection(Section.builder()
                .sectionCode("7-1")
                .name("7-1구간")
                .distance(9.0)
                .duration("3시간")
                .difficulty("중")
                .startPoint("어린이대공원")
                .endPoint("금정산성 동문")
                .checkpoints(Arrays.asList("만덕고개", "금정산성 남문"))
                .build());

        course7.addSection(Section.builder()
                .sectionCode("7-2")
                .name("7-2구간")
                .distance(13.0)
                .duration("4시간")
                .difficulty("중")
                .startPoint("금정산성 동문")
                .endPoint("상현마을")
                .checkpoints(Arrays.asList("금정산성 북문", "범어사", "노포동 고속버스터미널", "스포원"))
                .build());

        courseRepository.save(course7);


        // ==========================================
        // 8코스
        // ==========================================
        Course course8 = Course.builder()
                .name("8코스")
                .description("상현마을에서 시작해 동래와 연제를 거쳐 수영까지 이어지는 부산 동부의 도심 통과 코스입니다. 동천을 따라 걸으며 부산의 도심 풍경을 감상할 수 있습니다.")
                .distance(18.3)
                .duration("6시간")
                .difficulty("중")
                .region("금정구/동래구/연제구/수영구")
                .image("https://images.unsplash.com/photo-1617286243498-dff5d6fac156?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxrb3JlYW4lMjByaXZlciUyMHBhcmt8ZW58MXx8fHwxNzU5MDIxOTY2fDA&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral")
                .transportation("지하철 1호선 동래역, 2호선 수영역, 버스 다수 노선 이용 가능")
                .latitude(35.2047)
                .longitude(129.0842)
                .completedCount(156)
                .facilities(new Facilities(true, true, false, true))
                .highlights(Arrays.asList("동천 산책로", "동래 온천", "APEC 나루공원", "수영강 풍경"))
                .build();

        course8.addSection(Section.builder()
                .sectionCode("8-1")
                .name("8-1구간")
                .distance(10.7)
                .duration("4시간")
                .difficulty("중")
                .startPoint("상현마을")
                .endPoint("동천교(석대다리)")
                .checkpoints(Arrays.asList("땅뫼산", "명장정수사업소(회동지소)", "동대교"))
                .build());

        course8.addSection(Section.builder()
                .sectionCode("8-2")
                .name("8-2구간")
                .distance(7.6)
                .duration("2시간")
                .difficulty("하")
                .startPoint("동천교(석대다리)")
                .endPoint("민락교(수영구 방면)")
                .checkpoints(Arrays.asList("원동교", "수영4호교", "좌수영교", "APEC 나루공원"))
                .build());

        courseRepository.save(course8);


        // ==========================================
        // 9코스
        // ==========================================
        Course course9 = Course.builder()
                .name("9코스")
                .description("상현마을에서 시작해 기장군으로 돌아가는 순환형 코스입니다. 장전, 철마를 거쳐 기장군청에 이르는 부산 동북부의 내륙 산간 지역을 탐험할 수 있습니다.")
                .distance(19.8)
                .duration("7시간")
                .difficulty("중")
                .region("금정구/기장군")
                .image("https://images.unsplash.com/photo-1663030993965-f5f16d2ddf45?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxrb3JlYW4lMjBtb3VudGFpbiUyMGhpa2luZ3xlbnwxfHx8fDE3NTg5NDM1MDl8MA&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral")
                .transportation("버스 181번, 185번 이용 가능")
                .latitude(35.2447)
                .longitude(129.2224)
                .completedCount(89)
                .facilities(new Facilities(false, false, true, false))
                .highlights(Arrays.asList("장전 계곡", "철마 농촌 풍경", "모연정", "기장 내륙 산간"))
                .build();

        course9.addSection(Section.builder()
                .sectionCode("9-1")
                .name("9-1구간")
                .distance(9.0)
                .duration("3시간")
                .difficulty("중")
                .startPoint("상현마을")
                .endPoint("이곡마을")
                .checkpoints(Arrays.asList("장전2교", "장전마을(철마교)", "보림교"))
                .build());

        course9.addSection(Section.builder()
                .sectionCode("9-2")
                .name("9-2구간")
                .distance(10.8)
                .duration("4시간")
                .difficulty("중")
                .startPoint("이곡마을")
                .endPoint("기장군청")
                .checkpoints(Arrays.asList("모연정"))
                .build());

        courseRepository.save(course9);

        System.out.println("============ 초기 데이터 9개 코스 저장 완료 ============");
    }
}