/*
 * Copyright (c) 2010-2013, Regents of the University of Colorado 
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
package org.cleartk.ml.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import org.apache.uima.fit.factory.UimaContextFactory;
import org.cleartk.ml.Feature;
import org.cleartk.ml.Instance;
import org.cleartk.ml.jar.DefaultDataWriterFactory;
import org.cleartk.ml.jar.DirectoryDataWriterFactory;
import org.cleartk.ml.jar.JarClassifierBuilder;
import org.cleartk.ml.script.ExampleInstanceFactory.StringAnnotator;
import org.cleartk.test.util.DefaultTestBase;
import org.junit.Assert;
import org.junit.Test;

/**
 * <br>
 * Copyright (c) 2010-2013, Regents of the University of Colorado <br>
 * All rights reserved.
 */

public class ScriptTest extends DefaultTestBase {

  @Test
  public void testScript() throws Exception {

    // create the data writer
    StringAnnotator annotator = new StringAnnotator();
    annotator.initialize(UimaContextFactory.createUimaContext(DirectoryDataWriterFactory.PARAM_OUTPUT_DIRECTORY,
        this.outputDirectoryName, DefaultDataWriterFactory.PARAM_DATA_WRITER_CLASS_NAME,
        ScriptStringOutcomeDataWriter.class.getName(), ScriptStringOutcomeDataWriter.PARAM_SCRIPT_DIRECTORY,
        "scripts/test/"));

    // run process to produce a bunch of instances
    annotator.process(null);

    annotator.collectionProcessComplete();

    // check that the output files were written for each class
    BufferedReader reader = new BufferedReader(
        new FileReader(new File(this.outputDirectoryName, "training-data.libsvm")));
    Assert.assertTrue(reader.readLine().length() > 0);
    reader.close();

    // run the training command
    JarClassifierBuilder.trainAndPackage(this.outputDirectory);

    reader = new BufferedReader(new FileReader(new File(this.outputDirectoryName, "script.model")));
    Assert.assertTrue(reader.readLine().trim().equals("test"));
    reader.close();

    // read in the classifier and test it on new instances
    ScriptStringOutcomeClassifierBuilder builder = new ScriptStringOutcomeClassifierBuilder();
    ScriptStringOutcomeClassifier classifier;
    classifier = builder.loadClassifierFromTrainingDirectory(this.outputDirectory);
    for (Instance<String> instance : ExampleInstanceFactory.generateStringInstances(1000)) {
      List<Feature> features = instance.getFeatures();
      String encoded = classifier.featuresToString(features);
      Assert.assertTrue(encoded.equals(classifier.classify(features)));
    }
  }
}
