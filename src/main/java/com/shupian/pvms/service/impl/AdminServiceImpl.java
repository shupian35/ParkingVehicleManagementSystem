package com.shupian.pvms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.code.kaptcha.Producer;
import com.shupian.pvms.domain.ResponseResult;
import com.shupian.pvms.domain.entity.Admin;
import com.shupian.pvms.domain.vo.AdminLoginVo;
import com.shupian.pvms.domain.vo.AdminVo;
import com.shupian.pvms.exception.SystemException;
import com.shupian.pvms.service.AdminService;
import com.shupian.pvms.mapper.AdminMapper;
import com.shupian.pvms.utils.BeanCopyUtils;
import com.shupian.pvms.utils.CaptchaTextCreator;
import com.shupian.pvms.utils.JwtUtil;
import com.shupian.pvms.utils.RedisCache;
import com.sun.javafx.collections.MappingChange;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.FastByteArrayOutputStream;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.shupian.pvms.constants.SystemConstants.NORMAL;
import static com.shupian.pvms.enums.AppHttpCodeEnum.*;

/**
 * @author 19165
 * @description 针对表【admin】的数据库操作Service实现
 * @createDate 2022-05-31 14:43:48
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin>
        implements AdminService
{

    @Resource
    private AdminMapper adminMapper;

    @Autowired
    private RedisCache redisCache;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    @Autowired
    private CaptchaTextCreator captchaTextCreator;

    /**
     * 管理员登录
     *
     * @param adminLoginVo
     * @return
     */
    @Override
    public ResponseResult login(AdminLoginVo adminLoginVo, HttpServletRequest request)
    {

        String username = adminLoginVo.getUsername();
        String password = adminLoginVo.getPassword();
        String captcha = adminLoginVo.getCaptcha();
        String uuid = adminLoginVo.getUuid();
        // 1.1、非空校验
        if (StringUtils.isAnyEmpty(username, password))
        {
            throw new SystemException(PARAMETER_ERROR, "账号密码不能为空");
        }
        // 1.2、账号为4~9位
        if (username.length() < 4 || username.length() > 9)
        {
            throw new SystemException(PARAMETER_ERROR, "账号为4~9位");
        }
        // 1.3、密码为8~16位
        if (password.length() < 8 || password.length() > 16)
        {
            throw new SystemException(PARAMETER_ERROR, "密码为8~16位");
        }
        // 1.4、账号不能包含特殊字符
        String validPattern = "[\\s`!@#$%^&*_\\-~()+=|{}':;,\\[\\].<>/\\\\?！￥…（）—【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(username);
        if (matcher.find())
        {
            throw new SystemException(PARAMETER_ERROR, "账号不能包含特殊字符");
        }
        // 1.5、密码不能含有空字符
        String validPattern1 = "[\\s]";
        Matcher matcher1 = Pattern.compile(validPattern1).matcher(password);
        if (matcher1.find())
        {
            throw new SystemException(PARAMETER_ERROR, "密码不能包含空字符");
        }
        // 1.6、验证码为1~4位
        if (captcha.length() < 1 || captcha.length() > 4)
        {
            throw new SystemException(PARAMETER_ERROR, "验证码为1~4位");
        }
        // 1.7、验证码失效
        String key = "captcha_codes:" + uuid;
        String text;
        text = redisCache.getCacheObject(key);
        if (StringUtils.isAnyBlank(text))
        {
            throw new SystemException(PARAMETER_ERROR, "验证码失效请重试");
        }

        String result = text.substring(text.lastIndexOf("@") + 1);
        if (!result.equals(captcha))
        {
            throw new SystemException(PARAMETER_ERROR, "验证码错误请重试");
        }

        String encodePassword = DigestUtils.md5DigestAsHex(password.getBytes());
        LambdaQueryWrapper<Admin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Admin::getUsername, username)
                .eq(Admin::getPassword, encodePassword)
                .eq(Admin::getStatus, NORMAL);
        Admin admin1 = adminMapper.selectOne(lambdaQueryWrapper);
        if (Objects.isNull(admin1))
        {
            throw new SystemException(LOGIN_ERROR);
        }
        String jwt = JwtUtil.createJWT(String.valueOf(admin1.getId()), admin1.getUsername(), JwtUtil.JWT_TTL);
        String tokenKey = "token" + admin1.getId();
        redisCache.setCacheObject(tokenKey, jwt);
        Map<String, Object> map = new HashMap<>();
        map.put("token", jwt);
        request.getSession().setAttribute("adminId", admin1.getId());
        AdminVo adminVo = BeanCopyUtils.copyBean(admin1, AdminVo.class);
        map.put("adminInfo", adminVo);
        return ResponseResult.okResult(map);
    }

    /**
     * 管理员登出
     *
     * @param request
     * @return
     */
    @Override
    public ResponseResult logout(HttpServletRequest request)
    {
        String token = request.getHeader("token");
        Object adminId = request.getSession().getAttribute("adminId");
        if (token == null || adminId == null)
        {
            throw new SystemException(PARAMETER_ERROR);
        }
        String tokenKey = "token" + adminId.toString();
        String redisToken = redisCache.getCacheObject(tokenKey);
        if (redisToken.equals(token))
        {
            //删除redis中的token
            redisCache.deleteObject(tokenKey);
        }
        return ResponseResult.okResult();
    }

    /**
     * 获取图片验证码
     *
     * @return
     */
    @Override
    public ResponseResult getCaptchaImage()
    {
        // 生成 UUID
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String redisKey = "captcha_codes:" + uuid;

        // 生成验证码
        String text = captchaTextCreator.getText();
        int index = text.lastIndexOf("@");
        String code = text.substring(0, index);
        BufferedImage image = captchaProducerMath.createImage(code);

        // 将 text存入 Redis,设置过期时间为两分钟
        redisCache.setCacheObject(redisKey, text, 2, TimeUnit.MINUTES);

        FastByteArrayOutputStream fo = new FastByteArrayOutputStream();
        try
        {
            ImageIO.write(image, "jpg", fo);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        Map<Object, Object> map = new HashMap<>();
        map.put("captcha", Base64.encode(fo.toByteArray()));
        map.put("uuid", uuid);

        return ResponseResult.okResult(map);
    }
}


