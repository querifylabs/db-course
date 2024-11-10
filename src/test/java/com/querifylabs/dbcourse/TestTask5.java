package com.querifylabs.dbcourse;

import org.junit.jupiter.api.Test;

public class TestTask5 extends TestBase {
    @Test
    public void testPhysicalConvention() {
        var converted = optimizer.convert(
            "select sum(total_amount), passenger_count from public.taxirides group by passenger_count");

        var logicalOptimized = optimizer.optimize(converted);

        var physicalOptimized = optimizer.optimizePhysical(logicalOptimized);

        String expectedPlan = """
        PhysicalProject(EXPR$0=[$1], passenger_count=[$0])
          PhysicalAggregate(group=[{0}], EXPR$0=[SUM($1)])
            PhysicalProject(passenger_count=[$3], total_amount=[$16])
              PhysicalTableScan(table=[[public, taxirides]])
        """;

        validatePlan(physicalOptimized.rel, expectedPlan);
    }
}
