package com.ticketmaster.portal.webui.client.dialogs.charts;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.layout.Layout;

public class ChartUtils {

	private static native JavaScriptObject getFunction(String functionName)/*-{
		var fn = $wnd.window[functionName];
		var ret = fn();
		return ret;
	}-*/;

	public static DynamicForm createDynamicForm(String functionName) {
		JavaScriptObject vvv = getFunction(functionName);
		return DynamicForm.getOrCreateRef(vvv);
	}

	private static native JavaScriptObject getFunctionChart(String functionName, JavaScriptObject container,
			JavaScriptObject data, String chart_type, JavaScriptObject form)/*-{
		var fn = $wnd.window[functionName];
		var ret = fn(container, data, chart_type, form);
		return ret;
	}-*/;

	public static native JavaScriptObject destroyChart(JavaScriptObject chart)/*-{
		chart.destroy();
	}-*/;

	public static native void setProperty(JavaScriptObject object, String name, Object value) /*-{
		object[name] = value;
	}-*/;

	public static JavaScriptObject executeChart(String functionName, Layout container, Record[] records, String chart_type,
			DynamicForm dfCurrent) {

		JavaScriptObject arr = JSOHelper.convertToJavaScriptArray(records);
		return getFunctionChart(functionName, container.getJsObj(), arr, chart_type, dfCurrent.getJsObj());

	}
}
