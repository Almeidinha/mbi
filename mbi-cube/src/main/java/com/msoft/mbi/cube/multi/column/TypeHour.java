package com.msoft.mbi.cube.multi.column;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

import com.msoft.mbi.cube.util.logicOperators.OperaTor;

public class TypeHour implements DataType<Time> {


    public static final Time BRANCO = Time.valueOf("00:00:00");

    public Time getValue(ResultSet set, String field) throws SQLException {
        return set.getTime(field);
    }

    public Time format(Time time) {
        if (time == BRANCO || time == null) {
            return null;
        }
        return time;
    }

    @Override
    public OperaTor getOperator(String operator) {
        return null;
    }

}
