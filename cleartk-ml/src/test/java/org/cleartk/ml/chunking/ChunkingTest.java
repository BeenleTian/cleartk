/* 
 * Copyright (c) 2012, Regents of the University of Colorado 
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
package org.cleartk.ml.chunking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.uima.fit.util.JCasUtil;
import org.cleartk.ml.chunking.BiesoChunking;
import org.cleartk.ml.chunking.BioChunking;
import org.cleartk.ml.chunking.IoChunking;
import org.cleartk.test.util.DefaultTestBase;
import org.cleartk.test.util.type.Chunk;
import org.cleartk.test.util.type.Token;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for classes in the chunking package.
 * 
 * <br>
 * Copyright (c) 2012, Regents of the University of Colorado <br>
 * All rights reserved.
 * 
 * @author Steven Bethard
 */
public class ChunkingTest extends DefaultTestBase {

  @Test
  public void testIOChunkingCreateOutcomes() throws Exception {
    this.tokenBuilder.buildTokens(this.jCas, "The quick brown fox jumped over the lazy dog");
    List<Token> tokens = new ArrayList<Token>(JCasUtil.select(this.jCas, Token.class));
    // "quick brown"
    Chunk foo = new Chunk(this.jCas, tokens.get(1).getBegin(), tokens.get(2).getEnd());
    foo.setChunkType("foo");
    foo.addToIndexes();
    // "fox"
    Chunk bar = new Chunk(this.jCas, tokens.get(3).getBegin(), tokens.get(3).getEnd());
    bar.setChunkType("bar");
    bar.addToIndexes();
    // " lazy dog"
    Chunk bar2 = new Chunk(this.jCas, tokens.get(7).getBegin() - 1, tokens.get(8).getEnd());
    bar2.setChunkType("bar");
    bar2.addToIndexes();
    List<Chunk> chunks = Arrays.asList(foo, bar, bar2);

    IoChunking<Token, Chunk> chunking;
    List<String> expected;
    List<String> actual;

    chunking = new IoChunking<Token, Chunk>(Token.class, Chunk.class, "chunkType");
    expected = Arrays.asList("O", "I-foo", "I-foo", "I-bar", "O", "O", "O", "I-bar", "I-bar");
    actual = chunking.createOutcomes(this.jCas, tokens, chunks);
    Assert.assertEquals(expected, actual);

    chunking = new IoChunking<Token, Chunk>(Token.class, Chunk.class, null);
    expected = Arrays.asList("O", "I", "I", "I", "O", "O", "O", "I", "I");
    actual = chunking.createOutcomes(this.jCas, tokens, chunks);
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testBIOChunkingCreateOutcomes() throws Exception {
    this.tokenBuilder.buildTokens(this.jCas, "The quick brown fox jumped over the lazy dog");
    List<Token> tokens = new ArrayList<Token>(JCasUtil.select(this.jCas, Token.class));
    // "quick brown"
    Chunk foo = new Chunk(this.jCas, tokens.get(1).getBegin(), tokens.get(2).getEnd());
    foo.setChunkType("foo");
    foo.addToIndexes();
    // "fox"
    Chunk bar = new Chunk(this.jCas, tokens.get(3).getBegin(), tokens.get(3).getEnd());
    bar.setChunkType("bar");
    bar.addToIndexes();
    // " lazy dog" - note the space before and the -1 in the expression below
    Chunk bar2 = new Chunk(this.jCas, tokens.get(7).getBegin() - 1, tokens.get(8).getEnd());
    bar2.setChunkType("bar");
    bar2.addToIndexes();
    List<Chunk> chunks = Arrays.asList(foo, bar, bar2);

    BioChunking<Token, Chunk> chunking;
    List<String> expected;
    List<String> actual;

    chunking = new BioChunking<Token, Chunk>(Token.class, Chunk.class, "chunkType");
    expected = Arrays.asList("O", "B-foo", "I-foo", "B-bar", "O", "O", "O", "B-bar", "I-bar");
    actual = chunking.createOutcomes(this.jCas, tokens, chunks);
    Assert.assertEquals(expected, actual);

    chunking = new BioChunking<Token, Chunk>(Token.class, Chunk.class);
    expected = Arrays.asList("O", "B", "I", "B", "O", "O", "O", "B", "I");
    actual = chunking.createOutcomes(this.jCas, tokens, chunks);
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testBiesoChunkingCreateOutcomes() throws Exception {
    this.tokenBuilder.buildTokens(this.jCas, "The quick brown fox jumped over the lazy dog ");
    List<Token> tokens = new ArrayList<Token>(JCasUtil.select(this.jCas, Token.class));
    // "The quick brown"
    Chunk foo = new Chunk(this.jCas, tokens.get(0).getBegin(), tokens.get(2).getEnd());
    foo.setChunkType("foo");
    foo.addToIndexes();
    // "fox"
    Chunk bar = new Chunk(this.jCas, tokens.get(3).getBegin(), tokens.get(3).getEnd());
    bar.setChunkType("bar");
    bar.addToIndexes();
    // " lazy dog "
    Chunk bar2 = new Chunk(this.jCas, tokens.get(7).getBegin() - 1, tokens.get(8).getEnd() + 1);
    bar2.setChunkType("bar");
    bar2.addToIndexes();
    List<Chunk> chunks = Arrays.asList(foo, bar, bar2);

    BiesoChunking<Token, Chunk> chunking;
    List<String> expected;
    List<String> actual;

    chunking = new BiesoChunking<Token, Chunk>(Token.class, Chunk.class, "chunkType");
    expected = Arrays.asList("B-foo", "I-foo", "E-foo", "S-bar", "O", "O", "O", "B-bar", "E-bar");
    actual = chunking.createOutcomes(this.jCas, tokens, chunks);
    Assert.assertEquals(expected, actual);

    chunking = new BiesoChunking<Token, Chunk>(Token.class, Chunk.class);
    expected = Arrays.asList("B", "I", "E", "S", "O", "O", "O", "B", "E");
    actual = chunking.createOutcomes(this.jCas, tokens, chunks);
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testIOChunkingCreateChunks() throws Exception {
    this.tokenBuilder.buildTokens(this.jCas, "The quick brown fox jumped over the lazy dog");
    List<Token> tokens = new ArrayList<Token>(JCasUtil.select(this.jCas, Token.class));

    IoChunking<Token, Chunk> chunking;
    List<String> outcomes;
    List<Chunk> chunks;
    List<String> expectedTexts;
    List<String> expectedTypes;

    chunking = new IoChunking<Token, Chunk>(Token.class, Chunk.class, "chunkType");
    outcomes = Arrays.asList("O", "I-foo", "I-foo", "I-bar", "O", "O", "O", "I-bar", "I-bar");
    expectedTexts = Arrays.asList("quick brown", "fox", "lazy dog");
    expectedTypes = Arrays.asList("foo", "bar", "bar");
    chunks = chunking.createChunks(this.jCas, tokens, outcomes);
    Assert.assertEquals(expectedTexts, JCasUtil.toText(chunks));
    Assert.assertEquals(expectedTypes, getChunkTypes(chunks));

    chunking = new IoChunking<Token, Chunk>(Token.class, Chunk.class);
    outcomes = Arrays.asList("O", "I", "I", "I", "O", "O", "O", "I", "I");
    expectedTexts = Arrays.asList("quick brown fox", "lazy dog");
    expectedTypes = Arrays.asList(null, null);
    chunks = chunking.createChunks(this.jCas, tokens, outcomes);
    Assert.assertEquals(expectedTexts, JCasUtil.toText(chunks));
    Assert.assertEquals(expectedTypes, getChunkTypes(chunks));
  }

  @Test
  public void testBIOChunkingCreateChunks() throws Exception {
    this.tokenBuilder.buildTokens(this.jCas, "The quick brown fox jumped over the lazy dog");
    List<Token> tokens = new ArrayList<Token>(JCasUtil.select(this.jCas, Token.class));

    BioChunking<Token, Chunk> chunking;
    List<String> outcomes;
    List<Chunk> chunks;
    List<String> expectedTexts;
    List<String> expectedTypes;

    chunking = new BioChunking<Token, Chunk>(Token.class, Chunk.class, "chunkType");
    outcomes = Arrays.asList("O", "B-foo", "I-foo", "B-bar", "O", "O", "O", "I-bar", "I-bar");
    expectedTexts = Arrays.asList("quick brown", "fox", "lazy dog");
    expectedTypes = Arrays.asList("foo", "bar", "bar");
    chunks = chunking.createChunks(this.jCas, tokens, outcomes);
    Assert.assertEquals(expectedTexts, JCasUtil.toText(chunks));
    Assert.assertEquals(expectedTypes, getChunkTypes(chunks));

    chunking = new BioChunking<Token, Chunk>(Token.class, Chunk.class);
    outcomes = Arrays.asList("O", "B", "I", "B", "O", "O", "O", "I", "I");
    expectedTexts = Arrays.asList("quick brown", "fox", "lazy dog");
    expectedTypes = Arrays.asList(null, null, null);
    chunks = chunking.createChunks(this.jCas, tokens, outcomes);
    Assert.assertEquals(expectedTexts, JCasUtil.toText(chunks));
    Assert.assertEquals(expectedTypes, getChunkTypes(chunks));
  }

  @Test
  public void testBiesoChunkingCreateChunks() throws Exception {
    this.tokenBuilder.buildTokens(this.jCas, "The quick brown fox jumped over the lazy dog");
    List<Token> tokens = new ArrayList<Token>(JCasUtil.select(this.jCas, Token.class));

    BiesoChunking<Token, Chunk> chunking;
    List<String> outcomes;
    List<Chunk> chunks;
    List<String> expectedTexts;
    List<String> expectedTypes;

    chunking = new BiesoChunking<Token, Chunk>(Token.class, Chunk.class, "chunkType");
    outcomes = Arrays.asList("B-foo", "I-foo", "E-foo", "S-bar", "O", "O", "O", "I-bar", "I-bar");
    expectedTexts = Arrays.asList("The quick brown", "fox", "lazy dog");
    expectedTypes = Arrays.asList("foo", "bar", "bar");
    chunks = chunking.createChunks(this.jCas, tokens, outcomes);
    Assert.assertEquals(expectedTexts, JCasUtil.toText(chunks));
    Assert.assertEquals(expectedTypes, getChunkTypes(chunks));

    chunking = new BiesoChunking<Token, Chunk>(Token.class, Chunk.class);
    outcomes = Arrays.asList("B", "I", "E", "B", "O", "O", "O", "B", "E");
    expectedTexts = Arrays.asList("The quick brown", "fox", "lazy dog");
    expectedTypes = Arrays.asList(null, null, null);
    chunks = chunking.createChunks(this.jCas, tokens, outcomes);
    Assert.assertEquals(expectedTexts, JCasUtil.toText(chunks));
    Assert.assertEquals(expectedTypes, getChunkTypes(chunks));
  }

  private static List<String> getChunkTypes(List<Chunk> chunks) {
    List<String> types = new ArrayList<String>();
    for (Chunk chunk : chunks) {
      types.add(chunk.getChunkType());
    }
    return types;
  }
}
