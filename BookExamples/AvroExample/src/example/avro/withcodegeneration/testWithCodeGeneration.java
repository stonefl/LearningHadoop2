package example.avro.withcodegeneration;

import java.io.File;
import java.io.IOException;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
/**
 * Before this step it needs to:<br>
 * 1) create User class through the 'avro-tools.jar' following the instructions at http://avro.apache.org/docs/current/gettingstartedjava.html<br>
 * 2) add 'avro.jar', 'jackson-core-asl.jar', and 'jackson-mapper-asl.jar' from '/usr/lib/avro' into the java build path
 * Note: command 'java -jar /usr/lib/avro/avro-tools.jar compile schema user.avsc ./src' is used. The newest version of avro-tools.jar could also be used here
 */
public class testWithCodeGeneration {
	/**
	 * @param args
	 * @throws IOException 
	 */

	public static void main(String[] args) throws IOException {
		// Step 1: Create some users through different methods
		User user1 = new User();
		user1.setName("Alyssa");
		user1.setFavoriteNumber(256);
		//leave favorite color null
		
		//alternate constructor
		User user2 = new User("Ben", 7, "red");
		
		//Construct via builder
		User user3 = User.newBuilder()
						.setName("Charlie")
						.setFavoriteColor("blue")
						.setFavoriteNumber(null)
						.build();
		
		// Step 2: Serializing user1, user2, and user3 to disk
		File file = new File("users.avro");
		DatumWriter<User> userDatumWriter = new SpecificDatumWriter<User>(User.class);
		DataFileWriter<User> dataFileWriter = new DataFileWriter<User>(userDatumWriter);
		dataFileWriter.create(user1.getSchema(), file);
		dataFileWriter.append(user1);
		dataFileWriter.append(user2);
		dataFileWriter.append(user3);
		dataFileWriter.close();
		
		//Step 3: deserialize Users from disk
		DatumReader<User> userDatumReader = new SpecificDatumReader<User>(User.class);
		DataFileReader<User> dataFileReader = new DataFileReader<User>(file, userDatumReader);
		User user = null;
		while(dataFileReader.hasNext()){
			// Reuse user object by passing it to next(). This saves us from
			// allocating and garbage collecting many objects for files with
			// many items.
			user = dataFileReader.next(user);
			System.out.println(user);
		}

	}

}
