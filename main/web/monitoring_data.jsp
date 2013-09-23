<%--
--%><%@page import="com.serotonin.m2m2.Constants"%><%--
--%><%@page import="com.serotonin.m2m2.Common"%><%--
--%><%@page import="com.serotonin.m2m2.view.ShareUser"%><%--
--%><%@page pageEncoding="UTF-8"%><%--
--%><%@ include file="/WEB-INF/jsp/include/tech.jsp" %><%--
--%><%@ taglib prefix="main" tagdir="/WEB-INF/tags/main" %>

<main:eazyPage dwr="WatchListDwr" js="${modulePath}/web/js/view.js,/modules/watchlists/web/watchList.js">
  <jsp:attribute name="styles">
    <style>
    html > body .dijitTreeNodeLabelSelected {
        background-color: inherit;
        color: inherit;
    }
    .watchListAttr { min-width:600px; }
    .rowIcons img { padding-right: 3px; }
    html > body .dijitSplitContainerSizerH {
        border: 1px solid #FFFFFF;
        <!--background-color: #F07800;-->
        margin-top:4px;
        margin-bottom:4px;
    }
    .dijitSplitContainer-child { border: none !important; }
    .dijitTreeIcon { display: none; }
    .wlComponentMin {
        top:0px;
        left:0px;
        position:relative;
        margin:0px;
        padding:0px;
        width:16px;
        height:16px;
    }

    </style>
  </jsp:attribute>
  
  <jsp:body>
    <script type="text/javascript">
      dojo.require("dijit.layout.SplitContainer");
      dojo.require("dijit.layout.ContentPane");
      dojo.require("dojo.data.ItemFileWriteStore");
      dojo.require("dojo.store.Memory");
      dojo.require("dijit.form.FilteringSelect");

      require(["dijit/form/TimeTextBox", "dojo/domReady!","dojo/parser", "dijit/form/DateTextBox"]);
      
      mango.view.initWatchlist();
      mango.share.dwr = WatchListDwr;
      var owner;
      var pointNames = {};
      var pointList = [];
      var watchlistChangeId = 0;
      var iconSrc = "images/bullet_go.png";

      var flag=0;
      
      dojo.ready(function() {
        WatchListDwr.init(function(data) {
            mango.share.users = data.shareUsers;
            displayWatchList(data.selectedWatchList);
            var startDate = new Date();
            $set("startDate", startDate.getFullYear() + "-" + (startDate.getMonth()+1) + "-" + startDate.getDate());
            var endDate = new Date();
            $set("endHour",endDate.getHours());
            $set("endMinute", endDate.getMinutes());
            $set("endDate", endDate.getFullYear() + "-" + (endDate.getMonth()+1) + "-" + endDate.getDate());
          	});
        });
          
         function displayWatchList(data) {
          if (!data.points)
              // Couldn't find the watchlist. Reload the page
              window.location.reload();
          
          var points = data.points;
          owner = data.access == <c:out value="<%= ShareUser.ACCESS_OWNER %>"/>;
          
          // Add the new rows.
          for (var i=0; i<points.length; i++) {
              if (!pointNames[points[i]]) {
                  // The point id isn't in the list. Refresh the page to ensure we have current data.
                  window.location.reload();
                  return;
              }
              addToWatchListImpl(points[i]);
          }

          mango.view.watchList.reset();
      }
     
      function showChart(mangoId, event, source) {
          if (isMouseLeaveOrEnter(event, source)) {
              // Take the data in the chart textarea and put it into the chart layer div
              $set('p'+ mangoId +'ChartLayer', $get('p'+ mangoId +'Chart'));
              showMenu('p'+ mangoId +'ChartLayer', 4, 12);
          }
      }

      function hideChart(mangoId, event, source) {
          if (isMouseLeaveOrEnter(event, source))
              hideLayer('p'+ mangoId +'ChartLayer');
      }

      //
      // Image chart
      //
      function getImageChart() {
          var width = dojo.contentBox($("imageChartDiv")).w - 20;
         // startImageFader($("imageChartImg"));
          var pointlist=getChartPointList();
          WatchListDwr.getImageChartDataToday(pointlist,
                  width, 350, function(data) {
              $("imageChartDiv").innerHTML = data;
             // stopImageFader($("imageChartImg"));

              // Make sure the length of the chart doesn't mess up the watch list display. Do async to
              // make sure the rendering gets done.
              // TODO - onResized no longer works.
              //setTimeout('dijit.byId("splitContainer").onResized()', 2000);
          });
      }


      function getChartPointList() {
          var pointIds = $get("chartCB");
          for (var i=pointIds.length-1; i>=0; i--) {
              if (pointIds[i] == "_TEMPLATE_") {
                  pointIds.splice(i, 1);
              }
          }
          return pointIds;
      }
      <m2m2:moduleExists name="reports">
        function createReport() {
            var pointIds = getChartPointList();
            var pointList = "";
            for (var i=0; i<pointIds.length; i++) {
                if (i > 0)
                    pointList += ",";
                pointList += pointIds[i];
            }

            var select = $("watchListSelect");
            var name = escape(select.options[select.selectedIndex].text);
            window.location='reports.shtm?createName='+ name +'&createPoints='+ pointList;
        }
      </m2m2:moduleExists>

       function getWatchListData()
      {
    	  WatchListDwr.getPointByWatchList(function(data){
        	  var str="";
              for (var i=0; i<data.length; i++) {
            	var id=data[i].id;
            	var names=data[i].name;
            	var devicename=data[i].deviceName;
            	pointNames[id]=devicename+"-"+names;
            	//addToWatchListImpl(id);
              }
             // mango.view.watchList.reset();
    	  });
      }
      
       function addToWatchListImpl(pointId) {
           var pointContent = createFromTemplate("p_TEMPLATE_", pointId, "watchListTable");
           pointContent.mangoName = "watchListRow";
           $("p"+ pointId +"Name").innerHTML = pointNames[pointId];
       }
      getWatchListData(); 
      
      function setSitename()
      {
    	  WatchListDwr.getUserSelectWatchList(function(data){
       	   var listname=data;
       	   var name="";
       	   if(listname=="jingxi")
       		   name="荆溪北路站点";
       	   else if(listname=="jingyi")
       		   name="荆邑北路站点";
       	   else if(listname=="jinlan")
       		   name="金兰泵站站点";
       	   else if(listname=="所有点数据")
       		   name=listname;
       	   $("watchlistsitename").innerHTML=name;
          });
      }
      setSitename();
      
      function showChart()
      {
    	  var list=getChartPointList();
    	  if(list=="")
    		  $("chartdiv").style.display="none";
    	  else
    		  {
	        	  if(flag==0)
	    		  {
			    	  $("chartdiv").style.display="block";
			    	  $("chart").src="${modulePath}/web/chart.jsp?list="+list;
	    		  }
	    	  else
	    		  getChartTime();
    		  }
      }
      
      function getChartTime()
      {
    	  flag=1;
 //   	  startImageFader($("imageChartImg"));
    	  var tmpDate = $get("startDate").split("-");
    	  var startTime = new Date();
    	  startTime.setFullYear(tmpDate[0],(tmpDate[1]-1),tmpDate[2]);
    	  startTime.setHours($get("startHour"),$get("startMinute"));
    	  tmpDate = $get("endDate").split("-");
    	  var endTime = new Date();
    	  endTime.setFullYear(tmpDate[0],(tmpDate[1]-1),tmpDate[2]);
    	  endTime.setHours($get("endHour"),$get("endMinute"));
    	  
           WatchListDwr.getTimespan(startTime,endTime,function(data) {
             var from=data.from;
             var to=data.to; 
//             stopImageFader($("imageChartImg"));
             var list=getChartPointList();
             if(list!="" && from<to)
            	 {
	                 if($("chartdiv").style.display=="none")
	            	 {
	            	 	$("chartdiv").style.display="block";
	            	 }
	                
	    	  		$("chart").src= "${modulePath}/web/chart.jsp?list="+list+"&from="+from+"&to="+to;
            	 }
             else
            	 {
            	 	alert("请选择监测点或监测结束时间必须大于监测开始时间！")
            	 }
              }); 
      }
      
    </script>
    <div>
    	<fmt:message key="watchlist.watchlist"/> <tag:help id="watchList"/>
   		  <div id="watchlisthtml">
   		  <div id="watchlistsitename" style="font-weight:bold; margin:10px 0px 10px 10px"></div>
   		  	<table style="display:none;">
              <tbody id="p_TEMPLATE_">
                <tr id="p_TEMPLATE_BreakRow"><td class="horzSeparator" colspan="5"></td></tr>
                <tr>
                  <td width="1">
                     <table class="rowIcons">
                      <tr>
                        <td onmouseover="mango.view.showChange('p'+ getMangoId(this) +'Change', 4, 12);"
                                onmouseout="mango.view.hideChange('p'+ getMangoId(this) +'Change');"
                                id="p_TEMPLATE_ChangeMin" style="display:none;"><img alt="" id="p_TEMPLATE_Changing" 
                                src="images/icon_edit.png"/><div id="p_TEMPLATE_Change" class="labelDiv" 
                                style="visibility:hidden;top:10px;left:1px;" onmouseout="hideLayer(this);">
                          <tag:img png="hourglass" title="common.gettingData"/>
                        </div></td>
                        <td id="p_TEMPLATE_ChartMin" style="display:none;" onmouseover="showChart(getMangoId(this), event, this);"
                                onmouseout="hideChart(getMangoId(this), event, this);"><img alt="" 
                                src="images/icon_chart.png"/><div id="p_TEMPLATE_ChartLayer" class="labelDiv" 
                                style="visibility:hidden;top:0;left:0;"></div><textarea
                                style="display:none;" id="p_TEMPLATE_Chart"><tag:img png="hourglass"
                                title="common.gettingData"/></textarea></td>
                      </tr>
                    </table> 
                  </td>
                  <td id="p_TEMPLATE_Name" style="font-weight:bold"></td>
                  <td id="p_TEMPLATE_Value" align="center"><img src="images/hourglass.png"/></td>
                  <td id="p_TEMPLATE_Time" align="center"></td>
                  <td style="width:1px; white-space:nowrap;">
                    <input type="checkbox" name="chartCB" id="p_TEMPLATE_ChartCB" value="_TEMPLATE_"
                            title="<fmt:message key="watchlist.consolidatedChart"/>" onclick="showChart()"/>
                  </td>
                </tr>
                <tr><td colspan="5" style="padding-left:16px;" id="p_TEMPLATE_Messages"></td></tr>
              </tbody>
            </table>
            <table id="watchListTable" class="wide"></table>
   		  </div>
	      <div class="borderDiv" style="width: 100%;">
	        <table class="wide">
	          <tr>
	            <td class="smallTitle"><fmt:message key="watchlist.chart"/> <tag:help id="watchListCharts"/></td>
	            <td align="right"><!--<tag:dateRange/>-->
		            <label for="startDate">起始时间:</label>
					<input type="text" name="startDate" id="startDate" value="2013-06-01" data-dojo-type="dijit.form.DateTextBox" required="true" />
					<label for="startHour"></label>
					<select name="startHour" id="startHour">
					  <option>00</option>
					  <option>01</option>
					  <option>02</option>
					  <option>03</option>
					  <option>04</option>
					  <option>05</option>
					  <option>06</option>
					  <option>07</option>
					  <option>08</option>
					  <option>09</option>
					  <option>10</option>
					  <option>11</option>
					  <option>12</option>
					  <option>13</option>
					  <option>14</option>
					  <option>15</option>
					  <option>16</option>
					  <option>17</option>
					  <option>18</option>
					  <option>19</option>
					  <option>20</option>
					  <option>21</option>
					  <option>22</option>
					  <option>23</option>
					</select>
					<label for="startMinute">:</label>
					<select name="startMinute" id="startMinute">
					  <option>00</option>
					  <option>01</option>
					  <option>02</option>
					  <option>03</option>
					  <option>04</option>
					  <option>05</option>
					  <option>06</option>
					  <option>07</option>
					  <option>08</option>
					  <option>09</option>
					  <option>10</option>
					  <option>11</option>
					  <option>12</option>
					  <option>13</option>
					  <option>14</option>
					  <option>15</option>
					  <option>16</option>
					  <option>17</option>
					  <option>18</option>
					  <option>19</option>
					  <option>20</option>
					  <option>21</option>
					  <option>22</option>
					  <option>23</option>
					  <option>24</option>
					  <option>25</option>
					  <option>26</option>
					  <option>27</option>
					  <option>28</option>
					  <option>29</option>
					  <option>30</option>
					  <option>31</option>
					  <option>32</option>
					  <option>33</option>
					  <option>34</option>
					  <option>35</option>
					  <option>36</option>
					  <option>37</option>
					  <option>38</option>
					  <option>39</option>
					  <option>40</option>
					  <option>41</option>
					  <option>42</option>
					  <option>43</option>
					  <option>44</option>
					  <option>45</option>
					  <option>46</option>
					  <option>47</option>
					  <option>48</option>
					  <option>49</option>
					  <option>50</option>
					  <option>51</option>
					  <option>52</option>
					  <option>53</option>
					  <option>54</option>
					  <option>55</option>
					  <option>56</option>
					  <option>57</option>
					  <option>58</option>
					  <option>59</option>
					</select>
						<label for="endDate">结束时间:</label>
						<input type="text" name="endDate" id="endDate" value="2013-06-01" data-dojo-type="dijit.form.DateTextBox" required="true" />
						<label for="endHour"></label>
						<label for="endMinute">:</label>
						<select name="endHour" id="endHour">
						  <option>00</option>
						  <option>01</option>
						  <option>02</option>
						  <option>03</option>
						  <option>04</option>
						  <option>05</option>
						  <option>06</option>
						  <option>07</option>
						  <option>08</option>
						  <option>09</option>
						  <option>10</option>
						  <option>11</option>
						  <option>12</option>
						  <option>13</option>
						  <option>14</option>
						  <option>15</option>
						  <option>16</option>
						  <option>17</option>
						  <option>18</option>
						  <option>19</option>
						  <option>20</option>
						  <option>21</option>
						  <option>22</option>
						  <option>23</option>
						</select>
						<select name="endMinute" id="endMinute">
						  <option>00</option>
						  <option>01</option>
						  <option>02</option>
						  <option>03</option>
						  <option>04</option>
						  <option>05</option>
						  <option>06</option>
						  <option>07</option>
						  <option>08</option>
						  <option>09</option>
						  <option>10</option>
						  <option>11</option>
						  <option>12</option>
						  <option>13</option>
						  <option>14</option>
						  <option>15</option>
						  <option>16</option>
						  <option>17</option>
						  <option>18</option>
						  <option>19</option>
						  <option>20</option>
						  <option>21</option>
						  <option>22</option>
						  <option>23</option>
						  <option>24</option>
						  <option>25</option>
						  <option>26</option>
						  <option>27</option>
						  <option>28</option>
						  <option>29</option>
						  <option>30</option>
						  <option>31</option>
						  <option>32</option>
						  <option>33</option>
						  <option>34</option>
						  <option>35</option>
						  <option>36</option>
						  <option>37</option>
						  <option>38</option>
						  <option>39</option>
						  <option>40</option>
						  <option>41</option>
						  <option>42</option>
						  <option>43</option>
						  <option>44</option>
						  <option>45</option>
						  <option>46</option>
						  <option>47</option>
						  <option>48</option>
						  <option>49</option>
						  <option>50</option>
						  <option>51</option>
						  <option>52</option>
						  <option>53</option>
						  <option>54</option>
						  <option>55</option>
						  <option>56</option>
						  <option>57</option>
						  <option>58</option>
						  <option>59</option>
						</select>						
	            </td>
	            <td>
                      <input type="button" name="getChartTime" id="getChartTime" value="获取图形" onclick="getChartTime()"/>
	            <%--  <tag:img id="imageChartImg" png="control_play_blue" title="watchlist.imageChartButton"
	                      onclick="getChartTime()"/>
	               <tag:img id="chartDataImg" png="bullet_down" title="watchlist.chartDataButton"
	                      onclick="getChartData()"/> --%>
	            </td>
	          </tr>
	          <tr><td colspan="3" id="imageChartDiv"></td></tr>
	        </table>
	        <div style="height:400px;width:100%; display:none" id="chartdiv">
	            <iframe id="chart" name="chart" frameborder="0" width="100%"  height="400px" scrolling="no"></iframe>
	        </div>
	      </div>
    </div>
  </jsp:body>
</main:eazyPage>
