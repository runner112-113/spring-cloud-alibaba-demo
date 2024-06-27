package com.runner.abtlr4;

import io.seata.sqlparser.SQLRecognizer;
import io.seata.sqlparser.antlr.SQLOperateRecognizerHolder;
import io.seata.sqlparser.antlr.SQLOperateRecognizerHolderFactory;
import io.seata.sqlparser.antlr.mysql.parser.MySqlLexer;
import io.seata.sqlparser.antlr.mysql.parser.MySqlParser;
import io.seata.sqlparser.antlr.mysql.stream.ANTLRNoCaseStringStream;
import io.seata.sqlparser.antlr.mysql.visit.StatementSqlVisitor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Runner
 * @version 1.0
 * @date 2024/6/27 15:56
 * @description
 */
@SpringBootTest
public class Antlr4Test {

    @Test
    public void testSelect() {
        String sql = "SELECT t1.column1,t1.column2,t1.column3,t2.xy from tableC t1 left join tableA t2 on t1.id=t2.oid where t1.column1 = 1 and t2.yy=6";

        List<SQLRecognizer> recognizerList = create(sql, "Mysql");
        System.out.println(recognizerList);

    }


    public List<SQLRecognizer> create(String sqlData, String dbType) {

        io.seata.sqlparser.antlr.mysql.parser.MySqlLexer lexer = new MySqlLexer(new ANTLRNoCaseStringStream(sqlData));

        CommonTokenStream tokenStream = new CommonTokenStream(lexer);

        io.seata.sqlparser.antlr.mysql.parser.MySqlParser parser = new io.seata.sqlparser.antlr.mysql.parser.MySqlParser(tokenStream);

        io.seata.sqlparser.antlr.mysql.parser.MySqlParser.SqlStatementsContext sqlStatementsContext = parser.sqlStatements();

        List<io.seata.sqlparser.antlr.mysql.parser.MySqlParser.SqlStatementContext> sqlStatementContexts = sqlStatementsContext.sqlStatement();

        List<SQLRecognizer> recognizers = null;
        SQLRecognizer recognizer = null;

        for (MySqlParser.SqlStatementContext sql : sqlStatementContexts) {

            StatementSqlVisitor visitor = new StatementSqlVisitor();

            String originalSQL = visitor.visit(sql).toString();

            SQLOperateRecognizerHolder recognizerHolder =
                    SQLOperateRecognizerHolderFactory.getSQLRecognizerHolder(dbType.toLowerCase());
            if (sql.dmlStatement().updateStatement() != null) {
                recognizer = recognizerHolder.getUpdateRecognizer(originalSQL);
            } else if (sql.dmlStatement().insertStatement() != null) {
                recognizer = recognizerHolder.getInsertRecognizer(originalSQL);
            } else if (sql.dmlStatement().deleteStatement() != null) {
                recognizer = recognizerHolder.getDeleteRecognizer(originalSQL);
            } else if (sql.dmlStatement().selectStatement() != null) {
                recognizer = recognizerHolder.getSelectForUpdateRecognizer(originalSQL);
            }

            if (recognizer != null) {
                if (recognizers == null) {
                    recognizers = new ArrayList();
                }
                recognizers.add(recognizer);
            }
        }
        return recognizers;
    }
}
