package cn.metaq.es.data;

import java.util.Map;

public interface RowMapper<T> {

    Map mapRow(T var1);
}
