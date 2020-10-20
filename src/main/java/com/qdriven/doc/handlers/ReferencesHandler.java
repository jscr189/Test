package com.qdriven.doc.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.qdriven.doc.util.Constants;
import com.qdriven.doc.util.Util;

import generated.Article.Back;
import generated.Article.Back.RefList;
import generated.Article.Back.RefList.Ref;
import generated.Article.Back.RefList.Ref.ElementCitation;
import generated.Article.Back.RefList.Ref.ElementCitation.ArticleTitle;
import generated.Article.Back.RefList.Ref.ElementCitation.PersonGroup;
import generated.Article.Back.RefList.Ref.ElementCitation.PersonGroup.Name;
import generated.Article.Back.RefList.Ref.ElementCitation.Source;
import generated.Article.Back.RefList.Ref.MixedCitation;

public class ReferencesHandler {
	Back aBack;

	public ReferencesHandler() {
		aBack = new Back();
		RefList aRefList = new RefList();
		aRefList.setTitle("References");
		aBack.setRefList(aRefList);
	}
	
	public Back getBack() {
		return this.aBack;
	}
	
	public ReferencesHandler (Back theBack, String theRefText){
		this.aBack = theBack;
		RefList aRefList = getBack().getRefList();
		String aRefStr = Util.removeNLineTabs(theRefText);
		aRefStr = aRefStr.replaceAll("[\\n\\t ]", " ");
		aRefStr = aRefStr.trim();
		int charAt = aRefStr.indexOf(".");
		Ref aRef = new Ref();
		try{
			Short aRefID = Short.valueOf(aRefStr.substring(0,charAt));
			aRef.setId("B"+String.format("%02d" , aRefID));
			aRef.setLabel(aRefID);
		}catch (NumberFormatException npe){
			System.out.println("Not a valid RefID in ReferencesHandler con.. "+aRefStr.substring(0,charAt));
		}
		
		MixedCitation aMixedCit = new MixedCitation();
		aMixedCit.getContent().add(theRefText);
		aRef.setMixedCitation(aMixedCit);
		
		ElementCitation aElementCit = new ElementCitation();
		aElementCit.setPublicationType("journal");
		
		PersonGroup aPersonGrp = new PersonGroup();
		aPersonGrp.setPersonGroupType("author");
		aPersonGrp.getName().addAll(processNames(theRefText));
		
		aElementCit.setPersonGroup(aPersonGrp);
		processReffValue(aElementCit,theRefText);
		
		aRef.setElementCitation(aElementCit);
		aRefList.getRef().add(aRef);
	}
	
	public List<Name> processNames(String theNames) {
		List<Name> aNameList = new ArrayList<Name>();
		int charAt = theNames.indexOf(".");
		try{
			String aNamesStr = theNames.substring(charAt+1,theNames.length()-1).trim();
			if(aNamesStr.indexOf(".") != -1) {
				String aNamesStrs = aNamesStr.substring(0,aNamesStr.indexOf("."));
				String[] aNamesArry = aNamesStrs.split(",");
				
				// Zhu Z, Song L, He J, Sun Y, Liu X, Zou X,
				
				for (String aNameStr : aNamesArry) {
					Name Name = new Name();
					// Zhu Z
					if(aNameStr.trim().equals("et al")) {
					}else {
						String[] nameArry = aNameStr.split(" ");
						StringBuilder namesStrBuilder = new StringBuilder();
						for(int i =0;i<nameArry.length-1;i++){
							namesStrBuilder.append(nameArry[i]);
						}
						Name.setSurname(namesStrBuilder.toString().trim());
						Name.setGivenNames(nameArry[nameArry.length-1].trim());
						aNameList.add(Name);
					}
				}
			}
		} catch (Exception aIOOBExp){
			System.out.println(aIOOBExp.getMessage() +" Not a Valid Names In ReferencesHandler.processNames "+theNames);
		}
		return aNameList;
	}
	
	public void processReffValue(ElementCitation theElementCit, String theRefStr) {
		ArticleTitle aArticleTitle = new ArticleTitle();
		Source aSource = new Source();
		
		theRefStr = Util.removeNLineTabs(theRefStr);
		theRefStr = theRefStr.replaceAll("[\\n\\t ]", " ");
		theRefStr = theRefStr.trim();
		theRefStr = theRefStr.split("doi:")[0].trim();
		if (theRefStr != null && !theRefStr.equals("")) {
			String[] aVRefSryArray = theRefStr.split("\\.");
			int limt = aVRefSryArray.length;
			int cliten = 0;

			for (int i = limt - 1; i > 0; i++) {
				aVRefSryArray[i] = aVRefSryArray[i].trim();
				if (aVRefSryArray[i] != null && !aVRefSryArray[i].equals("")) {
					Pattern p1 = Pattern.compile(Constants.REG_EX_ONLY_4_DIGITS);
					if (p1.matcher(aVRefSryArray[i]).matches()) {
						theElementCit.setYear(Integer.parseInt(aVRefSryArray[i]));
					} else {
						// J Clin Pathol 2013; 66: 644–648.
						// (\w*\s)*\d{4};\s*\d+:\s*\d+
						String[] values_0 = aVRefSryArray[i].split(";");
						// J Clin Pathol 2013 split at number
						String[] values_1 = values_0[0].split("(?<=\\D)(?=\\d)");
						aSource.setLang("en");
						aSource.setValue(values_1[0].trim());
						theElementCit.setSource(aSource);
						try{
							if (values_1.length > 1) {
								
								theElementCit.setYear(Integer.parseInt(values_1[1].trim()));
								
								// 66: 644–648
								String[] values_2 = values_0[1].trim().split(":");
								theElementCit.setVolume(String.valueOf(values_2[0].trim()));
								
								// 644–648
								if (values_2.length > 1) {
									String[] values_3 = values_2[1].split("–");
									theElementCit.setFpage(String.valueOf(values_3[0].trim()));
									if (values_3.length > 1)
									theElementCit.setLpage(String.valueOf(values_3[1].trim()));
								}
								
							} else {
								theElementCit.setYear(Integer.parseInt(values_0[1].trim()));
							}
						} catch (Exception nfe){
							//System.out.println(nfe.getMessage()+ " Not a Valid Names In ReferencesHandler.processReffValue "+theRefStr);
						}
					}
					cliten = i;
					break;
				}
			}
			String clitVal = "";
			List <Name> names= processNames(theRefStr);
			for (int i = 1; i < cliten; i++) {
				 
				if(null != theElementCit.getSource() && aVRefSryArray[i].contains(theElementCit.getSource().getValue()))
					continue;
				if(aVRefSryArray[i].trim().equals("et al")) 
					continue;
				
				String[] nameArry = aVRefSryArray[i].trim().split(" ");
				boolean flag = true;
				for(Name name: names){
					if(nameArry[0].equals(name.getSurname())) flag = false; break;
				}
				if(flag)
				clitVal += aVRefSryArray[i];
			}
			aArticleTitle.getContent().add(clitVal.trim());
		}
		theElementCit.setArticleTitle(aArticleTitle);
	}

}
