package com.wh.Mapper;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * Created by WH-2013 on 2017/4/1.
 */
public interface MyMapper<T> extends Mapper<T>,MySqlMapper<T> {
}
