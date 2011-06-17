#!/bin/bash
dataset="dods://150.165.126.97:8080/thredds/dodsC/test/MIMR_SRB1_1_pr-change_2011-2030.nc"
pontos="../../resources/arquivos_de_entrada/postoBoqueirao.txt"
inicio="2000-10-01"
fim="2060-10-01"
output="extracao.txt"
java -jar extrator-opendap.jar -d $dataset -p $pontos -i $inicio -f $fim -o $output
