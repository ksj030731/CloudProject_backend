package chatting.dto;

import chatting.domain.Announcement;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AnnouncementDto {
    private Long id;
    private String title;
    private String content;
    private String date;
    private String author;
    private String category;

    public AnnouncementDto(Announcement entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.date = entity.getDate().toString();
        this.author = entity.getAuthor();
        this.category = entity.getCategory();
    }
}