package org.molgenis.vipweb.controller;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipweb.model.constants.FilterTreeType;
import org.molgenis.vipweb.model.dto.FilterTreeDto;
import org.molgenis.vipweb.service.FilterTreeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/filtertree")
@RequiredArgsConstructor
public class FilterTreeController implements ApiController {
    private final FilterTreeService filterTreeService;

    @GetMapping
    public Page<FilterTreeDto> getFilterTrees(@RequestParam FilterTreeType t) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("name"));
        return filterTreeService.getFilterTrees(t, pageable);
    }

    @GetMapping("/default")
    public FilterTreeDto getDefaultFilterTree(@RequestParam FilterTreeType t) {
        return filterTreeService.getDefaultFilterTree(t);
    }
}
