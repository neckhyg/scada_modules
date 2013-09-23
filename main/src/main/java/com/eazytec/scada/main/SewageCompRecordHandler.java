package com.eazytec.scada.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;

import com.serotonin.db.pair.IntStringPair;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.web.mvc.UrlHandler;

public class SewageCompRecordHandler implements UrlHandler{

	public static final String KEY_SEWAGE_COMP_RECORD_LISTS = "sewageCompRecordList";
	public static final String KEY_SELECTED_SEWAGE_COMP_RECORD = "selectedsewageCompRecord";
	public static final String KEY_SEWAGE_COMP_RECORD_PERMISSION = "sewageCompRecordPermission";
	
	@Override
	public View handleRequest(HttpServletRequest request,
			HttpServletResponse response, Map<String, Object> model)
			throws Exception {
		User user = Common.getUser(request);
		prepareModel(request, model, user);
		return null;
	}
	
	protected void prepareModel(HttpServletRequest request,
			Map<String, Object> model, User user) {

		SewageCompanyDao sewageCompanyDao = new SewageCompanyDao();
		SewageRecordDao sewageRecordDao = new SewageRecordDao();
		List<SewageCompany> sewageCompanyLists = sewageCompanyDao.getSewageCompanies();
		int selected = 0;
		List<IntStringPair> sewageCompanyPair = new ArrayList<IntStringPair>(sewageCompanyLists.size());

		for (SewageCompany sewageCompany : sewageCompanyLists) {

			sewageCompanyPair.add(new IntStringPair(sewageCompany.getId(), sewageCompany.getName()));

		}

		model.put(KEY_SEWAGE_COMP_RECORD_LISTS, sewageCompanyPair);
		model.put(KEY_SELECTED_SEWAGE_COMP_RECORD, selected);
//		model.put(KEY_SEWAGE_COMP_RECORD_PERMISSION, sewageRecordDao.getSewageRecordPermissionById(user.getId()).isSewageRecord());
	}
	
}
