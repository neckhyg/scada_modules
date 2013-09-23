<%--
--%><%@page import="com.serotonin.m2m2.DataTypes"%><%--
--%><%@ include file="/WEB-INF/jsp/include/tech.jsp" %>

<script type="text/javascript">
  function testValueParams() {
      startImageFader("valueTestImg", true);
      hide("valueTestRow");
      PachubeDataSourceDwr.testPachubeValueParams($get("apiKey"), $get("timeoutSeconds"), $get("retries"),
              $get("feedId"), $get("dataStreamId"), $get("dataTypeId"), $get("binary0Value"), function (result) {
          stopImageFader("valueTestImg");
          show("valueTestRow");
          $set("valueTestResult", encodeHtml(result));
      });
  }

  function saveDataSourceImpl(basic) {
      PachubeDataSourceDwr.savePachubeDataSource(basic, $get("apiKey"),
            $get("updatePeriods"), $get("updatePeriodType"), $get("timeoutSeconds"), $get("retries"), saveDataSourceCB);
  }
  
  function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions) {
      pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key="dsEdit.pachube.feedId"/>";
      pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.feedId; };
      pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key="dsEdit.pachube.dataStreamId"/>";
      pointListColumnFunctions[pointListColumnFunctions.length] =
          function(p) { return encodeHtml(p.pointLocator.dataStreamId); };
  }
  
  function editPointCBImpl(locator) {
      $set("feedId", locator.feedId);
      $set("dataStreamId", locator.dataStreamId);
      $set("dataTypeId", locator.dataTypeId);
      $set("binary0Value", locator.binary0Value);
      $set("settable", locator.settable);
      dataTypeChanged();
  }
  
  function savePointImpl(locator) {
      delete locator.relinquishable;
      
      locator.feedId = $get("feedId");
      locator.dataStreamId = $get("dataStreamId");
      locator.dataTypeId = $get("dataTypeId");
      locator.binary0Value = $get("binary0Value");
      locator.settable = $get("settable");
      PachubeDataSourceDwr.savePachubePointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }
  
  function dataTypeChanged() {
      var dataTypeId = $get("dataTypeId");
      if (dataTypeId == <%= DataTypes.BINARY %>)
          show("binaryZeroValueRow");
      else
          hide("binaryZeroValueRow");
  }
</script>

<tag:dataSourceAttrs descriptionKey="dsEdit.pachube.desc" helpId="pachubeDS">
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.updatePeriod"/></td>
    <td class="formField">
      <input type="text" id="updatePeriods" value="${dataSource.updatePeriods}" class="formShort"/>
      <tag:timePeriods id="updatePeriodType" value="${dataSource.updatePeriodType}" s="true" min="true" h="true"/>
    </td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.pachube.apiKey"/></td>
    <td class="formField"><input id="apiKey" type="text" value="${dataSource.apiKey}" class="formLong"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.pachube.timeout"/></td>
    <td class="formField"><input id="timeoutSeconds" type="text" value="${dataSource.timeoutSeconds}"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.pachube.retries"/></td>
    <td class="formField"><input id="retries" type="text" value="${dataSource.retries}"/></td>
        </tr>
</tag:dataSourceAttrs>

<tag:pointList pointHelpId="pachubePP">
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.pointDataType"/></td>
    <td class="formField"><tag:dataTypeOptions name="dataTypeId" onchange="dataTypeChanged()" excludeImage="true"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.pachube.feedId"/></td>
    <td class="formField"><input type="text" id="feedId"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired">
      <fmt:message key="dsEdit.pachube.dataStreamId"/>
      <tag:img id="valueTestImg" png="accept" title="dsEdit.pachube.testValue" onclick="testValueParams()"/>
    </td>
    <td class="formField"><input type="text" id="dataStreamId"/></td>
  </tr>
  
  <tbody id="valueTestRow" style="display:none">
    <tr>
      <td></td>
      <td id="valueTestResult"></td>
    </tr>
  </tbody>
  
  <tbody id="binaryZeroValueRow">
    <tr>
      <td><fmt:message key="dsEdit.pachube.binaryZeroValue"/></td>
      <td class="formField"><input type="text" id="binary0Value"/></td>
    </tr>
  </tbody>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.settable"/></td>
    <td class="formField"><input type="checkbox" id="settable"/></td>
  </tr>
</tag:pointList>