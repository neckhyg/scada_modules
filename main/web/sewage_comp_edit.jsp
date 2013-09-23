<%--
--%><%@page import="com.serotonin.m2m2.Constants"%><%--
--%><%@page import="com.serotonin.m2m2.Common"%><%--
--%><%@page import="com.serotonin.m2m2.view.ShareUser"%><%--
--%><%@page pageEncoding="UTF-8"%><%--
--%><%@ include file="/WEB-INF/jsp/include/tech.jsp" %><%--
--%><%@ taglib prefix="main" tagdir="/WEB-INF/tags/main" %>

<main:eazyPage dwr="SewageCompanyDwr" js="${modulePath}/web/js/view.js,${modulePath}/web/main.js">
  <jsp:attribute name="styles">
    <style>
    </style>
  </jsp:attribute>
  
  <jsp:body>
    <script type="text/javascript">
    var permission = ${sewageCompPermission};
    
require(["dojo/ready","dojo/query!css3","dojo/NodeList-traverse"],function(ready,query,dom){
    ready(function(){
      loadSewageCompanyList();
    });
});

function loadSewageCompanyList(){
  hide($("sewageCompRecord"));
  hide($("hourglass"));
  hide($("sewageCompanyDetails"));
	
  checkPermission();
	
  SewageCompanyDwr.getSewageCompanies(function(sewageCompanyList){
       dwr.util.removeAllRows("sewageCompanyList");
       if (sewageCompanyList.length == 0) {
           show($("noCompany"));
           hide($("sewageCompanyList"));
       }
       else {
           hide($("noCompany"));
           show($("sewageCompanyList"));
           dwr.util.addRows("sewageCompanyList", sewageCompanyList,
               [ function(data) { return data.id; },
                 function(data) { return data.name; },
                 function(data) { return data.address; },
                 function(data) { return data.telephone; },
                 function(data) { return data.dataPointName; },
                 function(data) {

                     var result = "<a onclick='loadsewageCompRecord("+ data.id +")' style='cursor:pointer;'>采样记录</a>";
	                 	if(permission){
		                     result += "&nbsp;<img src='images/pencil.png' class='ptr' title='<fmt:message key="sewageCompany.edit"/>' "+
                          "onclick='editSewageCompany("+ data.id +")'/>";
		                      result += "&nbsp;<img src='images/delete.png' class='ptr' title='<fmt:message key="sewageCompany.delete"/>' "+
                           "onclick='deleteSewageCompany("+ data.id +")'/>";
	                 	}
                 return result;}],
               {
                   rowCreator: function(options) {
                       var tr = document.createElement("tr");
                       tr.className = "row"+ (options.rowIndex % 2 == 0 ? "" : "Alt");
                       return tr;
                   },
                   cellCreator: function(options) {
                       var td = document.createElement("td");
                       if (options.cellNum == 2)
                           td.align = "right";
                       if (options.cellNum == 3)
                           td.align = "center";
                       return td;
                   }
               });
       }
      });
}

function refreshSewageCompanyList(){
	dataPointChanged($get("dataPointSelect"));
}

function newSewageCompany(){
  $set("sewageCompanyId","");
  $set("sewageCompanyName","");
  $set("sewageCompanyAddress","");
  $set("sewageCompanyTelephone","");
  $set("sewageCompanyDataPointId","");
  show($("sewageCompanyDetails"));
}

function saveSewageCompany(){
  if($get("sewageCompanyId")==""){
    var sewageCompanyId = -1;
  }
  else{
    var sewageCompanyId = $get("sewageCompanyId");
  }
  
  var sewageCompany = {id:sewageCompanyId,name:$get("sewageCompanyName"),address:$get("sewageCompanyAddress"),telephone:$get("sewageCompanyTelephone"),dataPointId:$get("dataPointListSelect")};
  SewageCompanyDwr.saveSewageCompany(sewageCompany);
  hide($("sewageCompanyDetails"));
}

function editSewageCompany(id){
  show($("sewageCompanyDetails"));
  SewageCompanyDwr.getSewageCompany(id,function(sewageCompany){
      $set("sewageCompanyId",sewageCompany.id);
      $set("sewageCompanyName",sewageCompany.name);
      $set("sewageCompanyAddress",sewageCompany.address);
      $set("sewageCompanyTelephone",sewageCompany.telephone);
      $set("dataPointListSelect",sewageCompany.dataPointId);
      });
}

function deleteSewageCompany(id){
  SewageCompanyDwr.deleteSewageCompany(id);
  loadSewageCompanyList();
}


