<%--
--%><%@ include file="/WEB-INF/jsp/include/tech.jsp" %>

<script type="text/javascript">
  var currentChangeType;
  
  function saveDataSourceImpl(basic) {
      AsciiEditDwr.saveFileDataSource(basic, $get("updatePeriods"), $get("updatePeriodType"), $get("filePath"), 
              $get("quantize"), saveDataSourceCB);
  }
  
  function initImpl() {
      hide("tsDiv1");
      hide("tsDiv2");
  }
  
  function editPointCBImpl(locator) {
      $set("dataTypeId", locator.dataTypeId);
      $set("valueRegex", locator.valueRegex);
      $set("customTimestamp",locator.customTimestamp);
      checkTimestampChanged();

      if (locator.customTimestamp) {
          $set("timestampFormat",locator.timestampFormat);
          $set("timestampRegex", locator.timestampRegex);
      }
  }
  
  function savePointImpl(locator) {
      delete locator.relinquishable;
      
      locator.dataTypeId = $get("dataTypeId");
      locator.valueRegex = $get("valueRegex");
      locator.customTimestamp = $get("customTimestamp");
      locator.timestampFormat = $get("timestampFormat");
      locator.timestampRegex = $get("timestampRegex");
      
      AsciiEditDwr.saveFilePointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }
  
  function checkFile() {
      AsciiEditDwr.checkFile($get("filePath"), function(result) { alert(result); });
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
</script>

<tag:dataSourceAttrs descriptionKey="ascii.file.desc" helpId="asciiFileReaderDS">
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.updatePeriod"/></td>
    <td class="formField">
      <input type="text" id="updatePeriods" value="${dataSource.updatePeriods}" class="formShort"/>
      <tag:timePeriods id="updatePeriodType" value="${dataSource.updatePeriodType}" s="true" min="true" h="true"/>
    </td>
  </tr>
    
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.quantize"/></td>
    <td class="formField"><sst:checkbox id="quantize" selectedValue="${dataSource.quantize}"/></td>
  </tr>

  <tr>
    <td class="formLabelRequired"><fmt:message key="ascii.file.filePath"/></td>
    <td class="formField">
      <input id="filePath" type="text" value="${dataSource.filePath}"/><br/>
      <input id="checkBtn" type="button" value="<fmt:message key="ascii.file.checkFile"/>" onclick="checkFile();" />
    </td>
  </tr>
</tag:dataSourceAttrs>

<tag:pointList pointHelpId="asciiFileReaderPP">
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.pointDataType"/></td>
    <td class="formField"><tag:dataTypeOptions name="dataTypeId" onchange="dataTypeChanged()" excludeImage="true"/></td>
  </tr>

  <tr>
    <td class="formLabelRequired"><fmt:message key="ascii.file.valueRegex"/></td>
    <td class="formField">
      <input id="valueRegex" type="text" value=""/>
      <select id="valueSuggestions" onchange="valueSuggestChanged();">
        <option value=""> &nbsp; </option>
        <option value="((\b[0-9]+)?\.)?[0-9]+\b"> <fmt:message key="ascii.file.regex.number"/>  </option>
      </select>
    </td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="ascii.file.timestampCheck"/></td>
    <td class="formField"><input id="customTimestamp" type="checkbox" onchange="checkTimestampChanged();"/></td>
  </tr>
  
  <tr id="tsDiv1">
    <td class="formLabelRequired"><fmt:message key="ascii.file.timestampFormat"/></td>
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
    <td class="formLabelRequired"><fmt:message key="ascii.file.timestampRegex"/></td>
    <td class="formField"><input id="timestampRegex" type="text" value=""/></td>
  </tr>
</tag:pointList>