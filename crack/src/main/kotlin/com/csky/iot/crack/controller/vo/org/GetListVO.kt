package com.csky.iot.crack.controller.vo.org

import javax.validation.constraints.NotNull

class GetListVO {
    @NotNull(message = "userId不能为空")
    var userId: Long? = null

    @NotNull(message = "parentOrgId不能为空")
    var parentOrgId: Long? = null
}