/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.waterforpeople.mapping.app.gwt.client.displaytemplate;

import java.io.Serializable;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class DisplayTemplateMappingDto extends BaseDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8172849878858010289L;
	/**
	 * The idea is that each object hold what is a row in the current velocity
	 * templates
	 */

	private String languageCode = null;
	private String rowDescription = null;
	private String attributeName = null;
	private String attributeFormattingInstructions = null;
	private Integer displayOrder = null;
	private String accessPointType = null;

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getRowDescription() {
		return rowDescription;
	}

	public void setRowDescription(String rowDescription) {
		this.rowDescription = rowDescription;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getAttributeFormattingInstructions() {
		return attributeFormattingInstructions;
	}

	public void setAttributeFormattingInstructions(
			String attributeFormattingInstructions) {
		this.attributeFormattingInstructions = attributeFormattingInstructions;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public String getAccessPointType() {
		return accessPointType;
	}

	public void setAccessPointType(String accessPointType) {
		this.accessPointType = accessPointType;
	}

}
