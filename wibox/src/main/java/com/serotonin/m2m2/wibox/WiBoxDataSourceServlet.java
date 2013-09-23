package com.serotonin.m2m2.wibox;

import com.aginova.util.DBConnectionProperties;
import com.serotonin.m2m2.wibox.request.DataRequest;
import com.serotonin.m2m2.wibox.request.HaveDataRequest;
import com.serotonin.m2m2.wibox.request.HealthRequest;
import com.serotonin.m2m2.wibox.request.LinkupRequest;
import com.serotonin.m2m2.wibox.request.NotificationRequest;
import com.serotonin.m2m2.wibox.request.UpdateRequest;
import com.serotonin.m2m2.wibox.request.WiBoxRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WiBoxDataSourceServlet extends HttpServlet
{
  private static final Log LOG = LogFactory.getLog(WiBoxDataSourceServlet.class);
  private static final long serialVersionUID = -1L;
  public static WiBoxMulticaster multicaster = new WiBoxMulticaster();
  private final Map<String, MoteData> moteDataMap = new HashMap();

  public void init()
  {
    DBConnectionProperties.getDBConnectionProperties().setProperty(DBConnectionProperties.INSTALL_APPLICATION, DBConnectionProperties.APPLICATION_TEMPERATURE);
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException
  {
    doImpl(request, response);
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
  {
    doImpl(request, response);
  }

  private void doImpl(HttpServletRequest request, HttpServletResponse response) throws IOException
  {
    Map params = request.getParameterMap();
    if (params.isEmpty())
    {
      response.getWriter().append("AGINOVA DATA STORAGE READY");
    } else {
      String xmlType = request.getParameter("xmlType");
      String password = request.getParameter("password");

      WiBoxRequest req = null;
      if ("\"Health\"".equals(xmlType)) {
        req = new HealthRequest(password, request.getParameter("seqno"), request.getParameter("voltage"), request.getParameter("originaddr"), request.getParameter("sourceaddr"), request.getParameter("quality"), request.getParameter("RSSI"));
      }
      else if ("\"LinkUp\"".equals(xmlType)) {
        req = new LinkupRequest(password, request.getParameter("mote_id"));
      } else if ("\"HaveData\"".equals(xmlType)) {
        String moteId = request.getParameter("mote_id");
        String productNumber = request.getParameter("product");
        if (!this.moteDataMap.containsKey(moteId))
          this.moteDataMap.put(moteId, new MoteData(moteId, productNumber));
        req = new HaveDataRequest(password, request.getParameter("isReader"), request.getParameter("count"), request.getParameter("resets"), request.getParameter("assPrd"), request.getParameter("mac"), request.getParameter("APChannel"), request.getParameter("time"), request.getParameter("storeFwdEnabled"), moteId, request.getParameter("gr1"), request.getParameter("gr2"), request.getParameter("gr3"), request.getParameter("gr4"), request.getParameter("gr1p"), request.getParameter("gr2p"), request.getParameter("gr3p"), request.getParameter("gr4p"), request.getParameter("retryCount"), request.getParameter("APMAC"), request.getParameter("rssi"), request.getParameter("parent"), productNumber, request.getParameter("upTime"), request.getParameter("voltage"), request.getParameter("codeVersion"));
      }
      else if ("Data".equals(xmlType)) {
        String moteId = request.getParameter("mote_id");
        MoteData moteData = (MoteData)this.moteDataMap.get(moteId);
        if (moteData != null) {
          req = new DataRequest(password, moteData, request.getParameter("timestamp"), request.getParameter("data_id"), request.getParameter("data_index"), request.getParameter("data_type"), request.getParameter("data"), request.getParameter("clbr"));
        }

      }
      else if ("Update".equals(xmlType)) {
        req = new UpdateRequest(password, request.getParameter("unique_id"), request.getParameter("mote_id"), request.getParameter("msg"));
      }
      else if ("Notification".equals(xmlType)) {
        req = new NotificationRequest(password, request.getParameter("unique_id"), request.getParameter("mote_id"), request.getParameter("code"), request.getParameter("msg"));
      }
      else {
        LOG.warn("Unknown request type received: " + xmlType);
      }
      if (req != null) {
        multicaster.multicast(req, password);

        if (req.isHandled())
          response.getWriter().append("AGINOVA DATA STORED OK");
        else
          LOG.info("Unhandled WiBox request with password: " + password + ", type: " + xmlType);
      }
    }
  }
}