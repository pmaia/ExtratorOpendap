*GrADS script to extract gridded values at station positions
*AUTHOR: pnobre@cptec.inpe.br 
*CREATION DATE: 22APR2003
*LAST ACTUALIZATION DATE: 29ABR2005
*BUG REPORTS: none to date

* linha comando: run getpmh
*input file: arquivo texto, com: <lon> <lat> <nome do posto> (para cada estacao, em graus decimais)
*output files: arquivos texto com: <lon> <lat> <alt> <AAAA-MM-DD> <00:00:00> <precipitação> <temp> (um arquivo por estacao)

function gstn(args)
*check whether there is an open control file
*'q file'
*if(subwrd(result,1)='')
* say 'NENHUM CTL AINDA ABERTO... '
* return
*endif

* atribui valores as variaveis
iname=subwrd(args,1)
_var1=subwrd(args,2)
_var2=subwrd(args,3)
timei=subwrd(args,4)
timef=subwrd(args,5)
_outname=subwrd(args,6)
_ctl=subwrd(args,7)

if(iname='' | iname='?' | _var='')
 say '# precisa passar parametros, EX.:' 
 say '# comando <arq_pontos.txt> <var1> <var2> <timei> <timef> <arq_saida.txt> <arq.ctl>'
 say '# run getpmh arq_pontos.txt prec temp 06Z16JAN1950 06Z16JAN1960 arq_saida.txt descritor.ctl'
 say '# script stoped'
 return
endif

* abre arquivo .ctl
'open '_ctl
                                                                                   
*check whether there is an open control file
'q file'
if(subwrd(result,1)='')
 say 'NENHUM CTL AINDA ABERTO... '
 return
endif

* toma a altitude
 'q dims'
 altline=sublin(result,4)
 _alt=subwrd(altline,6)

*trata a dimensao tempo da solicitacao de dados
tf=''
if(timei='')
*implies that actual time dimensions shall be used
 'q dims'
 timeline=sublin(result,5)
 timevar=subwrd(timeline,3)
 if(timevar='fixed')
  ti=subwrd(timeline,9)
  tf=ti
 else
  ti=subwrd(timeline,11)
  tf=subwrd(timeline,13)
 endif
else
*a specific time/timerange is given; first evaluates the initial time
 'set time 'timei
 'q dims'
 timeline=sublin(result,5)
 ti=subwrd(timeline,9)

*now evaluates the final time
 if(timef='')
*a single time is requested, so final time will be the same as initial time
  tf=ti
 else
*a time range is requested; inspect two final time possibilities
  if(timef='last')
   'set t 'timef
  else
   'set time 'timef
  endif
  'q dims'
  timeline=sublin(result,5)
  tf=subwrd(timeline,9)
 endif
endif
if(ti>tf)
 say '=> REQUESTED TIME RANGE: ti is > tf 'ti' 'tf' : pgm STOPED'
 return
endif

*descobre tempo final do ctl
'set t last'
'q dims'
lin=sublin(result,5)
tfinal=subwrd(lin,9)

*limita o periodo de dados solicitados ao periodo disponivel
 ti=math_int(ti)
 if(ti<1);ti=1;endif

 if(tf !='')  ; tf=math_int(tf); endif
 if(tf>tfinal); tf=tfinal      ; endif

*seta intervalo de tempo 
'set t 'ti' 'tf
'q dims'
timeline=sublin(result,5)
timevar=subwrd(timeline,3)
 timei=subwrd(timeline,6)
if(timevar='fixed')
 timef=''
else
 timef=subwrd(timeline,8)
endif

say '=> AVAILABLE TIME RANGE: 'timei' 'timef' 'ti' 'tf

timei='_'timei
if(timef!=''); timef='_'timef; endif
*find out the total number of grid points in the x direction
 'q file'
 lin = sublin(result,5)
 _npx=subwrd(lin,3)

*define o formato de saida dos valores de estacao:
*valores do campo desejado (variavel 1 e 2)
 fmt1 ='%12.1f'   
*latitudes e longitudes (graus decimais)
 fmt2 ='%9.4f'   
*contador do numero de registros
 fmt3 ='%4.0f'   
*latitudes e longitudes p formato saida/impressao
 fmt4='%19.4f'
*altitude p formato de saida
 fmt5='%7.0f'

 'set t 'ti
 'q time'
 aux=subwrd(result,3)
 mold=substr(aux,6,3)
 irec = 0
 tof='true'
 while (tof='true')
  b=read(iname)
  lin=sublin(b,1); a=subwrd(lin,1)

  if(a=0)
   val=sublin(b,2)
   if(val != '' & substr(val,1,1) != '*')
    say '=> NEW STATION: 'val
    irec = irec + 1
    _llon=subwrd(val,1)
    _llat=subwrd(val,2)
    posto=subwrd(val,3)

* extrai longitude e latitude no formato fmt4
     lonn=math_format(fmt4,_llon)
     latt=math_format(fmt4,_llat)
*loop sobre todos os tempos do intervalo desejado
    i=0
    val1ant=0
    val1=0
    t=ti-1
    while(t<tf)
     t=t+1
     'set t 't
     'q time'
*toma o terceito conjunto de caracteres como o tempo (EX: 00Z01JAN1971)
     tempo=subwrd(result,3)
     month=substr(tempo,6,3)

* se t=ti
     if(t=ti)
