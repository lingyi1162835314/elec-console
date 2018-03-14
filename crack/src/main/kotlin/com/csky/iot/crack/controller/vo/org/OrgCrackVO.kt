package com.csky.iot.crack.controller.vo.org

import javax.validation.constraints.NotNull

class OrgCrackVO {
    @NotNull(message = "userId不能为空")
    var userId: Long? = null

    @NotNull(message = "orgId不能为空")
    var orgId: Long? = null
}