function dataPointChanged(id) {
	hide($("sewageCompRecord"));
	hide($("hourglass"));
	hide($("sewageCompanyDetails"));
	
	checkPermission();
	
	SewageCompanyDwr.getSewageCompanyByDataPointHierarchyId(id,function(sewageCompanyList){
	       dwr.util.removeAllRows("sewageCompanyList");
	       if (sewageCompanyList.length == 0) {
	           show($("noCompany"));
	           hide($("sewageCompanyList"));
	       }
	       else {
	           hide($("noCompany"));
	           show($("sewageCompanyList"));
	           dwr.util.addRows("sewageCompanyList", sewageCompanyList,
	               [ function(data) { return data.id; },
	                 function(data) { return data.name; },
	                 function(data) { return data.address; },
	                 function(data) { return data.telephone; },
	                 function(data) { return data.dataPointName; },
	                 function(data) {

	                     var result = "<a onclick='loadsewageCompRecord("+ data.id +")' style='cursor:pointer;'>采样记录</a>";
		                 	if(permission){
			                     result += "&nbsp;<img src='images/pencil.png' class='ptr' title='<fmt:message key="sewageCompany.edit"/>' "+
	                             "onclick='editSewageCompany("+ data.id +")'/>";
			                      result += "&nbsp;<img src='images/delete.png' class='ptr' title='<fmt:message key="sewageCompany.delete"/>' "+
                                  "onclick='deleteSewageCompany("+ data.id +")'/>";
		                 	}

	                 return result;}],
	               {
	                   rowCreator: function(options) {
	                       var tr = document.createElement("tr");
	                       tr.className = "row"+ (options.rowIndex % 2 == 0 ? "" : "Alt");
	                       return tr;
	                   },
	                   cellCreator: function(options) {
	                       var td = document.createElement("td");
	                       if (options.cellNum == 2)
	                           td.align = "right";
	                       if (options.cellNum == 3)
	                           td.align = "center";
	                       return td;
	                   }
	               });
	       }
	      });
}

function loadsewageCompRecord(id) {
	show($("sewageCompRecord"));
	hide($("hourglassRecord"));
	hide($("sewageCompanyDetails"));
	
	SewageCompanyDwr.getSewageRecordsByCompany(id,function(sewageRecordList){
	       dwr.util.removeAllRows("sewageRecordList");
	       if (sewageRecordList.length == 0) {
	           show($("noRecord"));
	           hide($("sewageRecordList"));
	       }
	       else {
	           hide($("noRecord"));
	           show($("sewageRecordList"));
	           dwr.util.addRows("sewageRecordList", sewageRecordList,
	               [ function(data) { return data.id; },
	                 function(data) { return data.compName; },
	                 function(data) { return data.chroma; },
	                 function(data) { return data.ph; },
	                 function(data) { return data.codcr; },
	                 function(data) { return data.ammoniaNitrogen; },
	                 function(data) { return data.totalPhosphorus; },
	                 function(data) { return data.totalNitrogen; },
	                 function(data) { return data.chloride; },
	                 function(data) { return data.samplingUnit; },
	                 function(data) { return data.samplingDate; }],
	               {
	                   rowCreator: function(options) {
	                       var tr = document.createElement("tr");
	                       tr.className = "row"+ (options.rowIndex % 2 == 0 ? "" : "Alt");
	                       return tr;
	                   },
	                   cellCreator: function(options) {
	                       var td = document.createElement("td");
	                       if (options.cellNum == 2)
	                           td.align = "right";
	                       if (options.cellNum == 3)
	                           td.align = "center";
	                       return td;
	                   }
	               });
	       }
	      });
}

