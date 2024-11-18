package com.runner.distributed.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * distributed lock implement base mysql
 *
 * @author Runner
 * @version 1.0
 * @since 2024/10/28 16:09
 */
@Component
public class MysqlDistributedLock implements DistributedLock{

    private static final String LOCK_TABLE_NAME = "distributed_lock";

    @Autowired
    JdbcTemplate jdbcTemplate;

    public void lock() {
        Connection connection = null;
        Boolean autoCommit = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DataSourceUtils.getConnection(jdbcTemplate.getDataSource());
            autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(String.format("select * from %s where lock_type = mysql for update ", LOCK_TABLE_NAME));
            preparedStatement.execute();
        }catch (Exception e){

        }finally {
            if (connection != null) {
                try {
                    connection.commit();
                }catch (SQLException e) {

                }

                try {
                    connection.setAutoCommit(autoCommit);
                }catch (SQLException e) {

                }

                try {
                    connection.close();
                } catch (SQLException e) {

                }
            }

            // close PreparedStatement
            if (null != preparedStatement) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {

                }
            }

        }

    }

    public void lock(long holdTime, TimeUnit timeUnit) {

    }

    public void unlock() {

    }

    public boolean tryLock() {
        return false;
    }

    public boolean tryLock(long holdTime, TimeUnit timeUnit) {
        return false;
    }
}
