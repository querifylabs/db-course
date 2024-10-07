package com.querifylabs.dbcourse.sql;

import com.querifylabs.dbcourse.TestBase;
import org.apache.calcite.rel.logical.LogicalProject;
import org.apache.calcite.runtime.CalciteContextException;
import org.apache.calcite.sql.SqlFunction;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class SqlCustomFunctionTest extends TestBase {
    private static Stream<Arguments> validSql() {
        return Stream.of(
                Arguments.of(
                        "SELECT lower(column) from public.taxirides",
                        SqlLowerFunction.class),
                Arguments.of(
                        "SELECT upper(column) from public.taxirides",
                        SqlUpperFunction.class));
    }

    private static Stream<Arguments> invalidSql() {
        return Stream.of(
                Arguments.of(
                        "SELECT lower(t_pickupdate) from public.taxirides",
                        ".*(Cannot apply 'LOWER' to arguments of type 'LOWER\\(.*\\)'. Supported form\\"
                                + "(s\\)"
                                + ": LOWER\\(VARCHAR\\)).*"),
                Arguments.of(
                        "SELECT lower(column, 2) from public.taxirides",
                        ".*(No match found for function signature LOWER\\(<VARCHAR>, <NUMERIC>\\)).*"),
                Arguments.of(
                        "SELECT lower() from test", ".*(No match found for function signature LOWER\\(\\)).*"),
                Arguments.of(
                        "SELECT upper(t_pickupdate) from public.taxirides",
                        ".*(Cannot apply 'UPPER' to arguments of type 'UPPER\\(.*\\)'. Supported form\\"
                                + "(s\\)"
                                + ": UPPER\\(VARCHAR\\)).*"),
                Arguments.of(
                        "SELECT upper(column, 2) from public.taxirides",
                        ".*(No match found for function signature UPPER\\(<VARCHAR>, <NUMERIC>\\)).*"),
                Arguments.of(
                        "SELECT upper() from test", ".*(No match found for function signature UPPER\\(\\)).*"));
    }

    @ParameterizedTest
    @MethodSource("validSql")
    void testOnValidSql(String sql, Class<? extends SqlFunction> expectedFunction) {
        var unoptimized = optimizer.convert(sql);
        assertThat(unoptimized.rel).isExactlyInstanceOf(LogicalProject.class);

        assertThat(((LogicalProject)unoptimized.rel).getProjects())
                .hasSize(1)
                .satisfies(expr -> assertThat(expr).isExactlyInstanceOf(expectedFunction));
    }

    @ParameterizedTest
    @MethodSource("invalidSql")
    void testValidationOfInvalidFunctionCalls(String sql, String expectedErrMsg) {
        Assertions.assertThatThrownBy(() -> optimizer.convert(sql))
                .isExactlyInstanceOf(CalciteContextException.class)
                .hasMessageMatching(expectedErrMsg);
    }
}
