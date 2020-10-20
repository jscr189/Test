/**
 * 
 */
package com.qdriven.doc.handlers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import com.qdriven.doc.util.Constants;
import com.qdriven.doc.util.Util;

import generated.Article;
import generated.Article.Body;
import generated.Article.Body.SecType;
import generated.Article.Body.SecType.P;
import generated.Article.Body.SecType.P.Xref;
import generated.Article.Body.SecType.Sec;
import generated.Article.Body.SecType.Sec.Fig;
import generated.Article.Body.SecType.Sec.Fig.Caption;
import generated.Article.Body.SecType.Sec.Fig.Caption.Title;
import generated.ObjectFactory;

/**
 * @author chada
 *
 */
public class BodyTagHandler {

	Body aBody;

	public BodyTagHandler() {
		super();
		aBody = new Body();
	}

	public void processBody(String theSecType) {
		SecType secType = new SecType();
		if(Constants.INTRODUCTION_STR.equalsIgnoreCase(theSecType))
			secType.setSecType("intro");
		else if(Constants.MATERIAL_METHODS.equalsIgnoreCase(theSecType))
			secType.setSecType("materials|methods");
		else if(Constants.CASE_REPORT_STR.equalsIgnoreCase(theSecType))
			secType.setSecType("cases");
		else if(Constants.RESULTS_STR.equalsIgnoreCase(theSecType))
			secType.setSecType("results");
		else if(Constants.DISCUSSION_STR.equalsIgnoreCase(theSecType))
			secType.setSecType("discussion");
		else if(Constants.CONCLUSIONS_STR.equalsIgnoreCase(theSecType))
			secType.setSecType("Conclusions");
		else if(Constants.ACKNOWLEDGMENTS_STR.equalsIgnoreCase(theSecType))
			secType.setSecType("ack");
		else if(Constants.SUPPLEMENTARY_MATERIAL_STR.equalsIgnoreCase(theSecType))
				secType.setSecType("Supplementary Material");	
		else
			secType.setSecType(theSecType);
		secType.getTitleOrSecOrP().add(theSecType);
		aBody.getSecType().add(secType);
	}
	
	public Body getBody(){
		return aBody;
	}
	
	public void processSecTypeP(XWPFParagraph theDocPara, String theSecType){
		
		List<SecType> theSecTitle = getBody().getSecType();
		SecType aSt = theSecTitle.get(theSecTitle.size()-1);
		List<Serializable> sec  = aSt.getTitleOrSecOrP();
		P p = new ObjectFactory().createArticleBodySecTypeP();
		processSecTypeP(theDocPara, p);
		sec.add(p);
	}
	
	public void processSecTypeSec(XWPFParagraph theDocPara, String theSecType){
		
		List<SecType> theSecTitle = getBody().getSecType();
		SecType aSt = theSecTitle.get(theSecTitle.size()-1);
		List<Serializable> secType  = aSt.getTitleOrSecOrP();
		Sec sec = new ObjectFactory().createArticleBodySecTypeSec();
		sec.getContent().add(new ObjectFactory().createArticleBodySecTypeSecTitle(theSecType));
		secType.add(sec);
	}
	
	public void processSecTypeOrSec(XWPFParagraph theDocPara, String theSecType){
		
		List<SecType> theSecTitle = getBody().getSecType();
		SecType aSecType = theSecTitle.get(theSecTitle.size()-1);
		List<Serializable> secType = aSecType.getTitleOrSecOrP();
		Sec aSec;
		if(secType.get(secType.size()-1).getClass().equals(java.lang.String.class)){
			 aSec = new Sec();
			aSecType.getTitleOrSecOrP().add(aSec);
		}else {
			aSec = (Sec)secType.get(secType.size()-1);
		}
		Article.Body.SecType.Sec.P p = new ObjectFactory().createArticleBodySecTypeSecP();
		processMPara(theDocPara, p);
		aSec.getContent().add(new ObjectFactory().createArticleBodySecTypeSecP(p));
	}
	
