package org.molgenis.vipweb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VcfCreatorTest {
  private VcfCreator vcfCreator;

  @BeforeEach
  void setUp() {
    vcfCreator = new VcfCreator();
  }

  @Test
  void create() {
    String expectedVcf =
        """
                        ##fileformat=VCFv4.2
                        ##contig=<ID=chr1,length=248956422>
                        ##contig=<ID=chr2,length=242193529>
                        ##contig=<ID=chr3,length=198295559>
                        ##contig=<ID=chr4,length=190214555>
                        ##contig=<ID=chr5,length=181538259>
                        ##contig=<ID=chr6,length=170805979>
                        ##contig=<ID=chr7,length=159345973>
                        ##contig=<ID=chr8,length=145138636>
                        ##contig=<ID=chr9,length=138394717>
                        ##contig=<ID=chr10,length=133797422>
                        ##contig=<ID=chr11,length=135086622>
                        ##contig=<ID=chr12,length=133275309>
                        ##contig=<ID=chr13,length=114364328>
                        ##contig=<ID=chr14,length=107043718>
                        ##contig=<ID=chr15,length=101991189>
                        ##contig=<ID=chr16,length=90338345>
                        ##contig=<ID=chr17,length=83257441>
                        ##contig=<ID=chr18,length=80373285>
                        ##contig=<ID=chr19,length=58617616>
                        ##contig=<ID=chr20,length=64444167>
                        ##contig=<ID=chr21,length=46709983>
                        ##contig=<ID=chr22,length=50818468>
                        ##contig=<ID=chrX,length=156040895>
                        ##contig=<ID=chrY,length=57227415>
                        ##contig=<ID=chrM,length=16569>
                        ##FORMAT=<ID=GT,Number=1,Type=String,Description="Genotype">
                        #CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\tSAMPLE
                        chr1\t27358\t.\tGC\tC\t.\t.\t.\tGT\t1|1
                        chr1\t37358\t.\tGC\tC\t.\t.\t.\tGT\t1|1
                        chr14\t74527358\t.\tG\tA\t.\t.\t.\tGT\t1|1
                        """;
    assertEquals(expectedVcf, vcfCreator.create("1-37358-GC-C\n14-74527358-G-A\n1-27358-GC-C"));
  }

  @Test
  void createInvalidFormat() {
    assertThrows(VcfParseException.class, () -> vcfCreator.create("14 74527358 G>A"));
  }

  @Test
  void createInvalidChromosome() {
    assertThrows(VcfParseException.class, () -> vcfCreator.create("23-37358-GC-C"));
  }

  @Test
  void createInvalidPosition() {
    assertThrows(VcfParseException.class, () -> vcfCreator.create("1-invalid-GC-C"));
  }

  @Test
  void createInvalidReferenceBases() {
    assertThrows(VcfParseException.class, () -> vcfCreator.create("1-37358-XY-C"));
  }

  @Test
  void createInvalidAlternateBases() {
    assertThrows(VcfParseException.class, () -> vcfCreator.create("1-37358-GC-Z"));
  }
}
