package com.star.controller;

import com.star.common.Result;
import com.star.model.vo.ChartVO;
import com.star.service.IViewsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 可视化接口
 */
@RestController
@RequestMapping(value = "/views")
public class ViewsController {

    @Resource
    private IViewsService viewsService;

    /**
     * 统计一些系统的基础数据
     *
     * @return Result<List < ChartVO>>
     */
    @GetMapping("/staticControls")
    public Result<List<ChartVO>> staticControls() {
        return viewsService.staticControls();
    }

}
