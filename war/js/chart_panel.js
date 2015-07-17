if (window.isc && !window.isc.module_Chart_Panel) {
	isc.module_Chart_Panel = 1;
	var _lngProps = {
		init : function() {
			this.Super('init', arguments);
			this.initWidget(this);
		},
		initWidget : function() {

		}
	}

	isc.ClassFactory.defineClass("ChartPanel", "VLayout");
}