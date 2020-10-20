package com.qdriven.doc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import com.qdriven.doc.handlers.AbstractTagHandler;
import com.qdriven.doc.handlers.ArticleMetaTagHandler;
import com.qdriven.doc.handlers.ArticleTag;
import com.qdriven.doc.handlers.AuthorNotesCorrespTagHandler;
import com.qdriven.doc.handlers.BodyTagHandler;
import com.qdriven.doc.handlers.ContribGroupTagHandler;
import com.qdriven.doc.handlers.FrontTagHandler;
import com.qdriven.doc.handlers.HistoryTagHandler;
import com.qdriven.doc.handlers.JournalMetaTagHandler;
import com.qdriven.doc.handlers.KeyWordTagHandler;
import com.qdriven.doc.handlers.ReferencesHandler;
import com.qdriven.doc.handlers.TitleGroupTagHandler;
import com.qdriven.doc.util.Constants;
import com.qdriven.doc.util.Util;

import generated.Article;
import generated.Article.Front;
import generated.Article.Front.ArticleMeta.ContribGroup;

public class ProcessDocThread {
	private File file;

	public ProcessDocThread(File file) {
		super();
		this.file = file;
	}

	public String convert() {
		FileInputStream fis;
		long startTime = System.nanoTime();
		String out = "Error occurred while processing the file "+file.getName();
		try {
			fis = new FileInputStream(file);
			String fileNum = file.getName().split("_")[0];
			System.out.println("File Process started "+file.getName());
			String output = "out_" + fileNum + ".xml";

			ArticleTag articleTag = new ArticleTag();
			Article article	= articleTag.getArticle();
			BodyTagHandler bodyT = new BodyTagHandler();
			ReferencesHandler refHandler = null;
			FrontTagHandler frontTagHandler = null;
			Front front = null;
			String aHeaderStr = Constants.JOURNAL_STR;
			XWPFDocument xdoc = new XWPFDocument(OPCPackage.open(fis));
			List paragraphList = (List) xdoc.getParagraphs();
			String theElocationID = null;
			for (Object paragraph : paragraphList) {
				XWPFParagraph docPara = (XWPFParagraph) paragraph;
				String javaParaStr = docPara.getText();
				javaParaStr = Util.removeNLineTabs(javaParaStr);
				
				if (Util.isStringNotEmpty(javaParaStr)) {
					boolean isHeader = this.isHeader(docPara);
					
					if(isHeader){
						if (javaParaStr.equals(Constants.ABSTRACT_STR)) {
							aHeaderStr = Constants.ABSTRACT_STR;
							continue;
						} else if (javaParaStr.equals(Constants.INTRODUCTION_STR)) {
							aHeaderStr = Constants.INTRODUCTION_STR;
							bodyT.processBody(Constants.INTRODUCTION_STR);
							continue;
						} else if (javaParaStr.equals(Constants.MATERIAL_METHODS)) {
							aHeaderStr = Constants.MATERIAL_METHODS;
							bodyT.processBody(Constants.MATERIAL_METHODS);
							continue;
						} else if (javaParaStr.equals(Constants.CASE_REPORT_STR)) {
							aHeaderStr = Constants.CASE_REPORT_STR;
							bodyT.processBody(Constants.CASE_REPORT_STR);
							continue;
						} else if (javaParaStr.equals(Constants.RESULTS_STR)) {
							aHeaderStr = Constants.RESULTS_STR;
							bodyT.processBody(Constants.RESULTS_STR);
							continue;
						} else if (javaParaStr.equals(Constants.DISCUSSION_STR)) {
							aHeaderStr = Constants.DISCUSSION_STR;
							bodyT.processBody(Constants.DISCUSSION_STR);
							continue;
						} else if (javaParaStr.equals(Constants.CONCLUSIONS_STR)) {
							aHeaderStr = Constants.CONCLUSIONS_STR;
							bodyT.processBody(Constants.CONCLUSIONS_STR);
							continue;
						} else if (javaParaStr.equals(Constants.ACKNOWLEDGMENTS_STR)) {
							aHeaderStr = Constants.ACKNOWLEDGMENTS_STR;
							bodyT.processBody(Constants.ACKNOWLEDGMENTS_STR);
							continue;
						} else if (javaParaStr.equals(Constants.SUPPLEMENTARY_MATERIAL_STR)) {
							aHeaderStr = Constants.SUPPLEMENTARY_MATERIAL_STR;
							bodyT.processBody(Constants.SUPPLEMENTARY_MATERIAL_STR);
							continue;
						} else if (javaParaStr.equals(Constants.REFERENCES_STR)) {
							aHeaderStr = Constants.REFERENCES_STR;
							refHandler = new ReferencesHandler();
							article.setBack(refHandler.getBack());
							continue;
						}  else if(aHeaderStr.equals(Constants.MATERIAL_METHODS) || javaParaStr.equals(Constants.CASE_REPORT_STR)){
							bodyT.processSecTypeSec(docPara, javaParaStr);
						} else if(aHeaderStr.equals(Constants.RESULTS_STR)){
							bodyT.processSecTypeSec(docPara, javaParaStr);
						} else if(javaParaStr.contains("Table ")){
							break;
						}
					} else {
						if(aHeaderStr.equals(Constants.ABSTRACT_STR)){
							
							if(javaParaStr.contains(Constants.KEY_WORDS_STR)){
								KeyWordTagHandler keyWrdHandler = new KeyWordTagHandler(javaParaStr);
								front.getArticleMeta().setKwdGroup(keyWrdHandler.getKeywdGrp());
							} else if(javaParaStr.contains(Constants.CORRESPONDENCE_STR)){
								AuthorNotesCorrespTagHandler authorNotesCorrespTagHandler = new AuthorNotesCorrespTagHandler(javaParaStr);
								front.getArticleMeta().setAuthorNotes(authorNotesCorrespTagHandler.getAuthorGrp());
							} else if(javaParaStr.contains(Constants.RECEIVED_STR)){
								front.getArticleMeta().setElocationId(theElocationID);
								HistoryTagHandler historyTagHandler = new HistoryTagHandler(javaParaStr);
								front.getArticleMeta().setHistory(historyTagHandler.getHistoryTag());
							} else if(javaParaStr.contains(Constants.RUNNING_TITLE_STR)){
								//TODO no implementation for now
							} else {
								AbstractTagHandler tgHandler = new AbstractTagHandler(docPara);
								front.getArticleMeta().setAbstract(tgHandler.getAbstract());
							} 
						} else if(aHeaderStr.equals(Constants.INTRODUCTION_STR)) {
							bodyT.processSecTypeP(docPara,javaParaStr);
							article.setBody(bodyT.getBody());
						} else if(aHeaderStr.equals(Constants.MATERIAL_METHODS) || aHeaderStr.equals(Constants.CASE_REPORT_STR)) {
							bodyT.processSecTypeOrSec(docPara,javaParaStr);
						} else if(aHeaderStr.equals(Constants.RESULTS_STR)) {
							bodyT.processSecTypeOrSec(docPara,javaParaStr);
						} else if(aHeaderStr.equals(Constants.DISCUSSION_STR)) {
							bodyT.processSecTypeP(docPara ,javaParaStr);
						} else if(aHeaderStr.equals(Constants.CONCLUSIONS_STR) || 
								aHeaderStr.equals(Constants.ACKNOWLEDGMENTS_STR) || 
								aHeaderStr.equals(Constants.SUPPLEMENTARY_MATERIAL_STR)) {
							bodyT.processSecTypeP(docPara,javaParaStr);
						} else if(javaParaStr.contains(Constants.FIGURE_STR)) {
							bodyT.processFigure(docPara,javaParaStr);
							aHeaderStr = Constants.FIGURE_STR;
						} else if(aHeaderStr.equals(Constants.REFERENCES_STR) && !javaParaStr.contains("Table ")) {
							new ReferencesHandler(article.getBack(),javaParaStr);
						} else if(aHeaderStr.equals(Constants.JOURNAL_STR)){
							if (null == frontTagHandler) {
								String aSplitPara =javaParaStr.split(",")[0];
								theElocationID = aSplitPara.substring(aSplitPara.lastIndexOf(":")+1,aSplitPara.length()).trim();
								frontTagHandler = new FrontTagHandler(javaParaStr);
								front = frontTagHandler.getFront();
								JournalMetaTagHandler journalMetaTagHandler = new JournalMetaTagHandler(front,javaParaStr);
								front.setJournalMeta(journalMetaTagHandler.getJournalMeta());
								ArticleMetaTagHandler articleMetaTagHandler = new ArticleMetaTagHandler(javaParaStr);
								front.setArticleMeta(articleMetaTagHandler.getArticleMeta());
								article.setFront(front);
								isHeader= true;
							} else if (null == front.getArticleMeta().getTitleGroup()) {
								TitleGroupTagHandler tgHandler = new TitleGroupTagHandler(javaParaStr);
								front.getArticleMeta().setTitleGroup(tgHandler.getTitleGrp());
							} else if (null == front.getArticleMeta().getContribGroup()) {
								ContribGroupTagHandler tgHandler = new ContribGroupTagHandler(javaParaStr);
								front.getArticleMeta().setContribGroup(tgHandler.getContribGrp());
							} else if (!aHeaderStr.equals(Constants.ABSTRACT_STR)) {
								ContribGroup contribGroup = front.getArticleMeta().getContribGroup();
								ContribGroupTagHandler tgHandler = new ContribGroupTagHandler(javaParaStr,contribGroup);
								front.getArticleMeta().setContribGroup(tgHandler.getContribGrp());
							}
						}
					}
				}
			}

			File file = new File(System.getProperty("user.dir")+"/src/main/resources/output/" + output);
			
			JAXBContext jaxbContext = JAXBContext.newInstance(Article.class);
			// CreateMarshaller
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			// Required formatting??
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
			// Writes XML file to file-system
			jaxbMarshaller.marshal(article, file);
			long endTime = System.nanoTime();
			out = "Took " + (endTime - startTime)/1000 + " ns to process the file "+file.getName()+". Output file - "+output+" is generated successfully!!!";
			// thisClass.adjustOutputFile(file, "src/main/resources/out/"+output);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;
	}

	public boolean isHeader(XWPFParagraph para) {
		
		boolean isHeader = false;
		for (XWPFRun run : para.getRuns()) {
			String aParaText = run.getText(0);
			aParaText = Util.removeNLineTabs(aParaText);
			if (run.isBold() && Util.isStringNotEmpty(aParaText)) {
				isHeader = true;
			} else if (!run.isBold() && Util.isStringNotEmpty(aParaText)) {
				isHeader = false;
			}
		}
		return isHeader;
	}

}
