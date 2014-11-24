#
# Alocacao de buffers em memorias
#

#conjuntos 
set I;
set J;

#parametros
param S{i in I}; /*tamanho do buffer i*/
param R{i in I}; /*taxa de acesso do buffer i*/
param Q{i in I};  /*quantidade de portas do buffer i*/

param C{j in J};  /* capacidade da memoria j */
param B{j in J};  /* largura da banda da memoria j */
param P{j in J};  /* quantidade de portas da memoria j*/



#variaveis
var x{i in I,j in J}, >=0, integer;
var F, >=0 , integer; /*Frequencia*/

#funcao objetivo
maximize FREQ: F;
  
#restricoes  
s.t. R1{i in I}:sum{j in J}S[i]*x[i,j]<=C[j];
s.t. R2{i in I}: F * sum{j in J}R[i]*x[i,j] <= B[j];
s.t. R3{i in I}:sum{j in J}Q[i]*x[i,j]<=P[j];
s.t. R4{i in I}:sum{j in J}x[i,j] = 1;
s.t. R5:F<=100;

solve;

printf"Frequencia Maxima %f", F;

#dados
data;

#conjuntos 
set I:=
1 #bufferA
2 #bufferB 
3 #bufferC 
4 #bufferD 
;
set J:=
1 #memory1 
2 #memory2 
;

#parametros
param S:=
1 4
2 4
3 2
4 3
;
param R:=
1 4 
2 2
3 1
4 4
;
param Q:=
1 4
2 4
3 2
4 3
;
param C:=
1 6
2 7
;
param B:=
1 80
2 80
;
param P:=
1 6
2 7
;

