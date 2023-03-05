package com.example.projectfuture.service;

import com.example.projectfuture.domain.Article;
import com.example.projectfuture.domain.UserAccount;
import com.example.projectfuture.domain.type.SearchType;
import com.example.projectfuture.dto.ArticleDto;
import com.example.projectfuture.dto.ArticleWithCommentsDto;
import com.example.projectfuture.dto.UserAccountDto;
import com.example.projectfuture.repository.ArticleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;


@DisplayName("비즈니스 로직 - 게시글")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {
    @InjectMocks private ArticleService sut;
    @Mock private ArticleRepository articleRepository;

    @DisplayName("검색글 없이 게시글을 검색하면, 게시글 페이지를 반환한다.")
    @Test
    void givenNoSearchParameters_whenSearchingArticles_thenReturnsArticlePage() {
        //Given
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findAll(pageable)).willReturn(Page.empty());

        //When
        Page<ArticleDto> articles = sut.searchArticles(null, null, pageable);

        //Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findAll(pageable);
    }

    @DisplayName("검색어와 함께 게시글을 검색하면, 게시글 페이지를 반환한다.")
    @Test
    void givenSearchParameters_whenSearchingArticles_thenReturnsArticlePage() {
        //Given
        SearchType searchType = SearchType.TITLE;
        String searchKeyword = "title";
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findByTitleContaining(searchKeyword, pageable)).willReturn(Page.empty());

        //When
        Page<ArticleDto> articles = sut.searchArticles(searchType, searchKeyword, pageable);

        //Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findByTitleContaining(searchKeyword, pageable);
    }

    @DisplayName("검색어 없이 게시글을 해시테그 검색하면, 빈 페이지를 반환한다.")
    @Test
    void givenNoSearchParameters_whenSearchingArticlesviaHashtag_thenReturnsEmptyPage() {
        //Given
        Pageable pageable = Pageable.ofSize(20);

        //When
        Page<ArticleDto> articles = sut.searchArticlesviaHashtag(null, pageable);

        //Then
        assertThat(articles).isEqualTo(Page.empty(pageable));
        then(articleRepository).shouldHaveNoInteractions();
    }

    @DisplayName("게시글을 해시테그 검색하면, 게시글 페이지를 반환한다.")
    @Test
    void givenHashtag_whenSearchingArticlesviaHashtag_thenReturnsArticlesPage() {
        //Given
        String hashtag = "#java";
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findByHashtag(hashtag, pageable)).willReturn(Page.empty(pageable));

        //When
        Page<ArticleDto> articles = sut.searchArticlesviaHashtag(hashtag, pageable);

        //Then
        assertThat(articles).isEqualTo(Page.empty(pageable));
        then(articleRepository).should().findByHashtag(hashtag, pageable);
    }

    @DisplayName("해시테그를 조회하면, 유니크 해시태그 리스트를 반환한다.")
    @Test
    void givenNothing_whenCalling_thenReturnsHashtaglist() {
        //Given
        List<String> expectedHashtag = List.of("#java", "#spring", "#boot");
        given(articleRepository.findAllDistinctHashtag()).willReturn(expectedHashtag);

        //When
        List<String> actualHashtag = sut.getHashtags();

        //Then
        assertThat(actualHashtag).isEqualTo(expectedHashtag);
        then(articleRepository).should().findAllDistinctHashtag();
    }

    @DisplayName("게시글을 조회, 게시글을 반환한다.")
    @Test
    void givenArticleId_whenSearchingArticles_thenReturnsArticle() {
        //Given
        Long articleId = 1L;
        Article article = createArticle();
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        //When
        ArticleWithCommentsDto dto = sut.getArticle(articleId);

        //Then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title", article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("hashtag", article.getHashtag());
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("없는 게시글을 조회하면, 예외를 던진다.")
    @Test
    void givenNoexistArticleId_whenSearchingArticles_thenThrowException() {
        //Given
        Long articleId = 0L;
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        //When
        Throwable throwable = catchThrowable(() -> sut.getArticle(articleId));

        //Then
        assertThat(throwable)
                .isInstanceOf(EntityNotFoundException.class).hasMessage("게시글이 없습니다.");  //게시글의 id를 공개하지 않는 방향으로 결정

        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("게시글 정보를 입력하면, 게시글을 생성한다.")
    @Test
    void givenArticleInfo_whenSavingArticle_thenSavesArticle() {
        // Given
        ArticleDto dto = createArticleDto();
        given(articleRepository.save(any(Article.class))).willReturn(createArticle());

        // When
        sut.saveArticles(dto);

        // Then
        then(articleRepository).should().save(any(Article.class));
    }

    @DisplayName("게시글의 수정 정보를 입력하면, 게시글을 수정한다.")
    @Test
    void givenModifiedInfo_whenUpdatingArticle_thenUpdatesArticle() {
        // Given
        Article article = createArticle();
        ArticleDto dto = createArticleDto("새 타이틀", "새 내용", "#new");
        given(articleRepository.getReferenceById(dto.id())).willReturn(article);

        //When
        sut.updateArticles(dto);

        //Then
        assertThat(article)
                .hasFieldOrPropertyWithValue("title", dto.title())
                .hasFieldOrPropertyWithValue("content", dto.content())
                .hasFieldOrPropertyWithValue("hashtag", dto.hashtag());
        then(articleRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("없는 게시글의 수정정보를 입력하면, 경고하고 수정이 이뤄지지 않는다.")
    @Test
    void givenNoexistArticleInfo_whenUpdatingArticles_thenLogsWarningAndDoesNothing() {
        //Given
        ArticleDto dto = createArticleDto("새 타이틀", "새 내용", "#new");
        given(articleRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);

        //When
        sut.updateArticles(dto);

        //Then
        then(articleRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("게시글의 ID를 입력하면, 게시글을 삭제한다..")
    @Test
    void givenArticleId_whenDeletingArticle_thenDeletesArticle() {
        // Given
        Long articleId = 1L;
        willDoNothing().given(articleRepository).deleteById(articleId);

        //When
        sut.deleteArticle(1L);

        //Then
        then(articleRepository).should().deleteById(articleId);
    }

    @DisplayName("게시글 수를 조회하면, 게시글 수를 반환한다.")
    @Test
    void givenNothing_whenCountingArticles_thenReturnsArticleCount() {
        //Given
        long expected = 0L;
        given(articleRepository.count()).willReturn(expected);

        //When
        long actual = sut.getArticleCount();

        //Then
        assertThat(actual).isEqualTo(expected);
        then(articleRepository).should().count();
    }

    //아래는 태스트를 위한 메소드

    private UserAccount createUserAccount() {
        return UserAccount.of(
                "misohb",
                "Password",
                "misohb@gmail.com",
                "misohb",
                null
        );
    }

    private Article createArticle() {
        return  Article.of(
                createUserAccount(),
                "title",
                "content",
                "#test"
        );
    }

    private ArticleDto createArticleDto() {
        return createArticleDto("title", "content", "#java");
    }

    private ArticleDto createArticleDto(String title, String content, String hashtag) {
        return ArticleDto.of(
                1L,
                createUserAccountDto(),
                title,
                content,
                hashtag,
                LocalDateTime.now(),
                "misohb",
                LocalDateTime.now(),
                "misohb"
        );
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                1L,
                "misohb",
                "Password",
                "misohb@gmail.com",
                "misohb",
                "Hello",
                LocalDateTime.now(),
                "misohb",
                LocalDateTime.now(),
                "misohb"
        );
    }
}
