package com.qdriven.doc.handlers;

import generated.Article.Front.ArticleMeta.AuthorNotes;
import generated.Article.Front.ArticleMeta.AuthorNotes.Corresp;

import com.qdriven.doc.util.Constants;
import com.qdriven.doc.util.Util;

import generated.ObjectFactory;

public class AuthorNotesCorrespTagHandler {
	String para;
	int num;
	AuthorNotes authorNotesTag;
	
	public AuthorNotesCorrespTagHandler(String para) {
		super();
		this.para = para;
		authorNotesTag = new AuthorNotes();
		processCrossAuth();
	}
	
	private void processCrossAuth() {
		String str = para.replace(Constants.CORRESPONDENCE_STR+":","");
		String[] str_ = str.split(":");
		if(str_.length>=2) {
			int i =str_.length-1;
			Corresp aCorresp = new Corresp();
			if(Util.isStringNotEmpty(str_[i])) { 
				String aCorrespName =str_[0];
				String eMail = str_[i].trim().replaceAll("[<,>]","");
				aCorresp.getContent().add(Constants.CORRESPONDENCE_STR+":"+aCorrespName+": E-mail:");
				aCorresp.getContent().add(new ObjectFactory().createArticleFrontArticleMetaAuthorNotesCorrespEmail(eMail));
			}
			authorNotesTag.setCorresp(aCorresp);
		}
		
	}
	
	public AuthorNotes getAuthorGrp() {
		return authorNotesTag;
	}
}
