package org.molgenis.vipweb.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipweb.FileUploadUtils;
import org.molgenis.vipweb.VcfParseException;
import org.molgenis.vipweb.model.Error;
import org.molgenis.vipweb.model.dto.FileCreateDto;
import org.molgenis.vipweb.model.dto.VcfCreateDto;
import org.molgenis.vipweb.model.dto.VcfDto;
import org.molgenis.vipweb.service.VcfService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vcf")
@RequiredArgsConstructor
public class VcfController implements ApiController {
    private final VcfService vcfService;

    /**
     * curl friendly endpoint to upload vcf files, e.g.
     *
     * <pre>curl --upload-file variants.vcf <host>/api/vcf/${filename}</pre>
     */
    @PutMapping("/{filename}")
    public VcfDto uploadVcf(
            HttpServletRequest httpServletRequest,
            @PathVariable("filename") String filename,
            @RequestParam(value = "public", required = false, defaultValue = "false") boolean isPublic) {
        FileCreateDto fileCreateDto =
                FileUploadUtils.fromRequest(httpServletRequest, filename, isPublic);
        return vcfService.upload(fileCreateDto);
    }

    @PostMapping
    public VcfDto createVcf(@RequestBody VcfCreateDto vcf) {
        return vcfService.create(vcf);
    }

    @GetMapping("/{id}")
    public VcfDto getVcfById(@PathVariable("id") Integer id) {
        return vcfService.getVcfById(id);
    }

    @GetMapping
    public Page<VcfDto> getVcfs() {
        Pageable pageable = PageRequest.of(0, 20);
        return vcfService.getVcfs(pageable);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVcf(@PathVariable("id") Integer id) {
        vcfService.deleteVcf(id);
    }

    @ExceptionHandler(value = VcfParseException.class)
    private ResponseEntity<Error> handleVcfParseException(VcfParseException vcfParseException) {
        return new ResponseEntity<>(Error.from(vcfParseException.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
