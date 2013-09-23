/*     */ package com.eazytec.scada.main;
/*     */ 
/*     */ import com.serotonin.db.spring.ExtendedJdbcTemplate;
/*     */ import com.serotonin.m2m2.db.dao.BaseDao;
/*     */ import com.serotonin.m2m2.vo.User;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.List;
/*     */ import org.springframework.jdbc.core.RowMapper;
/*     */ import org.springframework.transaction.TransactionStatus;
/*     */ import org.springframework.transaction.support.TransactionCallbackWithoutResult;
/*     */ import org.springframework.transaction.support.TransactionTemplate;
/*     */ 
/*     */ public class SewageCompanyDao extends BaseDao
/*     */ {
/*     */   public SewageCompany getSewageCompany(int id)
/*     */   {
/*  19 */     String sql = "select sc.id,sc.name, sc.address, sc.telephone,sc.datapointid,.dph.name from sewage_companies sc,datapointhierarchy dph where sc.datapointid = dph.id and sc.id =? order by sc.id";
/*  20 */     return (SewageCompany)queryForObject(sql, new Object[] { Integer.valueOf(id) }, new SewageCompanyRowMrapper());
/*     */   }
/*     */ 
/*     */   public List<SewageCompany> getSewageCompanies()
/*     */   {
/*  25 */     String sql = "select sc.id,sc.name, sc.address, sc.telephone,sc.datapointid,.dph.name from sewage_companies sc,datapointhierarchy dph where sc.datapointid = dph.id order by sc.id";
/*  26 */     return query(sql, new SewageCompanyRowMrapper());
/*     */   }
/*     */ 
/*     */   public boolean saveSewageCompany(final SewageCompany sewageCompany) {
/*  30 */     final ExtendedJdbcTemplate ejt2 = this.ejt;
/*  31 */     getTransactionTemplate().execute(new TransactionCallbackWithoutResult()
/*     */     {
/*     */       protected void doInTransactionWithoutResult(TransactionStatus status) {
/*  34 */         if (sewageCompany.getId() == -1) {
/*  35 */           sewageCompany.setId(SewageCompanyDao.this.doInsert("insert into sewage_companies (name, address, telephone,datapointid) values (?,?,?,?)", new Object[] { sewageCompany.getName(), sewageCompany.getAddress(), sewageCompany.getTelephone(), Integer.valueOf(sewageCompany.getDataPointId()) }));
/*     */         }
/*     */         else
/*  38 */           ejt2.update("update sewage_companies set name=?,address=?,telephone=?,datapointid=? where id=?", new Object[] { sewageCompany.getName(), sewageCompany.getAddress(), sewageCompany.getTelephone(), Integer.valueOf(sewageCompany.getDataPointId()), Integer.valueOf(sewageCompany.getId()) });
/*     */       }
/*     */     });
/*  42 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean deleteSewageCompany(final int id) {
/*  46 */     final ExtendedJdbcTemplate ejt2 = this.ejt;
/*  47 */     getTransactionTemplate().execute(new TransactionCallbackWithoutResult()
/*     */     {
/*     */       protected void doInTransactionWithoutResult(TransactionStatus status) {
/*  50 */         ejt2.update("delete from sewage_companies where id=?", new Object[] { Integer.valueOf(id) });
/*     */       }
/*     */     });
/*  53 */     return true;
/*     */   }
/*     */ 
/*     */   public List<DataPointHierarchy> getDataPointHierarchy()
/*     */   {
/*  72 */     String sql = "select * from datapointhierarchy";
/*  73 */     return query(sql, new DataPointHierarchyRowMrapper());
/*     */   }
/*     */ 
/*     */   public DataPointHierarchy getDataPointHierarchyById(int id)
/*     */   {
/*  89 */     String sql = "select * from datapointhierarchy where id =?";
/*  90 */     return (DataPointHierarchy)queryForObject(sql, new Object[] { Integer.valueOf(id) }, new DataPointHierarchyRowMrapper());
/*     */   }
/*     */ 
/*     */   public List<SewageCompany> getSewageCompanyByDataPointHierarchyId(int dphid) {
/*  94 */     String sql = "select sc.id,sc.name, sc.address, sc.telephone,sc.datapointid,.dph.name from sewage_companies sc,datapointhierarchy dph where sc.datapointid = dph.id and sc.datapointid =?";
/*  95 */     return query(sql, new Object[] { Integer.valueOf(dphid) }, new SewageCompanyRowMrapper());
/*     */   }
/*     */ 
/*     */   public User getSewageCompanyPermissionById(int userId)
/*     */   {
/* 100 */     String sql = "select sewageCompany from users where id = ?";
/* 101 */     return (User)queryForObject(sql, new Object[] { Integer.valueOf(userId) }, new UserRowMrapper());
/*     */   }
/*     */   class UserRowMrapper implements RowMapper<User> {
/*     */     UserRowMrapper() {
/*     */     }
/*     */ 
/*     */     public User mapRow(ResultSet resultSet, int i) throws SQLException {
/* 108 */       User user = new User();
/*     */ 
/* 110 */       return user;
/*     */     }
/*     */   }
/*     */ 
/*     */   class DataPointHierarchyRowMrapper
/*     */     implements RowMapper<DataPointHierarchy>
/*     */   {
/*     */     DataPointHierarchyRowMrapper()
/*     */     {
/*     */     }
/*     */ 
/*     */     public DataPointHierarchy mapRow(ResultSet resultSet, int i)
/*     */       throws SQLException
/*     */     {
/*  80 */       DataPointHierarchy dataPointHierarchy = new DataPointHierarchy();
/*  81 */       dataPointHierarchy.setId(resultSet.getInt(1));
/*  82 */       dataPointHierarchy.setParentId(resultSet.getInt(2));
/*  83 */       dataPointHierarchy.setName(resultSet.getString(3));
/*  84 */       return dataPointHierarchy;
/*     */     }
/*     */   }
/*     */ 
/*     */   class SewageCompanyRowMrapper
/*     */     implements RowMapper<SewageCompany>
/*     */   {
/*     */     SewageCompanyRowMrapper()
/*     */     {
/*     */     }
/*     */ 
/*     */     public SewageCompany mapRow(ResultSet resultSet, int i)
/*     */       throws SQLException
/*     */     {
/*  60 */       SewageCompany sewageCompany = new SewageCompany();
/*  61 */       sewageCompany.setId(resultSet.getInt(1));
/*  62 */       sewageCompany.setName(resultSet.getString(2));
/*  63 */       sewageCompany.setAddress(resultSet.getString(3));
/*  64 */       sewageCompany.setTelephone(resultSet.getString(4));
/*  65 */       sewageCompany.setDataPointId(resultSet.getInt(5));
/*  66 */       sewageCompany.setDataPointName(resultSet.getString(6));
/*  67 */       return sewageCompany;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Documents and Settings\Administrator\桌面\modules\main\lib\main-1.0.0.jar
 * Qualified Name:     com.eazytec.scada.main.SewageCompanyDao
 * JD-Core Version:    0.6.2
 */