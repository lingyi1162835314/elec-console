package com.csky.iot.crack.controller

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.csky.iot.crack.common.ApiResult
import com.csky.iot.crack.common.LogUtil
import com.csky.iot.crack.common.ResponseCode
import com.csky.iot.crack.common.codeCheck
import com.csky.iot.crack.controller.vo.org.*
import com.csky.iot.crack.exception.CskyException
import com.csky.iot.crack.service.BaseService
import com.csky.iot.crack.service.OrgService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/org")
class OrgController(val baseService: BaseService,
                    val orgService: OrgService): BaseController() {

    @PostMapping("/add")
    fun addOrg(@Validated @RequestBody orgVO: OrgVO): ApiResult<JSONObject>  {
        LogUtil.i(javaClass, "add org start:${JSON.toJSONString(orgVO)}")

        val userId = orgVO.userId!!
        val name = orgVO.name
        val type = orgVO.type
        val parentOrgId = orgVO.parentOrgId

        try {
            codeCheck(name == null, ResponseCode.PARAM_ERROR, "文件名不为空")
            codeCheck(type == null, ResponseCode.PARAM_ERROR, "文件类型不为空")
            codeCheck(parentOrgId == null, ResponseCode.PARAM_ERROR, "parentOrgId不为空")

            codeCheck(!baseService.checkToken(userId, getHeaderToken()), ResponseCode.ILLEGAL_USER, "用户权限错误")

            orgService.addOrg(orgVO)

        } catch (e: CskyException) {
            LogUtil.e(javaClass, "[$userId]${e.code}:${e.message}")

            return ApiResult(e.code, e.message)
        }

        LogUtil.i(javaClass, "[$userId]add org ends")

        return ApiResult(
                ResponseCode.SUCCESS.code,
                "添加文件成功")
    }

    @PostMapping("/getList")
    fun getOrgList(@Validated @RequestBody getListVO: GetListVO): ApiResult<List<JSONObject>?> {
        LogUtil.i(javaClass, "get device and org start:${JSON.toJSONString(getListVO)}")

        val userId = getListVO.userId!!
        val parentOrgId = getListVO.parentOrgId!!

        val orgList = try {
            codeCheck(!baseService.checkToken(userId, getHeaderToken()), ResponseCode.ILLEGAL_USER, "用户权限错误")

            orgService.getOrgList(userId, parentOrgId)

        } catch (e: CskyException) {
            LogUtil.e(javaClass, "[$userId]${e.code}:${e.message}")

            return ApiResult(e.code, e.message)
        }

        LogUtil.i(javaClass, "[$userId]get device and org ends: $orgList")

        return ApiResult(
                ResponseCode.SUCCESS.code,
                "获取文件列表成功",
                orgList)
    }

    @PostMapping("/update")
    fun updateOrg(@Validated @RequestBody orgVO: OrgVO): ApiResult<JSONObject>  {
        LogUtil.i(javaClass, "update org start:${JSON.toJSONString(orgVO)}")

        val userId = orgVO.userId!!
        val orgId = orgVO.orgId

        try {
            codeCheck(orgId == null, ResponseCode.PARAM_ERROR, "orgId不为空")

            codeCheck(!baseService.checkToken(userId, getHeaderToken()), ResponseCode.ILLEGAL_USER, "用户权限错误")

            orgService.updateOrg(orgVO)

        } catch (e: CskyException) {
            LogUtil.e(javaClass, "[$userId]${e.code}:${e.message}")

            return ApiResult(e.code, e.message)
        }


        LogUtil.i(javaClass, "[$userId]update org ends")

        return ApiResult(
                ResponseCode.SUCCESS.code,
                "修改文件信息成功")
    }

    @PostMapping("/delete")
    fun deleteOrg(@Validated @RequestBody orgCrackVO: OrgCrackVO): ApiResult<JSONObject>  {
        LogUtil.i(javaClass, "delete org starts:${JSON.toJSONString(orgCrackVO)}")

        val userId = orgCrackVO.userId!!
        val orgId = orgCrackVO.orgId!!

        try {
            codeCheck(!baseService.checkToken(userId, getHeaderToken()), ResponseCode.ILLEGAL_USER, "用户权限错误")

            orgService.deleteOrg(userId, orgId)

        } catch (e: CskyException) {
            LogUtil.e(javaClass, "[$userId]${e.code}:${e.message}")

            return ApiResult(e.code, e.message)
        }

        LogUtil.i(javaClass, "[$userId]delete org ends")

        return ApiResult(
                ResponseCode.SUCCESS.code,
                "删除文件成功")
    }

    @PostMapping("/forceDelete")
    fun forceDeleteOrg(@Validated @RequestBody orgCrackVO: OrgCrackVO): ApiResult<JSONObject>  {
        LogUtil.i(javaClass, "force delete org starts:${JSON.toJSONString(orgCrackVO)}")

        val userId = orgCrackVO.userId!!
        val orgId = orgCrackVO.orgId!!

        try {
            codeCheck(!baseService.checkToken(userId, getHeaderToken()), ResponseCode.ILLEGAL_USER, "用户权限错误")

            orgService.forceDeleteOrg(userId, orgId)

        } catch (e: CskyException) {
            LogUtil.e(javaClass, "[$userId]${e.code}:${e.message}")

            return ApiResult(e.code, e.message)
        }

        LogUtil.i(javaClass, "[$userId]force delete org ends")

        return ApiResult(
                ResponseCode.SUCCESS.code,
                "强制删除文件成功")
    }

    @PostMapping("/device/offline")
    fun getOfflineDevices(@Validated @RequestBody getUserDevicesVO: GetUserDevicesVO): ApiResult<List<JSONObject>?>  {
        LogUtil.i(javaClass, "get LowPower Devices start:${JSON.toJSONString(getUserDevicesVO)}")

        val userId = getUserDevicesVO.userId!!

        val orgList = try {
            codeCheck(!baseService.checkToken(userId, getHeaderToken()), ResponseCode.ILLEGAL_USER, "用户权限错误")

            orgService.getOfflineDevices(userId)

        } catch (e: CskyException) {
            LogUtil.e(javaClass, "[$userId]${e.code}:${e.message}")

            return ApiResult(e.code, e.message)
        }

        LogUtil.i(javaClass, "[$userId]get LowPower Devices ends: $orgList")

        return ApiResult(
                ResponseCode.SUCCESS.code,
                "获取离线设备成功",
                orgList)
    }


    @PostMapping("/device/lowPower")
    fun getLowPowerDevices(@Validated @RequestBody getUserDevicesVO: GetUserDevicesVO): ApiResult<List<JSONObject>?>  {
        LogUtil.i(javaClass, "get LowPower Devices start:${JSON.toJSONString(getUserDevicesVO)}")

        val userId = getUserDevicesVO.userId!!

        val orgList = try {
            codeCheck(!baseService.checkToken(userId, getHeaderToken()), ResponseCode.ILLEGAL_USER, "用户权限错误")

            orgService.getLowPowerDevices(userId)

        } catch (e: CskyException) {
            LogUtil.e(javaClass, "[$userId]${e.code}:${e.message}")

            return ApiResult(e.code, e.message)
        }

        LogUtil.i(javaClass, "[$userId]get LowPower Devices ends: $orgList")

        return ApiResult(
                ResponseCode.SUCCESS.code,
                "获取低电量设备成功",
                orgList)
    }

//    @PostMapping("/device/details")
//    fun getDeviceDetails(@Validated @RequestBody orgVO: OrgVO): ApiResult<JSONObject>  {
//        LogUtil.i(javaClass, "get details of devices start:${JSON.toJSONString(orgVO)}")
//
//        val userId = orgVO.userId!!
//        val orgId = orgVO.orgId!!
//
//        val responseJson = try {
//            codeCheck(!baseService.checkToken(userId, getHeaderToken()), ResponseCode.ILLEGAL_USER, "用户权限错误")
//
//            orgService.getDeviceDetails(userId, orgId)
//
//        } catch (e: CskyException) {
//            LogUtil.e(javaClass, "[$userId]${e.code}:${e.message}")
//
//            return ApiResult(e.code, e.message)
//        }
//
//        LogUtil.i(javaClass, "[$userId]get details of devices ends: $responseJson")
//
//        return ApiResult(
//                ResponseCode.SUCCESS.code,
//                "获取设备详细信息成功",
//                responseJson)
//    }

    @PostMapping("/device/crack")
    fun getDeviceCrack(@Validated @RequestBody orgCrackVO: OrgCrackVO): ApiResult<List<JSONObject>?>  {
        LogUtil.i(javaClass, "get details of devices start:${JSON.toJSONString(orgCrackVO)}")

        val userId = orgCrackVO.userId!!
        val orgId = orgCrackVO.orgId!!

        val jsonList = try {
            codeCheck(!baseService.checkToken(userId, getHeaderToken()), ResponseCode.ILLEGAL_USER, "用户权限错误")

            orgService.getCrackDetails(userId, orgId)

        } catch (e: CskyException) {
            LogUtil.e(javaClass, "[$userId]${e.code}:${e.message}")

            return ApiResult(e.code, e.message)
        }

        LogUtil.i(javaClass, "[$userId]get details of devices ends: $jsonList")

        return ApiResult(
                ResponseCode.SUCCESS.code,
                "获取设备裂缝信息成功",
                jsonList)
    }

}