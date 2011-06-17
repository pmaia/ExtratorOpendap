/**
 *Neste subpacote está a lógica responsável pela interpolação dos dados. Há duas implementações, uma para quando a interpolação é realizada 
 *com arquivos NetCDFs locais e outra para Interpolação via OPeNDAP. A diferença é que InterpoladorNetCDF possui algumas vantagens, pois lida 
 *com um arquivo local, enquanto InterpoladorOPeNDAP tem de lidar com as limitações impostas pelo protocolo OPeNDAP. Essencialmente, para a 
 *aplicação apenas a interpolação via OPeNDAP é importante. A vantagem da hierarquia construída, através da Interface InterpoladorIF, é 
 *permitir facilmente a adição de novas estratégias de interpolação de dados, não ficando restrita à interpolação bilinear  que foi utilizada.  
 */
package br.edu.ufcg.lsd.seghidro.extratoropendap.interpolador;