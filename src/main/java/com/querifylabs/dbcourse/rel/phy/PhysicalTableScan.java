package com.querifylabs.dbcourse.rel.phy;

import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.hint.RelHint;

import java.util.List;

public class PhysicalTableScan extends TableScan implements PhysicalRel {
    // TODO Task 6: Add information about projected columns and additional filters
    protected PhysicalTableScan(RelOptCluster cluster, RelTraitSet traitSet, List<RelHint> hints, RelOptTable table) {
        super(cluster, traitSet, hints, table);
    }
}
