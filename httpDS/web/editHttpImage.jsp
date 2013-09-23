<%--
--%><%@page import="com.serotonin.m2m2.http.vo.HttpImagePointLocatorVO"%>
<%@include file="/WEB-INF/jsp/include/tech.jsp"%>

<script type="text/javascript">
  function saveDataSourceImpl(basic) {
	  HttpDataSourceDwr.saveHttpImageDataSource(basic, $get("updatePeriods"),
              $get("updatePeriodType"), saveDataSourceCB);
  }
  
  function editPointCBImpl(locator) {
      $set("url", locator.url);
      $set("timeoutSeconds", locator.timeoutSeconds);
      $set("retries", locator.retries);
      $set("scaleType", locator.scaleType);
      $set("scalePercent", locator.scalePercent);
      $set("scaleWidth", locator.scaleWidth);
      $set("scaleHeight", locator.scaleHeight);
      $set("readLimit", locator.readLimit);
      $set("webcamLiveFeedCode", locator.webcamLiveFeedCode);
      
      scaleTypeChanged();
  }
  
  function savePointImpl(locator) {
      delete locator.settable;
      delete locator.dataTypeId;
      delete locator.relinquishable;
      
      locator.url = $get("url");
      locator.timeoutSeconds = $get("timeoutSeconds");
      locator.retries = $get("retries");
      locator.scaleType = $get("scaleType");
      locator.scalePercent = $get("scalePercent");
      locator.scaleWidth = $get("scaleWidth");
      locator.scaleHeight = $get("scaleHeight");
      locator.readLimit = $get("readLimit");
      locator.webcamLiveFeedCode = $get("webcamLiveFeedCode");
      
      HttpDataSourceDwr.saveHttpImagePointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }
  
  function scaleTypeChanged() {
      var type = $get("scaleType");
      if (type == <c:out value="<%= HttpImagePointLocatorVO.SCALE_TYPE_NONE %>"/>) {
          hide("scalePercentRow");
          hide("scaleWidthRow");
          hide("scaleHeightRow");
      }
      else if (type == <c:out value="<%= HttpImagePointLocatorVO.SCALE_TYPE_PERCENT %>"/>) {
          show("scalePercentRow");
          hide("scaleWidthRow");
          hide("scaleHeightRow");
      }
      else if (type == <c:out value="<%= HttpImagePointLocatorVO.SCALE_TYPE_BOX %>"/>) {
          hide("scalePercentRow");
          show("scaleWidthRow");
          show("scaleHeightRow");
      }
  }
</script>

<tag:dataSourceAttrs descriptionKey="dsEdit.httpImage.desc" helpId="httpImageDS">
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.updatePeriod"/></td>
    <td class="formField">
      <input type="text" id="updatePeriods" value="${dataSource.updatePeriods}" class="formShort"/>
      <tag:timePeriods id="updatePeriodType" value="${dataSource.updatePeriodType}" s="true" min="true" h="true"/>
    </td>
  </tr>
</tag:dataSourceAttrs>

<tag:pointList pointHelpId="httpImagePP">
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.httpImage.url"/></td>
    <td class="formField">
      <input id="url" type="text" class="formLong"/>
      <tag:img png="bullet_go" onclick="window.open($get('url'), 'httpImageTarget')" title="dsEdit.httpImage.openUrl"/>
    </td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.httpImage.timeout"/></td>
    <td class="formField"><input id="timeoutSeconds" type="text"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.httpImage.retries"/></td>
    <td class="formField"><input id="retries" type="text"/></td>
  </tr>

  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.httpImage.scalingType"/></td>
    <td class="formField">
      <select id="scaleType" onchange="scaleTypeChanged()">
        <option value="<c:out value="<%= HttpImagePointLocatorVO.SCALE_TYPE_NONE %>"/>"><fmt:message key="dsEdit.httpImage.scalingType.none"/></option>
        <option value="<c:out value="<%= HttpImagePointLocatorVO.SCALE_TYPE_PERCENT %>"/>"><fmt:message key="dsEdit.httpImage.scalingType.percent"/></option>
        <option value="<c:out value="<%= HttpImagePointLocatorVO.SCALE_TYPE_BOX %>"/>"><fmt:message key="dsEdit.httpImage.scalingType.box"/></option>
      </select>
    </td>
  </tr>
  
  <tbody id="scalePercentRow">
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.httpImage.scalePercent"/></td>
      <td class="formField"><input id="scalePercent" type="text"/></td>
    </tr>
  </tbody>
  
  <tbody id="scaleWidthRow">
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.httpImage.scaleWidth"/></td>
      <td class="formField"><input id="scaleWidth" type="text"/></td>
    </tr>
  </tbody>
  
  <tbody id="scaleHeightRow">
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.httpImage.scaleHeight"/></td>
      <td class="formField"><input id="scaleHeight" type="text"/></td>
    </tr>
  </tbody>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.httpImage.readLimit"/></td>
    <td class="formField"><input id="readLimit" type="text"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.httpImage.liveFeed"/></td>
    <td class="formField"><textarea id="webcamLiveFeedCode" rows="10" cols="37"></textarea></td>
  </tr>
</tag:pointList>