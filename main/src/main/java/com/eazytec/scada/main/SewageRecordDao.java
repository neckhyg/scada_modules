/*    */ package com.eazytec.scada.main;
/*    */ 
/*    */ import com.serotonin.db.spring.ExtendedJdbcTemplate;
/*    */ import com.serotonin.m2m2.db.dao.BaseDao;
/*    */ import com.serotonin.m2m2.vo.User;
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.SQLException;
/*    */ import java.util.List;
/*    */ import org.springframework.jdbc.core.RowMapper;
/*    */ import org.springframework.transaction.TransactionStatus;
/*    */ import org.springframework.transaction.support.TransactionCallbackWithoutResult;
/*    */ import org.springframework.transaction.support.TransactionTemplate;
/*    */ 
/*    */ public class SewageRecordDao extends BaseDao
/*    */ {
/*    */   public List<SewageRecord> getSewageRecordsByCompanyId(int compId)
/*    */   {
/* 19 */     String sql = "select r.id,r.comp_id,r.chroma,r.ph,r.codcr,r.ammonia_nitrogen,r.total_phosphorus,r.total_nitrogen,r.chloride,r.sampling_unit,r.sampling_date,c.name from sewage_records r,sewage_companies c where r.comp_id = ? and r.comp_id = c.id order by r.id";
/* 20 */     return query(sql, new Object[] { Integer.valueOf(compId) }, new SewageRecordRowMrapper());
/*    */   }
/*    */ 
/*    */   public List<SewageRecord> getAllSewageRecords()
/*    */   {
/* 25 */     String sql = "select r.id,r.comp_id,r.chroma,r.ph,r.codcr,r.ammonia_nitrogen,r.total_phosphorus,r.total_nitrogen,r.chloride,r.sampling_unit,r.sampling_date,c.name from sewage_records r,sewage_companies c where r.comp_id = c.id order by r.id";
/* 26 */     return query(sql, new SewageRecordRowMrapper());
/*    */   }
/*    */ 
/*    */   public boolean saveSewageRecord(final SewageRecord sewageRecord)
/*    */   {
/* 31 */     final ExtendedJdbcTemplate ejt2 = this.ejt;
/* 32 */     getTransactionTemplate().execute(new TransactionCallbackWithoutResult()
/*    */     {
/*    */       protected void doInTransactionWithoutResult(TransactionStatus status) {
/* 35 */         if (sewageRecord.getId() == -1) {
/* 36 */           sewageRecord.setId(SewageRecordDao.this.doInsert("insert into sewage_records (comp_id, chroma, ph,codcr, ammonia_nitrogen, total_phosphorus, total_nitrogen, chloride, sampling_unit, sampling_date) values (?,?,?,?,?,?,?,?,?,?)", new Object[] { Integer.valueOf(sewageRecord.getCompId()), Float.valueOf(sewageRecord.getChroma()), Float.valueOf(sewageRecord.getPh()), Float.valueOf(sewageRecord.getCodcr()), Float.valueOf(sewageRecord.getAmmoniaNitrogen()), Float.valueOf(sewageRecord.getTotalPhosphorus()), Float.valueOf(sewageRecord.getTotalNitrogen()), Float.valueOf(sewageRecord.getChloride()), sewageRecord.getSamplingUnit(), sewageRecord.getSamplingDate() }));
/*    */         }
/*    */         else
/* 39 */           ejt2.update("update sewage_companies set comp_id=?,chroma=?,ph=?,codcr=?,ammonia_nitrogen=?,total_phosphorus=?,total_nitrogen=?,chloride=?,sampling_unit=?,sampling_date=? where id=?", new Object[] { Integer.valueOf(sewageRecord.getCompId()), Float.valueOf(sewageRecord.getChroma()), Float.valueOf(sewageRecord.getPh()), Float.valueOf(sewageRecord.getCodcr()), Float.valueOf(sewageRecord.getAmmoniaNitrogen()), Float.valueOf(sewageRecord.getTotalPhosphorus()), Float.valueOf(sewageRecord.getTotalNitrogen()), Float.valueOf(sewageRecord.getChloride()), sewageRecord.getSamplingUnit(), sewageRecord.getSamplingDate(), Integer.valueOf(sewageRecord.getId()) });
/*    */       }
/*    */     });
/* 43 */     return true;
/*    */   }
/*    */ 
/*    */   public SewageRecord getSewageRecordById(int recordId)
/*    */   {
/* 48 */     String sql = "select r.id,r.comp_id,r.chroma,r.ph,r.codcr,r.ammonia_nitrogen,r.total_phosphorus,r.total_nitrogen,r.chloride,r.sampling_unit,r.sampling_date,c.name from sewage_records r,sewage_companies c where r.comp_id = c.id and r.id = ?";
/* 49 */     return (SewageRecord)queryForObject(sql, new Object[] { Integer.valueOf(recordId) }, new SewageRecordRowMrapper());
/*    */   }
/*    */ 
/*    */   public boolean deleteSewageRecord(final int id)
/*    */   {
/* 54 */     final ExtendedJdbcTemplate ejt2 = this.ejt;
/* 55 */     getTransactionTemplate().execute(new TransactionCallbackWithoutResult()
/*    */     {
/*    */       protected void doInTransactionWithoutResult(TransactionStatus status) {
/* 58 */         ejt2.update("delete from sewage_records where id=?", new Object[] { Integer.valueOf(id) });
/*    */       }
/*    */     });
/* 61 */     return true;
/*    */   }
/*    */ 
/*    */   public User getSewageRecordPermissionById(int userId)
/*    */   {
/* 66 */     String sql = "select sewageRecord from users where id = ?";
/* 67 */     return (User)queryForObject(sql, new Object[] { Integer.valueOf(userId) }, new UserRowMrapper());
/*    */   }
/*    */ 
/*    */   class SewageRecordRowMrapper
/*    */     implements RowMapper<SewageRecord>
/*    */   {
/*    */     SewageRecordRowMrapper()
/*    */     {
/*    */     }
/*    */ 
/*    */     public SewageRecord mapRow(ResultSet resultSet, int i)
/*    */       throws SQLException
/*    */     {
/* 84 */       SewageRecord sewageRecord = new SewageRecord();
/* 85 */       sewageRecord.setId(resultSet.getInt(1));
/* 86 */       sewageRecord.setCompId(resultSet.getInt(2));
/* 87 */       sewageRecord.setChroma(resultSet.getFloat(3));
/* 88 */       sewageRecord.setPh(resultSet.getFloat(4));
/* 89 */       sewageRecord.setCodcr(resultSet.getFloat(5));
/* 90 */       sewageRecord.setAmmoniaNitrogen(resultSet.getFloat(6));
/* 91 */       sewageRecord.setTotalPhosphorus(resultSet.getFloat(7));
/* 92 */       sewageRecord.setTotalNitrogen(resultSet.getFloat(8));
/* 93 */       sewageRecord.setChloride(resultSet.getFloat(9));
/* 94 */       sewageRecord.setSamplingUnit(resultSet.getString(10));
/* 95 */       sewageRecord.setSamplingDate(resultSet.getString(11));
/* 96 */       sewageRecord.setCompName(resultSet.getString(12));
/* 97 */       return sewageRecord;
/*    */     }
/*    */   }
/*    */ 
/*    */   class UserRowMrapper
/*    */     implements RowMapper<User>
/*    */   {
/*    */     UserRowMrapper()
/*    */     {
/*    */     }
/*    */ 
/*    */     public User mapRow(ResultSet resultSet, int i)
/*    */       throws SQLException
/*    */     {
/* 74 */       User user = new User();
/*    */ 
/* 76 */       return user;
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Documents and Settings\Administrator\桌面\modules\main\lib\main-1.0.0.jar
 * Qualified Name:     com.eazytec.scada.main.SewageRecordDao
 * JD-Core Version:    0.6.2
 */