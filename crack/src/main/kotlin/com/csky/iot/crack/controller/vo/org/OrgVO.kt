package com.csky.iot.crack.controller.vo.org

import javax.validation.constraints.NotNull

class OrgVO {
    @NotNull(message = "userId不能为空")
    var userId: Long? = null

   // @NotNull(message = "parentId不能为空")
    var parentOrgId: Long? = null

    //@NotNull(message = "文件类型不能为空")
    var type: Boolean? = null

   // @NotBlank(message = "文件名不能为空")
    var name: String? = null

    var cid: String? = null

    var ip: String? = null

    var pix: Int? = null

    var port: String? = null

    var durationTime: Long? = null

    var crackX: Int? = null

    var crackY: Int? = null

    var enable: Boolean? = null

    var deviceAddress: String? = null

    var orgId: Long? = null
}