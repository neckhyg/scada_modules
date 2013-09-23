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

public class SewageCompanyHandler implements UrlHandler {

	public static final String KEY_DATA_POINT_LISTS = "dataPointList";
	public static final String KEY_SELECTED_DATA_POINT = "selecteddataPoint";
	public static final String KEY_SEWAGE_COMP_PERMISSION = "sewageCompPermission";

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
		List<DataPointHierarchy> dataPointLists = sewageCompanyDao
				.getDataPointHierarchy();
		int selected = 0;
		List<IntStringPair> dataPointListsNames = new ArrayList<IntStringPair>(
				dataPointLists.size());

		for (DataPointHierarchy dataPointHierarchy : dataPointLists) {

			dataPointListsNames.add(new IntStringPair(dataPointHierarchy
					.getId(), dataPointHierarchy.getName()));

		}

		model.put(KEY_DATA_POINT_LISTS, dataPointListsNames);
		model.put(KEY_SELECTED_DATA_POINT, selected);
//		model.put(KEY_SEWAGE_COMP_PERMISSION, sewageCompanyDao.getSewageCompanyPermissionById(user.getId()).isSewageCompany());
	}

}
