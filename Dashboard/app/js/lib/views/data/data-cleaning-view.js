FLOW.DataCleaningAppletView = FLOW.View.extend({
  showRawDataReportAppletBool:false,
  showRawDataImportAppletBool:false,

  showRawDataReportApplet: function () {
    this.set('showRawDataReportAppletBool', true);
  },

  showRawDataImportApplet: function () {
    this.set('showRawDataImportAppletBool', true);
  }
});