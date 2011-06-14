package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.HashMap;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.config.ConfigurationItemDto;
import org.waterforpeople.mapping.app.gwt.client.config.ConfigurationService;
import org.waterforpeople.mapping.app.gwt.client.config.ConfigurationServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.framework.gwt.wizard.client.ContextAware;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Edits/creates Survey objects
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyEditWidget extends Composite implements ContextAware,
		ChangeHandler, ClickHandler {

	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	private static final String POINT_TYPES_CONFIG = "pointTypes";
	private static final String FORM_LABEL_CSS = "input-label-padded";
	private static final String TXT_BOX_CSS = "txt-box";
	private static final String WP_TYPE = "WaterPoint";
	private static final String SP_TYPE = "SanitationPoint";
	private static final String PI_TYPE = "PublicInstitution";
	private static final String TRAWLER_TYPE = "Trawler";
	private static final String PROCEDURE_TYPE = "Procedure";

	private VerticalPanel panel;
	private Map<String, Object> bundle;
	private TextBox nameBox;
	private TextBox descriptionBox;
	private TextBox versionBox;
	private ListBox pointTypeBox;
	private Label surveyIdLabel;
	private SurveyServiceAsync surveyService;
	private SurveyDto currentDto;
	private SurveyGroupDto groupDto;
	private boolean isChanged;
	private Button editNotificationButton;

	public SurveyEditWidget() {
		panel = new VerticalPanel();
		surveyService = GWT.create(SurveyService.class);
		nameBox = new TextBox();
		descriptionBox = new TextBox();
		versionBox = new TextBox();
		versionBox.setReadOnly(true);
		pointTypeBox = new ListBox(false);
		ConfigurationServiceAsync configService = GWT
				.create(ConfigurationService.class);
		configService.getConfigurationItem(POINT_TYPES_CONFIG,
				new AsyncCallback<ConfigurationItemDto>() {
					@Override
					public void onFailure(Throwable caught) {
						// add default set of points
						installPointTypes(null);
					}

					@Override
					public void onSuccess(ConfigurationItemDto result) {
						if (result != null) {
							installPointTypes(result.getValue().split(","));
						} else {
							installPointTypes(null);
						}
					}
				});

		panel.add(buildRow(TEXT_CONSTANTS.name(), nameBox));
		panel.add(buildRow(TEXT_CONSTANTS.description(), descriptionBox));
		panel.add(buildRow(TEXT_CONSTANTS.pointType(), pointTypeBox));
		panel.add(buildRow(TEXT_CONSTANTS.version(), versionBox));
		surveyIdLabel = new Label();
		surveyIdLabel.setStylePrimaryName(FORM_LABEL_CSS);
		panel.add(buildRow(TEXT_CONSTANTS.surveyId(), surveyIdLabel));
		editNotificationButton = new Button(
				TEXT_CONSTANTS.manageNotifications());
		editNotificationButton.addClickHandler(this);
		panel.add(editNotificationButton);
		currentDto = null;
		initWidget(panel);
	}

	private void installPointTypes(String[] pointTypes) {
		if (pointTypes == null || pointTypes.length == 0) {
			pointTypeBox.addItem(TEXT_CONSTANTS.waterPoint(), WP_TYPE);
			pointTypeBox.addItem(TEXT_CONSTANTS.sanitationPoint(), SP_TYPE);
			pointTypeBox.addItem(TEXT_CONSTANTS.publicInst(), PI_TYPE);
		} else {
			for (int i = 0; i < pointTypes.length; i++) {
				if (WP_TYPE.equalsIgnoreCase(pointTypes[i].trim())) {
					pointTypeBox.addItem(TEXT_CONSTANTS.waterPoint(), WP_TYPE);
				} else if (SP_TYPE.equalsIgnoreCase(pointTypes[i].trim())) {
					pointTypeBox.addItem(TEXT_CONSTANTS.sanitationPoint(),
							SP_TYPE);
				} else if (PI_TYPE.equalsIgnoreCase(pointTypes[i].trim())) {
					pointTypeBox.addItem(TEXT_CONSTANTS.publicInst(), PI_TYPE);
				} else if (TRAWLER_TYPE.equalsIgnoreCase(pointTypes[i].trim())) {
					pointTypeBox
							.addItem(TEXT_CONSTANTS.trawler(), TRAWLER_TYPE);
				}else if (PROCEDURE_TYPE.equalsIgnoreCase(pointTypes[i].trim())){
					pointTypeBox.addItem(TEXT_CONSTANTS.procedure(),PROCEDURE_TYPE);
				}
			}
		}

		if (currentDto != null) {
			// just in case the box wasn't loaded when we bound the currentDTO
			// to the controls, do it again.
			ViewUtil.setListboxSelection(pointTypeBox,
					currentDto.getPointType());
		}
	}

	private Widget buildRow(String label, Widget widget) {
		Label l = new Label();
		l.setText(label);
		l.setStylePrimaryName(FORM_LABEL_CSS);
		if (widget instanceof TextBox) {
			((TextBox) widget).setStylePrimaryName(TXT_BOX_CSS);
			((TextBox) widget).addChangeHandler(this);
		}
		if (widget instanceof ListBox) {
			((ListBox) widget).addChangeHandler(this);
		}
		HorizontalPanel row = new HorizontalPanel();
		row.add(l);
		row.add(widget);
		return row;
	}

	private void populateWidgets() {
		isChanged = false;
		if (currentDto != null) {
			nameBox.setText(currentDto.getName());
			descriptionBox.setText(currentDto.getDescription());
			versionBox.setText(currentDto.getVersion());
			surveyIdLabel.setText(currentDto.getKeyId().toString());
			ViewUtil.setListboxSelection(pointTypeBox,
					currentDto.getPointType());
		}
	}

	private boolean validateInput() {
		return ViewUtil.isTextPopulated(nameBox);
	}

	public void saveSurvey(final CompletionListener listener) {
		if (validateInput()) {
			if (currentDto == null) {
				currentDto = new SurveyDto();
				currentDto.setPath(groupDto.getCode());
				currentDto.setSurveyGroupId(groupDto.getKeyId());
			}
			currentDto.setName(nameBox.getText().trim());
			currentDto.setCode(nameBox.getText().trim());
			currentDto
					.setDescription(descriptionBox.getText() != null ? descriptionBox
							.getText().trim() : null);
			currentDto.setPointType(ViewUtil.getListBoxSelection(pointTypeBox,
					false));
			surveyService.saveSurvey(currentDto, groupDto.getKeyId(),
					new AsyncCallback<SurveyDto>() {

						@Override
						public void onFailure(Throwable caught) {
							MessageDialog errDia = new MessageDialog(
									TEXT_CONSTANTS.error(), TEXT_CONSTANTS
											.errorTracePrefix()
											+ " "
											+ caught.getLocalizedMessage());
							errDia.showRelativeTo(panel);
							if (listener != null) {
								listener.operationComplete(false, null);
							}
						}

						@Override
						public void onSuccess(SurveyDto result) {

							currentDto = result;
							if (listener != null) {
								listener.operationComplete(true,
										getContextBundle(true));
							}
						}
					});
		} else {
			MessageDialog validationDialog = new MessageDialog(
					TEXT_CONSTANTS.inputError(), TEXT_CONSTANTS.invalidSurvey());
			validationDialog.showRelativeTo(panel);
		}
	}

	@Override
	public void persistContext(CompletionListener listener) {
		if (isChanged) {
			saveSurvey(listener);
		} else {
			listener.operationComplete(true, getContextBundle(true));
		}
	}

	@Override
	public void setContextBundle(Map<String, Object> bundle) {
		this.bundle = bundle;
		currentDto = (SurveyDto) bundle.get(BundleConstants.SURVEY_KEY);
		groupDto = (SurveyGroupDto) bundle
				.get(BundleConstants.SURVEY_GROUP_KEY);
		populateWidgets();
	}

	@Override
	public Map<String, Object> getContextBundle(boolean doPopulation) {
		if (bundle == null) {
			bundle = new HashMap<String, Object>();
		}
		if (doPopulation) {
			bundle.put(BundleConstants.SURVEY_KEY, currentDto);
		}
		return bundle;
	}

	@Override
	public void flushContext() {
		// no-op
	}

	@Override
	public void onChange(ChangeEvent event) {
		if (currentDto != null) {
			if (currentDto.getCode() != null
					&& !currentDto.getCode().equals(nameBox.getText())) {
				isChanged = true;
			} else if (currentDto.getDescription() != null
					&& !currentDto.getDescription().equals(
							descriptionBox.getText())) {
				isChanged = true;
			} else if ((ViewUtil.isTextPopulated(nameBox) && currentDto
					.getCode() == null)
					|| (ViewUtil.isTextPopulated(descriptionBox) && currentDto
							.getDescription() == null)) {
				isChanged = true;
			} else if(!ViewUtil.getListBoxSelection(pointTypeBox, false).equals(currentDto.getPointType())){
				isChanged = true;
			}else {
				isChanged = false;
			}
		} else {
			// if we haven't saved, set isChanged to true if either field is non
			// empty
			isChanged = (ViewUtil.isTextPopulated(nameBox) || ViewUtil
					.isTextPopulated(descriptionBox));
		}
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == editNotificationButton) {
			NotificationSubscriptionDialog dia = new NotificationSubscriptionDialog(
					currentDto.getKeyId(), "rawDataReport", null);
			dia.show();
		}

	}
}
