<%--
--%><%@ include file="/WEB-INF/jsp/include/tech.jsp" %>

<script type="text/javascript">
  function saveDataSourceImpl(basic) {
      httpListenCancel();
      WiBoxEditDwr.saveWiBoxHttpDataSource(basic, $get("password"), saveDataSourceCB);
  }
  
  function httpListen(filtered) {
      $set("httpListenMessage", "<fmt:message key="dsEdit.httpReceiver.listening"/>");
      listenClear();
      httpListenButtons(true);
      WiBoxEditDwr.wiBoxHttpListenForData(filtered ? $get("password") : null, httpListenCB);
  }
  
  function httpListenButtons(listening) {
      setDisabled("httpListenFilteredBtn", listening);
      setDisabled("httpListenAllBtn", listening);
      setDisabled("httpListenCancelBtn", !listening);
  }
  
  function listenClear() {
      dwr.util.removeAllOptions("httpListenData");
  }
  
  function httpListenCB() {
      setTimeout(httpListenUpdate, 2000);
  }
  
  function httpListenUpdate() {
      WiBoxEditDwr.wiBoxHttpListenerUpdate(function(result) {
          if (result) {
              if (result.message)
                  $set("httpListenMessage", result.message);
              dwr.util.addOptions("httpListenData", result.data);
              httpListenCB();
          }
      });
  }

  function httpListenCancel() {
      WiBoxEditDwr.cancelTestingUtility(function() {
          httpListenButtons(false);
          $set("httpListenMessage", "<fmt:message key="common.cancelled"/>");
      });
  }
  
  function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions) {
      pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key="dsEdit.wiboxHttp.moteId"/>";
      pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.moteId; };
      
      pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key="dsEdit.wiboxHttp.dataKey"/>";
      pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.dataKey; };
  }
  
  function editPointCBImpl(locator) {
      $set("moteId", locator.moteId);
      $set("dataKey", locator.dataKey);
      $set("dataTypeId", locator.dataTypeId);
  }
  
  function savePointImpl(locator) {
      delete locator.settable;
      delete locator.relinquishable;
      
      locator.moteId = $get("moteId");
      locator.dataKey = $get("dataKey");
      locator.dataTypeId = $get("dataTypeId");
      
      WiBoxEditDwr.saveWiBoxHttpPointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }
</script>

<tag:dataSourceAttrs descriptionKey="dsEdit.wiboxHttp.desc" helpId="wiboxDS">
  <jsp:attribute name="extraPanels">
    <td valign="top">
      <div class="borderDiv marB">
        <table>
          <tr><td class="smallTitle"><fmt:message key="dsEdit.wiboxHttp.wiboxListener"/></td></tr>
          <tr>
            <td>
              <input id="httpListenFilteredBtn" type="button" value="<fmt:message key="dsEdit.wiboxHttp.startListenerFiltered"/>" onclick="httpListen(true);"/>
              <input id="httpListenAllBtn" type="button" value="<fmt:message key="dsEdit.wiboxHttp.startListenerAll"/>" onclick="httpListen(false);"/>
              <input type="button" value="<fmt:message key="dsEdit.wiboxHttp.listenerClear"/>" onclick="listenClear();"/>
              <input id="httpListenCancelBtn" type="button" value="<fmt:message key="common.cancel"/>" onclick="httpListenCancel();"/>
            </td>
          </tr>
          <tr><td id="httpListenMessage" class="formError"></td></tr>
          <tr><td class="formField"><ul id="httpListenData"></ul></td></tr>
          <tr>
            <td>
              <input type="button" value="<fmt:message key="dsEdit.wiboxHttp.listenerClear"/>" onclick="listenClear();"/>
            </td>
          </tr>
        </table>
      </div>
    </td>
  </jsp:attribute>

  <jsp:body>
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.wiboxHttp.password"/></td>
      <td class="formField"><input type="text" id="password" value="${dataSource.password}"/></td>
    </tr>
  </jsp:body>
</tag:dataSourceAttrs>

<tag:pointList pointHelpId="wiboxPP">
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.wiboxHttp.moteId"/></td>
    <td class="formField"><input type="text" id="moteId"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.wiboxHttp.dataKey"/></td>
    <td class="formField"><input type="text" id="dataKey"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.pointDataType"/></td>
    <td class="formField">
      <select id="dataTypeId">
        <tag:dataTypeOptions excludeImage="true" excludeMultistate="true"/>
      </select>
    </td>
  </tr>
</tag:pointList>