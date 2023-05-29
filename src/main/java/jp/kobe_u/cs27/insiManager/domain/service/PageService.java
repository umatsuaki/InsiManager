package jp.kobe_u.cs27.insiManager.domain.service;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public class PageService {

    @Transactional(readOnly = true)
    public Page<Bean> getPage(Pageable pageable) {
        List<Bean> list = dao.getList(pageable.getOffset(), pageable.getPageSize());
        int count = dao.getCount();
        return new PageImpl<Bean>(list, pageable, count);
    }
}
