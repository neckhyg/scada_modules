
package com.serotonin.m2m2.reports.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ReportEventExportServlet extends ReportExportBase {
    private static final long serialVersionUID = -1;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        execute(request, response, CONTENT_EVENTS);
    }
}