function checkPermission(){
	var dshtml = "";
	if(permission){
		dshtml +=  '<tag:img src="${modulePath}/web/images/new.png" title="sewageCompany.new" onclick="newSewageCompany()" id="c${NEW_ID}Img"/>';
	}
	dshtml +=  '<tag:img src="${modulePath}/web/images/refresh.png" title="common.refresh" onclick="refreshSewageCompanyList()"/>';
	$("sewageCompanyOperationAdd").innerHTML = dshtml;
}

    </script>
		<table>
            <tr>
              <td class="smallTitle"><fmt:message key="sewageCompany.dataPointSelect"/></td>
              <td>                
              		<sst:select id="dataPointSelect" value="${selecteddataPoint}"  onchange="dataPointChanged(this.value)">
                  		<c:forEach items="${dataPointList}" var="dp">
                    		<sst:option value="${dp.key}">${dp.value}</sst:option>
                  		</c:forEach>
                	</sst:select>
              	</td>
            </tr>
		</table>
      <table cellpadding="0" cellspacing="0">
        <tr><td valign="top">
            <div class="borderDiv marB" style="max-height:300px;overflow:auto;">
              <table width="100%">
                <tr>
                  <td>
                    <span class="smallTitle"><fmt:message key="sewageCompany.title"/></span>
                  </td>
                  <td align="right" id="sewageCompanyOperationAdd"></td>
                </tr>
              </table>

              <table cellspacing="1">
                <tr class="rowHeader">
                  <td><fmt:message key="sewageCompany.id"/></td>
                  <td><fmt:message key="sewageCompany.name"/></td>
                  <td><fmt:message key="sewageCompany.address"/></td>
                  <td><fmt:message key="sewageCompany.telephone"/></td>
                  <td><fmt:message key="sewageCompany.dataPointName"/></td>
                  <td></td>
                </tr>
                <tr id="hourglass" class="row"><td colspan="8" align="center"><tag:img png="hourglass" title="common.loading"/></td></tr>
                <tr id="noCompany" class="row" style="display:none;"><td colspan="8"><fmt:message key="sewageCompany.noInstances"/></td></tr>
                <tbody id="sewageCompanyList"></tbody>
              </table>
        </div></td></tr>
        </table>

        <table>
        <tr><td valign="top" id="sewageCompanyDetails" style="display:none;">
        <div class="borderDiv">
          <table>
            <tr>
              <td>
                <span class="smallTitle"><fmt:message key="sewageCompany.sewageCompanyDetails"/></span> <tag:help id="sewageCompanyDetails"/>
              </td>
              <td align="right">
                <tag:img src="${modulePath}/web/images/save.png" title="sewageCompany.save" onclick="saveSewageCompany()" id="r${NEW_ID}Img"/>
              </td>
            </tr>
            <tr>
              <td class="smallTitle"><fmt:message key="sewageCompany.id"/></td>
              <td><input type="text" id="sewageCompanyId" name="sewageCompanyId" value="" readonly="true"/></td>
            </tr>
            <tr>
              <td class="smallTitle"><fmt:message key="sewageCompany.name"/></td>
              <td><input type="text" id="sewageCompanyName" name="sewageCompanyName" value=""/></td>
            </tr>
            <tr>
              <td class="smallTitle"><fmt:message key="sewageCompany.address"/></td>
              <td><input type="text" id="sewageCompanyAddress" name="sewageCompanyAddress" value=""/></td>
            </tr>
            <tr>
              <td class="smallTitle"><fmt:message key="sewageCompany.telephone"/></td>
              <td><input type="text" id="sewageCompanyTelephone" name="sewageCompanyTelephone" value=""/></td>
            </tr>
            <tr>
              <td class="smallTitle"><fmt:message key="sewageCompany.dataPointName"/></td>
              <td>                
              		<sst:select id="dataPointListSelect" value="${selecteddataPoint}" >
                  		<c:forEach items="${dataPointList}" var="dp">
                    		<sst:option value="${dp.key}">${dp.value}</sst:option>
                  		</c:forEach>
                	</sst:select>
              	</td>
            </tr>
          </table>
        </div></td></tr>
       </table>

  		<table cellpadding="0" cellspacing="0" id="sewageCompRecord">
             <tr class="rowHeader">
               <td><fmt:message key="sewageRecord.id"/></td>
               <td><fmt:message key="sewageCompany.name"/></td>
               <td><fmt:message key="sewageRecord.chroma"/></td>
               <td><fmt:message key="sewageRecord.ph"/></td>
               <td><fmt:message key="sewageRecord.codcr"/></td>
               <td><fmt:message key="sewageRecord.ammoniaNitrogen"/></td>
               <td><fmt:message key="sewageRecord.totalPhosphorus"/></td>
               <td><fmt:message key="sewageRecord.totalNitrogen"/></td>
               <td><fmt:message key="sewageRecord.chloride"/></td>
                <td><fmt:message key="sewageRecord.samplingUnit"/></td>
               <td><fmt:message key="sewageRecord.samplingDate"/></td>
             </tr>
             <tr id="hourglassRecord" class="row"><td colspan="8" align="center"><tag:img png="hourglass" title="common.loading"/></td></tr>
             <tr id="noRecord" class="row" style="display:none;"><td colspan="8"><fmt:message key="sewageRecord.noInstances"/></td></tr>
             <tbody id="sewageRecordList"></tbody>
           </table>

  </jsp:body>
</main:eazyPage>
