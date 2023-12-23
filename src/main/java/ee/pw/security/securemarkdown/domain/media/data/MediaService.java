package ee.pw.security.securemarkdown.domain.media.data;

import ee.pw.security.securemarkdown.domain.media.entity.Media;
import ee.pw.security.securemarkdown.domain.media.enums.MediaType;
import ee.pw.security.securemarkdown.domain.note.entity.Note;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaService {

	private final MediaRepository mediaRepository;

	public void attachMediasToPhotos(
		List<MultipartFile> multipartFiles,
		Note note
	) {
		List<Media> medias = multipartFiles
			.stream()
			.map(multipartFile ->
				extractMediaDataAndReturnMediaObject(multipartFile, note)
			)
			.toList();

		mediaRepository.saveAll(medias);
	}

	public Media getSinglePhotoById(Long mediaId) {
		return mediaRepository
			.findById(mediaId)
			.orElseThrow(() ->
				new IllegalArgumentException("Photo with given id does not exist!")
			);
	}

	private Media extractMediaDataAndReturnMediaObject(
		MultipartFile file,
		Note note
	) {
		try {
			byte[] mediaData = file.getBytes();

			return Media
				.builder()
				.mediaData(mediaData)
				.mediaType(MediaType.PHOTO)
				.note(note)
				.build();
		} catch (IOException exception) {
			throw new IllegalStateException("Photo cannot be added");
		}
	}
}
