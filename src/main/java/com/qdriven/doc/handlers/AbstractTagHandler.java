package com.qdriven.doc.handlers;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import com.qdriven.doc.util.Util;

import generated.Article.Front.ArticleMeta.Abstract;
import generated.Article.Front.ArticleMeta.Abstract.P;
import generated.ObjectFactory;

public class AbstractTagHandler {
	Abstract abstractTag;
	XWPFParagraph aAbstractPara;
	
	public AbstractTagHandler(XWPFParagraph theAbstractPara) {
		this.aAbstractPara = theAbstractPara;
		abstractTag = new Abstract();
		
		processAbstract();
	}
	
	private void processAbstract(){
		P p = new P();
		for (XWPFRun run : aAbstractPara.getRuns()) {
			String aAbstractStr = run.getText(0);
			aAbstractStr = Util.removeNLineTabs(aAbstractStr);
			if (Util.isStringNotEmpty(aAbstractStr)) {
				if(run.isItalic()){
					p.getContent().add(new ObjectFactory().createArticleFrontArticleMetaAbstractPItalic(aAbstractStr));
				}else if(run.getSubscript().getValue() == 2){
					try{
						p.getContent().add(new ObjectFactory().createArticleFrontArticleMetaAbstractPSup(Byte.valueOf(aAbstractStr)));
						} catch (NumberFormatException nFExp){
							p.getContent().add(aAbstractStr);
						}
				} else{
					p.getContent().add(aAbstractStr);
				}
			}
		}
		abstractTag.setP(p);
	}
	
	public Abstract getAbstract() {
		return abstractTag;
	}
}
