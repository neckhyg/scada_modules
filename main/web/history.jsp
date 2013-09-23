<%--
--%><%@page import="com.serotonin.m2m2.Common"%><%--
--%><%@page import="com.serotonin.m2m2.view.ShareUser"%><%--
--%><%@page pageEncoding="UTF-8"%><%--
--%><%@ include file="/WEB-INF/jsp/include/tech.jsp" %><%--
--%><%@ taglib prefix="main" tagdir="/WEB-INF/tags/main" %>

<main:eazyPage dwr="HistoryDwr" js="/resources/view.js">
<jsp:attribute name="styles">
<style>
</style>
</jsp:attribute>

<jsp:body>
<script type="text/javascript">
  dojo.require("dojo/parser");
  dojo.require("dojo.store.Memory");
  dojo.require("dijit.form.FilteringSelect");
  dojo.require("dijit.form.DateTextBox");

  require(["dijit/form/TimeTextBox", "dojo/domReady!","dojo/parser", "dijit/form/DateTextBox"]);

  dojo.ready(function() {

      // Point lookup
//      new dijit.form.FilteringSelect({
//        store: new dojo.store.Memory({ data: pointList }),
//        style: "width: 250px;",
//        queryExpr: "*\${0}*",
//        highlightMatch: "all",
//        required: false,
//        onChange: function(point) {
//        if (this.item)
//          $set("pointId",this.item.id);
//        }
//      }, "picker");

      var startDate = new Date();
      $set("startDate", startDate.getFullYear() + "-" + (startDate.getMonth()+1) + "-" + startDate.getDate());
      var endDate = new Date();
      $set("endHour",endDate.getHours());
      $set("endMinute", endDate.getMinutes());
        $set("endDate", endDate.getFullYear() + "-" + (endDate.getMonth()+1) + "-" + endDate.getDate());
      });

// history
function getHistoryValues(page){
  var xid = $get("picker");
  var limit = parseInt($get("historyLimit"));
  //var page = 1;
  if (isNaN(limit))
    alert("<fmt:message key="pointDetails.recordCountError"/>");
  else {
    HistoryDwr.getHistoryValue(xid, limit, page, function(response) {
        var pages = response.data.pages;
        var data = response.data.history;
      var str = "页数：";
      for(var i=1; i<=pages; i++)
      {
      if(i == page)
         str += page;
      else
      str += "&nbsp;<a href='#' onclick='getHistoryValues("+ i +")'>" + i + "</a>&nbsp;";
      if(!(i%30))
      str +="<br/>";
      }
        $set("totalPage",str);
        dwr.util.removeAllRows("historyTableData");
        if (!data || data.length == 0)
        dwr.util.addRows("historyTableData", ["<fmt:message key="common.noData"/>"], [function(data) { return data; }]);
        else {
        dwr.util.addRows("historyTableData", data,
          [
          function(data) { return data.time; },
          function(data) { return data.value; }
          ],
          { 
    rowCreator:function(options) {
    var tr = document.createElement("tr");
    tr.className = "row"+ (options.rowIndex % 2 == 0 ? "" : "Alt");
    return tr;
}
});
    }
    });
}
}

function searchHistoryValues(page){
  var tmpDate = $get("startDate").split("-");
  var startTime = new Date();
  startTime.setFullYear(tmpDate[0],(tmpDate[1]-1),tmpDate[2]);
  startTime.setHours($get("startHour"),$get("startMinute"));
  tmpDate = $get("endDate").split("-");
  var endTime = new Date();
  endTime.setFullYear(tmpDate[0],(tmpDate[1]-1),tmpDate[2]);
  endTime.setHours($get("endHour"),$get("endMinute"));
  //var page = 1;
  HistoryDwr.searchHistoryValues($get("picker"),startTime,endTime, page, function(response){
        var pages = response.data.pages;
      var data = response.data.history;
      var str = "页数：";
      for(var i=1; i<=pages; i++)
      {
      if(i == page)
       str += page;
       else
       str += "&nbsp;<a href='#' onclick='searchHistoryValues("+ i +")'>" + i + "</a>&nbsp;";
      if(!(i%30))
      str +="<br/>";
      }
        $set("totalPage", str);
      dwr.util.removeAllRows("historyTableData");
      if (!data || data.length == 0)
      dwr.util.addRows("historyTableData", ["<fmt:message key="common.noData"/>"], [function(data) { return data; }]);
      else {
      dwr.util.addRows("historyTableData", data,
        [
        function(data) { return data.time; },
        function(data) { return data.value; }
        ],
        {
rowCreator:function(options) {
var tr = document.createElement("tr");
tr.className = "row" + (options.rowIndex % 2 == 0 ? "" : "Alt");
return tr;
}
});
      }
      });
}
</script>

<jview:init username="admin"/>

请选择需要查询的数据点:
<select name="picker" id="picker">
  <c:forEach items="${userPoints}" var="point">
  <option value="${point.xid}">${sst:dquotEncode(point.extendedName)}</option>
  </c:forEach>
</select>
<%-- <div id="picker"></div><div style="display:inline;"></div> --%>

<input type="hidden" name="pointId" id="pointId" value=""/>
<h3>查询最新记录:</h3>
显示最新<input id="historyLimit" type="text" style="text-align:right;" value="${historyLimit}" class="formVeryShort"/>条记录
&nbsp;&nbsp;<input type="submit" name="getHistory" id="getHistory" value="查询" onclick="getHistoryValues(1);"/>

<br/>

<h3>查询某时间段记录:</h3>
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

<br/>
<label for="endDate">结束时间:</label>
<input type="text" name="endDate" id="endDate" value="2013-06-01" data-dojo-type="dijit.form.DateTextBox" required="true" />
<label for="endHour"></label>
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
<label for="endMinute">:</label>
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

&nbsp;&nbsp;<input type="submit" name="submit" id="submit" value="查询" onclick="searchHistoryValues(1);"/>
<br/>
<div class="borderDiv marB marR" style="text-align:center;">
<div id="totalPage"></div>
  <table cellspacing="1" style="text-align:left; margin:0px auto;">
    <tr class="rowHeader">
      <td><fmt:message key="common.time"/></td>
      <td><fmt:message key="common.value"/></td>
    </tr>
    <tbody id="historyTableData"></tbody>
  </table>
</div>

</jsp:body>
</main:eazyPage>

