<%--
--%><%@ include file="/WEB-INF/jsp/include/tech.jsp" %>
<%-- <%@page import="com.serotonin.m2m2.Common"%> --%>
<%@page import="com.serotonin.m2m2.scripting.ScriptDataSourceVO"%>

<script type="text/javascript">
  dojo.require("dojo.store.Memory");
  dojo.require("dijit.form.FilteringSelect");
  
  var pointsArray = new Array();
  var contextArray = new Array();
  
  function initImpl() {
      // Create the list of all available points.
      <c:forEach items="${userPoints}" var="dp"><%--
        --%><c:if test="${dp.dataSourceId != dataSource.id}"><%--
          --%>pointsArray.push({ id: ${dp.id}, name: '${sst:quotEncode(dp.extendedName)}', type: '<m2m2:translate message="${dp.dataTypeMessage}"/>', fancyName: '${sst:quotEncode(dp.extendedName)}' });<%--
        --%></c:if><%--
      --%></c:forEach>

      // Add points that are already selected to the context array
      <c:forEach items="${dataSource.context}" var="ivp">addToContextArray(${ivp.key}, '${sst:quotEncode(ivp.value)}');</c:forEach>
      
      // Draw the selected point list.
      refreshContextPoints();
      
      // Create the lookup
      new dijit.form.FilteringSelect({
          store: new dojo.store.Memory({ data: pointsArray }),
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
      
      createContextualMessageNode("contextContainer", "context");
      createContextualMessageNode("cronMessages", "cronPattern");
      
      logLevelChanged();
  }
  
  function selectPoint(pointId) {
      if (!containsPoint(pointId)) {
          addToContextArray(pointId, "p"+ pointId);
          refreshContextPoints();
      }
  }
  
  function containsPoint(pointId) {
      return getElement(contextArray, pointId, "id") != null;
  }
  
  function addToContextArray(pointId, varName) {
      var data = getElement(pointsArray, pointId);
      if (data) {
          // Disable the name in the lookup
          data.fancyName = "<span class='disabled'>"+ data.name +"</span>";
          
          // Missing names imply that the point was deleted, so ignore.
          contextArray.push({
              id : pointId,
              pointName : data.name,
              pointType : data.type,
              varName: varName
          });
      }
  }
  
  function removeFromContextArray(pointId) {
      removeElement(contextArray, pointId);
      refreshContextPoints();
      
      var data = getElement(pointsArray, pointId);
      if (data)
          data.fancyName = data.name;
  }
  
  function refreshContextPoints() {
      dwr.util.removeAllRows("contextTable");
      if (contextArray.length == 0) {
          show($("contextTableEmpty"));
          hide($("contextTableHeaders"));
      }
      else {
          hide($("contextTableEmpty"));
          show($("contextTableHeaders"));
          dwr.util.addRows("contextTable", contextArray,
              [
                  function(data) { return data.pointName; },
                  function(data) { return data.pointType; },
                  function(data) {
                      return "<input type='text' value='"+ data.varName +"' onblur='updateVarName("+ data.id +", this.value)' class='formShort'/>";
                  },
                  function(data) { 
                      return "<img src='images/bullet_delete.png' class='ptr' onclick='removeFromContextArray("+ data.id +")'/>";
                  }
              ],
              {
                  rowCreator:function(options) {
                      var tr = document.createElement("tr");
                      tr.className = "smRow"+ (options.rowIndex % 2 == 0 ? "" : "Alt");
                      return tr;
                  }
              }
          );
      }
  }
  
  function updateVarName(pointId, varName) {
      var data = getElement(contextArray, pointId);
      if (data)
            data.varName = varName;
  }
  
  
  function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions) {
      pointListColumnHeaders.push("<sst:i18n key="dsEdit.script.varName" escapeDQuotes="true"/>");
      pointListColumnFunctions.push(function(p) { return p.pointLocator.varName; });
  }
  
  function saveDataSourceImpl(basic) {
      ScriptingEditDwr.saveDataSource(basic, createContextArray(), 
              $get("script"), $get("cronPattern"), $get("executionDelaySeconds"), $get("logLevel"), saveDataSourceCB);
  }
  
  function editPointCBImpl(locator) {
      $set("varName", locator.varName);
      $set("dataTypeId", locator.dataTypeId);
      $set("settable", locator.settable);
  }
  
  function savePointImpl(locator) {
      delete locator.relinquishable;
      locator.varName = $get("varName");
      locator.dataTypeId = $get("dataTypeId");
      locator.settable = $get("settable");
      ScriptingEditDwr.savePointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }

  function validateScript() {
      hide("scriptValidationOutput");
      ScriptingEditDwr.validateDataSourceScript(createContextArray(), $get("script"), $get("logLevel"),
              function(response) {
          var output = "";
          if (response.hasMessages)
              output = response.messages[0].genericMessage;
          else if (response.data.out)
              output = response.data.out;
          else
              output = "<sst:i18n key="dsEdit.script.noOutput" escapeDQuotes="true"/>";
          $set("scriptValidationOutput", output);
          show("scriptValidationOutput");
      });
  }
  
  function createContextArray() {
      var context = new Array();
      for (var i=0; i<contextArray.length; i++) {
          context[context.length] = {
              key : contextArray[i].id,
              value : contextArray[i].varName
          };
      }
      return context;
  }
  
  function logLevelChanged() {
      if ($get("logLevel") == <%= ScriptDataSourceVO.LogLevel.NONE %>)
          hide("logPathMsg");
      else
          show("logPathMsg")
  }
</script>

<tag:dataSourceAttrs descriptionKey="dsEdit.script.desc" helpId="scriptingDS">
  <tr>
    <td class="formLabelRequired"><fmt:message key="common.cronPattern"/></td>
    <td class="formField">
      <input type="text" id="cronPattern" value="${dataSource.cronPattern}"/>
      <tag:img png="bullet_go_left" onclick="$set('cronPattern', $get('sampleCrons'))"/>
      <select id="sampleCrons">
        <option value="0 * * * * ?"><fmt:message key="dsEdit.script.crons.min"/></option>
        <option value="0 0/15 * * * ?"><fmt:message key="dsEdit.script.crons.15min"/></option>
        <option value="0 0 * * * ?"><fmt:message key="dsEdit.script.crons.hour"/></option>
        <option value="0 0 0/4 * * ?"><fmt:message key="dsEdit.script.crons.4hour"/></option>
        <option value="0 0 0 * * ?"><fmt:message key="dsEdit.script.crons.day"/></option>
      </select>
      <tag:help id="cronPatterns"/>
      <span id="cronMessages"></span>
    </td>
  </tr>
  
  <tr>
    <td class="formLabel"><fmt:message key="dsEdit.script.delay"/></td>
    <td class="formField"><input type="text" id="executionDelaySeconds" value="${dataSource.executionDelaySeconds}"/></td>
  </tr>
  
  <tr>
    <td class="formLabel"><fmt:message key="dsEdit.script.scriptContext"/></td>
    <td class="formField">
      <div id="pointLookup"></div>
      
      <table cellspacing="1" id="contextContainer">
        <tbody id="contextTableEmpty" style="display:none;">
          <tr><th colspan="4"><fmt:message key="dsEdit.script.noPoints"/></th></tr>
        </tbody>
        <tbody id="contextTableHeaders" style="display:none;">
          <tr class="smRowHeader">
            <td><fmt:message key="dsEdit.script.pointName"/></td>
            <td><fmt:message key="dsEdit.pointDataType"/></td>
            <td><fmt:message key="dsEdit.script.varName"/></td>
            <td></td>
          </tr>
        </tbody>
        <tbody id="contextTable"></tbody>
      </table>
    </td>
  </tr>
  
  <tr>
    <td class="formLabelRequired">
      <fmt:message key="dsEdit.script.script"/> <tag:img png="accept" onclick="validateScript();" title="common.validate"/>
    </td>
    <td class="formField">
      <textarea id="script" rows="20" cols="80">${dataSource.script}</textarea>
      <div id="scriptValidationOutput" style="display:none;"></div>
    </td>
  </tr>

  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.script.logLevel"/></td>
    <td class="formField">
      <tag:exportCodesOptions id="logLevel" optionList="<%= ScriptDataSourceVO.LOG_LEVEL_CODES.getIdKeys() %>"
              value="${dataSource.logLevel}" onchange="logLevelChanged()"/>
      <div id="logPathMsg">
        <fmt:message key="dsEdit.script.logPath">
          <fmt:param value="${dataSource.logPath}"/>
        </fmt:message>
      </div>
    </td>
  </tr>
</tag:dataSourceAttrs>

<tag:pointList pointHelpId="scriptingPP">
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.script.varName"/></td>
    <td class="formField"><input type="text" id="varName"/></td>
  </tr>

  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.pointDataType"/></td>
    <td class="formField">
      <tag:dataTypeOptions id="dataTypeId" excludeImage="true"/>
    </td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.settable"/></td>
    <td class="formField"><input type="checkbox" id="settable"/></td>
  </tr>
</tag:pointList>