package org.waterforpeople.mapping.adapter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.MappingSpreadsheetColumnToAttribute;
import org.waterforpeople.mapping.domain.MappingSpreadsheetDefinition;
import org.waterforpeople.mapping.helper.SpreadsheetMappingAttributeHelper;

import com.gallatinsystems.common.data.spreadsheet.GoogleSpreadsheetAdapter;
import com.gallatinsystems.common.data.spreadsheet.domain.ColumnContainer;
import com.gallatinsystems.common.data.spreadsheet.domain.RowContainer;
import com.gallatinsystems.common.data.spreadsheet.domain.SpreadsheetContainer;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.google.gdata.util.ServiceException;

public class SpreadsheetAccessPointAdapter {
	private static final Logger log = Logger
			.getLogger(SpreadsheetAccessPointAdapter.class.getName());

	public void processSpreadsheetOfAccessPoints(String spreadsheetName) {
		GoogleSpreadsheetAdapter gsa = new GoogleSpreadsheetAdapter();
		BaseDAO<AccessPoint> accessPointBaseDAO = new BaseDAO<AccessPoint>(
				AccessPoint.class);
		try {
			SpreadsheetContainer sc = gsa
					.getSpreadsheetContents(spreadsheetName);
			for (RowContainer row : sc.getRowContainerList()) {
				AccessPoint ap = processRow(row, spreadsheetName);
				accessPointBaseDAO.save(ap);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<String> listColumns(String spreadsheetName)
			throws IOException, ServiceException {
		GoogleSpreadsheetAdapter gas = new GoogleSpreadsheetAdapter();
		return gas.listColumns(spreadsheetName);
	}

	public ArrayList<String> listSpreadsheets(String feedURL)
			throws IOException, ServiceException {
		return new GoogleSpreadsheetAdapter().listSpreasheets(feedURL);
	}

	private AccessPoint processRow(RowContainer row, String spreadsheetName) {
		AccessPoint ap = new AccessPoint();
		// Structure of string from GoogleSpreadsheet
		// <gsx:dateofvisit>10/27/2009</gsx:dateofvisit>
		// <gsx:latitude>8.153982</gsx:latitude>
		// <gsx:longitude>-15.4330992</gsx:longitude>
		// <gsx:communitycode>PC24</gsx:communitycode>
		// <gsx:watersystemstatus>System needs repair but is
		// functioning</gsx:watersystemstatus>
		// <gsx:photocodeforprimarywatertechnology>No
		// photo</gsx:photocodeforprimarywatertechnology>
		// <gsx:linksforphotos>No photo</gsx:linksforphotos>
		// <gsx:captianforwaterpointphoto>Gravity fed system that needs
		// repairs</gsx:captianforwaterpointphoto>
		// <gsx:photoofprimarysanitationtechnology>No
		// photo</gsx:photoofprimarysanitationtechnology>
		// <gsx:linkforphotos>PC24san</gsx:linkforphotos>
		// <gsx:typeofsanitaitontechnology>Ventilated improved pit
		// latrines</gsx:typeofsanitaitontechnology>
		// <gsx:primaryimprovedsanitationtechnologyinuseinthecommunity>Don't
		// know</gsx:primaryimprovedsanitationtechnologyinuseinthecommunity>
		// <gsx:numberofhouseholdswithimprovedsanitation>Don't
		// know</gsx:numberofhouseholdswithimprovedsanitation>
		Class cls = null;
		HashMap<String, String> attributeTypeMap = new HashMap<String, String>();

		try {
			cls = Class
					.forName("org.waterforpeople.mapping.domain.AccessPoint");
			Method methlist[] = cls.getDeclaredMethods();

			for (Method method : methlist) {
				String methodName = method.getName();
				if (methodName.contains("set")) {
					Class[] paramTypes = method.getParameterTypes();
					if (paramTypes.length > 0) {
						attributeTypeMap.put(methodName, paramTypes[0]
								.getName());
					}
				}
			}

		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		HashMap<String, String> colsToAttributesMap = getColsToAttributeMap(spreadsheetName);
		for (ColumnContainer col : row.getColumnContainersList()) {
			String colName = col.getColName();
			String attributeName = colsToAttributesMap.get(colName);
			if (attributeName != null && !attributeName.trim().isEmpty()) {
				try {

					Class partypes[] = new Class[1];
					String paramTypeClass = attributeTypeMap.get("set"
							+ attributeName);
					partypes[0] = Class.forName(paramTypeClass);
					java.lang.reflect.Method meth = cls.getMethod("set"
							+ attributeName, partypes);

					if (paramTypeClass.contains("Double")) {
						Object arglist[] = new Object[1];
						arglist[0] = parseDouble(col.getColContents());

						Object retobj = meth.invoke(ap, arglist);
					} else if (paramTypeClass.contains("String")) {
						Object arglist[] = new Object[1];
						arglist[0] = col.getColContents();

						Object retobj = meth.invoke(ap, arglist);
					} else if (paramTypeClass.contains("Date")) {
						Object arglist[] = new Object[1];
						arglist[0] = parseDate(col.getColContents());
						Object retobj = meth.invoke(ap, arglist);
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		// for (ColumnContainer col : row.getColumnContainersList()) {
		// if (col.getColName().equals("dateofvisit")) {
		// ap.setCreatedDateTime(parseDate(col.getColContents()));
		// } else if (col.getColName().equals("latitude")) {
		// ap.setLatitude(parseDouble(col.getColContents()));
		// } else if (col.getColName().equals("longitude")) {
		// ap.setLongitude(parseDouble(col.getColContents()));
		// } else if (col.getColName().equals("communitycode")) {
		// ap.setCommunityCode(col.getColContents());
		// } else if (col.getColName().equals("watersystemstatus")) {
		// ap.setPointStatus(col.getColContents());
		// } else if (col.getColName().equals(
		// "photocodeforprimarywatertechnology")) {
		//
		// } else if (col.getColName().equals("linksforphotos")) {
		// ap.setPhotoURL(col.getColContents());
		// } else if (col.getColName().equals("captianforwaterpointphoto")) {
		// ap.setPointPhotoCaption(col.getColContents());
		// } else if (col.getColName().equals(
		// "photoofprimarysanitationtechnology")) {
		//
		// } else if (col.getColName().equals("typeofsanitaitontechnology")) {
		//
		// } else if (col.getColName().equals(
		// "primaryimprovedsanitationtechnologyinuseinthecommunity")) {
		//
		// } else if (col.getColName().equals(
		// "numberofhouseholdswithimprovedsanitation")) {
		//
		// }
		// }
		return ap;
	}

	private Date parseDate(String value) {
		Date date;
		try {
			date = new Date(value);
		} catch (Exception ex) {
			date = null;
		}
		return date;
	}

	private Double parseDouble(String value) {
		Double valueD;
		try {
			valueD = new Double(value);
		} catch (Exception ex) {
			valueD = 0.0;
		}
		return valueD;
	}

	private HashMap getColsToAttributeMap(String spreadsheetName) {
		SpreadsheetMappingAttributeHelper samh = new SpreadsheetMappingAttributeHelper();
		MappingSpreadsheetDefinition mapDef = new MappingSpreadsheetDefinition();
		mapDef = samh.getMappingSpreadsheetDefinition(spreadsheetName);
		HashMap<String, String> colsToAttributesMap = new HashMap<String, String>();
		for (MappingSpreadsheetColumnToAttribute item : mapDef.getColumnMap()) {
			String capedString = item.getObjectAttribute().substring(0,1).toUpperCase();
			capedString = capedString + item.getObjectAttribute().substring(1);
			colsToAttributesMap.put(item.getSpreadsheetColumn(),capedString);
		}
		return colsToAttributesMap;
	}

}
