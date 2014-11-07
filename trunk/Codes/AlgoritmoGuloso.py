import copy;
listaMemorias    =  [['memoria1',6,6,80],['memoria2',7,7,80]]; #(nome,capacidade,qtdPortas,larguraBanda)

listaBuffers    = [['bufferA',4,4,4],['bufferB',4,4,2],['bufferC',2,2,1],['bufferD',3,3,4]]; #(nome,capacidade,qtdPortas,taxaAcesso)



#cria um dicionario de arrays de cada memoria para alocar os buffers
arrayMemoriaBuffer = {}
for i, item in enumerate(listaMemorias):
	arrayMemoriaBuffer[item[0]] = [];

def ordenarListaTupla(lista,i):
    lista.sort(key=lambda x: x[i]);
    return lista;

def alocaBuffer(lmemorias,lbuffers,f):
        qtdBuffers  = len(lbuffers);
        qtdMemorias = len(lmemorias);

        listaMemoriasAux = copy.deepcopy(lmemorias);
        listaBuffersAux   = copy.deepcopy(lbuffers);
        
        ordenarListaTupla(listaBuffersAux,3);#ordena pela taxa de acesso



        for i, bufer in enumerate(listaBuffersAux):   
                alocouBuffer = False;    
                for j, memoria in enumerate(listaMemoriasAux):
                        print("memoria%s cap:%i porta: %i banda: %i"%(memoria[0],memoria[1],memoria[2],memoria[3]))
                        if(memoria[1]>=bufer[1] and memoria[2]>=bufer[2] and memoria[3]>=(bufer[3]*f)):
                                varAuxCapNew = memoria[1] - bufer[1]; 
                                memoria[1] = varAuxCapNew;
                                varAuxPortNew = memoria[2] - bufer[2];
                                memoria[2] = varAuxPortNew;
                                arrayMemoriaBuffer[memoria[0]].append(bufer);
                                alocouBuffer = True;
                                break;
                if(alocouBuffer==True):
                        alocouBuffer = False;                        
                else:
                        listaMemoriasAux = [];
                        listaBuffersAux  = [];
                        return False;#nao alocou algum buffer
        listaMemoriasAux = [];
        listaBuffersAux  = [];
        return True;#alocou todos os buffers






def maximizaF(linf,lsup):
    f = (lsup+linf)/2

    while (lsup - linf  > 1):
       
        result = alocaBuffer(listaMemorias,listaBuffers,f)
        if result:
            linf = f
            f = (lsup+linf)/2
            print("alocou com f %i"%(f));
        else:
            lsup = f
            f = (lsup+linf)/2
            print("nao alocou com f %i"%(f));
linf = 1
lsup = 200
maximizaF(linf,lsup);



    
