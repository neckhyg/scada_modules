<%--
--%><%@page import="com.serotonin.m2m2.Constants"%><%--
--%><%@page import="com.serotonin.m2m2.Common"%><%--
--%><%@page import="com.serotonin.m2m2.view.ShareUser"%><%--
--%><%@page pageEncoding="UTF-8"%><%--
--%><%@ include file="/WEB-INF/jsp/include/tech.jsp" %><%--
--%><%@ taglib prefix="main" tagdir="/WEB-INF/tags/main" %><%--
--%><%@ taglib prefix="views" tagdir="/WEB-INF/tags/graphicalViews" %>

<main:eazyPage dwr="GraphicalViewDwr" js="${modulePath}/web/js/view.js,/modules/graphicalViews/web/graphicalViews.js,/modules/graphicalViews/web/wz_jsgraphics.js">
  <jsp:attribute name="styles">
    <style>
    </style>
  </jsp:attribute>

    <jsp:body>

        <script type="text/javascript">
            <c:if test="${!empty currentView}">
                    mango.view.initNormalView();
            </c:if>

            function unshare() {
                GraphicalViewDwr.deleteViewShare(function() { window.location = '/views.shtm'; });
            }

        </script>

        <views:displayView view="${currentView}" emptyMessageKey="views.noViews"/>

    </jsp:body>

</main:eazyPage>
