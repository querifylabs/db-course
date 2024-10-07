package com.querifylabs.dbcourse.sql;

import com.querifylabs.dbcourse.TestBase;
import org.apache.calcite.rel.logical.LogicalProject;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.runtime.CalciteContextException;
import org.apache.calcite.sql.SqlFunction;
import org.apache.calcite.sql.type.SqlTypeName;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class TestTask2 extends TestBase {
    private static Stream<Arguments> validSql() {
        return Stream.of(
                Arguments.of(
                        "SELECT base64decode(store_and_fwd_flag) from public.taxirides",
                        SqlBase64DecodeFunction.class));
    }

    private static Stream<Arguments> invalidSql() {
        return Stream.of(
                Arguments.of(
                        "SELECT base64decode(store_and_fwd_flag, 2) from public.taxirides",
                        ".*(Invalid number of arguments to function 'base64decode'\\. Was expecting 1 arguments).*"),
                Arguments.of(
                        "SELECT base64decode() from public.taxirides",
                        ".*(Invalid number of arguments to function 'base64decode'\\. Was expecting 1 arguments).*"));
    }

    @ParameterizedTest
    @MethodSource("validSql")
    void testOnValidSql(String sql, Class<? extends SqlFunction> expectedFunction) {
        var unoptimized = optimizer.convert(sql);
        assertThat(unoptimized.rel).isExactlyInstanceOf(LogicalProject.class);

        assertThat(((LogicalProject)unoptimized.rel).getProjects())
                .hasSize(1)
                .allSatisfy(expr -> {
                    assertThat(expr).isExactlyInstanceOf(RexCall.class);
                    var call = (RexCall)expr;
                    assertThat(call.getOperator()).isExactlyInstanceOf(SqlBase64DecodeFunction.class);
                    assertThat(call.getType().getSqlTypeName()).isEqualTo(SqlTypeName.VARBINARY);
                    assertThat(call.getType().isNullable()).isEqualTo(true);
                });
    }

    @ParameterizedTest
    @MethodSource("invalidSql")
    void testValidationOfInvalidFunctionCalls(String sql, String expectedErrMsg) {
        Assertions.assertThatThrownBy(() -> optimizer.convert(sql))
                .isExactlyInstanceOf(CalciteContextException.class)
                .hasMessageMatching(expectedErrMsg);
    }
}
