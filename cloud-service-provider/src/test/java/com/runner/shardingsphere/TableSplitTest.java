package com.runner.shardingsphere;


import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.shardingsphere.infra.parser.cache.SQLStatementCacheLoader;
import org.apache.shardingsphere.infra.parser.sql.SQLStatementParserExecutor;
import org.apache.shardingsphere.sql.parser.api.CacheOption;
import org.apache.shardingsphere.sql.parser.api.SQLParserEngine;
import org.apache.shardingsphere.sql.parser.core.ParseASTNode;
import org.apache.shardingsphere.sql.parser.sql.common.statement.SQLStatement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * @author Runner
 * @version 1.0
 * @date 2024/6/26 16:54
 * @description
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TableSplitTest {

    @Autowired
    DataSource dataSource;

    @Autowired
    JdbcTemplate jdbcTemplate;



    @Test
    public void testInsertDataSource() {
        assert dataSource != null;
            Boolean execute = jdbcTemplate.execute(new StatementCallback<Boolean>() {
                public Boolean doInStatement(Statement stmt) throws SQLException, DataAccessException {
                    return stmt.execute("INSERT INTO test(id,name) value (0,'zhangsan')");
                }
            });
    }

    @Test
    public void testInsertForeachDataSource() {
        assert dataSource != null;

        for (int i = 20; i < 25; i++) {
            final int finalI = i;
            Boolean execute = jdbcTemplate.execute("INSERT INTO test(id,name) value (?,?)",new PreparedStatementCallback<Boolean>() {
                public Boolean doInPreparedStatement(PreparedStatement stmt) throws SQLException, DataAccessException {
                    stmt.setInt(1, finalI);
                    stmt.setString(2, "zhnanngsan" + finalI);
                    return stmt.execute();
                }
            });
        }

    }


    @Test
    public void testQueryDataSource() {
        assert dataSource != null;

        List<Map<String, Object>> map = jdbcTemplate.query("Select * from test", new ColumnMapRowMapper());
      /*  Map<String, Object> map = jdbcTemplate.execute(new StatementCallback<Map<String, Object>>() {
            public Map<String, Object> doInStatement(Statement stmt) throws SQLException, DataAccessException {
                ResultSet set = stmt.executeQuery("Select * from test");
                ColumnMapRowMapper columnMapRowMapper = new ColumnMapRowMapper();
                return columnMapRowMapper.mapRow(set, 0);
            }
        });*/

        System.out.println(map);
    }


    @Test
    public void testSPSqlParser() {
     String sql = "Select * from test";

        SQLStatementCacheLoader statementCacheLoader = new SQLStatementCacheLoader("MYSQL", new CacheOption(0, 0), false);

        SQLStatement sqlStatement = statementCacheLoader.load(sql);


    }
}
