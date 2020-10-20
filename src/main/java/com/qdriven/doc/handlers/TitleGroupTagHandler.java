package com.qdriven.doc.handlers;

import generated.Article.Front.ArticleMeta.TitleGroup;

public class TitleGroupTagHandler {
	TitleGroup titleGrpTag;
	
	public TitleGroupTagHandler(String title) {
		super();
		titleGrpTag = new TitleGroup();
		processArticleTitle(title);
	}
	
	private void processArticleTitle(String theArticleTitle){
		titleGrpTag.setArticleTitle(theArticleTitle);
	}
	public TitleGroup getTitleGrp() {
		return titleGrpTag;
	}
}
