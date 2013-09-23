<%--
--%><%@ include file="/WEB-INF/jsp/include/tech.jsp" %>
<script type="text/javascript">
  dojo.require("dojo.store.Memory");
  dojo.require("dijit.form.FilteringSelect");
  
  var allPoints = [];  
  var selectedPoints = [];  
  
  dojo.ready(function() { 
      PachubePublisherDwr.initSender(function(response) {
          dojo.forEach(response.data.allPoints, function(item) {
              allPoints.push({
                  id: item.id, 
                  name: item.extendedName, 
                  enabled: item.enabled, 
                  type: item.dataTypeMessage,
                  fancyName: item.extendedName
              });
          });
          
          dojo.forEach(response.data.publisher.points, function(item) {
              addToSelectedArray(item.dataPointId, item.feedId, item.dataStreamId);
          });
          refreshSelectedPoints();
          
          // Create the lookup
          new dijit.form.FilteringSelect({
              store: new dojo.store.Memory({ data: allPoints }),
              labelAttr: "fancyName",
              labelType: "html",
              searchAttr: "name",
              autoComplete: false,
              style: "width: 254px;",
              queryExpr: "*\${0}*",
              highlightMatch: "all",
              required: false,
              onChange: function(point) {
                  if (this.item) {
                      selectPoint(this.item.id);
                      this.reset();
                  }
              }
          }, "pointLookup");        
      });
  });
  
  function selectPoint(pointId) {
      if (!containsPoint(pointId)) {
          addToSelectedArray(pointId, "", "");
          refreshSelectedPoints();
      }
  }
  
  function containsPoint(pointId) {
      return getElement(selectedPoints, pointId, "id") != null;
  }
  
  function addToSelectedArray(pointId, feedId, dataStreamId) {
      var data = getElement(allPoints, pointId);
      
      if (data) {
          data.fancyName = "<span class='disabled'>"+ data.name +"</span>";
          
          // Missing names imply that the point was deleted, so ignore.
          selectedPoints[selectedPoints.length] = {
              id : pointId,
              pointName : data.name,
              enabled : data.enabled,
              pointType : data.type,
              feedId: feedId,
              dataStreamId: dataStreamId
          };
      }
  }
  
  function removeFromSelectedPoints(pointId) {
      removeElement(selectedPoints, pointId);
      refreshSelectedPoints();
      
      var data = getElement(allPoints, pointId);
      if (data)
          data.fancyName = data.name;
  }
  
  function refreshSelectedPoints() {
      dwr.util.removeAllRows("selectedPoints");
      if (selectedPoints.length == 0)
          show("selectedPointsEmpty");
      else {
          hide("selectedPointsEmpty");
          dwr.util.addRows("selectedPoints", selectedPoints,
              [
                  function(data) { return data.pointName; },
                  function(data) { return "<img src='images/"+ (data.enabled ? "brick_go" : "brick_stop") +".png'/>"; },
                  function(data) { return data.pointType; },
                  function(data) {
                          return "<input type='text' value='"+ data.feedId +"' "+
                                  "onblur='updateFeedId("+ data.id +", this.value)'/>";
                  },
                  function(data) {
                          return "<input type='text' value='"+ data.dataStreamId +"' "+
                                  "onblur='updateDataStreamId("+ data.id +", this.value)'/>";
                  },
                  function(data) { 
                          return "<img src='images/bullet_delete.png' class='ptr' "+
                                  "onclick='removeFromSelectedPoints("+ data.id +")'/>";
                  }
              ],
              {
                  rowCreator: function(options) {
                      var tr = document.createElement("tr");
                      tr.className = "row"+ (options.rowIndex % 2 == 0 ? "" : "Alt");
                      return tr;
                  },
                  cellCreator: function(options) {
                      var td = document.createElement("td");
                      if (options.cellNum == 1 || options.cellNum == 4)
                          td.align = "center";
                      return td;
                  } 
              });
      }
  }
  
  function updateFeedId(pointId, feedId) {
      updateElement(selectedPoints, pointId, "feedId", feedId);
  }
  
  function updateDataStreamId(pointId, dataStreamId) {
      updateElement(selectedPoints, pointId, "dataStreamId", dataStreamId);
  }
  
  function savePublisherImpl(name, xid, enabled, cacheWarningSize, cacheDiscardSize, changesOnly, sendSnapshot,
          snapshotSendPeriods, snapshotSendPeriodType) {
      // Clear messages.
      hide("apiKeyMsg");
      hide("timeoutSecondsMsg");
      hide("retriesMsg");
      hide("pointsMsg");
      
      var points = new Array();
      for (var i=0; i<selectedPoints.length; i++)
          points[points.length] = {dataPointId: selectedPoints[i].id, feedId: selectedPoints[i].feedId,
                  dataStreamId: selectedPoints[i].dataStreamId};
      
      PachubePublisherDwr.savePachubeSender(name, xid, enabled, points, $get("apiKey"), $get("timeoutSeconds"),
              $get("retries"), cacheWarningSize, cacheDiscardSize, changesOnly, sendSnapshot, snapshotSendPeriods,
              snapshotSendPeriodType, savePublisherCB);
  }
