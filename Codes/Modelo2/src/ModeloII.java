import ilog.concert.*;
import ilog.cplex.*;

import java.io.FileWriter; 
import java.io.IOException; 
import java.io.PrintWriter; 
import java.util.Scanner; 


public class ModeloII 
{
	private double startTime = 0;
	private double endTime = 0;
	private int countMemories = 0;
	private double taxaDeReducao = 0.1;// 10%
	private double frequencia;
	private double frequenciaReduzida;
	private double periodoReduzido;
	public boolean solveMe(int[] tamanhoBuffer, int[] taxaDeAcessoBuffer, int[] qtdPortasBuffer,
			int[] capacidadeMemoria, int[] larguraBandaMemoria, int[] qtdPortasMemoria,
			int qtdBuffers, int qtdMemoria, double periodo)
	{
		//quantidade de buffers
		/*int qtdBuffers = 4;
		int qtdMemoria = 2;
		
		int[] capacidadeMemoria   = {6,7};
		int[] larguraBandaMemoria = {80,80};
		int[] qtdPortasMemoria    = {6,7};
		
		int[] tamanhoBuffer       = {3,3,2,3};
		int[] taxaDeAcessoBuffer  = {4,2,1,4};
		int[] qtdPortasBuffer     = {3,2,2,3};
		*/
		
		//faz a redução do periodo em 10%
		frequencia = 1/periodo;
		frequenciaReduzida = frequencia*taxaDeReducao;
		periodoReduzido = 1/frequenciaReduzida;
		
		boolean result = false;
		try {
			//definindo o modelo
			IloCplex cplex = new IloCplex();
			
			//variaveis binarias
			IloNumVar[][] x = new IloNumVar[qtdBuffers][];
	        for(int i = 0; i < qtdBuffers; i++)
	            x[i] = cplex.boolVarArray(qtdMemoria);
	        
	        IloNumVar[] y = new IloNumVar[qtdMemoria];
	            y = cplex.boolVarArray(qtdMemoria);
	        
	        
	        //primeira equação
	        for(int j = 0;j<qtdMemoria;j++)
	        {
	        	IloLinearNumExpr v = cplex.linearNumExpr();
	            for(int i = 0; i < qtdBuffers; i++)
	              v.addTerm((double)tamanhoBuffer[i], x[i][j]);
	            cplex.addLe(v, cplex.prod(capacidadeMemoria[j], y[j]));
	        }
	        //segunda equação
	        for(int j = 0;j<qtdMemoria;j++)
	        {
	        	IloLinearNumExpr v = cplex.linearNumExpr();
	            for(int i = 0; i < qtdBuffers; i++)
	              v.addTerm((double)taxaDeAcessoBuffer[i], x[i][j]);
	            cplex.addLe(v,cplex.prod(larguraBandaMemoria[j], cplex.prod(y[j], periodoReduzido)));
	        }
	        //terceira equação
	        for(int j = 0;j<qtdMemoria;j++)
	        {
	        	IloLinearNumExpr v = cplex.linearNumExpr();
	            for(int i = 0; i < qtdBuffers; i++)
	              v.addTerm((double)qtdPortasBuffer[i], x[i][j]);
	            cplex.addLe(v, cplex.prod(qtdPortasMemoria[j], y[j]));
	        }
	        
	        //quarta equação
	        for(int i = 0; i < qtdBuffers; i++)
	            cplex.addEq(cplex.sum(x[i]), 1);

			//função objetivo
	        IloLinearNumExpr expr = cplex.linearNumExpr();
	        for (int i = 0; i < qtdMemoria; ++i) {
	              expr.addTerm(y[i], 1.);   
	        }
	        
	        cplex.addMinimize(expr);

	       
	        
	        startTime = cplex.getCplexTime();
	        result = cplex.solve();
	        endTime = cplex.getCplexTime();
	        /*
	        //salva as informações no arquivo para gerar gráficos
			if(result)
			{
				PrintWriter gravarArq = new PrintWriter(arq); 
		        gravarArq.printf(result+","+cplex.getObjValue()+","+cplex.getCplexTime()+"%n");
			}else
			{	
				PrintWriter gravarArq = new PrintWriter(arq); 
		        gravarArq.printf(result+","+"0"+","+cplex.getCplexTime()+"%n");
			}
	        */

	        System.out.println(cplex.getCplexTime());
	        if(result)
			{
	        	countMemories = (int)cplex.getObjValue();
				System.out.println("obj = "+cplex.getObjValue());
				//System.out.println("f = "+cplex.getValue(f));
				/*for(int i=0;i<qtdBuffers;i++)
				{
					for(int j=0;j<qtdMemoria;j++)
					{
						System.out.println("x["+i+"]["+j+"] :"+cplex.getValue(x[i][j]));
					}
				}*/
			}else
			{
				System.out.println("Infeasible");
			}

		} catch (IloException e) {
			e.printStackTrace();
		}
		return result;
	}

	public int getCountMemories()
	{
		return countMemories;
	}
	public double getTime() {
		return (endTime-startTime);
	}
	
}
