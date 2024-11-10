package com.querifylabs.dbcourse;

import org.junit.jupiter.api.Test;

public class TestTask4 extends TestBase {
    @Test
    public void testTrimUnusedFields() {
        var unoptimized = optimizer.convert(
            "select sum(total_amount), passenger_count from public.taxirides group by passenger_count");

        String expectedPlan = """
        LogicalProject(EXPR$0=[$1], passenger_count=[$0])
          LogicalAggregate(group=[{0}], EXPR$0=[SUM($1)])
            LogicalProject(passenger_count=[$3], total_amount=[$16])
              LogicalTableScan(table=[[public, taxirides]])
        """;

        validatePlan(unoptimized.rel, expectedPlan);
    }
}