* toma valor interpolado p variavel_1
* se ti>1, toma o valor de ti-1 para subtrair de ti
       if(ti>1)
        t=ti-1
        'set t 't
        val1=interp_stn(_var1)
        'set t 'ti
        valor1=interp_stn(_var1)
        val1ant=valor1
        valor1=valor1-val1
        t=ti
       else
        valor1=interp_stn(_var1)
        val1ant=valor1
       endif
       valor1=math_format(fmt1,valor1)
* toma valor interpolado p variavel_2
       val2=interp_stn(_var2)
       if(val2!='-')
        val2=math_format(fmt1,val2)
       endif
     else
* toma valor interpolado p variavel_1 caso t>ti
       val1=interp_stn(_var1)
       val1=(val1 - val1ant)
       if(val1<=0)
        val1=0
       endif
       val1ant=interp_stn(_var1)
       valor1=math_format(fmt1,val1)
       val1=0
* toma valor interpolado p variavel_2
       val2=interp_stn(_var2)
       if(val2!='-')
        val2=math_format(fmt1,val2)
       endif
     endif

* formata a altitude
     alt=math_format(fmt5,_alt)
* escreve a data
     ano=substr(tempo,9,4)
     nomemes=substr(tempo,6,3)
* chama a funcao nummes (EX: se nomemes=JAN -> mes = 01)
     mes=nummes(nomemes)
     dia=substr(tempo,4,2)
* escreve a data no formato AAAA-MM-DD
     data=ano%'-'%mes%'-'%dia
* hora definida
     h=substr(tempo,1,2)
     hora=h':00:00'

* toma o nível separado da data (EX: 00Z)     
     if(i=1)
      a=write(_outname,lonn' 'latt' 'alt' 'data' 'hora' 'valor1' 'val2)
     else
      a=write(_outname,lonn' 'latt' 'alt' 'data' 'hora' 'valor1' 'val2, append)
     endif
    endwhile
   endif
   c=close(_outname)
  else
   tof='false'
  endif
 endwhile
return

*GrADS function to do bilinear interpolation from 4 nearest gridpoints:
*adapted from GrADS' forum list, msg from Bernd Becker, UKMETOFFICE.
*pnobre@cptec.inpe.br 15APR2003

function interp_stn(valor)
* verifica se os valores existem
if(_var1='' |_var2='' | _llon='' | _llat='')
 say 'interp_stn stoped'; return
endif

* verifica se foram declaradas as duas variaveis
if(valor='-')
* valor='-'
 return('-')
else
 'clear'
 'set gxout contour'
* mudei, estava lat 0 90
 'set lat -20 2'
 'set lon -50 2'
 'd 'valor
 'q w2gr '_llon' '_llat
 xdim=subwrd(result,3)
 xdim=0.+xdim
 if ( xdim <=  1.) ; xdim=_npx+xdim ; endif
 ydim=subwrd(result,6)
* nearest x dimensions :
 x1= math_int(xdim)
 x2 = x1 + 1
* the weights are
 xw1= xdim-x1
 xw2= 1.-xw1
* nearest y dimensions :
 y1= math_int(ydim)
 y2 = y1 + 1
* the weights are
 yw1= ydim-y1
 yw2= 1.-yw1

*  station =           f(x1,y1)*xw2*yw2+
*                      f(x2,y1)*xw1*yw2+
*                      f(x1,y2)*xw2*yw1+
*                      f(x2,y2)*xw1*yw1

 f11=gpv(valor,x1,y1)
 f21=gpv(valor,x2,y1)
 f12=gpv(valor,x1,y2)
 f22=gpv(valor,x2,y2)

 station = f11*xw2*yw2 + f21*xw1*yw2 + f12*xw2*yw1 + f22*xw1*yw1

 return(station)
endif

function gpv(v,x,y)
*get grid point value of field "v"
'set x 'x
'set y 'y
'd 'v
val=subwrd(result,4)
return(val)

* GrADS function script - tomar o mes JAN,FEB.. e passar p 01,02..
* Deve-se informar o mes JAN,FEB..
* 19 abril 2005, kleciaforte@yahoo.com.br, ufcg

function nummes(mesi)
 if (mesi = JAN) ; mesn='01' ; endif
 if (mesi = FEB) ; mesn='02' ; endif
 if (mesi = MAR) ; mesn='03' ; endif
 if (mesi = APR) ; mesn='04' ; endif
 if (mesi = MAY) ; mesn='05' ; endif
 if (mesi = JUN) ; mesn='06' ; endif
 if (mesi = JUL) ; mesn='07' ; endif
 if (mesi = AUG) ; mesn='08' ; endif
 if (mesi = SEP) ; mesn='09' ; endif
 if (mesi = OCT) ; mesn='10' ; endif
 if (mesi = NOV) ; mesn='11' ; endif
 if (mesi = DEC) ; mesn='12' ; endif
return(mesn)

* GrADS function script to search and remove a character from a string
* 11 Mar 1998, pnobre@iri.ldeo.columbia.edu

function firstname(var)
if(var='')
*say 'USAGE: run firstname var_name <special_character>'
return
endif

chr = subwrd(args,2)
if(chr=''); chr='.'; endif
fvr=''
k = 1
ip= 1

* loop through all charateres of the input string
while(substr(var,k,1)!='')
   if(substr(var,k,1)=chr)
      nc = k - ip
      if (nc=0)
        fvr = 0%' '
      else
        fvr = fvr%substr(var,ip,nc)%' '
      endif
      ip = k + 1
   endif
   k = k + 1
endwhile

fvr=subwrd(fvr,1)

return(fvr)
