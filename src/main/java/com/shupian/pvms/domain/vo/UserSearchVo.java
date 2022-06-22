package com.shupian.pvms.domain.vo;
import lombok.Data;
@Data
public class UserSearchVo {
    //username,nickName,cardId,gender,status,startDate,endDate

    /**
     * 用户名
     */
    private String username;


    /**
     * 昵称
     */
    private String nickName;

    /**
     * 卡号
     */
    private String cardId;

    /**
     * 用户性别（0男，1女，2未知）
     */
    private String gender;

    /**
     * 账号状态（0正常 1停用）
     */
    private String status;

    /**
     *
     */
    private String startDate;

    private String endDate;

    private Integer pageNum;
    private Integer pageSize;
}
