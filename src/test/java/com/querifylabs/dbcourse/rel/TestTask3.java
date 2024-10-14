package com.querifylabs.dbcourse.rel;

import com.querifylabs.dbcourse.TestBase;
import com.querifylabs.dbcourse.sql.ExtendedSqlOperatorTable;
import com.querifylabs.dbcourse.sql.SqlBase64DecodeFunction;
import org.apache.calcite.avatica.util.ByteString;
import org.apache.calcite.rel.logical.LogicalFilter;
import org.apache.calcite.rel.logical.LogicalProject;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.type.SqlTypeName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestTask3 extends TestBase {
    @Test
    void testOnValidSql() {
        var unoptimized = optimizer.convert(
                "select passenger_count from public.taxirides where store_and_fwd_flag = base64decode('QQ==')");
        var optimized = optimizer.optimize(unoptimized);

        assertThat(optimized.rel).isExactlyInstanceOf(LogicalProject.class);

        assertThat(((LogicalProject)optimized.rel).getInput())
                .satisfies(input -> {
                    assertThat(input).isExactlyInstanceOf(LogicalFilter.class);
                    var filter = (LogicalFilter)input;

                    assertThat(filter.getCondition()).satisfies(cond -> {
                        assertThat(cond.getKind()).isEqualTo(SqlKind.EQUALS);
                        var eq = (RexCall)cond;

                        assertThat(eq.getOperands())
                            .hasSize(2)
                            .satisfiesExactlyInAnyOrder(
                                    op -> {
                                        assertThat(op.getKind()).isEqualTo(SqlKind.CAST);
                                        assertThat(op.getType().getSqlTypeName()).isEqualTo(SqlTypeName.VARBINARY);
                                    },
                                    op -> {
                                        assertThat(op.getKind()).isEqualTo(SqlKind.LITERAL);
                                        assertThat(op.getType().getSqlTypeName()).isEqualTo(SqlTypeName.BINARY);
                                        assertThat(((RexLiteral)op).getValue()).isEqualTo(new ByteString(new byte[] {'A'}));
                                    });
                    });
                });
    }
}
