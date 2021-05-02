## Hadoop Practice: NO2 Air Quality Castilla y León.
<p align="center">
  <img width="auto" height="auto" src="https://intellitech.pro/wp-content/uploads/2016/12/hadoop-300x293.png">
</p>

### Input file
Input file where data was taken: http://datosabiertos.jcyl.es/web/jcyl/set/es/mediciones/calidad_aire_historico/1284212629698

### Compilation and execution
Runing the program: 
  - mvn package
  - mvn compile
  - hadoop jar hadoop-PracticeNO2.jar -conf ./conf/hadoop-local.xml ./input/ ./output
  
### Output file
It will contain information about the different values of NO2 in the province capitals of Castilla y León
