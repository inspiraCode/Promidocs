# Promidocs
Proceso de minado de documentos.

# REQUERIMIENTO
* Habilidad para transferir imágenes escaneadas a un servicio que lea e intuya diferentes campos dentro del pedimiento.
* Debe tomar en cuenta diferentes formatos y leer campos específicos.
* Debe entregar un archivo que contenga información relevante para los nuevos sistemas de pedimentos digitales.
* Debe proveer un procedimiento para digitalizar aquellos pedimentos que no pudieron ser leídos automáticamente.
* Debe proveer un procedimiento de validación para los pedimentos que fueron leídos.

# PROPUESTA DE DIGITALIZACIÓN DE PEDIMENTOS
* El cliente escanea los documentos con características específicas para facilitar su lectura.
* El cliente graba las imágenes escaneadas en un lugar específico de su sistema de archivos.
* Inspiracode levanta las imágenes e intenta leerlas
* Todo lo que los componentes de Inspiracode puedan leer lo entregan digitalizado en el formato requerido
* Todo lo que los componentes de Inspiracode <b>NO</b> puedan leer lo entregan para su captura en un programa que facilite al usuario realizar esta operación (solo aquellos campos que los componentes automáticos no pudieron leer)

# COMPONENTES QUE INFLUYEN EN LA SOLUCIÓN

* <b>Zona de despegue del cliente</b>: Carpeta que contiene las imagenes escaneadas.
* <b>Impulsor de documentos</b>: Elemento que carga los documentos del cliente en el ambiente de digitalización
* <b>Zona de aterrizaje del cliente</b>: Carpeta que recibe los archivos de texto con los datos leídos de las imágenes procesadas.
* <b>Zona de aterrizaje de PROMIDOCS</b>: Carpeta que recibe las imagenes escaneadas del cliente.
* <b>Zona de despegue de PROMIDOCS</b>: Carpeta que contiene los archivos de texto con los datos leídos de las imágenes procesadas.
* <b>OCRator</b>: Elemento que se encarga de recoger las imágenes de la <b>zona de aterrizaje de PROMIDOCS</b> y enviarlas a los diferentes <b>proveedores de OCR</b>.
* <b>Oráculo</b>: Elemento que se encarga de validar la lectura contra los requerimientos de los <b>campos de datos</b>. El <b>Oráculo</b> puede devolver los documentos al <b>OCRator</b> para un segundo intento de lectura, puede entregar los documentos a la <b>Zona de despegue de PROMIDOCS</b> o los puede asignar para captura manual en la aplicación <b>MANDator</b>.
* <b>MANDator</b>: Aplicación de captura manual, recibe imágenes para ser capturadas por usuarios por fallas en la lectura realizada por <b>OCRator</b> o como muestréo de auditoría de calidad.
* <b>Proveedores de OCR</b>: PROMIDOCS puede conectarse a uno o más proveedores de servicio de OCR sobre imágenes escaneadas para su lectura.

.
