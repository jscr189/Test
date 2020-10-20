/**
 * 
 */
package com.qdriven.doc.handlers;

import generated.Article.Front;

/**
 * @author chada
 *
 */
public class FrontTagHandler {
	Front front;

	/**
	 * @return the front
	 */
	public Front getFront() {
		return front;
	}

	/**
	 * @param front the front to set
	 */
	public void setFront(Front front) {
		this.front = front;
	}

	public FrontTagHandler(String frontStr) {
		super();
		this.front = new Front();
	}
}
