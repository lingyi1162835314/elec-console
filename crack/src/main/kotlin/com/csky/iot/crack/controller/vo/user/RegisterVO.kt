package com.csky.iot.crack.controller.vo.user

import org.hibernate.validator.constraints.NotBlank

class RegisterVO {
    @NotBlank(message = "姓名不能为空")
    var userName: String? = null

    @NotBlank(message = "手机号不能为空")
    var mobile: String? = null

    @NotBlank(message = "密码不能为空")
    var userPwd: String? = null

    @NotBlank(message = "验证码不能为空")
    var verification: String? = null
}