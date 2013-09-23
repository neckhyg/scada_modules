<%--
--%><%@ include file="/WEB-INF/jsp/include/tech.jsp" %>
<%@page import="com.serotonin.m2m2.Common"%>

<c:set var="NEW_ID"><%= Common.NEW_ID %></c:set>

<tag:page dwr="GlobalScriptsDwr" onload="init">
  <script type="text/javascript">
    var editingScript;

    function init() {
        GlobalScriptsDwr.init(function(list) {
            for (var i=0; i<list.length; i++) {
                appendScript(list[i].id);
                updateScript(list[i]);
            }
        });
    }

    function showScript(scriptId) {
        if (editingScript)
            stopImageFader($("s"+ editingScript.id +"Img"));
        hideContextualMessages("scriptDetails")
        GlobalScriptsDwr.get(scriptId, function(script) {
            if (!editingScript)
                show($("scriptDetails"));
            editingScript = script;
            
            $set("xid", script.xid);
            $set("name", script.name);
            $set("script", script.script);
            
            setUserMessage();
        });
        startImageFader($("s"+ scriptId +"Img"));
        
        if (scriptId == ${NEW_ID})
            hide($("deleteImg"));
        else
            show($("deleteImg"));
    }

    function saveScript() {
        setUserMessage();
        hideContextualMessages("scriptDetails")
        
        var gs = {
            id: editingScript.id,
            xid: $get("xid"),
            name: $get("name"),
            script: $get("script")
        };
        
        GlobalScriptsDwr.save(gs, function(response) {
            if (response.hasMessages)
                showDwrMessages(response.messages);
            else {
                if (editingScript.id == ${NEW_ID}) {
                    stopImageFader($("s"+ editingScript.id +"Img"));
                    editingScript = response.data.script;
                    appendScript(editingScript.id);
                    startImageFader($("s"+ editingScript.id +"Img"));
                    setUserMessage("<fmt:message key="globalScript.scriptAdded"/>");
                    show($("deleteImg"));
                }
                else
                    setUserMessage("<fmt:message key="globalScript.scriptSaved"/>");
                
                GlobalScriptsDwr.get(editingScript.id, updateScript);
            }
        });
    }
    
    function setUserMessage(message) {
        if (message) {
            show($("userMessage"));
            $("userMessage").innerHTML = message;
        }
        else
            hide($("userMessage"));
    }
    
    function appendScript(scriptId) {
        createFromTemplate("s_TEMPLATE_", scriptId, "scriptsTable");
    }
    
    function updateScript(script) {
        $("s"+ script.id +"name").innerHTML = script.name;
    }
    
    function deleteScript() {
        GlobalScriptsDwr.deleteScript(editingScript.id, function() {
            stopImageFader($("s"+ editingScript.id +"Img"));
            $("scriptsTable").removeChild($("s"+ editingScript.id));
            hide($("scriptDetails"));
            editingScript = null;
        });
    }
  </script>

  <table>
    <tr>
      <td valign="top">
        <div class="borderDiv">
          <table width="100%">
            <tr>
              <td>
                <span class="smallTitle"><fmt:message key="globalScript.title"/></span>
                <tag:help id="globalScripts"/>
              </td>
              <td align="right"><tag:img png="add" onclick="showScript(${NEW_ID})" title="common.add" id="s${NEW_ID}Img"/></td>
            </tr>
          </table>
          <table id="scriptsTable">
            <tbody id="s_TEMPLATE_" onclick="showScript(getMangoId(this))" class="ptr" style="display:none;">
              <tr>
                <td><tag:img id="s_TEMPLATE_Img" src="${modulePath}/web/script-globe.png"/></td>
                <td class="link" id="s_TEMPLATE_name"></td>
              </tr>
            </tbody>
          </table>
        </div>
      </td>
      
      <td valign="top" style="display:none;" id="scriptDetails">
        <div class="borderDiv">
          <table width="100%">
            <tr>
              <td><span class="smallTitle"><fmt:message key="globalScript.script"/></span></td>
              <td align="right">
                <tag:img png="save" onclick="saveScript();" title="common.save"/>
                <tag:img id="deleteImg" png="delete" onclick="deleteScript();" title="common.delete" style="display:none;"/>
              </td>
            </tr>
          </table>
          
          <table>
            <tr>
              <td class="formLabelRequired"><fmt:message key="common.xid"/></td>
              <td class="formField"><input type="text" id="xid"/></td>
            </tr>
            
            <tr>
              <td class="formLabelRequired"><fmt:message key="globalScript.name"/></td>
              <td class="formField"><input type="text" id="name"/></td>
            </tr>
            
            <tr>
              <td class="formLabel"><fmt:message key="globalScript.script"/></td>
              <td class="formField"><textarea id="script" rows="40" cols="80"/></textarea></td>
            </tr>
          </table>
          
          <table>
            <tr>
              <td colspan="2" id="userMessage" class="formError" style="display:none;"></td>
            </tr>
          </table>
        </div>
      </td>
    </tr>
  </table>
</tag:page>