	public void processMPara(XWPFParagraph theDocpara, Article.Body.SecType.Sec.P thePara) {
		
		for (XWPFRun run : theDocpara.getRuns()) {
			String tex = run.getText(0);
			tex = Util.removeNLineTabs(tex);
			
			if (Util.isStringNotEmpty(tex)) {
				if (run.isItalic()){
					thePara.getContent().add(new ObjectFactory().createArticleBodySecTypeSecPItalic(tex));
				} else if(run.getSubscript().getValue() == 2){
					try{
						thePara.getContent().add(new ObjectFactory().createArticleBodySecTypeSecPSup(tex));
						} catch (NumberFormatException nFExp){
							thePara.getContent().add(tex);
						}
				} else if(run.getSubscript().getValue() == 3){
					try{
						thePara.getContent().add(new ObjectFactory().createArticleBodySecTypeSecPSub(Short.valueOf(tex)));
						} catch (NumberFormatException nFExp){
							thePara.getContent().add(tex);
						}
				} else {
					addSecTypeSecPXref(thePara, tex);
				}
				 if(tex.contains("Figure ")){
					String[] aFigureSplit = tex.split("Figure ");
					StringBuffer subStrBfr;
					if (Util.isStringNotEmpty(aFigureSplit[0])) 
						thePara.getContent().add(aFigureSplit[0]);
					
					for(int i=1;i<aFigureSplit.length;i++){
						int g=0;
						String splitFromOne = aFigureSplit[i];
						subStrBfr = new StringBuffer();
						for(int j=0;j<splitFromOne.length();j++){
							g= j;
							String sChar = String.valueOf(splitFromOne.charAt(j));
							if(null != sChar && !"".equals(sChar)
									&& (sChar.equals(")") || sChar.equals(","))){
								break;
							} else {
								subStrBfr.append(String.valueOf(splitFromOne.charAt(j)));
							}
						}
						try{
						Article.Body.SecType.Sec.P.Xref aXref = new ObjectFactory().createArticleBodySecTypeSecPXref();
						aXref.setRefType("fig");
						String figNumber;
						if(Util.isInteger(aFigureSplit[i].charAt(0)+""))
							figNumber = String.valueOf(aFigureSplit[i].charAt(0));
						else
							figNumber = String.valueOf(aFigureSplit[i].charAt(1));
						aXref.setRid("f" + String.format("%02d", Integer.parseInt(figNumber)));
						aXref.setValue("Figure "+subStrBfr.toString());
						thePara.getContent().add(new ObjectFactory().createArticleBodySecTypeSecPXref(aXref));
						thePara.getContent().add(splitFromOne.substring(g,splitFromOne.length()));
						}catch(NumberFormatException npe){
							System.out.println(npe.getMessage()+" "+aFigureSplit[i]);
						}
					}
				 }
			}
		}
	}
	
	public void processFigure(XWPFParagraph theDocPara, String theSecType){
		
		List<SecType> theSecTitle = getBody().getSecType();
		generated.Article.Body.SecType.Sec aSec =null;
		for(int i=0;i<theSecTitle.size();i++){
			SecType aSecType = theSecTitle.get(i);
			if(!(aSecType.getSecType().equals("materials|methods") ||
					aSecType.getSecType().equals("cases") || 
					aSecType.getSecType().equals("results"))) continue;
			List<Serializable> secType = aSecType.getTitleOrSecOrP();
			
			for(int j=0;j<secType.size();j++){
				if(secType.get(j).getClass() != generated.Article.Body.SecType.Sec.class)continue;
				aSec = (generated.Article.Body.SecType.Sec)secType.get(j);
					for(int k=0;k<aSec.getContent().size();k++){
						if(aSec.getContent().get(k).getClass().equals(javax.xml.bind.JAXBElement.class)){
							javax.xml.bind.JAXBElement<generated.Article.Body.SecType.Sec.P> p =  (javax.xml.bind.JAXBElement<generated.Article.Body.SecType.Sec.P>)aSec.getContent().get(k);
						
							try{
								if(p.getValue().getClass().equals(generated.Article.Body.SecType.Sec.Fig.class)) continue;
								}catch (ClassCastException c){
									continue;
								}
						for(int l=0;l<p.getValue().getContent().size();l++){
							if(p.getValue().getContent().get(l).getClass().equals(javax.xml.bind.JAXBElement.class)){
								javax.xml.bind.JAXBElement<generated.Article.Body.SecType.Sec.P.Xref> aXrefd =  (javax.xml.bind.JAXBElement<generated.Article.Body.SecType.Sec.P.Xref>)p.getValue().getContent().get(l);
								
								try{
									if(aXrefd.getValue().getClass() != generated.Article.Body.SecType.Sec.P.Xref.class) continue;
								}catch (ClassCastException c){
									continue;
								}
								
								String aStr = aXrefd.getValue().getValue();
								if(!aStr.contains("Figure"))continue;
								String numStr = aStr.replaceAll("[^0-9]+", " ");
								if(numStr.trim().equals(""))continue;
								int aFigNumber = getFigNumber(theSecType,numStr.trim());
								if(aFigNumber == 0)continue;
								Fig f = new ObjectFactory().createArticleBodySecTypeSecFig();
								f.setId("f"+String.format("%02d" , aFigNumber));
								f.setLabel("Figure "+aFigNumber+".");
								Caption caption = new ObjectFactory().createArticleBodySecTypeSecFigCaption();
								Title title = new ObjectFactory().createArticleBodySecTypeSecFigCaptionTitle();
								
								javax.xml.bind.JAXBElement<String> secc = (javax.xml.bind.JAXBElement<String>)aSec.getContent().get(0);
								//title.getContent().add(secc.getValue());
								
								try{
									
									//System.out.println(secc.getValue());
									//title.getContent().add(secc.getValue());
								} catch (ClassCastException c){
									//System.out.println(" exception");
								}
								List<HashMap<String, String>> aItalicBoldList = processPara(theDocPara);
								for(int t=0;t<aItalicBoldList.size();t++){
									HashMap<String, String> aItalicBold = aItalicBoldList.get(t);
									for(Map.Entry<String, String> aParaType : aItalicBold.entrySet()){
										if(aParaType.getKey().equals(Constants.ITALIC_STR)){
											title.getContent().add(new ObjectFactory().createArticleBodySecTypeSecFigCaptionTitleItalic(aParaType.getValue()));
										}else if(aParaType.getKey().equals(Constants.BOLD_STR)){
											title.getContent().add(new ObjectFactory().createArticleBodySecTypeSecFigCaptionTitleBold(aParaType.getValue()));
										} else {
											title.getContent().add(aParaType.getValue());
										}
									}
								}
								caption.setTitle(title);
								f.setCaption(caption);
								aSec.getContent().add(k+1, new ObjectFactory().createArticleBodySecTypeSecFig(f));
								if(true) return;
							}
						}
					}
				}
			}
		}
	}
	
