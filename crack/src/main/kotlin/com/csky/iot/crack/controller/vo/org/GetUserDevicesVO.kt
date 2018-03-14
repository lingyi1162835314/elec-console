package com.csky.iot.crack.controller.vo.org

import javax.validation.constraints.NotNull

class GetUserDevicesVO {
    @NotNull(message = "userId不能为空")
    var userId: Long? = null
}