</script>

<table cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top">
      <div class="borderDiv marR marB">
        <table>
          <tr>
            <td colspan="2" class="smallTitle"><fmt:message key="publisherEdit.pachube.props"/> <tag:help id="pachubePublishing"/></td>
          </tr>
          
          <tr>
            <td class="formLabelRequired"><fmt:message key="publisherEdit.pachube.apiKey"/></td>
            <td class="formField">
              <input type="text" id="apiKey" value="${publisher.apiKey}" class="formLong"/>
              <div id="apiKeyMsg" class="formError" style="display:none;"></div>
              <div><fmt:message key="publisherEdit.pachube.apiKeyAuthority"/></div>
            </td>
          </tr>
          
          <tr>
            <td class="formLabelRequired"><fmt:message key="publisherEdit.pachube.timeoutSeconds"/></td>
            <td class="formField">
              <input type="text" id="timeoutSeconds" value="${publisher.timeoutSeconds}"/>
              <div id="timeoutSecondsMsg" class="formError" style="display:none;"></div>
            </td>
          </tr>
          
          <tr>
            <td class="formLabelRequired"><fmt:message key="publisherEdit.pachube.retries"/></td>
            <td class="formField">
              <input type="text" id="retries" value="${publisher.retries}"/>
              <div id="retriesMsg" class="formError" style="display:none;"></div>
            </td>
          </tr>
        </table>
      </div>
    </td>
  </tr>
</table>

<table cellpadding="0" cellspacing="0"><tr><td>
  <div class="borderDiv">
    <table width="100%">
      <tr>
        <td class="smallTitle"><fmt:message key="publisherEdit.points"/></td>
        <td align="right"><div id="pointLookup"></div></td>
      </tr>
    </table>
    
    <table cellspacing="1" cellpadding="0">
      <tr class="rowHeader">
        <td><fmt:message key="publisherEdit.point.name"/></td>
        <td><fmt:message key="publisherEdit.point.status"/></td>
        <td><fmt:message key="publisherEdit.point.type"/></td>
        <td><fmt:message key="publisherEdit.pachube.point.feedId"/></td>
        <td><fmt:message key="publisherEdit.pachube.point.dataStreamId"/></td>
        <td></td>
      </tr>
      <tbody id="selectedPointsEmpty" style="display:none;"><tr><td colspan="5"><fmt:message key="publisherEdit.noPoints"/></td></tr></tbody>
      <tbody id="selectedPoints"></tbody>
    </table>
    <div id="pointsMsg" class="formError" style="display:none;"></div>
  </div>
</td></tr></table>