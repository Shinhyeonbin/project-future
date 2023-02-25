package com.example.projectfuture.service;

import com.example.projectfuture.domain.Article;
import com.example.projectfuture.domain.UserAccount;
import com.example.projectfuture.domain.type.SearchType;
import com.example.projectfuture.dto.ArticleDto;
import com.example.projectfuture.dto.ArticleWithCommentsDto;
import com.example.projectfuture.dto.UserAccountDto;
import com.example.projectfuture.repository.ArticleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
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
        assertThat(articles).isNotNull();
    }

    @DisplayName("검색어와 함께 게시글을 검색하면, 게시글 페이지를 반환한다.")
    @Test
    void givenSearchParameters_whenSearchingArticles_thenReturnsArticlePage() {
        //Given
        SearchType searchType = SearchType.TITLE;
        String searchKeyword = "title";
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findByTitle(searchKeyword, pageable)).willReturn(Page.empty());

        //When
        Page<ArticleDto> articles = sut.searchArticles(searchType, searchKeyword, pageable);

        //Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findByTitle(searchKeyword, pageable);
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
                .isInstanceOf(EntityNotFoundException.class).hasMessage("게시글이 없습니다. - articleId" + articleId);

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
        given(articleRepository.save(any(Article.class))).willReturn(null);
        Article article = createArticle();
        ArticleDto dto = createArticleDto("새 타이틀", "새 내용", "#new");
        given(articleRepository.getReferenceById(dto.id())).willReturn(article);

        //When
        sut.updateArticles(dto);

        //Then
        then(articleRepository).should().save(any(Article.class));
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

    @Disabled("구현중")
    @DisplayName("게시글의 ID를 입력하면, 게시글을 삭제한다..")
    @Test
    void givenArticleId_whenDeletingArticle_thenDeletesArticle() {

        // Given
        willDoNothing().given(articleRepository).delete(any(Article.class));
        Long articleId = 1L;
        willDoNothing().given(articleRepository).deleteById(articleId);

        //When
        sut.deleteArticle(1L);

        //Then
        then(articleRepository).should().deleteById(articleId);
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
