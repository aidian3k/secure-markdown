package ee.pw.security.securemarkdown.domain.media.entity;

import ee.pw.security.securemarkdown.domain.media.enums.MediaType;
import ee.pw.security.securemarkdown.domain.note.entity.Note;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Immutable;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity(name = "medias")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Immutable
public class Media {
    @Id
    @GeneratedValue(generator = "medias_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "media_data", nullable = false)
    @Lob
    private byte[] mediaData;

    @Column(name = "media_type", nullable = false)
    private MediaType mediaType;

    @ManyToOne(fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Note note;

    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime creationTimeStamp;
}
