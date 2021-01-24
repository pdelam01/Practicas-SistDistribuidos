# Hadoop Practice: NO2 Air Quality Castilla y León.

Input file where data was taken: http://datosabiertos.jcyl.es/web/jcyl/set/es/mediciones/calidad_aire_historico/1284212629698

Runing the program:
  -mvn package
  -mvn compile
  -hadoop jar hadoop-PracticeNO2.jar -conf ./conf/hadoop-local.xml ./input/ ./output