	String theFigureNumber;
	private int getFigNumber(String theFigNumStr, String theNumStr){
		String aNumStrArray[] = theNumStr.trim().split(" ");
		for(String aNumStr: aNumStrArray){
			if(theFigNumStr.contains("Figure " + aNumStr)){
				theFigureNumber = aNumStr;
				return Integer.parseInt(aNumStr);
			}
		}
		return 0;
	}
	
	public void processSecTypeP(XWPFParagraph theDocpara, P thePara) {
		
		String boldStr = "";
		String itlStr = "";
		String str = "";
		String subString = "";
		String supStr = "";
		for (XWPFRun run : theDocpara.getRuns()) {
			String tex = run.getText(0);
			tex = Util.removeNLineTabs(tex);
			if (Util.isStringNotEmpty(tex)) {
				if (Util.isStringNotEmpty(tex)) {
					if (run.getSubscript().getValue() == 3) {
						// SUBSCRIPT
						subString += tex;
						createSecTypePEmph(thePara,supStr, Constants.SUP_STR);
						itlStr = "";
						createSecTypePEmph(thePara,itlStr, Constants.ITALIC_STR);
						itlStr = "";
						if (Util.isStringNotEmpty(str)) {
							addSecTypePXref(thePara, str);
							str = "";
						}
					} else if (run.getSubscript().getValue() == 2) {
						// SUPERSCRIPT
						supStr += tex;
						createSecTypePEmph(thePara,subString, Constants.SUB_STR);
						subString = "";
						createSecTypePEmph(thePara,itlStr, Constants.ITALIC_STR);
						itlStr = "";
						if (Util.isStringNotEmpty(str)) {
							addSecTypePXref(thePara, str);
							str = "";
						}
					}else if (run.isItalic()) {
						// Italic
						itlStr += tex;
						createSecTypePEmph(thePara,subString, Constants.SUB_STR);
						subString = "";
						createSecTypePEmph(thePara,supStr, Constants.SUP_STR);
						supStr = "";
						if (Util.isStringNotEmpty(str)) {
							addSecTypePXref(thePara, str);
							str = "";
						}
					} else {
						str += tex;
						createSecTypePEmph(thePara,itlStr, Constants.ITALIC_STR);
						itlStr = "";
						createSecTypePEmph(thePara,subString, Constants.SUB_STR);
						subString = "";
						createSecTypePEmph(thePara,supStr, Constants.SUP_STR);
						supStr = "";
					}
				}
			}else {
				str += run.getText(0);
			}
		}
		createSecTypePEmph(thePara, itlStr, Constants.ITALIC_STR);
		itlStr = "";
		createSecTypePEmph(thePara, boldStr, Constants.BOLD_STR);
		boldStr = "";
		createSecTypePEmph(thePara,subString, Constants.SUB_STR);
		subString = "";
		createSecTypePEmph(thePara,supStr, Constants.SUP_STR);
		supStr = "";
		if (Util.isStringNotEmpty(str)) {
			addSecTypePXref(thePara, str);
			str = "";
		}
	}
	
