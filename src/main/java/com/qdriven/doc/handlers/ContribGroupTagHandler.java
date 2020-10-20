package com.qdriven.doc.handlers;

import generated.Article.Front.ArticleMeta.ContribGroup;
import generated.Article.Front.ArticleMeta.ContribGroup.Aff;
import generated.Article.Front.ArticleMeta.ContribGroup.Aff.AddrLine;
import generated.Article.Front.ArticleMeta.ContribGroup.Aff.AddrLine.NamedContent;
import generated.Article.Front.ArticleMeta.ContribGroup.Aff.Country;
import generated.Article.Front.ArticleMeta.ContribGroup.Aff.Institution;
import generated.Article.Front.ArticleMeta.ContribGroup.Contrib;
import generated.Article.Front.ArticleMeta.ContribGroup.Contrib.ContribId;
import generated.Article.Front.ArticleMeta.ContribGroup.Contrib.Name;
import generated.Article.Front.ArticleMeta.ContribGroup.Contrib.Xref;

import com.qdriven.doc.util.Constants;
import com.qdriven.doc.util.Util;

import generated.ObjectFactory;

public class ContribGroupTagHandler {
	ContribGroup contribGroup;
	String para;

	public ContribGroupTagHandler(String para) {
		super();
		this.para = para;
		contribGroup = new ContribGroup();
		processContribGroup();
	}
	
	public ContribGroupTagHandler(String para, ContribGroup theContribGroup) {
		super();
		this.para = para;
		contribGroup = theContribGroup;
		processAff();
	}
	
	
	private void processContribGroup() {
		String[] names = para.split(",");
		
		for (String name_ : names) {
			
			name_ = name_.trim();
			String[] name = name_.split("\\("); 
			
			Contrib contribTag = new Contrib();
			Name nameTag = new Name();
			if (name.length == 2) {
				contribTag.setContribType("author");
				ContribId aContribId = new ContribId();
				aContribId.setContribIdType("orcid");
				String aContribLink = name_.substring(name_.lastIndexOf("/")+1, name_.lastIndexOf(")"));
				aContribId.setValue(aContribLink);
				contribTag.setContribId(aContribId);
				name_ = name[0].trim();
				String[] nameDif = name_.split(" ");
				if (nameDif.length >= 2) {
					String givenN = nameDif[nameDif.length-2].trim();
					String surN = nameDif[nameDif.length - 1].trim();
					if(Util.isStringNotEmpty(givenN)) 
						nameTag.setGivenNames(givenN);
					if(Util.isStringNotEmpty(surN)) 
						nameTag.setSurname(surN);
				} else if (nameDif.length >= 1) {
					String givenN = nameDif[nameDif.length-1].trim();
					if(Util.isStringNotEmpty(givenN)) 
						nameTag.setGivenNames(givenN);
				}
				contribTag.setName(nameTag);
				String[] ref_ = name[1].trim().split("\\)");
				Xref xrefTag = new Xref();
				if (ref_.length == 2) {
					String ref = ref_[1].trim();
					xrefTag.setRefType("aff");
					xrefTag.setRid("aff"+ref.trim());
					xrefTag.setValue(ref.trim());
					contribTag.getXref().add(xrefTag);
				} else {
					System.out.println("worng ref!!"+name_);
				}
				contribGroup.getContrib().add(contribTag);
			}
		}
	}
	
	private void processAff(){
		String[] names = para.split(",");
			
		Aff affTag = new Aff();
		String refId = names[0].substring(0, 1);
		affTag.setId(Constants.AFF_STR + refId);
		try{
			affTag.getContent().add(new ObjectFactory().createArticleFrontArticleMetaContribGroupAffLabel(Short.valueOf(refId)));
		}catch (NumberFormatException npe){
			System.out.println("Not a valid Ref ID in ContribGroupTagHandler.processAff .."+refId);
		}
        
		Institution institutionTag = new Institution();
		
		institutionTag.setContentType("orgname");
		institutionTag.setValue(names[1].trim());
		affTag.getContent().add(new ObjectFactory().createArticleFrontArticleMetaContribGroupAffInstitution(institutionTag));
		
		institutionTag = new Institution();
		institutionTag.setContentType("normalized");
		institutionTag.setValue(names[1].trim());
		affTag.getContent().add(new ObjectFactory().createArticleFrontArticleMetaContribGroupAffInstitution(institutionTag));
		
		institutionTag = new Institution();
		institutionTag.setContentType("orgdiv1");
		institutionTag.setValue(names[0].substring(1));
		affTag.getContent().add(new ObjectFactory().createArticleFrontArticleMetaContribGroupAffInstitution(institutionTag));
		
		AddrLine addrLine = new AddrLine();
		if(names.length==5){
			NamedContent namedContent = new NamedContent();
			namedContent.setContentType("city");
			namedContent.setValue(names[2].trim());
			addrLine.getContent().add(new ObjectFactory().createArticleFrontArticleMetaContribGroupAffAddrLineNamedContent(namedContent));
		}
		
		NamedContent namedContent2 = new NamedContent();
		namedContent2.setContentType("state");
		if(names.length==5)
			namedContent2.setValue(names[3].trim());
		else if(names.length==4)
			namedContent2.setValue(names[2].trim());
		addrLine.getContent().add(new ObjectFactory().createArticleFrontArticleMetaContribGroupAffAddrLineNamedContent(namedContent2));
		affTag.getContent().add(new ObjectFactory().createArticleFrontArticleMetaContribGroupAffAddrLine(addrLine));
		
		Country country = new Country();
		country.setCountry("Country_Code");
		if(names.length==5)
			country.setValue(names[4].trim());
		else if(names.length==4)
			country.setValue(names[3].trim());
		affTag.getContent().add(new ObjectFactory().createArticleFrontArticleMetaContribGroupAffCountry(country));
	
		institutionTag = new Institution();
		institutionTag.setContentType("original");
		institutionTag.setValue(para.substring(1).trim());
		affTag.getContent().add(new ObjectFactory().createArticleFrontArticleMetaContribGroupAffInstitution(institutionTag));
		
		contribGroup.getAff().add(affTag);
	}
	
	public ContribGroup getContribGrp() {
		return contribGroup;
	}
	
}
