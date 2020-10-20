package com.qdriven.doc.handlers;

import com.qdriven.doc.util.Constants;
import com.qdriven.doc.util.Util;

import generated.Article.Front.ArticleMeta.History;
import generated.Article.Front.ArticleMeta.History.Date;

public class HistoryTagHandler {
	History historyTag;
	String para;

	public HistoryTagHandler(String para) {
		super();
		this.para = para;
		historyTag = new History();
		processDates();
	}

	private void processDates() {
		
		// Received June 7, 2019 Accepted August 27, 2019
		String[] datesArray = para.split("");
		for (String aHistoryDate : datesArray) {
			aHistoryDate = aHistoryDate.trim();
			if (Util.isStringNotEmpty(aHistoryDate)) {
				if (aHistoryDate.contains(Constants.RECEIVED_STR)) {
					historyTag.getDate().add(processDate(aHistoryDate, Constants.RECEIVED_STR));
				} else if (aHistoryDate.contains(Constants.ACCEPTED_STR)) {
					historyTag.getDate().add(processDate(aHistoryDate, Constants.ACCEPTED_STR));
				}
			}

		}
	}

	private Date processDate(String theHistoryDate, String theDateType) {
		theHistoryDate = theHistoryDate.replaceAll(theDateType, "");
		String[] dateYearArray = theHistoryDate.trim().split(",");
		String[] dateStrArray = new String[3];
		if (dateYearArray.length == 2) {
			dateStrArray[2] = dateYearArray[1].trim();
		} else {
			System.out.println("-- date wrong format");
		}
		String[] mntDay = dateYearArray[0].trim().split(" ");
		int i = 0;
		for (String str : mntDay) {
			if (Util.isStringNotEmpty(str)) {
				dateStrArray[i] = Util.removeNLineTabs(str);
				i++;
			}
		}
		Date historyDate = new Date();
		historyDate.setDateType(theDateType.toLowerCase());
	
		if(dateStrArray.length>=1)
			historyDate.setMonth(convertMonthToShort(dateStrArray[0].toLowerCase()));
		if(dateStrArray.length>=2)
			historyDate.setDay(Short.parseShort(dateStrArray[1]));
		if(dateStrArray.length>=3 && dateStrArray[2] !=null)
			historyDate.setYear(Integer.parseInt(dateStrArray[2]));

		return historyDate;
	}

	public History getHistoryTag() {
		return historyTag;
	}

	private short convertMonthToShort(String theMonth){
		Short aMonthInShort = -1;
		switch(theMonth) {
        case "january":
            aMonthInShort = 01;
        break;

        case "febuary":
            aMonthInShort = 02;
        break;

        case "march":
            aMonthInShort = 03;
        break;

        case "april":
            aMonthInShort = 04;
        break;

        case "may":
            aMonthInShort = 05;
        break;

        case "june":
            aMonthInShort = 06;
        break;

        case "july":
            aMonthInShort = 07;
        break;

        case "august":
            aMonthInShort = 8;
        break;

        case "september":
            aMonthInShort = 9;
        break;

        case "october":
            aMonthInShort = 10;
        break;

        case "november":
            aMonthInShort = 11;
        break;
        
        case "december":
            aMonthInShort = 12;
        break;

        }
		return aMonthInShort;
	}
}
