/*     */ package com.eazytec.scada.main;
/*     */ 
/*     */ import com.serotonin.json.JsonException;
/*     */ import com.serotonin.json.JsonReader;
/*     */ import com.serotonin.json.ObjectWriter;
/*     */ import com.serotonin.json.spi.JsonProperty;
/*     */ import com.serotonin.json.spi.JsonSerializable;
/*     */ import com.serotonin.json.type.JsonArray;
/*     */ import com.serotonin.json.type.JsonObject;
/*     */ import com.serotonin.json.type.JsonValue;
/*     */ import com.serotonin.m2m2.db.dao.DataPointDao;
/*     */ import com.serotonin.m2m2.db.dao.UserDao;
/*     */ import com.serotonin.m2m2.i18n.ProcessResult;
/*     */ import com.serotonin.m2m2.i18n.TranslatableJsonException;
/*     */ import com.serotonin.m2m2.i18n.TranslatableMessage;
/*     */ import com.serotonin.m2m2.view.ShareUser;
/*     */ import com.serotonin.m2m2.vo.DataPointVO;
/*     */ import com.serotonin.m2m2.vo.User;
/*     */ import com.serotonin.validation.StringValidation;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import org.apache.commons.lang3.StringUtils;
/*     */ 
/*     */ public class History
/*     */   implements JsonSerializable
/*     */ {
/*     */   public static final String XID_PREFIX = "HD_";
/*  29 */   private int id = -1;
/*     */ 
/*     */   @JsonProperty(read=false)
/*     */   private String xid;
/*     */   private int userId;
/*     */ 
/*     */   @JsonProperty
/*     */   private String name;
/*  35 */   private final List<DataPointVO> pointList = new CopyOnWriteArrayList();
/*  36 */   private List<ShareUser> watchListUsers = new ArrayList();
/*     */ 
/*     */   public int getUserAccess(User user) {
/*  39 */     if (user.getId() == this.userId) {
/*  40 */       return 3;
/*     */     }
/*  42 */     for (ShareUser wlu : this.watchListUsers) {
/*  43 */       if (wlu.getUserId() == user.getId())
/*  44 */         return wlu.getAccessType();
/*     */     }
/*  46 */     return 0;
/*     */   }
/*     */ 
/*     */   public int getId() {
/*  50 */     return this.id;
/*     */   }
/*     */ 
/*     */   public void setId(int id) {
/*  54 */     this.id = id;
/*     */   }
/*     */ 
/*     */   public String getXid() {
/*  58 */     return this.xid;
/*     */   }
/*     */ 
/*     */   public void setXid(String xid) {
/*  62 */     this.xid = xid;
/*     */   }
/*     */ 
/*     */   public String getName() {
/*  66 */     return this.name;
/*     */   }
/*     */ 
/*     */   public void setName(String name) {
/*  70 */     if (name == null)
/*  71 */       this.name = "";
/*     */     else
/*  73 */       this.name = name;
/*     */   }
/*     */ 
/*     */   public List<DataPointVO> getPointList() {
/*  77 */     return this.pointList;
/*     */   }
/*     */ 
/*     */   public int getUserId() {
/*  81 */     return this.userId;
/*     */   }
/*     */ 
/*     */   public void setUserId(int userId) {
/*  85 */     this.userId = userId;
/*     */   }
/*     */ 
/*     */   public List<ShareUser> getRealtimeListUsers() {
/*  89 */     return this.watchListUsers;
/*     */   }
/*     */ 
/*     */   public void setRealtimeListUsers(List<ShareUser> watchListUsers) {
/*  93 */     this.watchListUsers = watchListUsers;
/*     */   }
/*     */ 
/*     */   public void validate(ProcessResult response) {
/*  97 */     if (StringUtils.isBlank(this.name))
/*  98 */       response.addMessage("name", new TranslatableMessage("validate.required"));
/*  99 */     else if (StringValidation.isLengthGreaterThan(this.name, 50)) {
/* 100 */       response.addMessage("name", new TranslatableMessage("validate.notLongerThan", new Object[] { Integer.valueOf(50) }));
/*     */     }
/* 102 */     if (StringUtils.isBlank(this.xid))
/* 103 */       response.addMessage("xid", new TranslatableMessage("validate.required"));
/* 104 */     else if (StringValidation.isLengthGreaterThan(this.xid, 50))
/* 105 */       response.addMessage("xid", new TranslatableMessage("validate.notLongerThan", new Object[] { Integer.valueOf(50) }));
/* 106 */     else if (!new RealtimeListDao().isXidUnique(this.xid, this.id))
/* 107 */       response.addMessage("xid", new TranslatableMessage("validate.xidUsed"));
/*     */   }
/*     */ 
/*     */   public void jsonWrite(ObjectWriter writer)
/*     */     throws IOException, JsonException
/*     */   {
/* 115 */     writer.writeEntry("user", new UserDao().getUser(this.userId).getUsername());
/*     */ 
/* 117 */     List dpXids = new ArrayList();
/* 118 */     for (DataPointVO dpVO : this.pointList)
/* 119 */       dpXids.add(dpVO.getXid());
/* 120 */     writer.writeEntry("dataPoints", dpXids);
/*     */ 
/* 122 */     writer.writeEntry("sharingUsers", this.watchListUsers);
/*     */   }
/*     */ 
/*     */   public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException {
/* 126 */     String username = jsonObject.getString("user");
/* 127 */     if (StringUtils.isBlank(username))
/* 128 */       throw new TranslatableJsonException("emport.error.missingValue", new Object[] { "user" });
/* 129 */     User user = new UserDao().getUser(username);
/* 130 */     if (user == null)
/* 131 */       throw new TranslatableJsonException("emport.error.missingUser", new Object[] { username });
/* 132 */     this.userId = user.getId();
/*     */ 
/* 134 */     JsonArray jsonDataPoints = jsonObject.getJsonArray("dataPoints");
/*     */     DataPointDao dataPointDao;
/* 135 */     if (jsonDataPoints != null) {
/* 136 */       this.pointList.clear();
/* 137 */       dataPointDao = new DataPointDao();
/* 138 */       for (JsonValue jv : jsonDataPoints) {
/* 139 */         String xid = jv.toString();
/* 140 */         DataPointVO dpVO = dataPointDao.getDataPoint(xid);
/* 141 */         if (dpVO == null)
/* 142 */           throw new TranslatableJsonException("emport.error.missingPoint", new Object[] { xid });
/* 143 */         this.pointList.add(dpVO);
/*     */       }
/*     */     }
/*     */ 
/* 147 */     JsonArray jsonSharers = jsonObject.getJsonArray("sharingUsers");
/* 148 */     if (jsonSharers != null) {
/* 149 */       this.watchListUsers.clear();
/* 150 */       for (JsonValue jv : jsonSharers) {
/* 151 */         ShareUser shareUser = (ShareUser)reader.read(ShareUser.class, jv);
/* 152 */         if (shareUser.getUserId() != this.userId)
/*     */         {
/* 154 */           this.watchListUsers.add(shareUser);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Documents and Settings\Administrator\桌面\modules\main\lib\main-1.0.0.jar
 * Qualified Name:     com.eazytec.scada.main.History
 * JD-Core Version:    0.6.2
 */