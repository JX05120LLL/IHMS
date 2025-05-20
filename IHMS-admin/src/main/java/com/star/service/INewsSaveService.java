package com.star.service;

import com.star.common.Result;
import com.star.model.dto.query.extend.NewsSaveQueryDto;
import com.star.model.entity.NewsSave;
import com.star.model.vo.NewsSaveVO;

import java.util.List;

public interface INewsSaveService {

    Result<Void> save(NewsSave newsSave);

    Result<Void> batchDelete(List<Long> ids);

    Result<List<NewsSaveVO>> query(NewsSaveQueryDto newsSaveQueryDto);

    Result<Void> operation(NewsSave newsSave);


}
