package com.runner.shardingsphere;


import com.sun.org.apache.bcel.internal.generic.NEW;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
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

        for (int i = 0; i < 10; i++) {
            final int finalI = i;
            Boolean execute = jdbcTemplate.execute(new StatementCallback<Boolean>() {
                public Boolean doInStatement(Statement stmt) throws SQLException, DataAccessException {
                    return stmt.execute("INSERT INTO test(id,name) value ("+ finalI + ",'zhangsan')");
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
}
