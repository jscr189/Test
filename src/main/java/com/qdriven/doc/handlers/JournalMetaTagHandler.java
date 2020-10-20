/**
 * 
 */
package com.qdriven.doc.handlers;

import generated.Article.Front;
import generated.Article.Front.JournalMeta;
import generated.Article.Front.JournalMeta.Issn;
import generated.Article.Front.JournalMeta.JournalId;
import generated.Article.Front.JournalMeta.JournalTitleGroup;
import generated.Article.Front.JournalMeta.Publisher;
import generated.Article.Front.JournalMeta.JournalTitleGroup.AbbrevJournalTitle;

/**
 * @author chada
 *
 */
public class JournalMetaTagHandler {
	Front front;
	JournalMeta journalMeta = new JournalMeta();
	public JournalMetaTagHandler(Front front,String aJournalMetaStr) {
		super();
		this.front = new Front();
		this.journalMeta = new JournalMeta();
		processJournalMeta(aJournalMetaStr);
	}

	/**
	 * @return the journalMeta
	 */
	public JournalMeta getJournalMeta() {
		return journalMeta;
	}

	/**
	 * @param journalMeta the journalMeta to set
	 */
	public void setJournalMeta(JournalMeta journalMeta) {
		this.journalMeta = journalMeta;
	}

	private void processJournalMeta(String aJournalMetaStr) {
		String[] strs = aJournalMetaStr.split("\\(");
		
		JournalId journalId = new JournalId();
			journalId.setJournalIdType("nlm-ta");
			journalId.setValue("Braz J Med Biol Res");
			
		journalMeta.getJournalId().add(journalId);
		journalId = new JournalId();
			journalId.setJournalIdType("publisher-id");
			journalId.setValue("bjmbr");
		journalMeta.getJournalId().add(journalId);
		
		Issn issn = new Issn();
			issn.setPubType("epub");
			issn.setValue("1414-431X");
		journalMeta.setIssn(issn);
		JournalTitleGroup journalTitleGroup = new JournalTitleGroup();
			journalTitleGroup.setJournalTitle(strs[0].trim());
		AbbrevJournalTitle abbrevJournalTitle = new AbbrevJournalTitle();
			abbrevJournalTitle.setAbbrevType("publisher");
			abbrevJournalTitle.setValue("Braz. J. Med. Biol. Res.");
		journalTitleGroup.setAbbrevJournalTitle(abbrevJournalTitle);
			journalMeta.setJournalTitleGroup(journalTitleGroup);
		Publisher publisher = new Publisher();
			publisher.setPublisherName("Associa&#xe7;&#xe3;o Brasileira de Divulga&#xe7;&#xe3;o Cient&#xed;fica");
		journalMeta.setPublisher(publisher);
	}
}
