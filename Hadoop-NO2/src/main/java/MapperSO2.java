import java.io.IOException;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MapperSO2 extends Mapper<Object, Text, Text, DoubleWritable> {

	/**
	 * 
	 * The different fields on the input file are the following ones:
	 * 
	 * Fecha;CO (mg/m3);NO (ug/m3);NO2 (ug/m3);O3 (ug/m3);PM10 (ug/m3); PM25
	 * (ug/m3);SO2 (ug/m3);Provincia;Estación;Latitud;Longitud
	 * 
	 */

	private String[] fields = null;
	private String no2 = null;
	private String province = null;

	public void map(Object key, Text field, Context context) throws IOException, InterruptedException {
		fields = field.toString().split(";");

		/**
		 * On fields[3] there are the NO2 values 
		 * On fields[8] there are the Province values
		 */

		no2 = fields[3].trim();
		province = fields[8].trim();
		
		/**
		 * We also check the chosen field is indeed a number
		 */
		if (NumberUtils.isCreatable(no2.toString())) {
			context.write(new Text(province), new DoubleWritable(NumberUtils.toDouble(no2)));
		}
	}
}
