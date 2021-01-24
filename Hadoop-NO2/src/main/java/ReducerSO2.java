
import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class ReducerSO2 extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

	public void reduce(Text key, Iterable<DoubleWritable> fields, Context context)
			throws IOException, InterruptedException {

		int num = 0;
		double totalSO2 = 0.0;
		double mean = 0.0;

		/**
		 * We loop through the values of the samples taken
		 */
		for (DoubleWritable field : fields) {
			totalSO2 += field.get();
			num++;
		}

		mean = totalSO2 / num;

		context.write(key, new DoubleWritable(mean));
	}
}
