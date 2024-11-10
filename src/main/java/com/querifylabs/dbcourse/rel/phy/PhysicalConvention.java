package com.querifylabs.dbcourse.rel.phy;

import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.ConventionTraitDef;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelTrait;
import org.apache.calcite.plan.RelTraitDef;

public enum PhysicalConvention implements Convention {
    INSTANCE;

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public void register(RelOptPlanner planner) {
    }

    @Override
    public Class<?> getInterface() {
        return PhysicalRel.class;
    }

    @Override
    public String getName() {
        return "PHY";
    }

    @Override
    public RelTraitDef<Convention> getTraitDef() {
        return ConventionTraitDef.INSTANCE;
    }

    @Override
    public boolean satisfies(RelTrait trait) {
        return this == trait;
    }

}