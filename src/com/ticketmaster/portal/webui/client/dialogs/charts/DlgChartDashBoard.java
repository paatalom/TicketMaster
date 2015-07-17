package com.ticketmaster.portal.webui.client.dialogs.charts;

import java.util.LinkedHashMap;

import com.ticketmaster.portal.webui.client.TicketMaster;
import com.ticketmaster.portal.webui.client.component.mgtdialog.IExceptionCallBack;
import com.ticketmaster.portal.webui.client.component.mgtdialog.IMgtDialog;
import com.ticketmaster.portal.webui.client.utils.ClientUtils;
import org.moxieapps.gwt.highcharts.client.Chart;
import org.moxieapps.gwt.highcharts.client.Series;
import org.moxieapps.gwt.highcharts.client.ToolTip;
import org.moxieapps.gwt.highcharts.client.ToolTipData;
import org.moxieapps.gwt.highcharts.client.ToolTipFormatter;
import org.moxieapps.gwt.highcharts.client.labels.AxisLabelsData;
import org.moxieapps.gwt.highcharts.client.labels.AxisLabelsFormatter;
import org.moxieapps.gwt.highcharts.client.labels.XAxisLabels;
import org.moxieapps.gwt.highcharts.client.labels.YAxisLabels;
import org.moxieapps.gwt.highcharts.client.plotOptions.AreaPlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.Marker;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.PickerIcon.Picker;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.layout.VLayout;

public class DlgChartDashBoard extends IMgtDialog {
	private VLayout chartLayout;
	private DynamicForm dfCurrent;

	private VLayout dynFormLayout;
	private SelectItem siContractID;
	private SelectItem siChartTemplate;
	private LinkedHashMap<String, Series.Type> types = new LinkedHashMap<String, Series.Type>();
	private VLayout mainLayout;
	private JavaScriptObject chartElem;
	private Record[] last_records;

	// private void addTypes(LinkedHashMap<String, String> chart_types,
	// Series.Type type) {
	// types.put(type.name(), type);
	// chart_types.put(type.name(), type.name().toUpperCase());
	// }

	public DlgChartDashBoard() {
		try {
			setWidth(1000);
			setHeight(600);
			setPadding(5);

			mainLayout = new VLayout();
			mainLayout.setWidth100();
			mainLayout.setHeight100();
			mainLayout.setMembersMargin(5);

			addMember(mainLayout);

			DynamicForm df = new DynamicForm();
			siContractID = new SelectItem("contract_id", TicketMaster.constants.contract());

			siChartTemplate = new SelectItem("chart_template", TicketMaster.constants.chart_template());
			//siChartTemplate.setDefaultToFirstOption(true);
			ClientUtils.fillCombo(siContractID, "SubscriberDS", "searchParties", "contract_id", "contract_id");

			siContractID.setWrapTitle(false);
			siChartTemplate.setWrapTitle(false);
			siChartTemplate.setWidth(400);
			siContractID.setDefaultToFirstOption(true);
			PickerIcon piChart = new PickerIcon(new Picker("pie_chart.png"), new FormItemClickHandler() {

				@Override
				public void onFormItemClick(FormItemIconClickEvent event) {
					generateChart(siChartTemplate.getValue());

				}
			});
			siChartTemplate.setIcons(piChart);
			siChartTemplate.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					if (dfCurrent != null) {
						dfCurrent.removeFromParent();
						dfCurrent.destroy();
					}
					try {
						String functionName = siChartTemplate.getSelectedRecord().getAttribute("df_func_name");
						dfCurrent = ChartUtils.createDynamicForm(functionName);
						dfCurrent.setWidth100();
						dfCurrent.setAutoHeight();
						dynFormLayout.addMember(dfCurrent);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			// siChartTemplate.setOptionDataSource(DataSource.get("CorpChartTemplDS"));
			// siChartTemplate.setValueField("id")
			ClientUtils.fillCombo(siChartTemplate, "CorpChartTemplDS", null, "id",
					ClientUtils.language_id != 1 ? "template_name_geo" : "template_name_en");

			// ButtonItem bi = new ButtonItem("bi",
			// TicketMaster.constants.chart_name());
			// bi.setIcon("pie_chart.png");
			// bi.setStartRow(false);
			// bi.setEndRow(false);
			// bi.addClickHandler(new ClickHandler() {
			//
			// @Override
			// public void onClick(ClickEvent event) {
			// generateChart(siChartTemplate.getValue());
			// }
			// });
			df.setFields(siContractID, siChartTemplate);
			df.setAutoWidth();
			df.setNumCols(4);
			mainLayout.addMember(df);
			dynFormLayout = new VLayout();
			dynFormLayout.setWidth100();
			dynFormLayout.setAutoHeight();
			dynFormLayout.setMembersMargin(5);

			// vLayout.addMember(createChart());

			mainLayout.addMember(dynFormLayout);

			// vLayout.addMember(createChart());

		} catch (Exception e) {
			e.printStackTrace();
			SC.warn(e.toString());
		}
	}

	protected void generateChart(Object id) {
		if (dfCurrent == null)
			return;
		if (!dfCurrent.validate())
			return;
		Criteria criteria = dfCurrent.getValuesAsCriteria();
		criteria.setAttribute("id", id);
		if (siContractID.getValue() != null)
			criteria.setAttribute("contract_id", siContractID.getValue());
		DSRequest requestProperties = new DSRequest();
		requestProperties.setOperationId("execChart");
		DataSource.get("CorpChartTemplDS").fetchData(criteria, new DSCallback() {

			@Override
			public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest) {
				Record[] records = dsResponse.getData();
				putChart(records);
			}
		}, requestProperties);
	}

