package org.qing.learning;

import java.util.Arrays;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.Filter;
import org.apache.beam.sdk.transforms.FlatMapElements;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TypeDescriptors;

public class MinimalWordCount {
	public static void main(String[] args) {
		PipelineOptions options = PipelineOptionsFactory.create();
		Pipeline p = Pipeline.create(options);
		p.apply(TextIO.read().from("gs://apache-beam-samples/shakespeare/*"))
			.apply(
				FlatMapElements
					.into(TypeDescriptor.strings())
					.via((String word) -> Arrays.asList(word.split("[^\\p{L}]+")))
			)
			.apply(
				Filter.by((String word) -> !word.isEmpty())
			)
			.apply(
				Count.perElement()	
			)
			.apply(
				MapElements.into(TypeDescriptors.strings())
					.via(
						(KV<String, Long> wordCount) -> 
							wordCount.getKey() + ": " + wordCount.getValue()
					)
			)
			.apply(TextIO.write().to("wordcounts"));
		p.run().waitUntilFinish();
	}
}
