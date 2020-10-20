package com.qdriven.doc.handlers;

import generated.Article.Front.ArticleMeta.KwdGroup;

public class KeyWordTagHandler {
	String para;
	KwdGroup keyWdGrpTag;
	public KeyWordTagHandler(String para) {
		super();
		this.para = para;
		keyWdGrpTag = new KwdGroup();
		processKeywords();
	}
	
	private void processKeywords() {
		/*
		 * Key words: Primary immunodeficiency; Common variable immunodeficiency; Hypogam
			maglobulinemia; Chemotherapy; Antibody defects; Immunoglobulin therapy
		 */
		String keywrdGrp = para.replaceAll("Key words:","").trim();
		String[] kWrds = keywrdGrp.split(";");
		for(String kyWd : kWrds) {
			keyWdGrpTag.getKwd().add(kyWd.trim());
		}
		keyWdGrpTag.setLang("en");
	}
	
	public KwdGroup getKeywdGrp() {
		return keyWdGrpTag;
	}
	
}
