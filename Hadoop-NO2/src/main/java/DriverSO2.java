//DriverSO2 Application to find the maximum SO2
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class DriverSO2 extends Configured implements Tool {

	@Override
	public int run(String[] args) throws Exception {
		if (args.length != 2) {
			System.err.printf("Error! The required params are: {input file} {output dir}");
			System.exit(-1);
		}

		Job job = new Job(getConf(), "Mean NO2");
		job.setJarByClass(getClass());

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		/**
		 * We set the Mapper, Reducer, and Combiner classes
		 */
		job.setMapperClass(MapperSO2.class);
		job.setCombinerClass(ReducerSO2.class);
		job.setReducerClass(ReducerSO2.class);

		/**
		 * We specify the output key and value classes
		 */
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(DoubleWritable.class);

		/**
		 * We specify the output key and value classes of the mapper, so they match with
		 * the input values of the reducer
		 */
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(DoubleWritable.class);

		return job.waitForCompletion(true) ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new DriverSO2(), args);
		System.exit(exitCode);
	}
}
