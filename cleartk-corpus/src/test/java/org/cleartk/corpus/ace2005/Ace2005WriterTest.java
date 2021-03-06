/** 
 * Copyright (c) 2007-2008, Regents of the University of Colorado 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * Neither the name of the University of Colorado at Boulder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE. 
 */
package org.cleartk.corpus.ace2005;

import java.io.File;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.FileUtils;
import org.cleartk.ne.type.Ace2005Document;
import org.cleartk.ne.type.Chunk;
import org.cleartk.ne.type.NamedEntity;
import org.cleartk.ne.type.NamedEntityMention;
import org.cleartk.test.util.DefaultTestBase;
import org.cleartk.util.ViewUriUtil;
import org.junit.Assert;
import org.junit.Test;
import org.apache.uima.fit.factory.AnalysisEngineFactory;

/**
 * <br>
 * Copyright (c) 2007-2008, Regents of the University of Colorado <br>
 * All rights reserved.
 */

public class Ace2005WriterTest extends DefaultTestBase {

  @Test
  public void testMissingParameters() throws Exception {
    try {
      AnalysisEngineFactory.createEngine(Ace2005Writer.class);
      Assert.fail("expected exception with output directory not specified");
    } catch (ResourceInitializationException e) {
    }
  }

  @Test
  public void testOutputFile() throws Exception {
    AnalysisEngine engine = AnalysisEngineFactory.createEngine(
        Ace2005Writer.class,
        Ace2005Writer.PARAM_OUTPUT_DIRECTORY_NAME,
        this.outputDirectory.getPath());

    Ace2005Document document = new Ace2005Document(jCas);
    document.setAceSource("=source=");
    document.setAceType("=type=");
    document.setAceUri("uri.sgm");
    document.addToIndexes();

    jCas.setDocumentText("UCAR in North Boulder");

    Chunk ucarChunk = new Chunk(jCas, 0, 4);
    Chunk northBoulderChunk = new Chunk(jCas, 8, 21);
    Chunk boulderChunk = new Chunk(jCas, 14, 21);

    NamedEntityMention ucarMention = new NamedEntityMention(jCas, 0, 4);
    ucarMention.setAnnotation(ucarChunk);
    ucarMention.setHead(ucarChunk);
    ucarMention.setMentionType("=ORG=");
    NamedEntity ucarEntity = new NamedEntity(jCas);
    ucarEntity.setEntityClass("=ucar-class=");
    ucarEntity.setEntityId("=ucar-id=");
    ucarEntity.setEntitySubtype("=ucar-subtype=");
    ucarEntity.setEntityType("=ucar-type");
    ucarEntity.setMentions(new FSArray(jCas, 1));
    ucarEntity.setMentions(0, ucarMention);
    ucarMention.setMentionedEntity(ucarEntity);

    NamedEntityMention boulderMention = new NamedEntityMention(jCas, 14, 21);
    boulderMention.setAnnotation(northBoulderChunk);
    boulderMention.setHead(boulderChunk);
    boulderMention.setMentionType("=LOC=");
    NamedEntity boulderEntity = new NamedEntity(jCas);
    boulderEntity.setEntityClass("=boulder-class=");
    boulderEntity.setEntityId("=boulder-id=");
    boulderEntity.setEntitySubtype("=boulder-subtype=");
    boulderEntity.setEntityType("=boulder-type");
    boulderEntity.setMentions(new FSArray(jCas, 1));
    boulderEntity.setMentions(0, boulderMention);
    boulderMention.setMentionedEntity(boulderEntity);

    TOP[] items = new TOP[] {
        document,
        ucarChunk,
        northBoulderChunk,
        boulderChunk,
        ucarMention,
        ucarEntity,
        boulderMention,
        boulderEntity };
    for (TOP item : items) {
      item.addToIndexes();
    }

    ViewUriUtil.setURI(jCas, new File("uri.sgm").toURI());
    engine.process(jCas);
    engine.collectionProcessComplete();

    String expectedText = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "<source_file URI=\"uri.sgm\" SOURCE=\"=source=\" TYPE=\"=type=\">\n"
        + "  <document DOCID=\"uri\">\n"
        + "    <entity ID=\"0\" TYPE=\"=ucar-type\" SUBTYPE=\"=ucar-subtype=\" CLASS=\"=ucar-class=\">\n"
        + "      <entity_mention ID=\"1\" TYPE=\"=ORG=\">\n"
        + "        <extent>\n"
        + "          <charseq START=\"0\" END=\"3\">UCAR</charseq>\n"
        + "        </extent>\n"
        + "        <head>\n"
        + "          <charseq START=\"0\" END=\"3\">UCAR</charseq>\n"
        + "        </head>\n"
        + "      </entity_mention>\n"
        + "    </entity>\n"
        + "    <entity ID=\"2\" TYPE=\"=boulder-type\" SUBTYPE=\"=boulder-subtype=\" CLASS=\"=boulder-class=\">\n"
        + "      <entity_mention ID=\"3\" TYPE=\"=LOC=\">\n" + "        <extent>\n"
        + "          <charseq START=\"8\" END=\"20\">North Boulder</charseq>\n"
        + "        </extent>\n" + "        <head>\n"
        + "          <charseq START=\"14\" END=\"20\">Boulder</charseq>\n" + "        </head>\n"
        + "      </entity_mention>\n" + "    </entity>\n" + "  </document>\n" + "</source_file>\n");
    File outputFile = new File(this.outputDirectory, "uri.cleartk.xml");
    String actualText = FileUtils.file2String(outputFile).replace("\r", "");
    Assert.assertEquals(expectedText, actualText);
  }

  @Test
  public void testDescriptor() throws UIMAException {
    try {
      AnalysisEngineFactory.createEngine(Ace2005Writer.class);
      Assert.fail("expected exception with output directory not specified");
    } catch (ResourceInitializationException e) {
    }

    AnalysisEngine engine = AnalysisEngineFactory.createEngine(
        Ace2005Writer.class,
        Ace2005Writer.PARAM_OUTPUT_DIRECTORY_NAME,
        this.outputDirectory.getPath());
    Object outDirectory = engine.getConfigParameterValue(Ace2005Writer.PARAM_OUTPUT_DIRECTORY_NAME);
    Assert.assertEquals(this.outputDirectory.getPath(), outDirectory);

    engine.collectionProcessComplete();
  }
}
