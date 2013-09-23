<%--
--%><%@ include file="/WEB-INF/jsp/include/tech.jsp" %>

<script type="text/javascript">
  var currentChangeType;

  function saveDataSourceImpl(basic) {
      AsciiEditDwr.saveSerialDataSource(basic, $get("updatePeriods"), $get("updatePeriodType"), $get("commPortId"), 
              $get("baudRate"), $get("dataBits"), $get("stopBits"), $get("parity"), $get("timeout"), $get("retries"), 
              $get("stopMode"), $get("nChar"), $get("charStopMode"), $get("charX"), $get("hexValue"), 
              $get("stopTimeout"), $get("initString"), $get("bufferSize"), $get("quantize"), saveDataSourceCB);
  }
  
  function initImpl() {
      hide("tsDiv1");
      hide("tsDiv2");
      changeStopMode();
  }
  
  function editPointCBImpl(locator) {
      $set("dataTypeId", locator.dataTypeId);
      $set("valueRegex", locator.valueRegex);
      $set("command", locator.command);
      $set("customTimestamp",locator.customTimestamp);
      checkTimestampChanged();

      if (locator.customTimestamp) {
          $set("timestampFormat",locator.timestampFormat);
          $set("timestampRegex", locator.timestampRegex);
      }
  }
  
  function savePointImpl(locator) {
      locator.dataTypeId = $get("dataTypeId");
      locator.valueRegex = $get("valueRegex");
      locator.command = $get("command");
      locator.customTimestamp = $get("customTimestamp");
      locator.timestampFormat = $get("timestampFormat");
      locator.timestampRegex = $get("timestampRegex");
      
      AsciiEditDwr.saveSerialPointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }

  function valueSuggestChanged() {
      $set("valueRegex", $get("valueSuggestions"));
  }

  function timestampSuggestChanged() {
      suggest = $("timestampSuggestions");
      index = suggest.selectedIndex;
      dataFormat = suggest.options[index].title;
      $set("timestampFormat", dataFormat);
      $set("timestampRegex", $get("timestampSuggestions"));
  }
  
  function checkTimestampChanged() {
      if ($get("customTimestamp")) {
          show("tsDiv1");
          show("tsDiv2");
      }
      else {
          hide("tsDiv1");
          hide("tsDiv2");
      }
  }

  function changeStopMode() {
      var sm = $get("stopMode");
      if (sm == 0) {
          setDisabled("nChar", false);
          setDisabled("charStopMode", true);
          setDisabled("charX", true);
          setDisabled("hexValue", true);
          setDisabled("stopTimeout", true);
      }
      else if(sm == 1) {
          setDisabled("nChar", true);
          setDisabled("charStopMode", false);
          changeCharStopMode();
          setDisabled("stopTimeout", true);
      }
      else {
          setDisabled("nChar", true);
          setDisabled("charStopMode", true);
          setDisabled("charX", true);
          setDisabled("hexValue", true);
          setDisabled("stopTimeout", false);
      }
  }

  function changeCharStopMode() {
      var sm = $get("charStopMode");
      if (sm == 0) {
          setDisabled("charX", false);
          setDisabled("hexValue", true);
      }
      else {
          setDisabled("charX", true);
          setDisabled("hexValue", false);
      }
  }
</script>

<tag:dataSourceAttrs descriptionKey="ascii.serial.desc" helpId="asciiSerialDS">
  <tag:serialSettings/>

  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.updatePeriod"/></td>
    <td class="formField">
      <input type="text" id="updatePeriods" value="${dataSource.updatePeriods}" class="formShort"/>
      <tag:timePeriods id="updatePeriodType" value="${dataSource.updatePeriodType}" s="true" min="true" h="true"/>
    </td>
  </tr>
    
  <tr>
    <td class="formLabelRequired"><fmt:message key="ascii.serial.timeout"/></td>
    <td class="formField"><input type="text" id="timeout" value="${dataSource.timeout}"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="ascii.serial.retries"/></td>
    <td class="formField"><input type="text" id="retries" value="${dataSource.retries}"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.quantize"/></td>
    <td class="formField"><sst:checkbox id="quantize" selectedValue="${dataSource.quantize}"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="ascii.serial.stopMode"/></td>
    <td class="formField">
      <sst:select id="stopMode" onchange="changeStopMode()" value="${dataSource.stopMode}">
        <sst:option value="0"><fmt:message key="ascii.serial.stopMode.nChar"/></sst:option>
        <sst:option value="1"><fmt:message key="ascii.serial.stopMode.charX"/></sst:option>
        <sst:option value="2"><fmt:message key="ascii.serial.charStopMode.stopTimeout"/></sst:option>
      </sst:select>
    </td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="ascii.serial.nChar"/></td>
    <td class="formField"><input id="nChar" type="text" value="${dataSource.nChar}"/></td>
  </tr>

  <tr>
    <td class="formLabelRequired"><fmt:message key="ascii.serial.charStopMode"/></td>
    <td class="formField">
      <sst:select id="charStopMode" onchange="changeCharStopMode()" value="${dataSource.charStopMode}">
        <sst:option value="0"><fmt:message key="ascii.serial.charStopMode.charASCII"/></sst:option>
        <sst:option value="1"><fmt:message key="ascii.serial.charStopMode.hexValue"/></sst:option>
      </sst:select>
    </td>
  </tr>

  <tr>
    <td class="formLabelRequired"><fmt:message key="ascii.serial.charX"/></td>
    <td class="formField"><input id="charX" type="text" value="${dataSource.charX}" disabled="disabled"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="ascii.serial.hexValue"/></td>
    <td class="formField"><input id="hexValue" type="text" value="${dataSource.hexValue}" disabled="disabled"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="ascii.serial.stopTimeout"/></td>
    <td class="formField"><input id="stopTimeout" type="text" value="${dataSource.stopTimeout}" disabled="disabled"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="ascii.serial.bufferSize"/></td>
    <td class="formField">
      <sst:select id="bufferSize" value="${dataSource.bufferSize}">
        <sst:option value="2">2</sst:option>
        <sst:option value="4">4</sst:option>
        <sst:option value="8">8</sst:option>
        <sst:option value="16">16</sst:option>
        <sst:option value="32">32</sst:option>
        <sst:option value="64">64</sst:option>
        <sst:option value="256">256</sst:option>
        <sst:option value="1024">1024</sst:option>
      </sst:select>
    </td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="ascii.serial.initString"/></td>
    <td class="formField"><input id="initString" type="text" value=""/></td>
  </tr>
