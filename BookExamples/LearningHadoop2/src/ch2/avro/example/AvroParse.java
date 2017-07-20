package ch2.avro.example;

import java.io.File;
import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

/**
 * add 'avro.jar', 'jackson-core-asl.jar', and 'jackson-mapper-asl.jar' from '/usr/lib/avro' into the java build path<br>
 * command 'java -jar /usr/lib/avro/avro-tools.jar compile schema tweets_avro.avsc ./src' is used
 * @author cloudera
 *
 */

public class AvroParse {

	public static void main(String[] args) {
		testGenericRecord();
        testGeneratedCode();

	}

	private static void testGeneratedCode() {
		tweets_avro tweet = new tweets_avro();

        tweet.setText("The code generated tweet text");
        System.out.println(tweet.getText());

        try {
            File                        file        = new File("tweets.avro");
            DatumWriter<tweets_avro>    datumWriter = new SpecificDatumWriter<>(tweets_avro.class);
            DataFileWriter<tweets_avro> fileWriter  = new DataFileWriter<>(datumWriter);

            fileWriter.create(tweet.getSchema(), file);
            fileWriter.append(tweet);
            fileWriter.close();

            DatumReader<tweets_avro>    datumReader = new SpecificDatumReader<>(tweets_avro.class);
            DataFileReader<tweets_avro> fileReader  = new DataFileReader<>(file, datumReader);

            while (fileReader.hasNext()) {
                tweet = fileReader.next(tweet);
                System.out.println(tweet.getText());
            }
        } catch (IOException ie) {
            System.out.println("Error in parsing or writing files.");
        }

		
	}

	private static void testGenericRecord() {
		try {
			Schema schema = new Schema.Parser().parse(new File("tweets_avro.avsc"));
			GenericRecord tweet = new GenericData.Record(schema);
			
			tweet.put("text", "The generic tweet text");
			System.out.println(tweet.get("text"));
			
			File file = new File("tweets.avro");
			DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<GenericRecord>(schema);
			DataFileWriter<GenericRecord> fileWriter = new DataFileWriter<GenericRecord>(datumWriter);
			
			fileWriter.create(schema, file);
			fileWriter.append(tweet);
			fileWriter.close();
			
			DatumReader<GenericRecord>    datumReader  = new GenericDatumReader<GenericRecord>(schema);
            DataFileReader<GenericRecord> fileReader   = new DataFileReader(file, datumReader);
            GenericRecord                 genericTweet = null;
            
            while(fileReader.hasNext()){
            	genericTweet = fileReader.next(genericTweet);
            	for(Schema.Field field : genericTweet.getSchema().getFields()){
            		Object val = genericTweet.get(field.name());
            		if (val != null){
            			System.out.println(val);
            		}
            	}
            }

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
