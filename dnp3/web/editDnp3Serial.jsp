<%@ include file="/WEB-INF/jsp/include/tech.jsp" %>
<%@page import="br.org.scadabr.dnp3.vo.Dnp3SerialDataSourceVO"%>

<script type="text/javascript">
function saveDataSourceImpl(basic) {
    DnpEditDwr.saveDNP3SerialDataSource(basic,
          $get("sourceAddress"), $get("slaveAddress"), $get("commPortId"), $get("baudRate"), 
          $get("staticPollPeriods"), $get("rbePollPeriods"),
          $get("rbePeriodType"), $get("timeout"), $get("retries"), saveDataSourceCB);
}
</script>

<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.dnp3Serial.commPortId"/></td>
  <td class="formField"><input id="commPortId" type="text" value="${dataSource.commPortId}"/></td>
</tr>

<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.dnp3Serial.baud"/></td>
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