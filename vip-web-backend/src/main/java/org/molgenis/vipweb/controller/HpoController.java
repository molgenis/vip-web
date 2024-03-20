package org.molgenis.vipweb.controller;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipweb.model.dto.HpoTermDto;
import org.molgenis.vipweb.service.HpoService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hpo")
@RequiredArgsConstructor
public class HpoController implements ApiController {
    private final HpoService hpoService;

    @GetMapping
    public Page<HpoTermDto> getHpoTerms(@RequestParam String q) {
        return hpoService.getHpoTerms(q);
    }
}
