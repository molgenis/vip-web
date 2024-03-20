package org.molgenis.vipweb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.vipweb.model.constants.AffectedStatus;
import org.molgenis.vipweb.model.constants.Assembly;
import org.molgenis.vipweb.model.constants.SequencingMethod;
import org.molgenis.vipweb.model.constants.Sex;
import org.molgenis.vipweb.model.dto.*;

class SampleSheetGeneratorTest {

    private SampleSheetGenerator sampleSheetGenerator;

    @BeforeEach
    void setUp() {
        sampleSheetGenerator = new SampleSheetGenerator();
    }

    @Test
    void generate() {
        String filename = "my.vcf";
        FileDto fileDto = when(mock(FileDto.class).getFilename()).thenReturn(filename).getMock();
        VcfDto vcfDto = when(mock(VcfDto.class).getFile()).thenReturn(fileDto).getMock();

        HpoTermDto hpoTerm0 = when(mock(HpoTermDto.class).getTerm()).thenReturn("HP:0000445").getMock();
        HpoTermDto hpoTerm1 = when(mock(HpoTermDto.class).getTerm()).thenReturn("HP:0000348").getMock();
        SampleDto sampleChild =
                SampleDto.builder()
                        .individualId("child")
                        .paternalId("father")
                        .maternalId("mother")
                        .proband(true)
                        .sex(Sex.UNKNOWN)
                        .affected(AffectedStatus.TRUE)
                        .hpoTerms(List.of(hpoTerm0, hpoTerm1))
                        .build();

        SampleDto sampleFather =
                SampleDto.builder()
                        .individualId("father")
                        .sex(Sex.MALE)
                        .affected(AffectedStatus.FALSE)
                        .hpoTerms(List.of())
                        .build();
        SampleDto sampleMother =
                SampleDto.builder()
                        .individualId("mother")
                        .sex(Sex.FEMALE)
                        .affected(AffectedStatus.UNKNOWN)
                        .hpoTerms(List.of())
                        .build();

        JobDto jobDto = mock(JobDto.class);
        when(jobDto.getVcf()).thenReturn(vcfDto);
        when(jobDto.getSequencingMethod()).thenReturn(SequencingMethod.WES);
        when(jobDto.getAssembly()).thenReturn(Assembly.GRCh38);
        when(jobDto.getSamples()).thenReturn(List.of(sampleChild, sampleFather, sampleMother));

        String expectedSampleSheet =
                """
                        project_id\tfamily_id\tindividual_id\tpaternal_id\tmaternal_id\tsex\taffected\tproband\thpo_ids\tsequencing_method\tassembly\tvcf
                        vip\tfam\tchild\tfather\tmother\t\ttrue\ttrue\tHP:0000445,HP:0000348\tWES\tGRCh38\tdata/my.vcf
                        vip\tfam\tfather\t\t\tmale\tfalse\t\t\tWES\tGRCh38\tdata/my.vcf
                        vip\tfam\tmother\t\t\tfemale\t\t\t\tWES\tGRCh38\tdata/my.vcf
                        """;
        assertEquals(expectedSampleSheet, sampleSheetGenerator.generate(jobDto));
    }
}