</tag:dataSourceAttrs>

<tag:pointList pointHelpId="asciiSerialPP">
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.pointDataType"/></td>
    <td class="formField"><tag:dataTypeOptions name="dataTypeId" excludeImage="true"/></td>
  </tr>

  <tr>
    <td class="formLabelRequired"><fmt:message key="ascii.serial.command"/></td>
    <td class="formField"><input id="command" type="text" value=""/></td>
  </tr>

  <tr>
    <td class="formLabelRequired"><fmt:message key="ascii.serial.valueRegex"/></td>
    <td class="formField">
      <input id="valueRegex" type="text" value=""/>
      <select id="valueSuggestions" onchange="valueSuggestChanged();">
        <option value=""> &nbsp; </option>
        <option value="((\b[0-9]+)?\.)?[0-9]+\b"> <fmt:message key="ascii.serial.regex.number"/>  </option>
      </select>
    </td>
  </tr>

  <tr>
    <td class="formLabelRequired"><fmt:message key="ascii.serial.customTimestamp"/></td>
    <td class="formField"><input id="customTimestamp" type="checkbox" onchange="checkTimestampChanged();"/></td>
  </tr>

  <tr id="tsDiv1">
    <td class="formLabelRequired"><fmt:message key="ascii.serial.timestampFormat"/></td>
    <td class="formField">
      <input id="timestampFormat" type="text" value=""/>
      <select id="timestampSuggestions" onchange="timestampSuggestChanged();">
        <option value=""> &nbsp; </option>
        <option value="20\d{2}\/((0[1-9])|(1[0-2]))\/((0[1-9])|([1-2][0-9])|(3[0-1]))\s(([0-1][0-9])|(2[0-3])):([0-5][0-9]):([0-5][0-9])" title="yyyy/MM/dd KK:mm:ss"> YYYY/MM/DD HH:MM:SS (2010/12/25 18:30:00) </option>
        <option value="20\d{2}-((0[1-9])|(1[0-2]))-((0[1-9])|([1-2][0-9])|(3[0-1]))\s(([0-1][0-9])|(2[0-3])):([0-5][0-9]):([0-5][0-9])" title="yyyy-MM-dd KK:mm:ss"> YYYY-MM-DD HH:MM:SS (2010-12-25 18:30:00)  </option>
        <option value="\d{2}\/((0[1-9])|(1[0-2]))\/((0[1-9])|([1-2][0-9])|(3[0-1]))\s(([0-1][0-9])|(2[0-3])):([0-5][0-9]):([0-5][0-9])" title="yy/MM/dd KK:mm:ss"> YY/MM/DD HH:MM:SS (10/12/25 18:30:00) </option>
        <option value="\d{2}-((0[1-9])|(1[0-2]))-((0[1-9])|([1-2][0-9])|(3[0-1]))\s(([0-1][0-9])|(2[0-3])):([0-5][0-9]):([0-5][0-9])" title="yy-MM-dd KK:mm:ss"> YY-MM-DD HH:MM:SS (10-12-25 18:30:00) </option>
      </select>
    </td>
  </tr>

  <tr id="tsDiv2">
    <td class="formLabelRequired"><fmt:message key="ascii.serial.timestampRegex"/></td>
    <td class="formField"><input id="timestampRegex" type="text" value=""/></td>
  </tr>
</tag:pointList>