	public List<HashMap<String, String>> processPara(XWPFParagraph theDocpara) {
		
		List<HashMap<String, String>> aItalicBoldList = new ArrayList<HashMap<String, String>>();
		for (XWPFRun run : theDocpara.getRuns()) {
			String tex = run.getText(0);
			tex = Util.removeNLineTabs(tex);
			
			if (Util.isStringNotEmpty(tex)) {
				HashMap<String, String> aItalicBoldMap = new HashMap<String, String>();
				
				if(run.isBold()){
					tex = tex.replaceFirst("Figure "+ theFigureNumber+".","");
					if (Util.isStringNotEmpty(tex.trim())) aItalicBoldMap.put(Constants.BOLD_STR, tex.trim());
				} else if (run.isItalic()){
					if (Util.isStringNotEmpty(tex.trim())) aItalicBoldMap.put(Constants.ITALIC_STR, tex.trim());
				} else{
					if (Util.isStringNotEmpty(tex.trim())) aItalicBoldMap.put(Constants.PARA_STR, tex.trim());
				}
				aItalicBoldList.add(aItalicBoldMap);
			}
		}
		return aItalicBoldList;
	}
	
	private void createSecTypePEmph(P thePara, String value, String type) {
		if (Util.isStringNotEmpty(value)) {
			thePara.getContent().add(new ObjectFactory().createArticleBodySecTypePItalic(value));
		}
	}

	private void addSecTypePXref(Article.Body.SecType.P thePara, String theParaStr) {

		theParaStr = Util.removeNLineTabs(theParaStr.trim());
		String[] strs = theParaStr.split("\\(");
		for (String str : strs) {
			String[] strs_ = str.split("\\)");
			int i = 0;

			for (String st : strs_) {
				if (i == 0 && Util.isInteger(strs_[0])){
					thePara.getContent().add("(");
						thePara.getContent().add(new ObjectFactory().createArticleBodySecTypePXref(createSecTypePXref(strs_[0])));
						i++;
						thePara.getContent().add(")");
				} else {
					thePara.getContent().add(st);
				}
			}
		}
	}
	
	private Article.Body.SecType.P.Xref createSecTypePXref(String theXrefValue) {
		try {
			
			Xref aXref = new ObjectFactory().createArticleBodySecTypePXref();
			aXref.setRefType("bibr");
			aXref.setRid("B" + String.format("%02d" , Integer.parseInt(theXrefValue)));
			aXref.setValue(Short.valueOf(theXrefValue));
			return aXref;
		}catch(Exception ex) {
			//ex.printStackTrace(); 
		}
		return null;
	}
	
	private void addSecTypeSecPXref(Article.Body.SecType.Sec.P thePara, String theParaStr) {

		theParaStr = Util.removeNLineTabs(theParaStr);
		String[] strs = theParaStr.split("\\(");
		for (String str : strs) {
			String[] strs_ = str.split("\\)");
			int i = 0;

			for (String st : strs_) {
				if (i == 0 && Util.isInteger(strs_[0])) {
					thePara.getContent().add("(");
					thePara.getContent().add(new ObjectFactory().createArticleBodySecTypeSecPXref(createSecTypeSecPXref(strs_[0])));
					thePara.getContent().add(")");
					i++;
				} else {
					thePara.getContent().add(st);
				}
			}
		}
	}
	
	private Article.Body.SecType.Sec.P.Xref createSecTypeSecPXref(String theXrefValue) {
		try {
			
			Article.Body.SecType.Sec.P.Xref aXref = new ObjectFactory().createArticleBodySecTypeSecPXref();
			aXref.setRefType("bibr");
			aXref.setRid("B" + String.format("%02d" , Integer.parseInt(theXrefValue)));
			aXref.setValue(theXrefValue);
			return aXref;
		}catch(Exception ex) {
			//ex.printStackTrace(); 
		}
		return null;
	}
}