	protected void putChart(final Record[] records) {
		last_records = records;
		final DivElement divElement = Document.get().createDivElement();
		divElement.getStyle().setOverflow(com.google.gwt.dom.client.Style.Overflow.HIDDEN);
		divElement.setId(Document.get().createUniqueId());
		if (chartLayout != null)
			chartLayout.destroy();
		chartLayout = new VLayout() {
			@Override
			protected void onDraw() {
				// TODO Auto-generated method stub
				super.onDraw();
				try {
					final String functionName = siChartTemplate.getSelectedRecord().getAttribute("chart_func_name");
					chartElem = ChartUtils.executeChart(functionName, chartLayout, records,
							siContractID.getValueAsString(), dfCurrent);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		chartLayout.setWidth("100%");
		chartLayout.setHeight("100%");
		mainLayout.addMember(chartLayout);

	}

	public Chart createChart() {

		final Chart chart = new Chart()
				.setType(Series.Type.AREA)
				.setChartTitleText("ბილების სტატისტიკა")
				.setChartSubtitleText("ბოლო 12 თვის ხარჯების/გადახდების სტატისტიკა")
				.setAreaPlotOptions(
						new AreaPlotOptions().setPointStart(1940).setMarker(
								new Marker().setEnabled(false).setSymbol(Marker.Symbol.CIRCLE).setRadius(2)
										.setHoverState(new Marker().setEnabled(true)))

				).setToolTip(new ToolTip().setFormatter(new ToolTipFormatter() {
					public String format(ToolTipData toolTipData) {
						return toolTipData.getSeriesName() + " შეადგინა <b>" + toolTipData.getYAsLong()
								+ " ლარი </b><br/> " + toolTipData.getXAsLong() + " - ში";
					}
				}));

		chart.getXAxis().setLabels(new XAxisLabels().setFormatter(new AxisLabelsFormatter() {
			public String format(AxisLabelsData axisLabelsData) {
				// clean, unformatted number for year
				return String.valueOf(axisLabelsData.getValueAsLong());
			}
		}));

		chart.getYAxis().setAxisTitleText("თანხა ლარებში")
				.setLabels(new YAxisLabels().setFormatter(new AxisLabelsFormatter() {
					public String format(AxisLabelsData axisLabelsData) {
						return axisLabelsData.getValueAsLong() / 1000 + "k";
					}
				}));

		chart.addSeries(chart
				.createSeries()
				.setName("გადახდები")
				.setPoints(
						new Number[] { 0, 0, 0, 0, 0, 6, 11, 32, 110, 235, 369, 640, 1005, 1436, 2063, 3057, 4618,
								6444, 9822, 15468, 20434, 24126, 27387, 29459, 31056, 31982, 32040, 31233, 29224,
								27342, 26662, 26956, 27912, 28999, 28965, 27826, 25579, 25722, 24826, 24605, 24304,
								23464, 23708, 24099, 24357, 24237, 24401, 24344, 23586, 22380, 21004, 17287, 14747,
								13076, 12555, 12144, 11009, 10950, 10871, 10824, 10577, 10527, 10475, 10421, 10358,
								10295, 10104 }));
		chart.addSeries(chart
				.createSeries()
				.setName("ხარჯები")
				.setPoints(
						new Number[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 25, 50, 120, 150, 200, 426, 660, 869, 1060,
								1605, 2471, 3322, 4238, 5221, 6129, 7089, 8339, 9399, 10538, 11643, 13092, 14478,
								15915, 17385, 19055, 21205, 23044, 25393, 27935, 30062, 32049, 33952, 35804, 37431,
								39197, 45000, 43000, 41000, 39000, 37000, 35000, 33000, 31000, 29000, 27000, 25000,
								24000, 23000, 22000, 21000, 20000, 19000, 18000, 18000, 17000, 16000 }));

		return chart;
	}

	@Override
	public void fillFields(Record record) {
		search();
	}

	@Override
	public void validateSave(IExceptionCallBack callBack) {
	}

	@Override
	public Record getRecordForSave() {
		return null;
	}

	@Override
	public String getDataSourceName() {
		return null;
	}

	@Override
	public String getSaveOperation() {
		return null;
	}

	@Override
	public String getIdField() {
		return null;
	}

	@Override
	public String getWindowTitle() {
		return TicketMaster.constants.chart_name();
	}

	@Override
	public boolean saveOnServer() {
		return false;
	}

	private void search() {
		try {

		} catch (Exception e) {
			e.printStackTrace();
			SC.warn(e.toString());
		}
	}
}
