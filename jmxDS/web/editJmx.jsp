<%--
--%><%@ include file="/WEB-INF/jsp/include/tech.jsp" %>
<%@page pageEncoding="UTF-8" %>

<script type="text/javascript">
  dojo.require("dijit.Tree");
  dojo.require("dijit.tree.TreeStoreModel");
  dojo.require("dojo.data.ItemFileWriteStore");
  
  var tree;

  function initImpl() {
      setDisabled("objectNamesBtn", false);
  }

  function saveDataSourceImpl(basic) {
	  JmxEditDwr.saveJmxDataSource(basic, $get("useLocalServer"),
              $get("remoteServerAddr"), $get("updatePeriodType"), $get("updatePeriods"), $get("quantize"),
              saveDataSourceCB);
  }
  
  function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions) {
      pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key="dsEdit.jmx.attributeName"/>";
      pointListColumnFunctions[pointListColumnFunctions.length] =
              function(p) { return p.pointLocator.configurationDescription; };
  }
  
  function editPointCBImpl(locator) {
      $set("objectName", locator.objectName);
      $set("attributeName", locator.attributeName);
      $set("compositeItemName", locator.compositeItemName);
      $set("dataTypeId", locator.dataTypeId);
      $set("settable", locator.settable);
  }
  
  function savePointImpl(locator) {
      delete locator.relinquishable;
      
      locator.objectName = $get("objectName");
      locator.attributeName = $get("attributeName");
      locator.compositeItemName = $get("compositeItemName");
      locator.dataTypeId = $get("dataTypeId");
      locator.settable = $get("settable");
      
      JmxEditDwr.saveJmxPointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }
  
  function useLocalServerChange() {
      setDisabled("remoteServerAddr", $get("useLocalServer"));
  }
  
  function getObjectNames() {
      $set("objectNamesMessage", "<fmt:message key="dsEdit.jmx.gettingObjectNames"/>");
      setDisabled("objectNamesBtn", true);
      
      if (tree)
          tree.destroy();
      
      var localServer = $get("useLocalServer");
      JmxEditDwr.getJmxObjectNames(localServer, $get("remoteServerAddr"), function(response) {
          setDisabled("objectNamesBtn", false);
          if (response.hasMessages)
              $set("objectNamesMessage", response.messages[0].genericMessage);
          else {
              $set("objectNamesMessage");
              
              var storeItems = [];
              
              var root = {
                      name: "<b>"+ (localServer ? "<fmt:message key="dsEdit.jmx.dsconn.local"/>" : $get("remoteServerAddr")) +"</b>",
                      children: []
              };
              storeItems.push(root);
              
              for (var name in response.data.names) {
                  var obj = response.data.names[name];
                  var objItem = { name: name, children: [] };
                  root.children.push(objItem);
                  
                  for (var ai=0; ai<obj.length; ai++) {
                      var attr = obj[ai];
                      var attrItem; 
                      if (!attr.items) {
                          var func = "preAddPoint(\""+ name +"\", \""+ attr.name +"\", \"\")";
                          attrItem = {
                                  name: attr.name +"("+ attr.type +") "+ writeImageSQuote(null, null,
                                          "icon_comp_add", "<fmt:message key="dsEdit.jmx.addPoint"/>", func)
                          };
                          objItem.children.push(attrItem);
                      }
                      else {
                          attrItem = { name: attr.name +"("+ attr.type +")", children: [] };
                          objItem.children.push(attrItem);
                          
                          for (var ii=0; ii<attr.items.length; ii++) {
                              var item = attr.items[ii];
                              var func = "preAddPoint(\""+ name +"\", \""+ attr.name +"\", \""+ item.name +"\")";
                              var itemItem = { 
                                      name: item.name +"("+ item.type +") "+ writeImageSQuote(null, null,
                                              "icon_comp_add", "<fmt:message key="dsEdit.jmx.addPoint"/>", func)
                              };
                              
                              attrItem.children.push(itemItem);
                          }
                      }
                  }
              }
              
              // Create the item store
              var store = new dojo.data.ItemFileWriteStore({
                  data: { label: 'name', items: storeItems },
                  clearOnClose: true
              });
              
              var div = dojo.create("div");
              $("inspectionTree").appendChild(div);
              
              // Create the tree.
              tree = new dijit.Tree({
                  model: new dijit.tree.ForestStoreModel({ store: store }),
                  showRoot: false,
                  persist: false,
                  _createTreeNode: function(args) {
                      var tnode = new dijit._TreeNode(args);
                      tnode.labelNode.innerHTML = args.label;
                      return tnode;
                  }
              }, div);
              
              tree._expandNode(tree.getNodesByItem(root)[0]);
          }
      });
  }
  
  var addPointData;
  function preAddPoint(objectName, attrName, compositeName) {
      addPointData = {
              objectName: objectName,
              attrName: attrName,
              compositeName: compositeName
      };
      
      addPoint();
  }
  
  function addPointImpl() {
	  JmxEditDwr.getPoint(-1, function(point) {
          editPointCB(point);
          $set("objectName", addPointData.objectName);
          $set("attributeName", addPointData.attrName);
          $set("compositeItemName", addPointData.compositeName);
      });
  }
</script>

<tag:dataSourceAttrs descriptionKey="dsEdit.jmx.desc" helpId="jmxDS">
  <jsp:attribute name="extraPanels">
    <td valign="top">
      <div class="borderDiv marB">
        <table>
          <tr><td colspan="2" class="smallTitle"><fmt:message key="dsEdit.jmx.inspect"/></td></tr>
          <tr>
            <td colspan="2" align="center">
              <input id="objectNamesBtn" type="button" value="<fmt:message key="dsEdit.jmx.getObjectNames"/>" onclick="getObjectNames();"/>
            </td>
          </tr>
          
          <tr><td colspan="2" id="objectNamesMessage" class="formError"></td></tr>
          
          <tbody id="inspectionDetails">
            <tr><td colspan="2" id="inspectionTree"></td></tr>
          </tbody>
        </table>
      </div>
    </td> 
  </jsp:attribute>

  <jsp:body>
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.jmx.useLocalServer"/></td>
      <td class="formField">
        <sst:checkbox id="useLocalServer" selectedValue="${dataSource.useLocalServer}" onclick="useLocalServerChange()"/>
      </td>
    </tr>
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.jmx.remoteServerAddr"/></td>
      <td class="formField"><input id="remoteServerAddr" type="text" value="${dataSource.remoteServerAddr}"/></td>
    </tr>
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.updatePeriod"/></td>
      <td class="formField">
        <input type="text" id="updatePeriods" value="${dataSource.updatePeriods}" class="formShort"/>
        <tag:timePeriods id="updatePeriodType" value="${dataSource.updatePeriodType}" ms="true" s="true" min="true" h="true"/>
      </td>
    </tr>
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.quantize"/></td>
      <td class="formField"><sst:checkbox id="quantize" selectedValue="${dataSource.quantize}"/></td>
    </tr>
  </jsp:body>
</tag:dataSourceAttrs>
      
<tag:pointList pointHelpId="jmxPP">
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.jmx.objectName"/></td>
    <td class="formField"><input type="text" id="objectName"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.jmx.attributeName"/></td>
    <td class="formField"><input type="text" id="attributeName"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.jmx.compositeItemName"/></td>
    <td class="formField"><input type="text" id="compositeItemName"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.pointDataType"/></td>
    <td class="formField"><tag:dataTypeOptions id="dataTypeId" excludeImage="true" excludeMultistate="true"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.settable"/></td>
    <td class="formField"><input type="checkbox" id="settable"/></td>
  </tr>
</tag:pointList>