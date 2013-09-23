/*    */ package com.eazytec.scada.main;
/*    */ 
/*    */ import com.serotonin.m2m2.web.dwr.ModuleDwr;
/*    */ import com.serotonin.m2m2.web.dwr.util.DwrPermission;
/*    */ import java.util.List;
/*    */ 
/*    */ public class SewageCompanyDwr extends ModuleDwr
/*    */ {
/*    */   @DwrPermission(user=true)
/*    */   public List<SewageCompany> getSewageCompanies()
/*    */   {
/* 13 */     SewageCompanyDao sewageCompanyDao = new SewageCompanyDao();
/* 14 */     return sewageCompanyDao.getSewageCompanies();
/*    */   }
/*    */ 
/*    */   @DwrPermission(user=true)
/*    */   public SewageCompany getSewageCompany(int id) {
/* 19 */     SewageCompanyDao sewageCompanyDao = new SewageCompanyDao();
/* 20 */     return sewageCompanyDao.getSewageCompany(id);
/*    */   }
/*    */ 
/*    */   @DwrPermission(user=true)
/*    */   public boolean deleteSewageCompany(int id) {
/* 25 */     SewageCompanyDao sewageCompanyDao = new SewageCompanyDao();
/* 26 */     return sewageCompanyDao.deleteSewageCompany(id);
/*    */   }
/*    */ 
/*    */   @DwrPermission(user=true)
/*    */   public boolean saveSewageCompany(SewageCompany sewageCompany)
/*    */   {
/* 32 */     SewageCompanyDao sewageCompanyDao = new SewageCompanyDao();
/* 33 */     return sewageCompanyDao.saveSewageCompany(sewageCompany);
/*    */   }
/*    */ 
/*    */   @DwrPermission(user=true)
/*    */   public DataPointHierarchy getDataPointHierarchyById(int id) {
/* 38 */     SewageCompanyDao sewageCompanyDao = new SewageCompanyDao();
/* 39 */     return sewageCompanyDao.getDataPointHierarchyById(id);
/*    */   }
/*    */ 
/*    */   @DwrPermission(user=true)
/*    */   public List<SewageCompany> getSewageCompanyByDataPointHierarchyId(int dphid) {
/* 44 */     SewageCompanyDao sewageCompanyDao = new SewageCompanyDao();
/* 45 */     return sewageCompanyDao.getSewageCompanyByDataPointHierarchyId(dphid);
/*    */   }
/*    */ 
/*    */   @DwrPermission(user=true)
/*    */   public List<SewageRecord> getSewageRecordsByCompany(int compId) {
/* 50 */     SewageRecordDao sewageRecordDao = new SewageRecordDao();
/* 51 */     return sewageRecordDao.getSewageRecordsByCompanyId(compId);
/*    */   }
/*    */ }

/* Location:           C:\Documents and Settings\Administrator\桌面\modules\main\lib\main-1.0.0.jar
 * Qualified Name:     com.eazytec.scada.main.SewageCompanyDwr
 * JD-Core Version:    0.6.2
 */