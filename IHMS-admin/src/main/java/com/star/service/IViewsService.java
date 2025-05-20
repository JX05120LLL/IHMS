package com.star.service;

import com.star.common.Result;
import com.star.model.vo.ChartVO;

import java.util.List;

public interface IViewsService {

    Result<List<ChartVO>> staticControls();

}
