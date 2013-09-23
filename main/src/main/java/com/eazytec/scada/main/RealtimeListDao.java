package com.eazytec.scada.main;

import com.serotonin.db.spring.ExtendedJdbcTemplate;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.db.dao.BaseDao;
import com.serotonin.m2m2.db.dao.DataPointDao;
import com.serotonin.m2m2.view.ShareUser;
import com.serotonin.m2m2.vo.DataPointVO;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class RealtimeListDao extends BaseDao {

    public String generateUniqueXid() {
        return generateUniqueXid(RealtimeList.XID_PREFIX, "realtimeLists");
    }

    public boolean isXidUnique(String xid, int realtimeListId) {
        return isXidUnique(xid, realtimeListId, "realtimeLists");
    }

    /**
     * Note: this method only returns basic watchlist information. No data points or share users.
     */
    public List<RealtimeList> getRealtimeLists(final int userId) {
        return query("select id, xid, userId, name from realtime_lists " //
                + "where userId=? or id in (select realtimeListId from realtime_list_users where userId=?)" //
                + "order by name", new Object[] { userId, userId }, new RealtimeListRowMapper());
    }

    /**
     * Note: this method only returns basic watchlist information. No data points or share users.
     */
    public List<RealtimeList> getRealtimeLists() {
        return query("select id, xid, userId, name from realtime_lists", new RealtimeListRowMapper());
    }

    public RealtimeList getRealtimeList(int realtimeListId) {
        // Get the watch lists.
        RealtimeList realtimeList = queryForObject("select id, xid, userId, name from realtime_lists where id=?",
                new Object[] { realtimeListId }, new RealtimeListRowMapper());
        populateWatchlistData(realtimeList);
        return realtimeList;
    }

    public void populateWatchlistData(List<RealtimeList> realtimeLists) {
        for (RealtimeList realtimeList : realtimeLists)
            populateWatchlistData(realtimeList);
    }

    public void populateWatchlistData(RealtimeList realtimeList) {
        if (realtimeList == null)
            return;

        // Get the points for each of the watch lists.
        List<Integer> pointIds = queryForList(
                "select dataPointId from realtime_list_points where realtimeListId=? order by sortOrder",
                new Object[] { realtimeList.getId() }, Integer.class);
        List<DataPointVO> points = realtimeList.getPointList();
        DataPointDao dataPointDao = new DataPointDao();
        for (Integer pointId : pointIds)
            points.add(dataPointDao.getDataPoint(pointId));

        setRealtimeListUsers(realtimeList);
    }

    /**
     * Note: this method only returns basic watchlist information. No data points or share users.
     */
    public RealtimeList getRealtimeList(String xid) {
        return queryForObject("select id, xid, userId, name from realtime_lists where xid=?", new Object[] { xid },
                new RealtimeListRowMapper(), null);
    }

    public RealtimeList getSelectedRealtimeList(int userId) {
        RealtimeList realtimeList = queryForObject("select w.id, w.xid, w.userId, w.name "
                + "from realtime_lists w join selected_realtime_list s on s.realtimeListId=w.id where s.userId=?",
                new Object[] { userId }, new RealtimeListRowMapper(), null);
        populateWatchlistData(realtimeList);
        return realtimeList;
    }

    class RealtimeListRowMapper implements RowMapper<RealtimeList> {
        @Override
        public RealtimeList mapRow(ResultSet rs, int rowNum) throws SQLException {
            RealtimeList rl = new RealtimeList();
            rl.setId(rs.getInt(1));
            rl.setXid(rs.getString(2));
            rl.setUserId(rs.getInt(3));
            rl.setName(rs.getString(4));
            return rl;
        }
    }

    public void saveSelectedRealtimeList(int userId, int realtimeListId) {
        int count = ejt.update("update selected_realtime_list set realtimeListId=? where userId=?", new Object[] { realtimeListId,
                userId });
        if (count == 0)
            ejt.update("insert into selected_realtime_list (userId, realtimeListId) values (?,?)", new Object[] { userId,
                    realtimeListId });
    }

    public RealtimeList createNewRealtimeList(RealtimeList realtimeList, int userId) {
        realtimeList.setUserId(userId);
        realtimeList.setXid(generateUniqueXid());
        realtimeList.setId(doInsert("insert into realtime_lists (xid, userId, name) values (?,?,?)",
                new Object[] { realtimeList.getXid(), userId, realtimeList.getName() }));
        return realtimeList;
    }

    public void saveRealtimeList(final RealtimeList realtimeList) {
        final ExtendedJdbcTemplate ejt2 = ejt;
        getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
            @SuppressWarnings("synthetic-access")
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                if (realtimeList.getId() == Common.NEW_ID)
                    realtimeList.setId(doInsert("insert into realtime_lists (xid, name, userId) values (?,?,?)", new Object[] {
                            realtimeList.getXid(), realtimeList.getName(), realtimeList.getUserId() }));
                else
                    ejt2.update("update realtime_lists set xid=?, name=? where id=?", new Object[] { realtimeList.getXid(),
                            realtimeList.getName(), realtimeList.getId() });
                ejt2.update("delete from realtime_list_points where realtimeListId=?", new Object[] { realtimeList.getId() });
                ejt2.batchUpdate("insert into realtime_list_points values (?,?,?)", new BatchPreparedStatementSetter() {
                    @Override
                    public int getBatchSize() {
                        return realtimeList.getPointList().size();
                    }

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, realtimeList.getId());
                        ps.setInt(2, realtimeList.getPointList().get(i).getId());
                        ps.setInt(3, i);
                    }
                });

                saveRealtimeListUsers(realtimeList);
            }
        });
    }

    public void deleteRealtimeList(final int realtimeListId) {
        final ExtendedJdbcTemplate ejt2 = ejt;
        getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                ejt2.update("delete from realtime_list_points where realtimeListId=?", new Object[] { realtimeListId });
                ejt2.update("delete from realtime_list_users where realtimeListId=?", new Object[] { realtimeListId });
                ejt2.update("delete from selected_realtime_list where realtimeListId=?", new Object[] { realtimeListId });
                ejt2.update("delete from realtime_lists where id=?", new Object[] { realtimeListId });
            }
        });
    }

    //
    //
    // Watch list users
    //
    private void setRealtimeListUsers(RealtimeList realtimeList) {
        realtimeList.setRealtimeListUsers(query("select userId, accessType from realtime_list_users where realtimeListId=?",
                new Object[]{realtimeList.getId()}, new RealtimeListUserRowMapper()));
    }

    class RealtimeListUserRowMapper implements RowMapper<ShareUser> {
        @Override
        public ShareUser mapRow(ResultSet rs, int rowNum) throws SQLException {
            ShareUser wlu = new ShareUser();
            wlu.setUserId(rs.getInt(1));
            wlu.setAccessType(rs.getInt(2));
            return wlu;
        }
    }

    private void deleteRealtimeListUsers(int realtimeListId) {
        ejt.update("delete from realtime_list_users where realtimeListId=?", new Object[] { realtimeListId });
    }

    void saveRealtimeListUsers(final RealtimeList realtimeList) {
        // Delete anything that is currently there.
        deleteRealtimeListUsers(realtimeList.getId());

        // Add in all of the entries.
        ejt.batchUpdate("insert into realtime_list_users values (?,?,?)", new BatchPreparedStatementSetter() {
            @Override
            public int getBatchSize() {
                return realtimeList.getRealtimeListUsers().size();
            }

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ShareUser wlu = realtimeList.getRealtimeListUsers().get(i);
                ps.setInt(1, realtimeList.getId());
                ps.setInt(2, wlu.getUserId());
                ps.setInt(3, wlu.getAccessType());
            }
        });
    }

    public void removeUserFromRealtimeList(int realtimeListId, int userId) {
        ejt.update("delete from realtime_list_users where realtimeListId=? and userId=?", new Object[] { realtimeListId, userId });
    }
}
