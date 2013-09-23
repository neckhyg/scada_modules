/*    */ package com.eazytec.scada.main;
/*    */ 
/*    */ import com.serotonin.m2m2.web.dwr.ModuleDwr;
/*    */ import com.serotonin.m2m2.web.dwr.util.DwrPermission;
/*    */ import java.util.List;
/*    */ 
/*    */ public class SewageRecordDwr extends ModuleDwr
/*    */ {
/*    */   @DwrPermission(user=true)
/*    */   public List<SewageRecord> getAllSewageRecords()
/*    */   {
/* 13 */     SewageRecordDao sewageRecordDao = new SewageRecordDao();
/* 14 */     return sewageRecordDao.getAllSewageRecords();
/*    */   }
/*    */ 
/*    */   @DwrPermission(user=true)
/*    */   public List<SewageRecord> getSewageRecordsByCompanyId(int compId) {
/* 19 */     SewageRecordDao sewageRecordDao = new SewageRecordDao();
/* 20 */     return sewageRecordDao.getSewageRecordsByCompanyId(compId);
/*    */   }
/*    */ 
/*    */   @DwrPermission(user=true)
/*    */   public boolean saveSewageRecord(SewageRecord sewageRecord) {
/* 25 */     SewageRecordDao sewageRecordDao = new SewageRecordDao();
/* 26 */     return sewageRecordDao.saveSewageRecord(sewageRecord);
/*    */   }
/*    */ 
/*    */   @DwrPermission(user=true)
/*    */   public SewageRecord getSewageRecordById(int recordId) {
/* 31 */     SewageRecordDao sewageRecordDao = new SewageRecordDao();
/* 32 */     return sewageRecordDao.getSewageRecordById(recordId);
/*    */   }
/*    */ 
/*    */   @DwrPermission(user=true)
/*    */   public boolean deleteSewageRecord(int recordId) {
/* 37 */     SewageRecordDao sewageRecordDao = new SewageRecordDao();
/* 38 */     return sewageRecordDao.deleteSewageRecord(recordId);
/*    */   }
/*    */ }

/* Location:           C:\Documents and Settings\Administrator\桌面\modules\main\lib\main-1.0.0.jar
 * Qualified Name:     com.eazytec.scada.main.SewageRecordDwr
 * JD-Core Version:    0.6.2
 */