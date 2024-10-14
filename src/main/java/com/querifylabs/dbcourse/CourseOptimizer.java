package com.querifylabs.dbcourse;

import com.querifylabs.dbcourse.rel.FoldTreeRelVisitor;
import com.querifylabs.dbcourse.schema.ParquetSchema;
import com.querifylabs.dbcourse.sql.ExtendedSqlOperatorTable;
import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.config.CalciteConnectionProperty;
import org.apache.calcite.jdbc.CalciteSchema;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.impl.SqlParserImpl;
import org.apache.calcite.sql.type.SqlTypeFactoryImpl;
import org.apache.calcite.sql.util.SqlOperatorTables;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorUtil;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.calcite.sql2rel.StandardConvertletTable;
import org.apache.calcite.tools.Program;
import org.apache.calcite.tools.Programs;

import java.io.StringReader;
import java.util.List;

public class CourseOptimizer {
    protected RelDataTypeFactory dataTypeFactory;

    protected SqlValidator validator;
    protected SqlToRelConverter sqlToRelConverter;
    protected VolcanoPlanner volcanoPlanner;

    public CourseOptimizer(String rootPath) {
        dataTypeFactory = new SqlTypeFactoryImpl(new CustomDataTypeSystem());
        var catalogReader = new CalciteCatalogReader(
                CalciteSchema.createRootSchema(false, true, "", new ParquetSchema(rootPath, dataTypeFactory)),
                List.of(),
                dataTypeFactory,
                CalciteConnectionConfig.DEFAULT.set(CalciteConnectionProperty.CASE_SENSITIVE, "false"));
        var operatorTable = SqlOperatorTables.chain(
                ExtendedSqlOperatorTable.instance(), SqlStdOperatorTable.instance());
        validator = SqlValidatorUtil.newValidator(
                operatorTable, catalogReader, dataTypeFactory, SqlValidator.Config.DEFAULT);

        volcanoPlanner = new VolcanoPlanner();
        var cluster = RelOptCluster.create(volcanoPlanner, new RexBuilder(dataTypeFactory));

        sqlToRelConverter = new SqlToRelConverter(
                null, validator, catalogReader, cluster, StandardConvertletTable.INSTANCE,
                SqlToRelConverter.config().withTrimUnusedFields(true));
    }

    public RelRoot convert(String sql) {
        var parsed = parseSql(sql);
        var validated = validator.validate(parsed);
        return sqlToRelConverter.convertQuery(validated, false, true);
    }

    public RelRoot optimize(RelRoot unoptimized) {
        var program = getFullProgram();

        var optimized = program.run(volcanoPlanner,
                unoptimized.rel,
                RelTraitSet.createEmpty(),
                List.of(),
                List.of());

        return unoptimized.withRel(optimized);
    }

    protected SqlNode parseSql(String sql) throws ParserException {
        var parser = new SqlParserImpl(new StringReader(sql));
        parser.setQuotedCasing(Casing.UNCHANGED);
        parser.setUnquotedCasing(Casing.UNCHANGED);
        parser.setIdentifierMaxLength(255);
        parser.setConformance(SqlConformanceEnum.DEFAULT);

        try {
            return parser.parseSqlStmtEof();
        } catch (Exception e) {
            throw new ParserException(e);
        }
    }

    protected Program getFullProgram() {
        return Programs.sequence(
            foldConstantsProgram()
        );
    }

    protected Program foldConstantsProgram() {
        return (planner, rel, requiredOutputTraits, materializations, lattices) -> rel.accept(new FoldTreeRelVisitor());
    }
}
