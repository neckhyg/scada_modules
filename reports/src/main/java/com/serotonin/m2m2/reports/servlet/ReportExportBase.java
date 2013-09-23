
package com.serotonin.m2m2.reports.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.Translations;
import com.serotonin.m2m2.reports.ReportDao;
import com.serotonin.m2m2.reports.vo.ReportInstance;
import com.serotonin.m2m2.reports.web.ReportCommon;
import com.serotonin.m2m2.reports.web.UserCommentCsvStreamer;
import com.serotonin.m2m2.vo.export.EventCsvStreamer;
import com.serotonin.m2m2.vo.export.ExportCsvStreamer;


abstract public class ReportExportBase extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected static final int CONTENT_REPORT = 1;
    protected static final int CONTENT_EVENTS = 2;
    protected static final int CONTENT_COMMENTS = 3;

    protected void execute(HttpServletRequest request, HttpServletResponse response, int content) throws IOException {
        // Get the report instance id
        int instanceId = Integer.parseInt(request.getParameter("instanceId"));

        // Get the report instance
        ReportDao reportDao = new ReportDao();
        ReportInstance instance = reportDao.getReportInstance(instanceId);

        // Ensure the user is allowed access.
        ReportCommon.ensureReportInstancePermission(Common.getUser(request), instance);

        // Stream the content.
        response.setContentType("text/csv");

        Translations translations = Common.getTranslations();
        if (content == CONTENT_REPORT) {
            ExportCsvStreamer creator = new ExportCsvStreamer(response.getWriter(), translations);
            reportDao.reportInstanceData(instanceId, creator);
        }
        else if (content == CONTENT_EVENTS)
            new EventCsvStreamer(response.getWriter(), reportDao.getReportInstanceEvents(instanceId), translations);
        else if (content == CONTENT_COMMENTS)
            new UserCommentCsvStreamer(response.getWriter(), reportDao.getReportInstanceUserComments(instanceId),
                    translations);
    }
}
