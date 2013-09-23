<%--
--%><%@page import="com.serotonin.m2m2.Constants"%><%--
--%><%@page import="com.serotonin.m2m2.Common"%><%--
--%><%@page import="com.serotonin.m2m2.view.ShareUser"%><%--
--%><%@page pageEncoding="UTF-8"%><%--
--%><%@ include file="/WEB-INF/jsp/include/tech.jsp"%><%--
--%><%@ taglib prefix="main" tagdir="/WEB-INF/tags/main"%>

<main:eazyPage dwr="SewageRecordDwr" js="${modulePath}/web/js/view.js,${modulePath}/web/main.js">
	<jsp:attribute name="styles">
    <style>
	</style>
  </jsp:attribute>

	<jsp:body>
    <script type="text/javascript">
    
    dojo.require("dojo/parser");
    dojo.require("dojo.store.Memory");
    dojo.require("dijit.form.FilteringSelect");
    dojo.require("dijit.form.DateTextBox");
    
    var permission = ${sewageCompRecordPermission};
    
					require([ "dojo/ready", "dojo/query!css3", "dojo/NodeList-traverse" ], function(ready, query,dom) {
						ready(function() {
							loadSewageRecordList();
						});
					      var date = new Date();
					      $set("sewageRecordSamplingDate", date.getFullYear() + "-" + (date.getMonth()+1) + "-" + date.getDate());
					});

					function loadSewageRecordList() {
						hide($("hourglass"));
						hide($("sewageRecordDetails"));
						checkPermission();
						
						SewageRecordDwr.getAllSewageRecords(function(sewageRecordList) {
											dwr.util.removeAllRows("sewageRecordList");
											if (sewageRecordList.length == 0) {
												show($("noSewageRecord"));
												hide($("sewageRecordList"));
											} else {
												hide($("noSewageRecord"));
												show($("sewageRecordList"));
												dwr.util.addRows(
																"sewageRecordList",
																sewageRecordList,
																[
																		function(data) {
																			return data.id;
																		},
																		function(data) {
																			return data.compName;
																		},
																		function(data) {
																			return data.chroma;
																		},
																		function(data) {
																			return data.ph;
																		},
																		function(data) {
																			return data.codcr;
																		},
																		function(data) {
																			return data.ammoniaNitrogen;
																		},
																		function(data) {
																			return data.totalPhosphorus;
																		},
																		function(data) {
																			return data.totalNitrogen;
																		},
																		function(data) {
																			return data.chloride;
																		},
																		function(data) {
																			return data.samplingUnit;
																		},
																		function(data) {
																			return data.samplingDate;
																		},
																		function(data) {

																			if(permission){
																				var result = "<img src='images/pencil.png' class='ptr' title='<fmt:message key="sewageRecord.edit"/>' "
																				+ "onclick='editSewageRecord("
																				+ data.id
																				+ ")'/>";
																				result += "<img src='images/delete.png' class='ptr' title='<fmt:message key="sewageRecord.delete"/>' "
																						+ "onclick='deleteSewageRecord("
																						+ data.id
																						+ ")'/>";
																				return result;
																			} else {
																				return "<lable>您没有操作权限</label>";
																			}

																		} ],
																{
																	rowCreator : function(options) {
																		var tr = document.createElement("tr");
																		tr.className = "row" + (options.rowIndex % 2 == 0 ? "": "Alt");
																		return tr;
																	},
																	cellCreator : function(options) {
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
					
					function sewageCompChanged(compid) {
						hide($("hourglass"));
						hide($("sewageRecordDetails"));
						checkPermission();
						SewageRecordDwr.getSewageRecordsByCompanyId(compid,function(sewageRecordList) {
											dwr.util.removeAllRows("sewageRecordList");
											if (sewageRecordList.length == 0) {
												show($("noSewageRecord"));
												hide($("sewageRecordList"));
											} else {
												hide($("noSewageRecord"));
												show($("sewageRecordList"));
												dwr.util.addRows(
																"sewageRecordList",
																sewageRecordList,
																[
																		function(data) {
																			return data.id;
																		},
																		function(data) {
																			return data.compName;
																		},
																		function(data) {
																			return data.chroma;
																		},
																		function(data) {
																			return data.ph;
																		},
																		function(data) {
																			return data.codcr;
																		},
																		function(data) {
																			return data.ammoniaNitrogen;
																		},
																		function(data) {
																			return data.totalPhosphorus;
																		},
																		function(data) {
																			return data.totalNitrogen;
																		},
																		function(data) {
																			return data.chloride;
																		},
																		function(data) {
																			return data.samplingUnit;
																		},
																		function(data) {
																			return data.samplingDate;
																		},
																		function(data) {

																			if(permission){
																				var result = "<img src='images/pencil.png' class='ptr' title='<fmt:message key="sewageRecord.edit"/>' "
																				+ "onclick='editSewageRecord("
																				+ data.id
																				+ ")'/>";
																				result += "<img src='images/delete.png' class='ptr' title='<fmt:message key="sewageRecord.delete"/>' "
																						+ "onclick='deleteSewageRecord("
																						+ data.id
																						+ ")'/>";
																				return result;
																			} else {
																				return "<lable>您没有操作权限</label>";
																			}
																			
																		} ],
																{
																	rowCreator : function(options) {
																		var tr = document.createElement("tr");
																		tr.className = "row" + (options.rowIndex % 2 == 0 ? "": "Alt");
																		return tr;
																	},
																	cellCreator : function(options) {
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
					
					function newSewageRecord() {
						$set("sewageRecordId", "");
						$set("sewageRecordSamplingUnit", "");
						$set("sewageRecordSamplingDate", "");
						$set("sewageRecordChroma", "");
						$set("sewageRecordPh", "");
						$set("sewageRecordCodcr", "");
						$set("sewageRecordAmmoniaNitrogen", "");
						$set("sewageRecordTotalPhosphorus", "");
						$set("sewageRecordTotalNitrogen", "");
						$set("sewageRecordChloride", "");
						show($("sewageRecordDetails"));
					}
					
					function saveSewageRecord(){
	
						if ($get("sewageRecordId") == "") {
							var sewageRecordId = -1;
						} else {
							var sewageRecordId = $get("sewageRecordId");
						}

						var sewageRecord = {
							id : sewageRecordId,
							compId : $get("sewageRecordCompId"),
							chroma : $get("sewageRecordchroma"),
							ph : $get("sewageRecordPh"),
							codcr : $get("sewageRecordCodcr"),
							ammoniaNitrogen : $get("sewageRecordAmmoniaNitrogen"),
							totalPhosphorus : $get("sewageRecordTotalPhosphorus"),
							totalNitrogen : $get("sewageRecordTotalNitrogen"),
							chloride : $get("sewageRecordChloride"),
							samplingUnit : $get("sewageRecordSamplingUnit"),
							samplingDate : $get("sewageRecordSamplingDate") +  " " + $get("sewageRecordSamplinghour")+  ":"  + $get("sewageRecordSamplingMinute") 
						};
						SewageRecordDwr.saveSewageRecord(sewageRecord);
						hide($("sewageRecordDetails"));
					}

					function editSewageRecord(id) {
						  show($("sewageRecordDetails"));
						  SewageRecordDwr.getSewageRecordById(id,function(sewageRecord){
						      $set("sewageRecordId",sewageRecord.id);
						      $set("sewageRecordCompId",sewageRecord.compId);
						      $set("sewageRecordchroma",sewageRecord.chroma);
						      $set("sewageRecordPh",sewageRecord.ph);
						      $set("sewageRecordCodcr",sewageRecord.codcr);
						      $set("sewageRecordAmmoniaNitrogen",sewageRecord.ammoniaNitrogen);
						      $set("sewageRecordTotalPhosphorus",sewageRecord.totalPhosphorus);
						      $set("sewageRecordTotalNitrogen",sewageRecord.totalNitrogen);
						      $set("sewageRecordChloride",sewageRecord.chloride);
						      $set("sewageRecordSamplingUnit",sewageRecord.samplingUnit);
						      var arr=new Array(); 
						      var dates = sewageRecord.samplingDate;
								if(dates != null){
								      if(dates.indexOf(" ")>0){
								    	  arr = dates.split(" ");
									      $set("sewageRecordSamplingDate",arr[0]);
									      if(arr[1].indexOf(":")>0){
									    	  var arrTime=new Array(); 
									    	  arrTime = arr[1].split(":");
										      $set("sewageRecordSamplingHour",arrTime[0]);
										      $set("sewageRecordSamplingMinute",arrTime[1]);
									      }
								      } 
								} else {
								      $set("sewageRecordSamplingDate","");
								      $set("sewageRecordSamplingHour","00");
								      $set("sewageRecordSamplingMinute","00");
							      }
						      });
					}

					function deleteSewageRecord(id) {
						SewageRecordDwr.deleteSewageRecord(id);
						refreshSewageRecordList();
					}

					function refreshSewageRecordList() {
						sewageCompChanged($get("sewageCompanySelect"));
					}
					
					function checkPermission(){
						var dshtml = "";
						if(permission){
							dshtml +=  '<tag:img src="${modulePath}/web/images/new.png" title="sewageRecord.new" onclick="newSewageRecord()" id="c${NEW_ID}Img" />';
						}
						dshtml +=  '<tag:img src="${modulePath}/web/images/refresh.png" title="common.refresh" onclick="refreshSewageRecordList()" />';
						$("sewageRecordOperationAdd").innerHTML = dshtml;
					}
					
				</script>
		<table>
            <tr>
              <td class="smallTitle"><fmt:message key="sewageRecord.companySelect"/></td>
              <td>                
              		<sst:select id="sewageCompanySelect" value="${selectedsewageCompRecord}"  onchange="sewageCompChanged(this.value)">
                  		<c:forEach items="${sewageCompRecordList}" var="scrl">
                    		<sst:option value="${scrl.key}">${scrl.value}</sst:option>
                  		</c:forEach>
                	</sst:select>
              	</td>
            </tr>
		</table>
      <table cellpadding="0" cellspacing="0">
        <tr>
			<td>
            <div class="borderDiv marB" style="max-height: 300px; overflow: auto;">
              <table width="100%">
                <tr>
                  <td>
                    <span class="smallTitle"><fmt:message key="sewageRecord.title" /></span>
                  </td>
                  <td align="right"  id="sewageRecordOperationAdd" ></td>
                </tr>
              </table>

              <table cellspacing="1">
                <tr class="rowHeader">
	               <td><fmt:message key="sewageRecord.id" /></td>
	               <td><fmt:message key="sewageCompany.name" /></td>
	               <td><fmt:message key="sewageRecord.chroma" /></td>
	               <td><fmt:message key="sewageRecord.ph" /></td>
	               <td><fmt:message key="sewageRecord.codcr" /></td>
	               <td><fmt:message key="sewageRecord.ammoniaNitrogen" /></td>
	               <td><fmt:message key="sewageRecord.totalPhosphorus" /></td>
	               <td><fmt:message key="sewageRecord.totalNitrogen" /></td>
	               <td><fmt:message key="sewageRecord.chloride" /></td>
	                <td><fmt:message key="sewageRecord.samplingUnit" /></td>
	               <td><fmt:message key="sewageRecord.samplingDate" /></td>
	               <td></td>
                </tr>
                <tr id="hourglass" class="row">
					<td colspan="8" align="center"><tag:img png="hourglass" title="common.loading" /></td>
				</tr>
                <tr id="noSewageRecord" class="row"  style="display: none;">
					<td colspan="8"><fmt:message key="sewageRecord.noInstances" /></td>
				</tr>
                <tbody id="sewageRecordList"></tbody>
              </table>
        </div> </td> </tr>
        </table>

      <table cellpadding="0" cellspacing="0">
        <tr>
			<td valign="top" id="sewageRecordDetails"  style="display: none;">
        		<div class="borderDiv">
          			<table>
			            <tr>
			              <td>
			                <span class="smallTitle">
			                	<fmt:message key="sewageRecord.sewageRecordDetails" />
		                	</span>
								<tag:help id="sewageRecordDetails" />
			              </td>
			              <td align="right">
			                	<tag:img src="${modulePath}/web/images/save.png" title="sewageRecord.save" onclick="saveSewageRecord()" id="r${NEW_ID}Img" />
			              </td>
			            </tr>
			            <tr>
			              	<td class="smallTitle"><fmt:message key="sewageRecord.id" /></td>
			              	<td><input type="text" id="sewageRecordId" name="sewageRecordId" value="" readonly="true"/></td>
			            </tr>
			            <tr>
			              <td class="smallTitle"><fmt:message key="sewageRecord.samplingUnit" /></td>
			              <td><input type="text" id="sewageRecordSamplingUnit" name="sewageRecordSamplingUnit" value="" /></td>
			            </tr>
			            <tr>
			              <td class="smallTitle"><fmt:message key="sewageRecord.samplingDate" /></td>
			              <td>
			              <input type="text" name="sewageRecordSamplingDate" id="sewageRecordSamplingDate" value="2013-06-01" data-dojo-type="dijit.form.DateTextBox" required="true" />
							<label for="sewageRecordSamplingHour"></label>
							<select name="sewageRecordSamplingHour" id="sewageRecordSamplingHour">
							  <option>00</option>
							  <option>01</option>
							  <option>02</option>
							  <option>03</option>
							  <option>04</option>
							  <option>05</option>
							  <option>06</option>
							  <option>07</option>
							  <option>08</option>
							  <option>09</option>
							  <option>10</option>
							  <option>11</option>
							  <option>12</option>
							  <option>13</option>
							  <option>14</option>
							  <option>15</option>
							  <option>16</option>
							  <option>17</option>
							  <option>18</option>
							  <option>19</option>
							  <option>20</option>
							  <option>21</option>
							  <option>22</option>
							  <option>23</option>
							</select>
							<label for="sewageRecordSamplingMinute">:</label>
							<select name="sewageRecordSamplingMinute" id="sewageRecordSamplingMinute">
							  <option>00</option>
							  <option>01</option>
							  <option>02</option>
							  <option>03</option>
							  <option>04</option>
							  <option>05</option>
							  <option>06</option>
							  <option>07</option>
							  <option>08</option>
							  <option>09</option>
							  <option>10</option>
							  <option>11</option>
							  <option>12</option>
							  <option>13</option>
							  <option>14</option>
							  <option>15</option>
							  <option>16</option>
							  <option>17</option>
							  <option>18</option>
							  <option>19</option>
							  <option>20</option>
							  <option>21</option>
							  <option>22</option>
							  <option>23</option>
							  <option>24</option>
							  <option>25</option>
							  <option>26</option>
							  <option>27</option>
							  <option>28</option>
							  <option>29</option>
							  <option>30</option>
							  <option>31</option>
							  <option>32</option>
							  <option>33</option>
							  <option>34</option>
							  <option>35</option>
							  <option>36</option>
							  <option>37</option>
							  <option>38</option>
							  <option>39</option>
							  <option>40</option>
							  <option>41</option>
							  <option>42</option>
							  <option>43</option>
							  <option>44</option>
							  <option>45</option>
							  <option>46</option>
							  <option>47</option>
							  <option>48</option>
							  <option>49</option>
							  <option>50</option>
							  <option>51</option>
							  <option>52</option>
							  <option>53</option>
							  <option>54</option>
							  <option>55</option>
							  <option>56</option>
							  <option>57</option>
							  <option>58</option>
							  <option>59</option>
							</select>
			              </td>
			            </tr>
			            <tr>
			              <td class="smallTitle"><fmt:message key="sewageRecord.chroma" /></td>
			              <td><input type="text" id="sewageRecordChroma" name="sewageRecordChroma" value="" /></td>
			            </tr>
			            <tr>
			              <td class="smallTitle"><fmt:message key="sewageRecord.companyId" /></td>
			              <td>
			              		<sst:select id="sewageRecordCompId" value="${selectedsewageCompRecord}">
			                  		<c:forEach items="${sewageCompRecordList}" var="scrl">
			                    		<sst:option value="${scrl.key}">${scrl.value}</sst:option>
			                  		</c:forEach>
			                	</sst:select>
						</td>
			            </tr>
			            <tr>
			              <td class="smallTitle"><fmt:message key="sewageRecord.ph" /></td>
			              <td><input type="text" id="sewageRecordPh" name="sewageRecordPh" value="" /></td>
			            </tr>
			            <tr>
			              <td class="smallTitle"><fmt:message key="sewageRecord.codcr" /></td>
			              <td><input type="text" id="sewageRecordCodcr" name="sewageRecordCodcr" value="" /></td>
			            </tr>
			            <tr>
			              <td class="smallTitle"><fmt:message key="sewageRecord.ammoniaNitrogen" /></td>
			              <td><input type="text" id="sewageRecordAmmoniaNitrogen" name="sewageRecordAmmoniaNitrogen" value="" /></td>
			            </tr>
			            <tr>
			              <td class="smallTitle"><fmt:message key="sewageRecord.totalPhosphorus" /></td>
			              <td><input type="text" id="sewageRecordTotalPhosphorus" name="sewageRecordTotalPhosphorus" value="" /></td>
			            </tr>
			            <tr>
			              <td class="smallTitle"><fmt:message key="sewageRecord.totalNitrogen" /></td>
			              <td><input type="text" id="sewageRecordTotalNitrogen" name="sewageRecordTotalNitrogen" value="" /></td>
			            </tr>
			            <tr>
			              <td class="smallTitle"><fmt:message key="sewageRecord.chloride" /></td>
			              <td><input type="text" id="sewageRecordChloride" name="sewageRecordChloride" value="" /></td>
			            </tr>
			          </table>
			        </div>
				</td>
			</tr>
       </table>
  </jsp:body>
</main:eazyPage>
