
package com.serotonin.m2m2.reports.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.web.servlet.BaseInfoServlet;


public class ReportChartServlet extends BaseInfoServlet {
    private static final long serialVersionUID = -1;

    public static final String IMAGE_DATA_KEY = ReportChartServlet.class + ".IMAGE_DATA";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // The only place that should be calling this servlet is the report chart, generated by a Freemarker
        // template. The ReportChartCreator that controlled the generation put the generated image content into the
        // user object, so all i need to do is write that content to the response object.
        User user = Common.getUser(request);
        if (user != null) {
            @SuppressWarnings("unchecked")
            Map<String, byte[]> imageData = (Map<String, byte[]>) user.getAttribute(IMAGE_DATA_KEY);
            if (imageData != null) {
                String path = request.getPathInfo();

                // Path will be of the format "/<chartName>", so we need to ignore the first character.
                byte[] data = imageData.get(path.substring(1));
                if (data != null)
                    response.getOutputStream().write(data);
            }
        }
    }
}
