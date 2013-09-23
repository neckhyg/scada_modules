<%--
--%><%@page import="com.eazy.eazySerial.EazySerialDataSourceVO"%>
<%@page import="com.eazy.eazySerial.EazySerialPointLocatorVO"%>
<%@ page import="com.eazy.eazySerial.EazySerialDataSourceVO" %>
<%@ include file="/WEB-INF/jsp/include/tech.jsp" %>

<script type="text/javascript">
//  function saveDataSourceImpl(basic) {
//      EazySerialEditDwr.saveEazySerialDataSource(basic, $get("pollSeconds"),
//              $get("outputScale"), saveDataSourceCB);
//  }
  function saveDataSourceImpl(basic) {
      EazySerialEditDwr.saveEazySerialDataSource(basic, $get("updatePeriods"), $get("updatePeriodType"),
              $get("commPortId"), $get("baudRate"),  $get("flowControlIn"), $get("flowControlOut"),
              $get("dataBits"), $get("stopBits"),  $get("parity"),  saveDataSourceCB);
  }
  function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions) {
      pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key="dsEdit.eazySerial.attribute"/>";
      pointListColumnFunctions[pointListColumnFunctions.length] =
              function(p) { return p.pointLocator.configurationDescription; };
  }
  
  function editPointCBImpl(locator) {
      $set("attributeId", locator.attributeId);
      $set("dataTypeId", locator.dataTypeId);
  }
  
  function savePointImpl(locator) {
      delete locator.settable;
      delete locator.dataTypeId;
      delete locator.relinquishable;
      
      locator.attributeId = $get("attributeId");
      locator.dataTypeId = $get("dataTypeId");
      EazySerialEditDwr.saveEazySerialPointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }
</script>

<tag:dataSourceAttrs descriptionKey="dsEdit.eazySerial.desc" helpId="eazySerialDS">
<!--
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.eazySerial.pollSeconds"/></td>
    <td class="formField"><input id="pollSeconds" type="text" value="${dataSource.pollSeconds}"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.eazySerial.outputScale"/></td>
    <td class="formField">
      <tag:exportCodesOptions id="outputScale" value="${dataSource.outputScale}" optionList="<%= EazySerialDataSourceVO.OUTPUT_SCALE_CODES.getIdKeys() %>"/>
    </td>
  </tr>    -->
  <tr>
     <td class="formLabelRequired"><fmt:message key="dsEdit.updatePeriod"/></td>
      <td class="formField">
          <input type="text" id="updatePeriods" value="${dataSource.updatePeriods}" class="formShort"/>
          <tag:timePeriods id="updatePeriodType" value="${dataSource.updatePeriodType}" ms="true" s="true" min="true" h="true"/>
      </td>
  </tr>
    <tr>
        <td class="formLabelRequired"><fmt:message key="dsEdit.serial.port"/></td>
        <td class="formField">
            <c:choose>
                <c:when test="${!empty commPortError}">
                    <input id="commPortId" type="hidden" value=""/>
                    <span class="formError">${commPortError}</span>
                </c:when>
                <c:otherwise>
                    <sst:select id="commPortId" value="${dataSource.commPortId}">
                        <c:forEach items="${commPorts}" var="port">
                            <sst:option value="${port.name}">${port.name}</sst:option>
                        </c:forEach>
                    </sst:select>
                </c:otherwise>
            </c:choose>
        </td>
    </tr>

    <tr>
        <td class="formLabelRequired"><fmt:message key="dsEdit.serial.baud"/></td>
        <td class="formField">
            <sst:select id="baudRate" value="${dataSource.baudRate}">
                <sst:option>110</sst:option>
                <sst:option>300</sst:option>
                <sst:option>1200</sst:option>
                <sst:option>2400</sst:option>
                <sst:option>4800</sst:option>
                <sst:option>9600</sst:option>
                <sst:option>19200</sst:option>
                <sst:option>38400</sst:option>
                <sst:option>57600</sst:option>
                <sst:option>115200</sst:option>
                <sst:option>230400</sst:option>
                <sst:option>460800</sst:option>
                <sst:option>921600</sst:option>
            </sst:select>
        </td>
    </tr>

    <tr>
        <td class="formLabelRequired"><fmt:message key="dsEdit.serial.flowIn"/></td>
        <td class="formField">
            <sst:select id="flowControlIn" value="${dataSource.flowControlIn}">
                <sst:option value="0"><fmt:message key="dsEdit.serial.flow.none"/></sst:option>
                <sst:option value="1"><fmt:message key="dsEdit.serial.flow.rtsCts"/></sst:option>
                <sst:option value="4"><fmt:message key="dsEdit.serial.flow.xonXoff"/></sst:option>
            </sst:select>
        </td>
    </tr>

    <tr>
        <td class="formLabelRequired"><fmt:message key="dsEdit.serial.flowOut"/></td>
        <td class="formField">
            <sst:select id="flowControlOut" value="${dataSource.flowControlOut}">
                <sst:option value="0"><fmt:message key="dsEdit.serial.flow.none"/></sst:option>
                <sst:option value="2"><fmt:message key="dsEdit.serial.flow.rtsCts"/></sst:option>
                <sst:option value="8"><fmt:message key="dsEdit.serial.flow.xonXoff"/></sst:option>
            </sst:select>
        </td>
    </tr>

    <tr>
        <td class="formLabelRequired"><fmt:message key="dsEdit.serial.dataBits"/></td>
        <td class="formField">
            <sst:select id="dataBits" value="${dataSource.dataBits}">
                <sst:option value="5">5</sst:option>
                <sst:option value="6">6</sst:option>
                <sst:option value="7">7</sst:option>
                <sst:option value="8">8</sst:option>
            </sst:select>
        </td>
    </tr>

    <tr>
        <td class="formLabelRequired"><fmt:message key="dsEdit.serial.stopBits"/></td>
        <td class="formField">
            <sst:select id="stopBits" value="${dataSource.stopBits}">
                <sst:option value="1">1</sst:option>
                <sst:option value="3">1.5</sst:option>
                <sst:option value="2">2</sst:option>
            </sst:select>
        </td>
    </tr>

    <tr>
        <td class="formLabelRequired"><fmt:message key="dsEdit.serial.parity"/></td>
        <td class="formField">
            <sst:select id="parity" value="${dataSource.parity}">
                <sst:option value="0"><fmt:message key="dsEdit.serial.parity.none"/></sst:option>
                <sst:option value="1"><fmt:message key="dsEdit.serial.parity.odd"/></sst:option>
                <sst:option value="2"><fmt:message key="dsEdit.serial.parity.even"/></sst:option>
                <sst:option value="3"><fmt:message key="dsEdit.serial.parity.mark"/></sst:option>
                <sst:option value="4"><fmt:message key="dsEdit.serial.parity.space"/></sst:option>
            </sst:select>
        </td>
    </tr>
</tag:dataSourceAttrs>

<tag:pointList pointHelpId="eazySerialPP">
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.eazySerial.attribute"/></td>
    <td class="formField">
      <tag:exportCodesOptions id="attributeId" optionList="<%= EazySerialPointLocatorVO.ATTRIBUTE_CODES.getIdKeys() %>"/>
    </td>
  </tr> <tr>
    <tr>
        <td class="formLabelRequired"><fmt:message key="dsEdit.eazySerial.pointDataType"/></td>
        <td class="formField"><tag:dataTypeOptions name="dataTypeId" excludeImage="true" excludeAlphanumeric="true" /></td>
    </tr>
  </tr>
</tag:pointList>