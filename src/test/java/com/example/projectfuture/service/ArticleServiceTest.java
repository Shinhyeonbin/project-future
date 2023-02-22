package com.example.projectfuture.service;

import com.example.projectfuture.domain.type.SearchType;
import com.example.projectfuture.dto.ArticleDto;
import com.example.projectfuture.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


@DisplayName("비즈니스 로직 - 게시글")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {
    @InjectMocks private ArticleService sut;
    @Mock private ArticleRepository articleRepository;

    @DisplayName("게시글을 검색하면, 게시글 리스트를 반환한다.")
    @Test
    void givenSearchParameters_whenSearchingArticles_thenReturnsArticleList() {
        //Given

        //When
        Page<ArticleDto> articles = sut.searchArticles(SearchType.TITLE, "search keyword");   //제목, 본문, ID, 닉네임, 해시태그

        //Then
        assertThat(articles).isNotNull();
    }

    @DisplayName("게시글을 조회, 게시글을 반환한다.")
    @Test
    void givenArticleId_whenSearchingArticles_thenReturnsArticle() {
        //Given

        //When
        ArticleDto articles = sut.searchArticles(1L);   //제목, 본문, ID, 닉네임, 해시태그

        //Then
        assertThat(articles).isNotNull();
    }


}
