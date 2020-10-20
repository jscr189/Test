/**
 * 
 */
package com.qdriven.doc.handlers;

import generated.Article.Front.ArticleMeta;
import generated.Article.Front.ArticleMeta.ArticleCategories;
import generated.Article.Front.ArticleMeta.ArticleCategories.SubjGroup;
import generated.Article.Front.ArticleMeta.ArticleId;
/**
 * @author chada
 *
 */
public class ArticleMetaTagHandler {
	ArticleMeta articleMeta;

	/**
	 * @return the articleMeta
	 */
	public ArticleMeta getArticleMeta() {
		return articleMeta;
	}

	/**
	 * @param articleMeta the articleMeta to set
	 */
	public void setArticleMeta(ArticleMeta articleMeta) {
		this.articleMeta = articleMeta;
	}

	public ArticleMetaTagHandler(String frontStr) {
		super();
		articleMeta = new ArticleMeta();
		processArticleMeta(frontStr);
	}

	private void processArticleMeta(String headerStr) {
		String[] strs = headerStr.split("\\|");
		
		SubjGroup subjGroup = new SubjGroup();
		if(strs[0].trim().contains("Research Article")){
			subjGroup.setSubject("Research Article");
		}else if(strs[0].trim().contains("Case Report")){
			subjGroup.setSubject("Case Report");
		}
		subjGroup.setSubjGroupType("heading");
		ArticleCategories articleCategories = new ArticleCategories();
		articleCategories.setSubjGroup(subjGroup);
		String[] strs2 = strs[1].split("\\:");
		ArticleId articleId  = new ArticleId();
		articleId.setPubIdType("other");
		articleMeta.getArticleId().add(articleId);
		articleId =  new ArticleId();
		articleId.setPubIdType(strs2[0].trim());
		articleId.setValue(strs2[1].trim());
		articleMeta.getArticleId().add(articleId);
		articleMeta.setArticleCategories(articleCategories);
		articleMeta.setVolume(Short.parseShort("52"));
		articleMeta.setIssue(Short.parseShort("10"));
		this.setArticleMeta(articleMeta);
	}
}
