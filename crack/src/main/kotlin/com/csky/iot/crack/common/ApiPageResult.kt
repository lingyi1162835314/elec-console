package com.csky.iot.crack.common;

data class ApiPageResult<T>(

        val code: Int,

        val msg: String?,

        val result: T,

        val pageSize: Int,

        val pageNum: Int,

        val totalNum: Int,

        val pages: Int

)

data class PageModel(
        val data: Any,
        val pageSize: Int,
        val pageNum: Int,
        val totalNum: Int,
        val pages: Int = (if (totalNum % pageSize == 0) totalNum / pageSize else totalNum / pageSize + 1)
)

data class PageDTO<T>(
        val data: List<T>,
        val pageSize: Int,
        val pageNum: Int,
        val totalNum: Int,
        val pages: Int = (if (totalNum % pageSize == 0) totalNum / pageSize else totalNum / pageSize + 1)
)