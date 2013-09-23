package com.eazytec.scada.main;

import com.serotonin.m2m2.db.dao.DataPointDao;
import com.serotonin.m2m2.db.dao.SystemSettingsDao;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.rt.dataImage.AnnotatedPointValueTime;
import com.serotonin.m2m2.rt.dataImage.PointValueFacade;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.m2m2.vo.event.PointEventDetectorVO;
import com.serotonin.m2m2.web.dwr.ModuleDwr;
import com.serotonin.m2m2.web.dwr.beans.RenderedPointValueTime;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;
import com.serotonin.m2m2.web.taglib.Functions;

import java.util.*;

public class HistoryDwr extends ModuleDwr{
    private static final int PAGE_SIZE = 20;
    @DwrPermission(user = true)
    public ProcessResult getHistoryValues(List<String> list,int limit){

        Map<String, List<RenderedPointValueTime>> map = new HashMap<String, List<RenderedPointValueTime>>();
        Map<String, Boolean> alarm = new HashMap<String, Boolean>();

        PointValueFacade pointValueFacade;
        for(String xid : list){
            DataPointVO vo = (new DataPointDao()).getDataPoint(xid);

            pointValueFacade = new PointValueFacade(vo.getId());
            pointValueFacade.getLatestPointValues(limit);
            List<PointValueTime> pvtList = pointValueFacade.getLatestPointValues(limit);
            //获取当前值判断是否超出报警值
            List<PointEventDetectorVO> ped = vo.getEventDetectors();
            PointValueTime pvt = pvtList.get(0);
            double val = pvt.getValue().getDoubleValue();
            Iterator<PointEventDetectorVO> iter = ped.iterator();
            while(iter.hasNext()){
                PointEventDetectorVO p = iter.next();
                if(p.getDetectorType() == 1) // analog high limit
                {
                    if(val > p.getLimit())
                        alarm.put(xid,true);

                }
                if(p.getDetectorType() == 2) // analog low limit
                {
                    if(val < p.getLimit())
                        alarm.put(xid,true);

                }
            }

            List<RenderedPointValueTime> renderedData = new ArrayList<RenderedPointValueTime>(pvtList.size());
            for(PointValueTime pointValueTime : pvtList){
                RenderedPointValueTime renderedPointValueTime = new RenderedPointValueTime();
                renderedPointValueTime.setValue(Functions.getHtmlText(vo, pointValueTime));
                renderedPointValueTime.setTime(Functions.getTime(pointValueTime));
                if(pointValueTime.isAnnotated()){
                    AnnotatedPointValueTime annotatedPointValueTime = (AnnotatedPointValueTime)pointValueTime;
                    renderedPointValueTime.setAnnotation(annotatedPointValueTime.getAnnotation(getTranslations()));
                }
                renderedData.add(renderedPointValueTime);
            }
            map.put(xid, renderedData);

        }

//        SystemSettingsDao ssd = new SystemSettingsDao();
//        ssd.setIntValue("historyLimit",limit);

        ProcessResult response = new ProcessResult();
        response.addData("histories", map);
        response.addData("alarm",alarm);
        return response;
    }

    @DwrPermission(user = true)
    public ProcessResult getHistoryValue(String xid, int limit, int page){

        DataPointVO vo = (new DataPointDao()).getDataPoint(xid);
        PointValueFacade pointValueFacade = new PointValueFacade(vo.getId());
        List<PointValueTime> list = pointValueFacade.getLatestPointValues(limit);

        int pageSize = SystemSettingsDao.getIntValue("pageSize",20);
        int pages = 1;
        if(list.size()>0)
            pages = (list.size()-1)/pageSize + 1;
        if(page == 0)
            page = 1;
        if(pages > 1 ){
            if(page == 1 )
                list = list.subList(0, pageSize);
            else if (page == pages)
                list = list.subList((page-1)*pageSize,list.size());
            else
                list = list.subList((page-1)*pageSize,page*pageSize);
        }

        List<RenderedPointValueTime> renderedData = new ArrayList<RenderedPointValueTime>(list.size());
        for(PointValueTime pointValueTime : list){
            RenderedPointValueTime renderedPointValueTime = new RenderedPointValueTime();
            renderedPointValueTime.setValue(Functions.getHtmlText(vo, pointValueTime));
            renderedPointValueTime.setTime(Functions.getTime(pointValueTime));
            if(pointValueTime.isAnnotated()){
                AnnotatedPointValueTime annotatedPointValueTime = (AnnotatedPointValueTime)pointValueTime;
                renderedPointValueTime.setAnnotation(annotatedPointValueTime.getAnnotation(getTranslations()));
            }
            renderedData.add(renderedPointValueTime);
        }

        SystemSettingsDao ssd = new SystemSettingsDao();
        ssd.setIntValue("historyLimit",limit);

        ProcessResult response = new ProcessResult();
        response.addData("history", renderedData);
        response.addData("pages", pages);
        return response;
    }

    @DwrPermission(user = true)
    public ProcessResult searchHistoryValues(String xid, Date startDate, Date endDate, int page){

        DataPointVO vo = (new DataPointDao()).getDataPoint(xid);
        PointValueFacade pointValueFacade = new PointValueFacade(vo.getId());
        List<PointValueTime> list = pointValueFacade.getPointValuesBetween(startDate.getTime(),endDate.getTime());
        int pageSize = SystemSettingsDao.getIntValue("pageSize",20);
        int pages = 1;
        if(list.size()>0)
            pages = (list.size()-1)/pageSize + 1;
        if(page == 0)
            page = 1;
        if(pages > 1 ){
            if(page == 1 )
                list = list.subList(0, pageSize);
            else if (page == pages)
                list = list.subList((page-1)*pageSize,list.size());
            else
                list = list.subList((page-1)*pageSize,page*pageSize);
        }

        List<RenderedPointValueTime> renderedData = new ArrayList<RenderedPointValueTime>(list.size());
        for(PointValueTime pointValueTime : list){
            RenderedPointValueTime renderedPointValueTime = new RenderedPointValueTime();
            renderedPointValueTime.setValue(Functions.getHtmlText(vo, pointValueTime));
            renderedPointValueTime.setTime(Functions.getTime(pointValueTime));
            if(pointValueTime.isAnnotated()){
                AnnotatedPointValueTime annotatedPointValueTime = (AnnotatedPointValueTime)pointValueTime;
                renderedPointValueTime.setAnnotation(annotatedPointValueTime.getAnnotation(getTranslations()));
            }
            renderedData.add(renderedPointValueTime);
        }
        ProcessResult response = new ProcessResult();
        response.addData("history", renderedData);
        response.addData("pages",pages);
        return response;
    }

}
