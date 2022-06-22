package com.shupian.pvms.mapper;

import com.shupian.pvms.domain.entity.Admin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 19165
* @description 针对表【admin】的数据库操作Mapper
* @createDate 2022-05-31 14:43:48
* @Entity com.shupian.pvms.domain.entity.Admin
*/
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

}




