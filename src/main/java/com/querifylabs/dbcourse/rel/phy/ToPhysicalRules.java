package com.querifylabs.dbcourse.rel.phy;

import org.apache.calcite.plan.RelOptRule;

import java.util.List;

// TODO Task 5: Add conversion rules for scan, project, and aggregate
public class ToPhysicalRules {
    public static final PhysicalFilterRule FILTER_RULE =
        PhysicalFilterRule.DEFAULT_CONFIG.toRule(PhysicalFilterRule.class);

    public static final List<RelOptRule> ALL = List.of(FILTER_RULE);
}
