<%--
--%><%@ include file="/WEB-INF/jsp/include/tech.jsp" %>
<%@page pageEncoding="UTF-8" %>

<tag:page onload="setFocus">
  <script type="text/javascript">
    function setFocus() {
        $("sqlString").focus();
    }
  </script>
  
  <div class="borderDiv">
  <table width="100%" cellspacing="0" cellpadding="0" border="0">
    <tr>
      <td>
        <form action="sqlConsole.shtm" method="post">
          <table>
            <tr>
              <td colspan="2"><fmt:message key="sql.warning"/></td>
            </tr>
            <tr>
              <td class="formLabelRequired">
                <fmt:message key="sql.sql"/>
                <tag:help id="directQuerying"/>
              </td>
              <td><textarea id="sqlString" name="sqlString" rows="8" cols="80">${sqlString}</textarea></td>
            </tr>
            <tr>
              <td colspan="2" class="formError">${error}</td>
            </tr>
            
            <tr>
              <td colspan="2" align="center">
                <input type="submit" value="<fmt:message key="sql.tableList"/>" name="tables"/>
                <input type="submit" value="<fmt:message key="sql.query"/>" name="query"/>
                <input type="submit" value="<fmt:message key="sql.update"/>" name="update"/>
              </td>
            </tr>
          </table>
        </form>
        <br/>
        
        <c:if test="${data != null}">
          <table cellspacing="1">
            <tr class="rowHeader">
              <c:forEach items="${headers}" var="rowHeader">
                <td>${rowHeader}</td>
              </c:forEach>
            </tr>
            
            <c:forEach items="${data}" var="row">
              <tr class="row">
                <c:forEach items="${row}" var="col">
                  <td>${col}</td>
                </c:forEach>
              </tr>
            </c:forEach>
          </table>
        </c:if>
        
        <c:if test="${updateResult > -1}">
          <fmt:message key="sql.rowsUpdated">
            <fmt:param value="${updateResult}"/>
          </fmt:message>
        </c:if>
        
        <br/>
      </td>
    </tr>
  </table>
</div>
</tag:page>
