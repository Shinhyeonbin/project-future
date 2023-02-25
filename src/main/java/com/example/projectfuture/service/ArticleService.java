package com.example.projectfuture.service;

import com.example.projectfuture.domain.type.SearchType;
import com.example.projectfuture.dto.ArticleDto;
import com.example.projectfuture.dto.ArticleWithCommentsDto;
import com.example.projectfuture.repository.ArticleCommentRepository;
import com.example.projectfuture.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(SearchType searchType, String searchkeyword, Pageable pageable) {
        return Page.empty();
    }

    @Transactional(readOnly = true)
    public ArticleWithCommentsDto getArticle(Long articleId) {
        return null;
    }

    public void saveArticles(ArticleDto dto) {

    }

    public void updateArticles(ArticleDto articleId) {
    }

    public void deleteArticle(long articleId) {
    }
}
