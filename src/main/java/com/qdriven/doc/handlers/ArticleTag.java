package com.qdriven.doc.handlers;

import java.math.BigDecimal;

import generated.Article;

public class ArticleTag {
	Article article;

	public ArticleTag() {
		super();
		article = new Article();
		processArticle();
	}

	private void processArticle() {
		article.setArticleType("research-article");
		article.setDtdVersion(new BigDecimal(3.0));
	}
	
	public Article getArticle(){
		return article;
	}
}
