Para ejecutar los algoritmos deben configurarse los parámetros del archivo config.txt, teniendo en cuenta que para que puedan ser leídos por el programa debe haber una separación de un espacio. Un ejemplo del formato del archivo config.txt sería el siguiente:

Semilla: 12345678 23456781 34567812 45678123
Archivos: ch130.tsp pr144.tsp a280.tsp u1060.tsp d18512.tsp
logs: true
Algoritmo: TABU_2
k: 5
nIteraciones: 5000
inicioEntorno: 8
disminucionEntornoPorcentaje: 10
progresionDisminucionEntorno: 10
movimientosEmpeoramiento: 5
tenenciaTabu: 10
oscilamiento: 40

El programa se ejecutará mediante el .jar, para ello se debe escribir en la terminal (o símbolo del sistema si se utiliza Windows), desde el directorio donde se encuentran los archivos del programa un comando similar al siguiente teniendo en cuenta los directorios donde se hayan el ejecutable .jar y el archivo de texto config.txt:
java -jar Voraz-Grupo1-3.jar src/config.txt1