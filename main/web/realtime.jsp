<%--
--%><%@page import="com.serotonin.m2m2.Constants"%><%--
--%><%@page import="com.serotonin.m2m2.Common"%><%--
--%><%@page import="com.serotonin.m2m2.view.ShareUser"%><%--
--%><%@page pageEncoding="UTF-8"%><%--
--%><%@ include file="/WEB-INF/jsp/include/tech.jsp" %><%--
--%><%@ taglib prefix="main" tagdir="/WEB-INF/tags/main" %>

<main:eazyPage dwr="DataPointDetailsDwr" js="/resources/view.js" onload="init">
  <jsp:attribute name="styles">
    <style>
    </style>
  </jsp:attribute>
  

  <jsp:body>
    <script type="text/javascript">
      dojo.require("dojox.charting.Theme");
      dojo.require("dojox.charting.scaler.linear");
      dojo.require("dojox.charting.Chart2D");
      dojo.require("dojox.lang.functional");
      dojo.require("dojox.charting.widget.Legend");
      dojo.require("dojo.store.Memory");
      dojo.require("dijit.form.FilteringSelect");

      dojo.require("dojox.charting.themes.PlotKit.blue");

      var chart, limit = 10, magnitude = 30;
      var allPointsArray = [];

      function init(){

      }

var randomValue = function(){
  return Math.random() * magnitude;
};

var makeSeries = function(len){
    var s = [];
    do{
        s.push(randomValue());
  }while(s.length < len);
    return s;
};

var seriesA = makeSeries(limit),
  seriesB = makeSeries(limit),
  seriesC = makeSeries(limit);
  
var updateTest = function(){
  seriesA.shift();
  seriesA.push(randomValue());
  chart.updateSeries("Series A", seriesA);

  seriesB.shift();
  seriesB.push(randomValue());
  chart.updateSeries("Series B", seriesB);

  seriesC.shift();
  seriesC.push(randomValue());
  chart.updateSeries("Series C", seriesC);

  chart.render();
  setTimeout("updateTest()", 1000);
};


var makeUpdateableObjects = function() {
  chart = new dojox.charting.Chart2D("chart");
  chart.setTheme(dojox.charting.themes.PlotKit.blue);
  chart.addAxis("x", {fixLower: "minor", natural: true, min: 1, max: limit});
  chart.addAxis("y", {vertical: true, min: 0, max: 30, majorTickStep: 5, minorTickStep: 1});
  chart.addPlot("default", {type: dojox.charting.plot2d.Lines.LinesPlot});
  chart.addSeries("Series A", seriesA);
  chart.addSeries("Series B", seriesB);
  chart.addSeries("Series C", seriesC);
  chart.addPlot("grid", {type: "Grid", hMinorLines: true});
  chart.render();
    setTimeout("updateTest()", 1000);
};

dojo.addOnLoad(makeUpdateableObjects);
dojo.ready(function(){

          // Point lookup
          new dijit.form.FilteringSelect({
              store: new dojo.store.Memory({ data: pointList }),
              autoComplete: false,
              style: "width: 250px;",
              queryExpr: "*\${0}*",
              highlightMatch: "all",
              required: false,
              onChange: function(point) {
                  if (this.item)
                      window.location='realtime.shtm?dpid='+ this.item.id;
                      // add to realtimeList
              }
          }, "picker");        
    });

    </script>

        <jview:init username="admin"/>
        选择数据点:<div style="display:inline;"><div id="picker"></div></div>
        <div id="chart" style="width: 600px; height: 300px;"></div>
    </jsp:body>
</main:eazyPage>

<script type="text/javascript">
  var pointList = [
    <c:forEach items="${userPoints}" var="point">{id:${point.id},name:"${sst:dquotEncode(point.extendedName)}"},
    </c:forEach>
  ];
</script>
