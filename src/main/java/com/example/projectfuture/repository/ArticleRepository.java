package com.example.projectfuture.repository;

import com.example.projectfuture.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {

}